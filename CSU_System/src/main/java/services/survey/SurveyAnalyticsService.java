package services.survey;

import dtos.survey.AgeRangeDTO;
import dtos.survey.GroupCountDTO;
import models.enums.LabelEnum;

import java.util.*;
import java.util.stream.Collectors;

public class SurveyAnalyticsService {

    private static final String ND = "N/D";

    public <E extends Enum<E> & LabelEnum> List<GroupCountDTO> count1D(
            List<String> raw,
            Class<E> enumClass
    ) {
        Map<String, Long> m = new HashMap<>();
        for (String it : raw) {
            String key = (it == null || it.isBlank()) ? ND : it.trim();
            m.merge(key, 1L, Long::sum);
        }

        return m.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    String key = e.getKey();
                    String label = key;
                    try {
                        E en = Enum.valueOf(enumClass, key);
                        label = en.getLabel();
                    } catch (IllegalArgumentException ignored) {}
                    return new GroupCountDTO(label, e.getValue());
                })
                .collect(Collectors.toList());
    }

    public List<GroupCountDTO> bucketAges(List<String> ages, List<AgeRangeDTO> ranges) {
        Map<String, Long> m = new HashMap<>();
        for (String s : ages) {
            Integer age = parseInt(s).orElse(null);
            String bucket = bucketAge(age, ranges);
            m.merge(bucket, 1L, Long::sum);
        }

        List<GroupCountDTO> res = new ArrayList<>();
        for (AgeRangeDTO r : ranges) {
            res.add(new GroupCountDTO(r.getLabel(), m.getOrDefault(r.getLabel(), 0L)));
        }
        long nd = m.getOrDefault(ND, 0L);
        if (nd > 0) res.add(new GroupCountDTO(ND, nd));
        return res;
    }

    private Optional<Integer> parseInt(String v) {
        try { return Optional.of(Integer.parseInt(v.trim())); }
        catch (Exception e) { return Optional.empty(); }
    }

    private String bucketAge(Integer age, List<AgeRangeDTO> ranges) {
        if (age == null) return ND;
        return ranges.stream().filter(r -> r.contains(age))
                .map(AgeRangeDTO::getLabel).findFirst().orElse(ND);
    }
}