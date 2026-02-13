package models.people;
import jakarta.persistence.*;

@Entity
public class Admin extends User{
	private String positionInCSU;
	
	public Admin() {
		super();
	}

	public String getPositionInCSU() {
		return positionInCSU;
	}
	public void setPositionInCSU(String positionInCSU) {
		this.positionInCSU = positionInCSU;
	}
}
