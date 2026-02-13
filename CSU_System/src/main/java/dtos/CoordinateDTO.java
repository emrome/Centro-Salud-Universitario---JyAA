package dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Geographic coordinate consisting of latitude and longitude",
        requiredProperties = {"lat", "lng"}
)
public class CoordinateDTO extends GenericDTO{

    @Schema(description = "Latitude component of the coordinate", example = "-34.9214")
    private Double lat;

    @Schema(description = "Longitude component of the coordinate", example = "-57.9544")
    private Double lng;

    public CoordinateDTO() {}

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
