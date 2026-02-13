package dtos.survey;

import java.util.List;

public class DemographicsSummaryDTO {
    private List<GroupCountDTO> ageBuckets;   // para barras/pirámide (por ahora 1D)
    private List<GroupCountDTO> gender;       // varón/mujer/lgbt/nd
    private List<GroupCountDTO> job;          // tipo de empleo
    private List<GroupCountDTO> education;    // nivel educativo
    private List<GroupCountDTO> coverage;     // cobertura de salud

    public DemographicsSummaryDTO() {}

    public DemographicsSummaryDTO(
            List<GroupCountDTO> ageBuckets,
            List<GroupCountDTO> gender,
            List<GroupCountDTO> job,
            List<GroupCountDTO> education,
            List<GroupCountDTO> coverage
    ) {
        this.ageBuckets = ageBuckets;
        this.gender = gender;
        this.job = job;
        this.education = education;
        this.coverage = coverage;
    }

    public List<GroupCountDTO> getAgeBuckets() { return ageBuckets; }
    public List<GroupCountDTO> getGender() { return gender; }
    public List<GroupCountDTO> getJob() { return job; }
    public List<GroupCountDTO> getEducation() { return education; }
    public List<GroupCountDTO> getCoverage() { return coverage; }

    public void setAgeBuckets(List<GroupCountDTO> ageBuckets) { this.ageBuckets = ageBuckets; }
    public void setGender(List<GroupCountDTO> gender) { this.gender = gender; }
    public void setJob(List<GroupCountDTO> job) { this.job = job; }
    public void setEducation(List<GroupCountDTO> education) { this.education = education; }
    public void setCoverage(List<GroupCountDTO> coverage) { this.coverage = coverage; }
}