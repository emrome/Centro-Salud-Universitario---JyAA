package dtos.survey;

public class GroupCountDTO {
    private String group;
    private long count;

    public GroupCountDTO() {}
    public GroupCountDTO(String group, long count) { this.group = group; this.count = count; }

    public String getGroup() { return group; }
    public long getCount() { return count; }
    public void setGroup(String group) { this.group = group; }
    public void setCount(long count) { this.count = count; }
}