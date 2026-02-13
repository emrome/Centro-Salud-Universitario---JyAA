package dtos.geo;

public class ZoneCountDTO {
    public Long zoneId;
    public String zoneName;
    public Long count;
    public ZoneCountDTO() {}
    public ZoneCountDTO(Long zoneId, String zoneName, Long count) {
        this.zoneId = zoneId; this.zoneName = zoneName; this.count = count;
    }
}
