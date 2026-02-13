package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class CoordinateNormalizer {

    // Bounding box Berisso/La Plata (adjustable)
    private static final double LAT_MIN = -35.2, LAT_MAX = -34.5;
    private static final double LON_MIN = -58.3, LON_MAX = -57.3;

    private CoordinateNormalizer() {}

    public static Double normalizeLat(String raw) {
        return normalizeHeuristic(raw, List.of(2), -90d, 90d, LAT_MIN, LAT_MAX);
    }

    public static Double normalizeLon(String raw) {
        return normalizeHeuristic(raw, List.of(2, 3), -180d, 180d, LON_MIN, LON_MAX);
    }

    private static Double normalizeHeuristic(String raw,
                                             List<Integer> degreeOptions,
                                             double min, double max,
                                             Double bboxMin, Double bboxMax) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        String quick = s.replace(',', '.');
        if (countChar(quick, '.') <= 1 && quick.matches("[-+]?\\d+(\\.\\d+)?")) {
            try {
                double v = Double.parseDouble(quick);
                if (v >= min && v <= max && inBox(v, bboxMin, bboxMax)) return round6(v);
            } catch (NumberFormatException ignored) {}
        }

        boolean neg = quick.startsWith("-");
        String digits = quick.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return null;

        Double firstValid = null;
        for (int degDigits : degreeOptions) {
            String candidate = build(neg, digits, degDigits);
            if (candidate == null) continue;
            try {
                double v = Double.parseDouble(candidate);
                if (v < min || v > max) continue;
                if (inBox(v, bboxMin, bboxMax)) return round6(v);
                if (firstValid == null) firstValid = round6(v);
            } catch (NumberFormatException ignored) {}
        }
        return firstValid;
    }

    private static String build(boolean neg, String digits, int degDigits) {
        if (digits.length() <= degDigits) {
            return (neg ? "-" : "") + digits;
        }
        String deg = digits.substring(0, degDigits);
        String frac = digits.substring(degDigits);
        return (neg ? "-" : "") + deg + "." + frac;
    }

    private static boolean inBox(double v, Double min, Double max) {
        return (min == null || v >= min) && (max == null || v <= max);
    }

    private static int countChar(String s, char c) {
        int n = 0; for (int i = 0; i < s.length(); i++) if (s.charAt(i) == c) n++; return n;
    }

    private static Double round6(double v) {
        return new BigDecimal(v).setScale(6, RoundingMode.HALF_UP).doubleValue();
    }
}