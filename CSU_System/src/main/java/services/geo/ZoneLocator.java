package services.geo;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import daos.ZoneDAO;
import models.Zone;
import models.Coordinate;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.index.strtree.STRtree;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ZoneLocator: cachea polígonos de zonas en memoria, con índice espacial (STRtree).
 * Soporta consultas globales y filtradas por barrio.
 */
@ApplicationScoped
public class ZoneLocator {

    @Inject ZoneDAO zoneDAO;

    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    /** Entrada cacheada para cada zona. */
    private static final class Entry {
        final Long zoneId;
        final String name;
        final Long neighborhoodId;
        final PreparedGeometry pg;
        final Envelope env;
        Entry(Long zoneId, String name, Long neighborhoodId, PreparedGeometry pg) {
            this.zoneId = zoneId; this.name = name; this.neighborhoodId = neighborhoodId;
            this.pg = pg; this.env = pg.getGeometry().getEnvelopeInternal();
        }
    }

    /** Cache: zoneId -> Entry */
    private final Map<Long, Entry> entries = new ConcurrentHashMap<>();

    /** Índice global (todas las zonas). */
    private volatile STRtree globalIdx = new STRtree();

    /** Índices por barrio. */
    private final Map<Long, STRtree> idxByNeighborhood = new ConcurrentHashMap<>();

    // ======================
    // Ciclo de vida / cache
    // ======================

    @PostConstruct
    public synchronized void rebuild() {
        entries.clear();
        idxByNeighborhood.clear();
        globalIdx = new STRtree();

        List<Zone> zones = zoneDAO.findAll();  // si tenés findByNeighborhoodId(...) mejor para calentar por partes

        int loaded = 0, skipped = 0;
        for (Zone z : zones) {
            try {
                var poly = buildPolygon(z);
                if (poly == null) { skipped++; continue; }
                var pg = PreparedGeometryFactory.prepare(poly);
                var e = new Entry(z.getId(), safeName(z), z.getNeighborhood().getId(), pg);

                entries.put(e.zoneId, e);
                globalIdx.insert(e.env, e);

                idxByNeighborhood
                        .computeIfAbsent(e.neighborhoodId, k -> new STRtree())
                        .insert(e.env, e);

                loaded++;
            } catch (Exception ex) {
                skipped++;
            }
        }
        globalIdx.build();
        idxByNeighborhood.values().forEach(STRtree::build);

        System.out.printf("[ZoneLocator] loaded=%d, skipped=%d, neighborhoods=%d%n",
                loaded, skipped, idxByNeighborhood.size());
    }

    /** Forzá recarga completa (llamar tras cambios de polígonos). */
    public synchronized void invalidateAndRebuild() {
        rebuild();
    }

    // ==========
    // Consultas
    // ==========

    /** ¿El punto cae dentro (o en borde) de la zona? */
    public boolean contains(Long zoneId, double lat, double lon) {
        var e = entries.get(zoneId);
        if (e == null) return false;
        Point p = point(lat, lon);
        return e.pg.covers(p);
    }

    /** Devuelve el zoneId que contiene el punto, o null si no hay coincidencia (índice global). */
    public Long findContainingZoneId(double lat, double lon) {
        Point p = point(lat, lon);
        @SuppressWarnings("unchecked")
        List<Entry> candidates = globalIdx.query(p.getEnvelopeInternal());
        for (Entry e : candidates) {
            if (e.pg.covers(p)) return e.zoneId;
        }
        return null;
    }

    /** Igual que arriba, pero restringiendo a un barrio (más preciso/rápido si los polígonos se solapan entre barrios). */
    public Long findContainingZoneId(Long neighborhoodId, double lat, double lon) {
        STRtree idx = idxByNeighborhood.get(neighborhoodId);
        if (idx == null) return findContainingZoneId(lat, lon); // fallback global
        Point p = point(lat, lon);
        @SuppressWarnings("unchecked")
        List<Entry> candidates = idx.query(p.getEnvelopeInternal());
        for (Entry e : candidates) {
            if (e.pg.covers(p)) return e.zoneId;
        }
        return null;
    }

    /** Nombre de la zona, si está en caché. */
    public Optional<String> zoneName(Long zoneId) {
        var e = entries.get(zoneId);
        return (e == null) ? Optional.empty() : Optional.ofNullable(e.name);
    }

    /** Listado liviano de zonas de un barrio (para UI/combos/diagnóstico). */
    public List<ZoneInfo> zonesOfNeighborhood(Long neighborhoodId) {
        return entries.values().stream()
                .filter(e -> Objects.equals(e.neighborhoodId, neighborhoodId))
                .map(e -> new ZoneInfo(e.zoneId, e.name, e.neighborhoodId))
                .collect(Collectors.toList());
    }

    // ==========
    // Helpers
        // ==========

    private static String safeName(Zone z) {
        try { return z.getName(); } catch (Exception ignored) { return "Zona " + z.getId(); }
    }

    private static Point point(double lat, double lon) {
        return GF.createPoint(new org.locationtech.jts.geom.Coordinate(lon, lat)); // x=lon, y=lat
    }

    /**
     * Construye el polígono JTS (sin hoyos) a partir de Zone.coordinates.
     * Si falta cerrar el anillo, lo cierra. Devuelve null si hay < 3 puntos válidos.
     */
    private static Polygon buildPolygon(Zone z) {
        List<org.locationtech.jts.geom.Coordinate> ring = new ArrayList<>();
        var coords = z.getCoordinates();
        if (coords == null || coords.size() < 3) return null;

        for (models.Coordinate c : coords) {
            if (c == null) continue;
            Double lat = c.getLatitude(), lon = c.getLongitude();
            if (lat == null || lon == null) continue;
            ring.add(new org.locationtech.jts.geom.Coordinate(lon, lat)); // x=lon, y=lat
        }
        if (ring.size() < 3) return null;

        org.locationtech.jts.geom.Coordinate first = ring.get(0);
        org.locationtech.jts.geom.Coordinate last  = ring.get(ring.size() - 1);
        if (!first.equals2D(last)) {
            ring.add(new org.locationtech.jts.geom.Coordinate(first));
        }

        LinearRing shell = GF.createLinearRing(ring.toArray(new org.locationtech.jts.geom.Coordinate[0]));
        if (!shell.isValid()) {
            shell = (LinearRing) shell.buffer(0).getBoundary(); // fallback
        }
        return GF.createPolygon(shell, null);
    }

}
