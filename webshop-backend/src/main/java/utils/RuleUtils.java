package utils;

public class RuleUtils {

    private RuleUtils() {
    }

    public static boolean isPositive(double value) {
        return value >= 0;
    }

    public static boolean isNonEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }
}
