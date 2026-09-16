package com.exteragram.messenger.utils.system;

import android.content.Context;
import android.content.Intent;
import android.os.Process;

import org.telegram.messenger.ApplicationLoader;

public class ProcessUtils {

    public static void killApplication() {
        Process.killProcess(Process.myPid());
    }

    public static void restartApplication() {
        Context context = ApplicationLoader.applicationContext;
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
        killApplication();
    }
}
