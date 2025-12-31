package holiday.render;

public final class LargeItemStackCounts {
    private LargeItemStackCounts() {
    }

    public static String format(int count) {
        return String.valueOf(count);
    }

    public static float getScale(String string) {
        int length = string.length();

        if (length >= 7) {
            return 1 / 3f;
        } else if (length >= 5) {
            return 15 / 32f;
        } else if (length >= 3) {
            return 2 / 3f;
        }

        return 1;
    }
}
