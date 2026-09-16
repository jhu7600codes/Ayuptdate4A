package com.exteragram.messenger.utils.network;

import java.util.Set;

// exteraGram fetched these values from its own remote config service.
// This port has no such service, so every lookup yields the caller's default.
public class RemoteUtils {

    public static void init() {
    }

    public static Boolean getBooleanConfigValue(String key, boolean defaultValue) {
        return defaultValue;
    }

    public static Integer getIntConfigValue(String key, int defaultValue) {
        return defaultValue;
    }

    public static Float getFloatConfigValue(String key, float defaultValue) {
        return defaultValue;
    }

    public static Long getLongConfigValue(String key, long defaultValue) {
        return defaultValue;
    }

    public static String getStringConfigValue(String key, String defaultValue) {
        return defaultValue;
    }

    public static Set<String> getStringSetConfigValue(String key, Set<String> defaultValue) {
        return defaultValue;
    }
}
