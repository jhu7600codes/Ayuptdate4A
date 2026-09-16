package com.exteragram.messenger.utils.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.android.material.color.MaterialColors;
import java.util.HashMap;
import java.util.regex.Pattern;
import kotlin.jvm.internal.Intrinsics;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

/* loaded from: classes.dex */
public final class MonetUtils {
    private static final HashMap COLOR_MAP;
    public static final MonetUtils INSTANCE;
    private static final Pattern PARAM_PATTERN;
    private static final OverlayChangeReceiver overlayChangeReceiver;

    private MonetUtils() {
    }

    static {
        MonetUtils monetUtils = new MonetUtils();
        INSTANCE = monetUtils;
        HashMap hashMap = new HashMap();
        COLOR_MAP = hashMap;
        PARAM_PATTERN = Pattern.compile("^([^(]+)\\(([^)]+)\\)?$");
        overlayChangeReceiver = new OverlayChangeReceiver();
        hashMap.put("mBlack", Integer.valueOf(android.R.color.black));
        hashMap.put("mWhite", Integer.valueOf(android.R.color.white));
        hashMap.put("mRed200", Integer.valueOf(R.color.mRed200));
        hashMap.put("mRed500", Integer.valueOf(R.color.mRed500));
        hashMap.put("mRed800", Integer.valueOf(R.color.mRed800));
        hashMap.put("mGreen200", Integer.valueOf(R.color.mGreen200));
        hashMap.put("mGreen500", Integer.valueOf(R.color.mGreen500));
        hashMap.put("mGreen800", Integer.valueOf(R.color.mGreen800));
        monetUtils.initSystemColors();
    }

    /* JADX WARN: Removed duplicated region for block: B:71:0x00e6 A[Catch: Exception -> 0x0030, TryCatch #0 {Exception -> 0x0030, blocks: (B:6:0x000d, B:8:0x001c, B:10:0x0023, B:14:0x0034, B:16:0x003a, B:17:0x004f, B:19:0x0055, B:22:0x0070, B:50:0x00a1, B:38:0x00ad, B:27:0x00b9, B:64:0x00c7, B:66:0x00d1, B:69:0x00d8, B:71:0x00e6, B:73:0x00ef, B:75:0x00f9, B:76:0x0103, B:78:0x010d, B:81:0x0116), top: B:5:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x00ef A[Catch: Exception -> 0x0030, TryCatch #0 {Exception -> 0x0030, blocks: (B:6:0x000d, B:8:0x001c, B:10:0x0023, B:14:0x0034, B:16:0x003a, B:17:0x004f, B:19:0x0055, B:22:0x0070, B:50:0x00a1, B:38:0x00ad, B:27:0x00b9, B:64:0x00c7, B:66:0x00d1, B:69:0x00d8, B:71:0x00e6, B:73:0x00ef, B:75:0x00f9, B:76:0x0103, B:78:0x010d, B:81:0x0116), top: B:5:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x00f9 A[Catch: Exception -> 0x0030, TryCatch #0 {Exception -> 0x0030, blocks: (B:6:0x000d, B:8:0x001c, B:10:0x0023, B:14:0x0034, B:16:0x003a, B:17:0x004f, B:19:0x0055, B:22:0x0070, B:50:0x00a1, B:38:0x00ad, B:27:0x00b9, B:64:0x00c7, B:66:0x00d1, B:69:0x00d8, B:71:0x00e6, B:73:0x00ef, B:75:0x00f9, B:76:0x0103, B:78:0x010d, B:81:0x0116), top: B:5:0x000d }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final int getColor(java.lang.String r15) {
        /*
            Method dump skipped, instructions count: 288
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.exteragram.messenger.utils.ui.MonetUtils.getColor(java.lang.String):int");
    }

    public static final int harmonize(int i) {
        return MaterialColors.harmonize(i, ApplicationLoader.applicationContext.getColor(android.R.color.system_accent1_600));
    }

    public static final void registerReceiver(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        try {
            overlayChangeReceiver.register(context);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static final void unregisterReceiver(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        try {
            overlayChangeReceiver.unregister(context);
        } catch (Exception unused) {
        }
    }

    private static final class OverlayChangeReceiver extends BroadcastReceiver {
        private boolean isRegistered;

        public final void register(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            if (this.isRegistered) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter("android.intent.action.OVERLAY_CHANGED");
            intentFilter.addDataScheme("package");
            intentFilter.addDataSchemeSpecificPart("android", 0);
            context.registerReceiver(this, intentFilter);
            this.isRegistered = true;
        }

        public final void unregister(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            if (this.isRegistered) {
                context.unregisterReceiver(this);
                this.isRegistered = false;
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(intent, "intent");
            if (Intrinsics.areEqual("android.intent.action.OVERLAY_CHANGED", intent.getAction()) && Theme.getActiveTheme().isMonet()) {
                Theme.applyTheme(Theme.getActiveTheme(), Theme.isCurrentThemeNight());
            }
        }
    }

    private final void initSystemColors() {
        HashMap hashMap = COLOR_MAP;
        hashMap.put("a1_10", Integer.valueOf(android.R.color.system_accent1_10));
        hashMap.put("a1_50", Integer.valueOf(android.R.color.system_accent1_50));
        hashMap.put("a1_100", Integer.valueOf(android.R.color.system_accent1_100));
        hashMap.put("a1_200", Integer.valueOf(android.R.color.system_accent1_200));
        hashMap.put("a1_300", Integer.valueOf(android.R.color.system_accent1_300));
        hashMap.put("a1_400", Integer.valueOf(android.R.color.system_accent1_400));
        hashMap.put("a1_500", Integer.valueOf(android.R.color.system_accent1_500));
        hashMap.put("a1_600", Integer.valueOf(android.R.color.system_accent1_600));
        hashMap.put("a1_700", Integer.valueOf(android.R.color.system_accent1_700));
        hashMap.put("a1_800", Integer.valueOf(android.R.color.system_accent1_800));
        hashMap.put("a1_900", Integer.valueOf(android.R.color.system_accent1_900));
        hashMap.put("a2_10", Integer.valueOf(android.R.color.system_accent2_10));
        hashMap.put("a2_50", Integer.valueOf(android.R.color.system_accent2_50));
        hashMap.put("a2_100", Integer.valueOf(android.R.color.system_accent2_100));
        hashMap.put("a2_200", Integer.valueOf(android.R.color.system_accent2_200));
        hashMap.put("a2_300", Integer.valueOf(android.R.color.system_accent2_300));
        hashMap.put("a2_400", Integer.valueOf(android.R.color.system_accent2_400));
        hashMap.put("a2_500", Integer.valueOf(android.R.color.system_accent2_500));
        hashMap.put("a2_600", Integer.valueOf(android.R.color.system_accent2_600));
        hashMap.put("a2_700", Integer.valueOf(android.R.color.system_accent2_700));
        hashMap.put("a2_800", Integer.valueOf(android.R.color.system_accent2_800));
        hashMap.put("a2_900", Integer.valueOf(android.R.color.system_accent2_900));
        hashMap.put("a3_10", Integer.valueOf(android.R.color.system_accent3_10));
        hashMap.put("a3_50", Integer.valueOf(android.R.color.system_accent3_50));
        hashMap.put("a3_100", Integer.valueOf(android.R.color.system_accent3_100));
        hashMap.put("a3_200", Integer.valueOf(android.R.color.system_accent3_200));
        hashMap.put("a3_300", Integer.valueOf(android.R.color.system_accent3_300));
        hashMap.put("a3_400", Integer.valueOf(android.R.color.system_accent3_400));
        hashMap.put("a3_500", Integer.valueOf(android.R.color.system_accent3_500));
        hashMap.put("a3_600", Integer.valueOf(android.R.color.system_accent3_600));
        hashMap.put("a3_700", Integer.valueOf(android.R.color.system_accent3_700));
        hashMap.put("a3_800", Integer.valueOf(android.R.color.system_accent3_800));
        hashMap.put("a3_900", Integer.valueOf(android.R.color.system_accent3_900));
        hashMap.put("n1_10", Integer.valueOf(android.R.color.system_neutral1_10));
        hashMap.put("n1_50", Integer.valueOf(android.R.color.system_neutral1_50));
        hashMap.put("n1_100", Integer.valueOf(android.R.color.system_neutral1_100));
        hashMap.put("n1_200", Integer.valueOf(android.R.color.system_neutral1_200));
        hashMap.put("n1_300", Integer.valueOf(android.R.color.system_neutral1_300));
        hashMap.put("n1_400", Integer.valueOf(android.R.color.system_neutral1_400));
        hashMap.put("n1_500", Integer.valueOf(android.R.color.system_neutral1_500));
        hashMap.put("n1_600", Integer.valueOf(android.R.color.system_neutral1_600));
        hashMap.put("n1_700", Integer.valueOf(android.R.color.system_neutral1_700));
        hashMap.put("n1_800", Integer.valueOf(android.R.color.system_neutral1_800));
        hashMap.put("n1_900", Integer.valueOf(android.R.color.system_neutral1_900));
        hashMap.put("n2_10", Integer.valueOf(android.R.color.system_neutral2_10));
        hashMap.put("n2_50", Integer.valueOf(android.R.color.system_neutral2_50));
        hashMap.put("n2_100", Integer.valueOf(android.R.color.system_neutral2_100));
        hashMap.put("n2_200", Integer.valueOf(android.R.color.system_neutral2_200));
        hashMap.put("n2_300", Integer.valueOf(android.R.color.system_neutral2_300));
        hashMap.put("n2_400", Integer.valueOf(android.R.color.system_neutral2_400));
        hashMap.put("n2_500", Integer.valueOf(android.R.color.system_neutral2_500));
        hashMap.put("n2_600", Integer.valueOf(android.R.color.system_neutral2_600));
        hashMap.put("n2_700", Integer.valueOf(android.R.color.system_neutral2_700));
        hashMap.put("n2_800", Integer.valueOf(android.R.color.system_neutral2_800));
        hashMap.put("n2_900", Integer.valueOf(android.R.color.system_neutral2_900));
    }
}
