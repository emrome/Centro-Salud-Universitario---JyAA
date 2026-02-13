package services.geo;

public class ZoneInfo {
    public final Long id;
    public final String name;
    public final Long neighborhoodId;

    public ZoneInfo(Long id, String name, Long neighborhoodId) {
        this.id = id; this.name = name; this.neighborhoodId = neighborhoodId;
    }
}
