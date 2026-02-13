package models.people;
import java.util.List;

import models.Report;
import models.enums.Specialty;
import jakarta.persistence.*;

@Entity
public class HealthStaff extends User {

	@Enumerated(EnumType.STRING)
    private Specialty specialty;

	@Column(unique = true)
    private String license;

	@OneToMany(mappedBy = "author", cascade = CascadeType.PERSIST)
    private List<Report> createdReports;
    
    public HealthStaff() {
		super();
    }

	public Specialty getSpecialty() {
		return specialty;
	}
	public void setSpecialty(Specialty specialty) {
		this.specialty = specialty;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public List<Report> getCreatedReports() {
		return createdReports;
	}
	public void setCreatedReports(List<Report> createdReports) {
		this.createdReports = createdReports;
	}
}