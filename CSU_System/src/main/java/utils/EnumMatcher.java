package utils;

import models.enums.LabelEnum;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class EnumMatcher {
    private EnumMatcher() {}

    public static String key(String s){
        if (s == null) return "";
        String t = s.trim().replace("\n"," ").replace("\r"," ");
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}+","");
        return t.toUpperCase().replaceAll("[^A-Z0-9]+"," ").replaceAll("\\s+"," ").trim();
    }

    public static Optional<String> matchLabelCode(Class<? extends LabelEnum> enumClass, String raw){
        if (raw == null || raw.isBlank()) return Optional.empty();
        String in = key(raw);

        Object[] all = enumClass.getEnumConstants();
        for (Object o : all) {
            LabelEnum e = (LabelEnum) o;
            String k = key(e.getLabel());
            if (k.equals(in)) {
                return Optional.of(((Enum<?>) e).name());
            }
        }
        for (Object o : all) {
            LabelEnum e = (LabelEnum) o;
            String lbl = e.getLabel();
            String whole = key(lbl);
            if (in.equals(whole)) return Optional.of(((Enum<?>) e).name());
        }
        return Optional.empty();
    }

    public static String codeOrKey(Class<? extends LabelEnum> enumClass, String raw){
        return matchLabelCode(enumClass, raw).orElse(key(raw));
    }

    public static List<String> splitSmart(Class<? extends LabelEnum> enumClass, String raw){
        if (raw == null || raw.isBlank()) return List.of();

        if (matchLabelCode(enumClass, raw).isPresent()) {
            return List.of(raw.trim());
        }

        String safeRegex = "\\s*(?:/|;|\\n|\\r)\\s*";
        String[] safe = raw.split(safeRegex);
        if (safe.length > 1) {
            List<String> p = Arrays.stream(safe).map(String::trim).filter(s -> !s.isBlank()).toList();
            boolean allMap = p.stream().allMatch(s -> matchLabelCode(enumClass, s).isPresent());
            if (allMap) return p;
        }

        String[] tokens = Arrays.stream(raw.split("\\s*,\\s*"))
                .map(String::trim).filter(s -> !s.isBlank()).toArray(String[]::new);
        if (tokens.length == 1) return List.of(raw.trim());

        List<String> out = new ArrayList<>();
        StringBuilder acc = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (acc.length() > 0) acc.append(", ");
            acc.append(tokens[i]);
            String cur = acc.toString();
            if (matchLabelCode(enumClass, cur).isPresent()) {
                out.add(cur);
                acc.setLength(0);
            } else if (i == tokens.length - 1) {
                return List.of(raw.trim());
            }
        }
        return out.isEmpty() ? List.of(raw.trim()) : out;
    }
}