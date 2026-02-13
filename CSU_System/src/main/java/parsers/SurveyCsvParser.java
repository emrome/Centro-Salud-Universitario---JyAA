package parsers;

import dtos.survey.RawPersonResponseDTO;
import dtos.survey.RawQuestionAnswerDTO;
import models.enums.LabelEnum;
import models.enums.survey.SourceType;
import utils.QuestionCodes;
import mappers.AnswerMapper;
import utils.CoordinateNormalizer;
import utils.EnumMatcher;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@ApplicationScoped
public class SurveyCsvParser {

    private static final String FORM_UUID  = "ec5_uuid";
    private static final String FORM_LAT   = "lat_1_Presione_actualiza";
    private static final String FORM_LON   = "long_1_Presione_actualiza";

    private static final String BR_UUID    = "ec5_branch_uuid";
    private static final String BR_OWNER   = "ec5_branch_owner_uuid";

    // Patrones para buscar columnas de coordenadas de forma más flexible
    private static final Pattern LAT_PATTERN = Pattern.compile(".*lat.*actualiza.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern LON_PATTERN = Pattern.compile(".*lon.*actualiza.*", Pattern.CASE_INSENSITIVE);

    private static final Map<String, String> FORM_QUESTION_MAP = Map.of(
            "8_3",  QuestionCodes.AGE,
            "9_4",  QuestionCodes.GENDER,
            "13_7", QuestionCodes.EDUCATION,
            "16_10",QuestionCodes.JOB,
            "22_15",QuestionCodes.HEALTH_COVERAGE,
            "27_20",QuestionCodes.HEALTH_CONDITION,
            "82_59",QuestionCodes.MEDICATION_ACCESS,
            "83_60",QuestionCodes.MEDICATION_SOURCE
    );

    private static final Map<String, String> BRANCH_QUESTION_MAP = FORM_QUESTION_MAP;

    private static final Set<String> MULTI_CODES = Set.of(
            QuestionCodes.JOB,
            QuestionCodes.HEALTH_COVERAGE,
            QuestionCodes.HEALTH_CONDITION,
            QuestionCodes.MEDICATION_SOURCE
    );

    public List<RawPersonResponseDTO> parseFormCsv(InputStream input) {
        return parseCsvWithMeta(input, FORM_QUESTION_MAP, SourceType.FORM);
    }

    public List<RawPersonResponseDTO> parseBranchCsv(InputStream input) {
        return parseCsvWithMeta(input, BRANCH_QUESTION_MAP, SourceType.BRANCH);
    }

    private List<RawPersonResponseDTO> parseCsvWithMeta(InputStream input,
                                                        Map<String, String> questionMap,
                                                        SourceType type) {
        List<RawPersonResponseDTO> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            CSVParser csv = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            // Debug: imprimir headers para diagnóstico
            System.out.println("Headers encontrados en CSV:");
            csv.getHeaderMap().keySet().forEach(System.out::println);

            for (CSVRecord record : csv) {
                Map<String, String> row = record.toMap();

                RawPersonResponseDTO person = new RawPersonResponseDTO();
                person.setSourceType(type);

                if (type == SourceType.FORM) {
                    person.setSourceExternalId(getValue(row, FORM_UUID));
                    person.setSourceOwnerExternalId(null);

                    // Método mejorado para obtener coordenadas
                    String latStr = getCoordinateValue(row, FORM_LAT, LAT_PATTERN);
                    String lonStr = getCoordinateValue(row, FORM_LON, LON_PATTERN);

                    Double normalizedLat = CoordinateNormalizer.normalizeLat(latStr);
                    Double normalizedLon = CoordinateNormalizer.normalizeLon(lonStr);

                    person.setLatitude(normalizedLat);
                    person.setLongitude(normalizedLon);

                    // Debug detallado para coordenadas
                    System.out.printf("=== Registro %s ===%n", person.getSourceExternalId());
                    System.out.printf("  Lat raw: '%s' -> normalizada: %s%n", latStr, normalizedLat);
                    System.out.printf("  Lon raw: '%s' -> normalizada: %s%n", lonStr, normalizedLon);

                    if (normalizedLat == null || normalizedLon == null) {
                        System.err.printf("  ¡ALERTA! Coordenadas nulas para registro %s%n",
                                person.getSourceExternalId());
                    }

                } else {
                    person.setSourceExternalId(getValue(row, BR_UUID));
                    person.setSourceOwnerExternalId(getValue(row, BR_OWNER));
                    person.setLatitude(null);
                    person.setLongitude(null);
                }

                for (Map.Entry<String, String> entry : questionMap.entrySet()) {
                    String prefix = entry.getKey();
                    String code   = entry.getValue();

                    for (String column : row.keySet()) {
                        if (!column.startsWith(prefix)) continue;

                        String raw = row.get(column);
                        if (raw == null || raw.isBlank()) continue;

                        Class<? extends LabelEnum> enumClass = AnswerMapper.getEnumClass(code);

                        List<String> values;
                        if (MULTI_CODES.contains(code) && enumClass != null) {
                            values = EnumMatcher.splitSmart(enumClass, raw);
                        } else {
                            values = List.of(raw.trim());
                        }

                        person.addAnswer(new RawQuestionAnswerDTO(code, column, values));
                    }
                }

                result.add(person);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV", e);
        }
        return result;
    }

    /**
     * Método mejorado para obtener coordenadas que maneja diferentes formatos
     */
    private static String getCoordinateValue(Map<String, String> row, String exactKey, Pattern pattern) {
        // 1. Búsqueda exacta
        String value = getValue(row, exactKey);
        if (value != null && !value.isBlank()) {
            return cleanCoordinate(value);
        }

        // 2. Búsqueda por patrón regex
        for (Map.Entry<String, String> entry : row.entrySet()) {
            if (pattern.matcher(entry.getKey()).matches()) {
                String coordValue = entry.getValue();
                if (coordValue != null && !coordValue.isBlank()) {
                    return cleanCoordinate(coordValue);
                }
            }
        }

        return null;
    }

    /**
     * Limpia el formato de coordenadas removiendo puntos como separadores de miles
     */
    private static String cleanCoordinate(String coordinate) {
        if (coordinate == null || coordinate.isBlank()) {
            return null;
        }

        String cleaned = coordinate.trim();

        // Caso específico: formato "-34.932.092" con puntos como separadores de miles
        // Lo convertimos a "-34932092" para que el CoordinateNormalizer lo maneje correctamente
        if (cleaned.contains(".") && cleaned.indexOf('.') != cleaned.lastIndexOf('.')) {
            // Contar cuántos puntos tiene
            long puntos = cleaned.chars().filter(ch -> ch == '.').count();

            if (puntos == 2) {
                // Formato típico: "-34.932.092" -> "-34932092"
                cleaned = cleaned.replace(".", "");
                System.out.printf("Coordenada limpiada: %s -> %s%n", coordinate, cleaned);
            } else if (puntos > 2) {
                // Múltiples puntos: remover todos excepto poner uno como decimal
                String[] parts = cleaned.split("\\.");
                StringBuilder sb = new StringBuilder();
                sb.append(parts[0]); // parte entera con signo

                // Concatenar todas las partes numéricas
                for (int i = 1; i < parts.length - 1; i++) {
                    sb.append(parts[i]);
                }

                // La última parte como decimales
                sb.append(".").append(parts[parts.length - 1]);
                cleaned = sb.toString();
                System.out.printf("Coordenada multipunto limpiada: %s -> %s%n", coordinate, cleaned);
            }
        }

        return cleaned;
    }

    private static String getValue(Map<String,String> row, String exactKey) {
        if (row.containsKey(exactKey)) return row.get(exactKey);
        String target = exactKey.toLowerCase();
        for (String k : row.keySet()) {
            if (k.toLowerCase().equals(target)) return row.get(k);
        }
        for (String k : row.keySet()) {
            if (k.toLowerCase().contains(target)) return row.get(k);
        }
        return null;
    }
}