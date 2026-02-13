package models.people;
import models.enums.Occupation;
import models.enums.survey.GenderIdentity;
import jakarta.persistence.*;

@Entity
public class Surveyor extends Person {
    private String dni;

	@Enumerated(EnumType.STRING)
    private GenderIdentity gender;

	@Enumerated(EnumType.STRING)
    private Occupation occupation;

    public Surveyor() {
    	super();
    }

	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public GenderIdentity getGender() {
		return gender;
	}
	public void setGender(GenderIdentity gender) {
		this.gender = gender;
	}
	public Occupation getOccupation() {
		return occupation;
	}
	public void setOccupation(Occupation occupation) {
		this.occupation = occupation;
	}
}
