package services.survey;

import dtos.survey.AgeGenderCoordDTO;
import dtos.survey.AnswerCoordDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import utils.QuestionCodes;

import java.util.List;

@ApplicationScoped
public class SurveyQueryService {

    @Inject EntityManager em;

    /** Devuelve una fila por respuesta marcada de UNA pregunta (por código) -> strings crudos */
    public List<String> findAnswersByQuestionCodeForNeighborhood(String questionCode, Long neighborhoodId) {
        String jpql = """
      SELECT a.answer
      FROM Campaign c
      JOIN c.survey s
      JOIN s.questionAnswers qa
      JOIN qa.answers a
      WHERE c.neighborhood.id = :nid
        AND qa.question.code = :code
    """;
        return em.createQuery(jpql, String.class)
                .setParameter("nid", neighborhoodId)
                .setParameter("code", questionCode)
                .getResultList();
    }

    public List<String> findAnswersByQuestionCodeForCampaign(String questionCode, Long campaignId) {
        String jpql = """
      SELECT a.answer
      FROM Campaign c
      JOIN c.survey s
      JOIN s.questionAnswers qa
      JOIN qa.answers a
      WHERE c.id = :cid
        AND qa.question.code = :code
    """;
        return em.createQuery(jpql, String.class)
                .setParameter("cid", campaignId)
                .setParameter("code", questionCode)
                .getResultList();
    }

    public List<AnswerCoordDTO> findCoordsByHealthConditionForNeighborhood(String conditionCode, Long neighborhoodId) {
        String jpql = """
        SELECT new dtos.survey.AnswerCoordDTO(qa.id, a.answer, qa.latitude, qa.longitude)
        FROM Campaign c
          JOIN c.survey s
          JOIN s.questionAnswers qa
          JOIN qa.answers a
        WHERE c.neighborhood.id = :nid
          AND qa.question.code = :condCode
          AND a.answer = :condition
          AND qa.latitude IS NOT NULL
          AND qa.longitude IS NOT NULL
    """;
        return em.createQuery(jpql, AnswerCoordDTO.class)
                .setParameter("nid", neighborhoodId)
                .setParameter("condCode", utils.QuestionCodes.HEALTH_CONDITION)
                .setParameter("condition", conditionCode)
                .getResultList();
    }

    public List<AnswerCoordDTO> findCoordsByConditionAndPublicCoverageForNeighborhood(
            String conditionCode, Long neighborhoodId) {
        String jpql = """
        SELECT new dtos.survey.AnswerCoordDTO(qaCond.id, aCond.answer, qaCond.latitude, qaCond.longitude)
        FROM Campaign c
          JOIN c.survey s
          JOIN s.questionAnswers qaCond
          JOIN qaCond.answers aCond
          JOIN s.questionAnswers qaCov
          JOIN qaCov.answers aCov
        WHERE c.neighborhood.id = :nid
          AND qaCond.question.code = :condCode
          AND aCond.answer       = :condition
          AND qaCov.question.code = :covCode
          AND aCov.answer = :publicSystem
          AND qaCond.sourceExternalId = qaCov.sourceExternalId
          AND qaCond.latitude IS NOT NULL
          AND qaCond.longitude IS NOT NULL
    """;
        return em.createQuery(jpql, AnswerCoordDTO.class)
                .setParameter("nid", neighborhoodId)
                .setParameter("condCode", utils.QuestionCodes.HEALTH_CONDITION)
                .setParameter("condition", conditionCode)
                .setParameter("covCode", utils.QuestionCodes.HEALTH_COVERAGE)
                .setParameter("publicSystem", models.enums.survey.HealthCoverage.PUBLIC_SYSTEM.name())
                .getResultList();
    }

    public List<dtos.survey.AgeGenderCoordDTO> findCoordsByConditionWithAgeForNeighborhood(
            String conditionCode, Long neighborhoodId
    ) {
        String jpql = """
        SELECT new dtos.survey.AgeGenderCoordDTO(qaCond.id, aAge.answer, null, qaCond.latitude, qaCond.longitude)
        FROM Campaign c
          JOIN c.survey s
          JOIN s.questionAnswers qaCond
          JOIN qaCond.answers aCond
          JOIN s.questionAnswers qaAge
          JOIN qaAge.answers aAge
        WHERE c.neighborhood.id = :nid
          AND qaCond.question.code = :qCond
          AND aCond.answer = :cond
          AND qaAge.question.code = :qAge
          AND qaCond.sourceExternalId = qaAge.sourceExternalId
          AND qaCond.latitude IS NOT NULL
          AND qaCond.longitude IS NOT NULL
    """;
        return em.createQuery(jpql, dtos.survey.AgeGenderCoordDTO.class)
                .setParameter("nid", neighborhoodId)
                .setParameter("qCond", utils.QuestionCodes.HEALTH_CONDITION)
                .setParameter("cond", conditionCode)
                .setParameter("qAge", utils.QuestionCodes.AGE)
                .getResultList();
    }

    // coords con CONDICIÓN + COBERTURA PÚBLICA + EDAD (para filtrar >= minAge)
    public List<dtos.survey.AgeGenderCoordDTO> findCoordsByConditionPublicCoverageWithAgeForNeighborhood(
            String conditionCode, Long neighborhoodId
    ) {
        String jpql = """
        SELECT new dtos.survey.AgeGenderCoordDTO(qaCond.id, aAge.answer, null, qaCond.latitude, qaCond.longitude)
        FROM Campaign c
          JOIN c.survey s
          JOIN s.questionAnswers qaCond
          JOIN qaCond.answers aCond
          JOIN s.questionAnswers qaCov
          JOIN qaCov.answers aCov
          JOIN s.questionAnswers qaAge
          JOIN qaAge.answers aAge
        WHERE c.neighborhood.id = :nid
          AND qaCond.question.code = :qCond
          AND aCond.answer = :cond
          AND qaCov.question.code = :qCov
          AND aCov.answer = :publicSystem
          AND qaAge.question.code = :qAge
          AND qaCond.sourceExternalId = qaCov.sourceExternalId
          AND qaCond.sourceExternalId = qaAge.sourceExternalId
          AND qaCond.latitude IS NOT NULL
          AND qaCond.longitude IS NOT NULL
    """;
        return em.createQuery(jpql, dtos.survey.AgeGenderCoordDTO.class)
                .setParameter("nid", neighborhoodId)
                .setParameter("qCond", utils.QuestionCodes.HEALTH_CONDITION)
                .setParameter("cond", conditionCode)
                .setParameter("qCov", utils.QuestionCodes.HEALTH_COVERAGE)
                .setParameter("publicSystem", models.enums.survey.HealthCoverage.PUBLIC_SYSTEM.name())
                .setParameter("qAge", utils.QuestionCodes.AGE)
                .getResultList();
    }
}
