package models;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class Neighborhood extends BaseEntity {

	private String name;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name = "neighborhood_coordinate",
			joinColumns = @JoinColumn(name = "neighborhood_id"),
			inverseJoinColumns = @JoinColumn(name = "coordinate_id")
	)
	private List<Coordinate> geolocation;

	private String description;

	@OneToMany(mappedBy = "neighborhood", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private List<Zone> zones;
	
    public Neighborhood() {
		super();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Coordinate> getGeolocation() {
		return geolocation;
	}
	public void setGeolocation(List<Coordinate> geolocation) {
		this.geolocation = geolocation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Zone> getZones() {
		return zones;
	}
	public void setZones(List<Zone> zones) {
		this.zones = zones;
	}
	public void addZone(Zone zone) {
		if (this.zones != null) {
			this.zones.add(zone);
		} else {
			this.zones = List.of(zone);
		}
		zone.setNeighborhood(this);
	}
}