package resources.survey;

import dtos.geo.LatLngDTO;
import dtos.geo.ZoneCountDTO;
import dtos.survey.AgeGenderCoordDTO;
import dtos.survey.AnswerCoordDTO;
import interceptors.AllowedRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import services.geo.ZoneLocator;
import services.survey.SurveyQueryService;

import java.util.*;
import java.util.stream.Collectors;

@AllowedRoles({"Admin","HealthStaff"})
@Path("/analytics/map")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Map Analytics", description = "Heatmap y conteo por zona con filtros opcionales (edad mínima)")
public class MapAnalyticsResource {

    @Inject SurveyQueryService query;
    @Inject ZoneLocator zoneLocator;

    // ============================
    // A) Heatmap por condición
    // ============================

    @GET
    @Path("/heat-disease")
    @Operation(
            summary = "Heatmap de casos por condición de salud",
            description = "Devuelve lat/lng de encuestas del barrio que tienen la condición (ej: DIABETES). Se puede filtrar por edad mínima (minAge).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = LatLngDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
            }
    )
    public List<LatLngDTO> heatByDisease(
            @Parameter(description = "ID del barrio", required = true)
            @QueryParam("neighborhoodId") Long neighborhoodId,

            @Parameter(description = "Código de condición (enum HealthCondition)", example = "DIABETES")
            @QueryParam("condition") @DefaultValue("DIABETES") String condition,

            @Parameter(description = "Edad mínima (ej. 18 para adultos). Si no se envía, no se filtra por edad.")
            @QueryParam("minAge") Integer minAge
    ) {
        if (neighborhoodId == null) throw new BadRequestException("neighborhoodId es requerido");

        if (minAge == null) {
            // Versión rápida (sin edad): usa el método existente
            List<AnswerCoordDTO> rows = query.findCoordsByHealthConditionForNeighborhood(condition, neighborhoodId);
            return rows.stream()
                    .filter(r -> r.lat != null && r.lon != null)
                    .map(r -> new LatLngDTO(r.lat, r.lon))
                    .toList();
        }

        // Con edad: unimos AGE y filtramos en Java
        List<AgeGenderCoordDTO> rows =
                query.findCoordsByConditionWithAgeForNeighborhood(condition, neighborhoodId);

        return rows.stream()
                .filter(r -> r.lat != null && r.lon != null)
                .filter(r -> parseAge(r.ageStr).map(a -> a >= minAge).orElse(false))
                .map(r -> new LatLngDTO(r.lat, r.lon))
                .toList();
    }

    // ===================================================
    // B) Conteo por zona: condición + “sin obra social”
    //     (proxy: cobertura = PUBLIC_SYSTEM) + minAge opcional
    // ===================================================

    @GET
    @Path("/zone-counts")
    @Operation(
            summary = "Conteo por zona de casos con condición + sin obra social",
            description = "Cuenta por zona a quienes tienen la condición indicada y cobertura Pública. Admite minAge (ej. 18).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = ZoneCountDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
            }
    )
    public List<ZoneCountDTO> zoneCountsForDiseaseAndNoCoverage(
            @Parameter(description = "ID del barrio", required = true)
            @QueryParam("neighborhoodId") Long neighborhoodId,

            @Parameter(description = "Código de condición (enum HealthCondition)", example = "DIABETES")
            @QueryParam("condition") @DefaultValue("DIABETES") String condition,

            @Parameter(description = "Edad mínima (ej. 18). Si no se envía, no se filtra por edad.")
            @QueryParam("minAge") Integer minAge
    ) {
        if (neighborhoodId == null) throw new BadRequestException("neighborhoodId es requerido");

        // Traemos puntos; si no hay minAge usamos el método rápido existente.
        final List<AgeGenderCoordDTO> ptsWithAge;
        if (minAge == null) {
            // convertir AnswerCoordDTO -> AgeGenderCoordDTO sin edad (no filtra)
            List<AnswerCoordDTO> base = query.findCoordsByConditionAndPublicCoverageForNeighborhood(condition, neighborhoodId);
            ptsWithAge = base.stream()
                    .map(a -> new AgeGenderCoordDTO(a.qaId, null, null, a.lat, a.lon))
                    .toList();
        } else {
            ptsWithAge = query.findCoordsByConditionPublicCoverageWithAgeForNeighborhood(condition, neighborhoodId);
        }

        // Conteo por zona (aplicando minAge si corresponde)
        Map<Long, Long> counts = new HashMap<>();
        for (var p : ptsWithAge) {
            if (p.lat == null || p.lon == null) continue;
            if (minAge != null && !parseAge(p.ageStr).map(a -> a >= minAge).orElse(false)) continue;

            Long zoneId = zoneLocator.findContainingZoneId(neighborhoodId, p.lat, p.lon);
            if (zoneId != null) counts.merge(zoneId, 1L, Long::sum);
        }

        // Salida ordenada desc
        List<ZoneCountDTO> out = new ArrayList<>();
        for (var e : counts.entrySet()) {
            String zoneName = zoneLocator.zoneName(e.getKey()).orElse("Zona " + e.getKey());
            out.add(new ZoneCountDTO(e.getKey(), zoneName, e.getValue()));
        }
        out.sort((a, b) -> Long.compare(b.count, a.count));
        return out;
    }

    // =================
    // Helpers internos
    // =================

    private Optional<Integer> parseAge(String s) {
        if (s == null) return Optional.empty();
        try { return Optional.of(Integer.parseInt(s.trim())); }
        catch (Exception e) { return Optional.empty(); }
    }
}