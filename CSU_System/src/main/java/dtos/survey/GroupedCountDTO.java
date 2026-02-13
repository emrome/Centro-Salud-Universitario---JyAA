// dtos/survey/GroupedCountDTO.java
package dtos.survey;

public class GroupedCountDTO {
    private String group;     // ej: rango etario "20-29"
    private String subgroup;  // ej: género "Mujer cis"
    private Long count;

    public GroupedCountDTO() {}

    public GroupedCountDTO(String group, String subgroup, Long count) {
        this.group = group;
        this.subgroup = subgroup;
        this.count = count;
    }

    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }

    public String getSubgroup() { return subgroup; }
    public void setSubgroup(String subgroup) { this.subgroup = subgroup; }

    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}
