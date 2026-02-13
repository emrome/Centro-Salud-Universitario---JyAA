package mappers;

import models.enums.LabelEnum;
import utils.EnumMatcher;
import utils.QuestionCodes;

import java.util.Optional;

public class AnswerMapper {

    public static Class<? extends LabelEnum> getEnumClass(String qCode){
        return switch (qCode) {
            case QuestionCodes.HEALTH_COVERAGE   -> models.enums.survey.HealthCoverage.class;
            case QuestionCodes.JOB               -> models.enums.survey.Job.class;
            case QuestionCodes.HEALTH_CONDITION  -> models.enums.survey.HealthCondition.class;
            case QuestionCodes.EDUCATION         -> models.enums.survey.EducationLevel.class;
            case QuestionCodes.GENDER            -> models.enums.survey.GenderIdentity.class;
            case QuestionCodes.MEDICATION_ACCESS -> models.enums.survey.MedicationAccess.class;
            case QuestionCodes.MEDICATION_SOURCE -> models.enums.survey.MedicationSource.class;
            default -> null;
        };
    }

    public static Optional<String> map(String qCode, String raw) {
        Class<? extends LabelEnum> enumClass = getEnumClass(qCode);
        if (enumClass == null) return Optional.empty();
        return Optional.of(EnumMatcher.codeOrKey(enumClass, raw));
    }
}