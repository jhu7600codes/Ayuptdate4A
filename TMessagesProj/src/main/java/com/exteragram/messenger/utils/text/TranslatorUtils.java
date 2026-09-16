package com.exteragram.messenger.utils.text;

import com.exteragram.messenger.ExteraConfig;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

import java.util.Locale;

// Minimal stand-in for exteraGram's translator layer: Nagram ships its own
// translation stack, so this only keeps the target-language bookkeeping the
// settings screens rely on.
public class TranslatorUtils {

    public static void ensureTargetLanguageCompatibleWithProvider() {
        if (ExteraConfig.targetLang == null || ExteraConfig.targetLang.isEmpty()) {
            ExteraConfig.targetLang = "app";
        }
    }

    public static String normalizeLanguageCode(String code) {
        if (code == null) {
            return "app";
        }
        return code.replace('_', '-').toLowerCase(Locale.ROOT);
    }

    public static String getLanguageDisplayName(String code) {
        if (code == null || "app".equals(code)) {
            return LocaleController.getString(R.string.Default);
        }
        Locale locale = Locale.forLanguageTag(code);
        String name = locale.getDisplayName();
        return name.isEmpty() ? code : name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }

    public static String getTargetLanguageTitle() {
        return getLanguageDisplayName(ExteraConfig.targetLang);
    }
}
