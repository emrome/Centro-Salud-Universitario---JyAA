package models;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Zone extends BaseEntity {
	private String name;
	private String description;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name = "zone_coordinate",
			joinColumns = @JoinColumn(name = "zone_id"),
			inverseJoinColumns = @JoinColumn(name = "coordinate_id")
	)
	private List<Coordinate> coordinates;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "neighborhood_id")
	private Neighborhood neighborhood;

    public Zone() {
    	super();
    }

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}
	public Neighborhood getNeighborhood() {
		return neighborhood;
	}
	public void setNeighborhood(Neighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}

}