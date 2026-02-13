package dtos.survey;

public class AgeRangeDTO {
    private int min;
    private int max;
    private String label;

    public AgeRangeDTO(int min, int max) {
        this.min = min; this.max = max;
        this.label = (max >= 150) ? String.format("%02d+", min) : String.format("%02d-%02d", min, max);
    }
    public AgeRangeDTO(int min, int max, String label) {
        this.min = min; this.max = max; this.label = label;
    }

    public boolean contains(int age) { return age >= min && age <= max; }
    public int getMin() { return min; }
    public int getMax() { return max; }
    public String getLabel() { return label; }

    public void setMin(int min) { this.min = min; }
    public void setMax(int max) { this.max = max; }
    public void setLabel(String label) { this.label = label; }
}
