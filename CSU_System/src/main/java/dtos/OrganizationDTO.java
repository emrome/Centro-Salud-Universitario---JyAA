package dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import models.enums.MainActivity; // <-- usar el enum real

@Schema(name = "Organization")
public class OrganizationDTO {

    private Long id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "La Huella Barrial")
    private String name;

    @Schema(example = "Calle 10 N°123, Berisso")
    private String address;

    @Schema(description = "Main activity", implementation = MainActivity.class)
    private MainActivity mainActivity; // <-- ahora enum directo

    @Schema(example = "5", description = "Neighborhood ID")
    private Long neighborhoodId;

    @Schema(example = "Villa Argüello", description = "Read-only")
    private String neighborhoodName;

    private boolean deleted;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public MainActivity getMainActivity() { return mainActivity; }
    public void setMainActivity(MainActivity mainActivity) { this.mainActivity = mainActivity; }
    public Long getNeighborhoodId() { return neighborhoodId; }
    public void setNeighborhoodId(Long neighborhoodId) { this.neighborhoodId = neighborhoodId; }
    public String getNeighborhoodName() { return neighborhoodName; }
    public void setNeighborhoodName(String neighborhoodName) { this.neighborhoodName = neighborhoodName; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
