package com.qminder.burgers.qminder.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HeaderGeneraterUtil {

    public static Map<String, String> generateHeaderMap(String data) {
        HashMap<String, String> headers = new HashMap<>();
        Arrays.stream(data.split(";"))
                .forEach(s -> {
                    String trimmed = s.trim();
                    headers.put(findKey(trimmed), findValue(trimmed));
                });
        headers.remove("OptanonConsent");
        return headers;
    }

    private static String findValue(String line) {
        return line.substring(line.indexOf("=") + 1);
    }

    private static String findKey(String line) {
        return line.substring(0, line.indexOf("="));
    }
}
