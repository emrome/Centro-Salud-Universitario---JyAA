package services.survey;

import daos.CampaignDAO;
import daos.survey.*;
import dtos.survey.RawPersonResponseDTO;
import dtos.survey.RawQuestionAnswerDTO;
import exceptions.ResourceNotFoundException;
import mappers.AnswerMapper;
import models.Campaign;
import models.survey.*;
import jakarta.inject.Inject;
import parsers.SurveyCsvParser;
import utils.EnumMatcher;

import java.math.RoundingMode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.io.InputStream;

public class SurveyService {

    @Inject
    SurveyCsvParser parser;

    @Inject
    SurveyDAO surveyDAO;

    @Inject
    QuestionDAO questionDAO;

    @Inject
    CampaignDAO campaignDAO;

    public void create(InputStream formCsv, InputStream branchCsv, Long campaignId, String campaignName, String neighborhoodName) {
        Campaign campaign = validateCampaignExists(campaignId);

        List<RawPersonResponseDTO> people = loadPeople(formCsv, branchCsv);
        String surveyName = generateSurveyName(campaignName, neighborhoodName);
        Survey survey = prepareSurvey(surveyName);
        populateSurveyWithAnswers(people, survey);
        if (survey.getId() == null) {
            surveyDAO.save(survey);
        } else {
            surveyDAO.update(survey);
        }

        campaign.setSurvey(survey);
        campaignDAO.update(campaign);
    }

    public void delete(Long campaignId) {
        Campaign campaign = campaignDAO.findById(campaignId);
        if (campaign == null) {
            throw new ResourceNotFoundException("Campaign with ID " + campaignId + " does not exist.");
        }

        Survey survey = campaign.getSurvey();
        if (survey != null && !survey.isDeleted()) {
            survey.getQuestionAnswers().clear();
            survey.setUploadDate(LocalDate.now());
            surveyDAO.delete(survey);
            campaign.setSurvey(null);
            campaignDAO.update(campaign);
        }
    }

    private List<RawPersonResponseDTO> loadPeople(InputStream formCsv, InputStream branchCsv) {
        List<RawPersonResponseDTO> forms    = parser.parseFormCsv(formCsv);
        List<RawPersonResponseDTO> branches = parser.parseBranchCsv(branchCsv);

        Map<String, double[]> latLonByForm = new HashMap<>();
        for (RawPersonResponseDTO f : forms) {
            if (f.getSourceExternalId() != null && f.getLatitude() != null && f.getLongitude() != null) {
                latLonByForm.put(f.getSourceExternalId(), new double[]{ f.getLatitude(), f.getLongitude() });
            }
        }

        int filled = 0, missing = 0;
        for (RawPersonResponseDTO b : branches) {
            if (b.getLatitude() == null || b.getLongitude() == null) {
                String owner = b.getSourceOwnerExternalId(); // ec5_branch_owner_uuid
                double[] ll = owner == null ? null : latLonByForm.get(owner);
                if (ll != null) {
                    b.setLatitude(ll[0]);
                    b.setLongitude(ll[1]);
                    filled++;
                } else {
                    missing++;
                }
            }
        }

        System.out.printf("[Import] forms=%d, branches=%d, branchWithLatLonFilled=%d, branchMissingLatLon=%d%n",
                forms.size(), branches.size(), filled, missing);

        List<RawPersonResponseDTO> all = new ArrayList<>(forms.size() + branches.size());
        all.addAll(forms);
        all.addAll(branches);
        return all;
    }

    private String generateSurveyName(String campaignName, String neighborhoodName) {
        return campaignName + "_" + neighborhoodName + "_" + LocalDate.now();
    }

    private Survey prepareSurvey(String surveyName) {
        Optional<Survey> existing = surveyDAO.findByName(surveyName);
        if (existing.isPresent()) {
            Survey s = existing.get();
            s.getQuestionAnswers().clear();
            s.setUploadDate(LocalDate.now());
            s.setDeleted(false);
            return s;
        }
        Survey s = new Survey();
        s.setName(surveyName);
        s.setUploadDate(LocalDate.now());
        s.setQuestionAnswers(new ArrayList<>());
        return s;
    }

    private void populateSurveyWithAnswers(List<RawPersonResponseDTO> people, Survey survey) {
        for (RawPersonResponseDTO person : people) {
            for (RawQuestionAnswerDTO rawQa : person.getAnswers()) {
                Question question = findOrCreate(rawQa.getCode(), rawQa.getLabel());

                QuestionAnswer qa = new QuestionAnswer();
                qa.setQuestion(question);
                qa.setSurvey(survey);

                qa.setSourceType(person.getSourceType());
                qa.setSourceExternalId(person.getSourceExternalId());
                qa.setSourceOwnerExternalId(person.getSourceOwnerExternalId());
                qa.setLatitude(toBD(person.getLatitude()));
                qa.setLongitude(toBD(person.getLongitude()));

                java.util.Set<String> codes = new java.util.LinkedHashSet<>();
                for (String value : rawQa.getValues()) {
                    String code = AnswerMapper.map(rawQa.getCode(), value)
                            .orElse(EnumMatcher.key(value));
                    codes.add(code);
                }

                List<Answer> answerList = new ArrayList<>();
                for (String code : codes) {
                    Answer a = new Answer();
                    a.setAnswer(code);
                    a.setQuestionAnswer(qa);
                    answerList.add(a);
                }

                qa.setAnswers(answerList);
                survey.addQuestionAnswer(qa);
            }
        }
    }

    private static BigDecimal toBD(Double v) {
        if (v == null) return null;
        return new BigDecimal(String.valueOf(v))
                .setScale(6, RoundingMode.HALF_UP);
    }

    private Campaign validateCampaignExists(Long campaignId) {
        Campaign campaign = campaignDAO.findById(campaignId);
        if (campaign == null) {
            throw new ResourceNotFoundException("Campaign does not exist.");
        }
        return campaign;
    }

    private Question findOrCreate(String code, String label) {
        Question q = questionDAO.findByCode(code);
        if (q == null) {
            q = new Question();
            q.setCode(code);
            q.setText(label);
            questionDAO.save(q);
        }
        return q;
    }
}