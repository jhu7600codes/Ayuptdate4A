package com.exteragram.messenger.utils.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.SystemFonts;
import android.os.Build;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;

/* loaded from: classes.dex */
public abstract class FontUtils {
    private static int CANVAS_SIZE;
    private static Paint PAINT;
    private static String TEST_TEXT;
    private static volatile Boolean italicSupported;
    public static boolean loadSystemEmojiFailed;
    private static volatile Boolean mediumWeightSupported;
    private static Typeface systemEmojiTypeface;

    static {
        init();
    }

    private static void init() {
        List m;
        List m2;
        List m3;
        int dp = AndroidUtilities.dp(20.0f);
        CANVAS_SIZE = dp;
        loadSystemEmojiFailed = false;
        mediumWeightSupported = null;
        italicSupported = null;
        Paint paint = new Paint();
        PAINT = paint;
        paint.setTextSize(dp);
        paint.setAntiAlias(false);
        paint.setSubpixelText(false);
        paint.setFakeBoldText(false);
        String str = "en";
        try {
            if (LocaleController.getInstance() != null && LocaleController.getInstance().getCurrentLocale() != null) {
                str = LocaleController.getInstance().getCurrentLocale().getLanguage();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        m = java.util.Arrays.asList("zh", "ja", "ko");
        if (m.contains(str)) {
            TEST_TEXT = "你好";
            return;
        }
        m2 = java.util.Arrays.asList("ar", "fa");
        if (m2.contains(str)) {
            TEST_TEXT = "مرحبا";
            return;
        }
        if ("iw".equals(str)) {
            TEST_TEXT = "שלום";
            return;
        }
        if ("th".equals(str)) {
            TEST_TEXT = "สวัสดี";
            return;
        }
        if ("hi".equals(str)) {
            TEST_TEXT = "नमस्ते";
            return;
        }
        m3 = java.util.Arrays.asList("ru", "uk", "ky", "be", "sr");
        if (m3.contains(str)) {
            TEST_TEXT = "Привет";
        } else {
            TEST_TEXT = "R";
        }
    }

    public static boolean isMediumWeightSupported() {
        if (mediumWeightSupported == null) {
            synchronized (FontUtils.class) {
                try {
                    if (mediumWeightSupported == null) {
                        mediumWeightSupported = Boolean.valueOf(testTypeface(Typeface.create("sans-serif-medium", 0)));
                        FileLog.d("mediumWeightSupported = " + mediumWeightSupported);
                    }
                } finally {
                }
            }
        }
        return mediumWeightSupported.booleanValue();
    }

    public static boolean isItalicSupported() {
        if (italicSupported == null) {
            synchronized (FontUtils.class) {
                try {
                    if (italicSupported == null) {
                        italicSupported = Boolean.valueOf(testTypeface(Typeface.create("sans-serif", 2)));
                        FileLog.d("italicSupported = " + italicSupported);
                    }
                } finally {
                }
            }
        }
        return italicSupported.booleanValue();
    }

    private static boolean testTypeface(Typeface typeface) {
        List m;
        Canvas canvas = new Canvas();
        int i = CANVAS_SIZE;
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap createBitmap = Bitmap.createBitmap(i * 2, i, config);
        canvas.setBitmap(createBitmap);
        Paint paint = PAINT;
        paint.setTypeface(null);
        String str = TEST_TEXT;
        canvas.drawText(str, 0.0f, i, paint);
        Bitmap createBitmap2 = Bitmap.createBitmap(i * 2, i, config);
        canvas.setBitmap(createBitmap2);
        paint.setTypeface(typeface);
        canvas.drawText(str, 0.0f, i, paint);
        boolean z = !createBitmap.sameAs(createBitmap2);
        m = java.util.Arrays.asList(createBitmap, createBitmap2);
        AndroidUtilities.recycleBitmaps(m);
        return z;
    }

    public static File getSystemEmojiFontPath() {
        File fontFromSystemApi;
        if (Build.VERSION.SDK_INT >= 29 && (fontFromSystemApi = getFontFromSystemApi()) != null) {
            return fontFromSystemApi;
        }
        File fontFallback = getFontFallback();
        return fontFallback != null ? fontFallback : getFontFromFontsXml();
    }

    private static File getFontFromSystemApi() {
        try {
            Iterator<Font> it = SystemFonts.getAvailableFonts().iterator();
            File file = null;
            while (it.hasNext()) {
                File file2 = FontUtils$$ExternalSyntheticApiModelOutline2.m(it.next()).getFile();
                if (file2 != null) {
                    String lowerCase = file2.getName().toLowerCase();
                    if (lowerCase.contains("samsungcoloremoji")) {
                        return file2;
                    }
                    if (file == null && lowerCase.contains("emoji")) {
                        file = file2;
                    }
                }
            }
            return file;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private static File getFontFallback() {
        String[] strArr = {"/system/fonts/SamsungColorEmoji.ttf", "/system/fonts/NotoColorEmoji.ttf", "/system/fonts/AndroidEmoji.ttf"};
        for (int i = 0; i < 3; i++) {
            String str = strArr[i];
            File file = new File(str);
            if (file.exists()) {
                FileLog.d("emoji font file fallback = " + str);
                return file;
            }
        }
        return null;
    }

    private static File getFontFromFontsXml() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/system/etc/fonts.xml"));
            while (true) {
                boolean z = false;
                while (true) {
                    try {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            bufferedReader.close();
                            return null;
                        }
                        String trim = readLine.trim();
                        if (trim.startsWith("<family") && trim.contains("ignore=\"true\"")) {
                            z = true;
                        } else {
                            if (trim.startsWith("</family>")) {
                                break;
                            }
                            if (trim.startsWith("<font") && !z) {
                                int indexOf = trim.indexOf(">");
                                int indexOf2 = trim.indexOf("<", 1);
                                if (indexOf > 0 && indexOf2 > 0) {
                                    String substring = trim.substring(indexOf + 1, indexOf2);
                                    if (substring.toLowerCase().contains("emoji")) {
                                        File file = new File("/system/fonts/" + substring);
                                        if (file.exists()) {
                                            FileLog.d("emoji font file fonts.xml = " + substring);
                                            bufferedReader.close();
                                            return file;
                                        }
                                    } else {
                                        continue;
                                    }
                                }
                            }
                        }
                    } finally {
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static Typeface getSystemEmojiTypeface() {
        if (!loadSystemEmojiFailed && systemEmojiTypeface == null) {
            File systemEmojiFontPath = getSystemEmojiFontPath();
            if (systemEmojiFontPath != null) {
                systemEmojiTypeface = Typeface.createFromFile(systemEmojiFontPath);
            }
            if (systemEmojiTypeface == null) {
                loadSystemEmojiFailed = true;
            }
        }
        return systemEmojiTypeface;
    }

    public static Typeface getFontFromAssets(String str) {
        if (Build.VERSION.SDK_INT >= 26) {
            FontUtils$$ExternalSyntheticApiModelOutline1.m();
            Typeface.Builder m = FontUtils$$ExternalSyntheticApiModelOutline0.m(ApplicationLoader.applicationContext.getAssets(), str);
            if (str.contains("medium")) {
                m.setWeight(700);
            }
            if (str.contains("italic")) {
                m.setItalic(true);
            }
            return m.build();
        }
        return Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), str);
    }
}
