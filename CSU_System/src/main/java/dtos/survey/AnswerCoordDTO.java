package dtos.survey;

public class AnswerCoordDTO {
    public Long qaId;
    public String answer;
    public Double lat;
    public Double lon;

    public AnswerCoordDTO(Long qaId, String answer, Number lat, Number lon) {
        this.qaId = qaId;
        this.answer = answer;
        this.lat = (lat != null) ? lat.doubleValue() : null;
        this.lon = (lon != null) ? lon.doubleValue() : null;
    }

    public AnswerCoordDTO(Long qaId, String answer, Double lat, Double lon) {
        this(qaId, answer, (Number) lat, (Number) lon);
    }
}
