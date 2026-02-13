package dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        description = "Data Transfer Object representing a geographic zone within a neighborhood",
        requiredProperties = {"name", "neighborhoodId", "coordinates"}
)
public class ZoneDTO extends GenericDTO {

    @Schema(description = "Name of the zone", example = "East Zone")
    private String name;

    @Schema(description = "Description of the zone", example = "Residential area with parks and schools")
    private String description;

    @Schema(description = "ID of the neighborhood to which this zone belongs", example = "10")
    private Long neighborhoodId;

    @Schema(description = "List of geographic coordinates defining the boundaries of the zone")
    private List<CoordinateDTO> coordinates;

    public ZoneDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(Long neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public List<CoordinateDTO> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<CoordinateDTO> coordinates) {
        this.coordinates = coordinates;
    }
}
