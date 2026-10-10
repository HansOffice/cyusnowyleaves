package org.cyu.snowyleaves.core;

public final class SnowTint {
    private SnowTint() {
    }

    public static int mix(int originalColor, int snowColor, float frostAmount) {
        float amount = clamp(frostAmount, 0.0F, 1.0F);
        float frostCurve = amount * amount * (3.0F - 2.0F * amount);
        float snowBlend = clamp(frostCurve * 1.06F, 0.0F, 0.97F);
        float veilBlend = clamp(frostCurve * 0.56F, 0.0F, 0.56F);

        int originalRed = (originalColor >> 16) & 0xFF;
        int originalGreen = (originalColor >> 8) & 0xFF;
        int originalBlue = originalColor & 0xFF;

        int snowRed = polishSnowChannel((snowColor >> 16) & 0xFF, 0xF6);
        int snowGreen = polishSnowChannel((snowColor >> 8) & 0xFF, 0xFA);
        int snowBlue = polishSnowChannel(snowColor & 0xFF, 0xFF);

        int red = blend(originalRed, snowRed, snowBlend);
        int green = blend(originalGreen, snowGreen, snowBlend);
        int blue = blend(originalBlue, snowBlue, snowBlend);

        red = blend(red, screen(red, snowRed), veilBlend);
        green = blend(green, screen(green, snowGreen), veilBlend);
        blue = blend(blue, screen(blue, snowBlue), veilBlend);

        return (red << 16) | (green << 8) | blue;
    }

    private static int polishSnowChannel(int configured, int highlight) {
        return blend(configured, highlight, 0.45F);
    }

    private static int screen(int base, int snow) {
        return clamp(255 - ((255 - base) * (255 - snow) + 127) / 255, 0, 255);
    }

    private static int blend(int from, int to, float amount) {
        return clamp(Math.round(from + (to - from) * amount), 0, 255);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float clamp(float value, float min, float max) {
        if (!Float.isFinite(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }
}
