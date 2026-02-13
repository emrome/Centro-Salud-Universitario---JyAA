package models;
import java.util.List;
import models.people.SocialOrgRepresentative;
import models.enums.MainActivity;
import jakarta.persistence.*;

@Entity
public class Organization extends BaseEntity {
    private String name;

	private String address;

	@Enumerated(EnumType.STRING)
	private MainActivity mainActivity;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "neighborhood_id")
	private Neighborhood neighborhood;

	@OneToMany(mappedBy = "organization", cascade = CascadeType.PERSIST)
	private List<SocialOrgRepresentative> representatives;
	
    public Organization() {
		super();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public MainActivity getMainActivity() {
		return mainActivity;
	}
	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}
	public Neighborhood getNeighborhood() {
		return neighborhood;
	}
	public void setNeighborhood(Neighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}
	public List<SocialOrgRepresentative> getRepresentatives() {
		return representatives;
	}
	public void setRepresentatives(List<SocialOrgRepresentative> representatives) {
		this.representatives = representatives;
	}
	public void addRepresentative(SocialOrgRepresentative representative) {
		if (representatives != null) {
			representatives.add(representative);
			representative.setOrganization(this);
		}
	}
}