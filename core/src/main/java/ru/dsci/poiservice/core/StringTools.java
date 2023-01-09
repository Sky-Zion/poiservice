package ru.dsci.poiservice.core;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class StringTools {
    private final static String URL_REG_EXP = "[ \"\'\\[\\]\\^|\'\\`]*";

    public static String cleanUrl(String url) {
        return url.toLowerCase(Locale.ROOT).replaceAll(URL_REG_EXP, "");
    }

    public static String unicodeToString (String unicode) {
        byte[] bytes = unicode.getBytes(StandardCharsets.UTF_16);
        for(byte a: bytes) {
            System.out.print(a);
        }
        return null;
    }

}


