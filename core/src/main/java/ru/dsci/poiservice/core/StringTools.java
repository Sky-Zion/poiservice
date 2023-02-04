package ru.dsci.poiservice.core;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class StringTools {
    private final static String URL_REG_EXP = "[ \"\'\\[\\]\\^|\'\\`]*";

    public static String cleanUrl(String url) {
        return url.toLowerCase(Locale.ROOT).replaceAll(URL_REG_EXP, "");
    }

    public static String trimEntries(String string, int trimNumEntries, String delimiter) {
        String[] chunks = string.split(delimiter);
        if (chunks.length <= trimNumEntries)
            throw new IllegalArgumentException(String.format(
                    "the number of elements [%d] is less than you want to trim [%d]",
                    chunks.length,
                    trimNumEntries));
        StringBuilder result = new StringBuilder();
        for (int i = trimNumEntries; i < chunks.length; i++)
            result.append(i == chunks.length - 1? chunks[i]: chunks[i] + delimiter);
        return result.toString();
    }

}


