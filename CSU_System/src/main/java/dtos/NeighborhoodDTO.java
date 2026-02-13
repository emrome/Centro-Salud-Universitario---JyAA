package dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO representing a neighborhood and its geolocation")
public class NeighborhoodDTO extends GenericDTO{

    @Schema(description = "Name of the neighborhood", example = "Barrio Centro")
    private String name;

    @Schema(description = "Short description of the neighborhood", example = "Main central neighborhood with high density")
    private String description;

    @Schema(description = "List of geolocation coordinates that define the neighborhood boundary")
    private List<CoordinateDTO> geolocation;

    public NeighborhoodDTO() {}


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<CoordinateDTO> getGeolocation() { return geolocation; }
    public void setGeolocation(List<CoordinateDTO> geolocation) { this.geolocation = geolocation; }
}
