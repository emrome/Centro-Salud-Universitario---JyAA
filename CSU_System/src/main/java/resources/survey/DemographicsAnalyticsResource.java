package resources.survey;

import dtos.survey.*;
import interceptors.AllowedRoles;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import models.enums.survey.*;
import services.survey.SurveyAnalyticsService;
import services.survey.SurveyQueryService;
import utils.QuestionCodes;

import java.util.*;

@AllowedRoles({"Admin","HealthStaff"})
@Path("/analytics/demographics")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Analytics Resource")
public class DemographicsAnalyticsResource {

    @Inject
    SurveyQueryService query;
    @Inject
    SurveyAnalyticsService analytics;

    @GET
    @Path("/summary")
    public DemographicsSummaryDTO summary(
            @QueryParam("neighborhoodId") Long neighborhoodId,
            @QueryParam("campaignId") Long campaignId
    ) {
        if (neighborhoodId == null && campaignId == null) {
            throw new BadRequestException("Debe enviar neighborhoodId o campaignId");
        }
        boolean byCampaign = campaignId != null;

        List<String> ages = answersFor(QuestionCodes.AGE, neighborhoodId, campaignId, byCampaign);
        List<String> gender = answersFor(QuestionCodes.GENDER, neighborhoodId, campaignId, byCampaign);
        List<String> job = answersFor(QuestionCodes.JOB, neighborhoodId, campaignId, byCampaign);
        List<String> education = answersFor(QuestionCodes.EDUCATION, neighborhoodId, campaignId, byCampaign);
        List<String> coverage = answersFor(QuestionCodes.HEALTH_COVERAGE, neighborhoodId, campaignId, byCampaign);

        List<AgeRangeDTO> ranges = List.of(
                new AgeRangeDTO(0, 9), new AgeRangeDTO(10, 19), new AgeRangeDTO(20, 29),
                new AgeRangeDTO(30, 39), new AgeRangeDTO(40, 49), new AgeRangeDTO(50, 59),
                new AgeRangeDTO(60, 69), new AgeRangeDTO(70, 150)
        );

        return new DemographicsSummaryDTO(
                analytics.bucketAges(ages, ranges),
                analytics.count1D(gender, GenderIdentity.class),
                analytics.count1D(job, Job.class),
                analytics.count1D(education, EducationLevel.class),
                analytics.count1D(coverage, HealthCoverage.class)
        );
    }

    @GET
    @Path("/age-pyramid")
    public List<GroupedCountDTO> agePyramid(
            @QueryParam("neighborhoodId") Long neighborhoodId,
            @QueryParam("campaignId") Long campaignId
    ) {
        if (neighborhoodId == null && campaignId == null) {
            throw new BadRequestException("Debe enviar neighborhoodId o campaignId");
        }
        boolean byCampaign = campaignId != null;

        List<AgeRangeDTO> ranges = List.of(
                new AgeRangeDTO(0, 9), new AgeRangeDTO(10, 19), new AgeRangeDTO(20, 29),
                new AgeRangeDTO(30, 39), new AgeRangeDTO(40, 49), new AgeRangeDTO(50, 59),
                new AgeRangeDTO(60, 69), new AgeRangeDTO(70, 150)
        );

        List<String> ages = byCampaign
                ? query.findAnswersByQuestionCodeForCampaign(QuestionCodes.AGE, campaignId)
                : query.findAnswersByQuestionCodeForNeighborhood(QuestionCodes.AGE, neighborhoodId);

        List<String> genders = byCampaign
                ? query.findAnswersByQuestionCodeForCampaign(QuestionCodes.GENDER, campaignId)
                : query.findAnswersByQuestionCodeForNeighborhood(QuestionCodes.GENDER, neighborhoodId);

        Map<String, Map<String, Long>> grouped = new HashMap<>();
        for (int i = 0; i < ages.size(); i++) {
            String ageStr = ages.get(i);
            String genderKey = (i < genders.size()) ? genders.get(i) : "N/D";
            String genderLabel;
            try {
                genderLabel = GenderIdentity.valueOf(genderKey).getLabel();
            } catch (IllegalArgumentException e) {
                genderLabel = genderKey;
            }

            Integer age = null;
            try {
                age = Integer.parseInt(ageStr);
            } catch (Exception ignored) {
            }

            Integer finalAge = age;
            String bucket = (age == null)
                    ? "N/D"
                    : ranges.stream().filter(r -> r.contains(finalAge)).findFirst()
                    .map(AgeRangeDTO::getLabel).orElse("N/D");

            grouped.computeIfAbsent(bucket, k -> new HashMap<>())
                    .merge(genderLabel, 1L, Long::sum);
        }

        List<GroupedCountDTO> finalResult = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            for (var sub : entry.getValue().entrySet()) {
                finalResult.add(new GroupedCountDTO(entry.getKey(), sub.getKey(), sub.getValue()));
            }
        }
        return finalResult;
    }

    /**
     * Helper para summary
     */
    private List<String> answersFor(String code, Long neighborhoodId, Long campaignId, boolean byCampaign) {
        return byCampaign
                ? query.findAnswersByQuestionCodeForCampaign(code, campaignId)
                : query.findAnswersByQuestionCodeForNeighborhood(code, neighborhoodId);
    }
}