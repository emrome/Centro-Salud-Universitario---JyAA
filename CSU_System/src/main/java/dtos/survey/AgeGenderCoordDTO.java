package dtos.survey;

public class AgeGenderCoordDTO {
    public Long qaId;
    public String ageStr;
    public String gender;
    public Double lat;
    public Double lon;

    public AgeGenderCoordDTO(Long qaId, String ageStr, String gender, Number lat, Number lon) {
        this.qaId = qaId;
        this.ageStr = ageStr;
        this.gender = gender;
        this.lat = (lat != null) ? lat.doubleValue() : null;
        this.lon = (lon != null) ? lon.doubleValue() : null;
    }
}
