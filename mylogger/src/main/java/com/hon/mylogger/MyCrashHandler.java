package com.hon.mylogger;

/**
 * Created by Frank Hon on 2019/6/29 1:26 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class MyCrashHandler {

    public static void init(String versionName) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(versionName));
    }

    public static void init() {
        init("");
    }

    static class CrashHandler implements Thread.UncaughtExceptionHandler {

        private Thread.UncaughtExceptionHandler mDefaultHandler;

        private String mVersionName;

        CrashHandler(String versionName) {
            this.mVersionName = versionName;
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void uncaughtException(Thread thread, Throwable t) {
            MyLogger.e(t, collectCrashDeviceInfo());
            mDefaultHandler.uncaughtException(thread, t);
        }

        // device info
        private String collectCrashDeviceInfo() {
            String model = android.os.Build.MODEL;
            String androidVersion = android.os.Build.VERSION.RELEASE;
            String manufacturer = android.os.Build.MANUFACTURER;

            return mVersionName + "  " + model + "  " + androidVersion + "  " + manufacturer;
        }

    }

}
