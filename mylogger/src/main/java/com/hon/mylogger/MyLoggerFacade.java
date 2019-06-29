package com.hon.mylogger;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Frank_Hon on 6/20/2019.
 * E-mail: v-shhong@microsoft.com
 */
class MyLoggerFacade {
    private final ThreadLocal<String> explicitTag = new ThreadLocal<>();

    private static final int MAX_LOG_LENGTH = 4000;
    private static final int MAX_TAG_LENGTH = 23;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$[a-zA-Z]+)+$");

    private String mWriteLogFilePath;
    private static final String LOG_FILE_NAME = "my_logger.txt";

    private String[] fqcnIgnore = {
            MyLogger.class.getName(),
            MyLoggerFacade.class.getName()
    };

    public void v(String message, Object... args) {
        prepareLog(Log.VERBOSE, null, message, args);
    }

    public void v(Throwable t, String message, Object... args) {
        prepareLog(Log.VERBOSE, t, message, args);
    }

    public void v(Throwable t) {
        prepareLog(Log.VERBOSE, t, null);
    }

    public void d(String message, Object... args) {
        prepareLog(Log.DEBUG, null, message, args);
    }

    public void d(Throwable t, String message, Object... args) {
        prepareLog(Log.DEBUG, t, message, args);
    }

    public void d(Throwable t) {
        prepareLog(Log.DEBUG, t, null);
    }

    public void i(String message, Object... args) {
        prepareLog(Log.INFO, null, message, args);
    }

    public void i(Throwable t, String message, Object... args) {
        prepareLog(Log.INFO, t, message, args);
    }

    public void i(Throwable t) {
        prepareLog(Log.INFO, t, null);
    }

    public void w(String message, Object... args) {
        prepareLog(Log.WARN, null, message, args);
    }

    public void w(Throwable t, String message, Object... args) {
        prepareLog(Log.WARN, t, message, args);
    }

    public void w(Throwable t) {
        prepareLog(Log.WARN, t, null);
    }

    public void e(String message, Object... args) {
        prepareLog(Log.ERROR, null, message, args);
    }

    public void e(Throwable t, String message, Object... args) {
        prepareLog(Log.ERROR, t, message, args);
    }

    public void e(Throwable t) {
        prepareLog(Log.ERROR, t, null);
    }

    public void wtf(String message, Object... args) {
        prepareLog(Log.ASSERT, null, message, args);
    }

    public void wtf(Throwable t, String message, Object... args) {
        prepareLog(Log.ASSERT, t, message, args);
    }

    public void wtf(Throwable t) {
        prepareLog(Log.ASSERT, t, null);
    }

    public void log(int priority, String message, Object... args) {
        prepareLog(priority, null, message, args);
    }

    public void log(int priority, Throwable t, String message, Object... args) {
        prepareLog(priority, t, message, args);
    }

    public void log(int priority, Throwable t) {
        prepareLog(priority, t, null);
    }

    private void prepareLog(int priority, Throwable t, String message, Object... args) {
        String tag = getTag();
        if (!isLoggable(tag, priority)) {
            return;
        }

        if (TextUtils.isEmpty(message)) {
            if (t == null) {
                return;
            }
            message = getStackTraceString(t);
        } else {
            if (args.length != 0) {
                message = String.format(message, args);
            }

            if (t != null) {
                message += "\n" + getStackTraceString(t);
            }
        }

        log(priority, tag, message, t);

        writeLog2File(priority,tag,message);
    }

    private void log(int priority, String tag, String message, Throwable t) {
        if (message.length() < MAX_LOG_LENGTH) {
            if (priority == Log.ASSERT) {
                Log.wtf(tag, message);
            } else {
                Log.println(priority, tag, message);
            }

            return;
        }

        int i = 0;
        int length = message.length();
        while (i < length) {
            int newLine = message.indexOf('\n', i);
            if (newLine == -1) {
                newLine = length;
            }

            do {
                int end = Math.min(newLine, i + MAX_LOG_LENGTH);
                String part = message.substring(i, end);
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, part);
                } else {
                    Log.println(priority, tag, part);
                }
                i = end;
            } while (i < newLine);
            i++;
        }
    }

    private void writeLog2File(int priority, String tag, String message) {

        if (mWriteLogFilePath == null || "".equals(mWriteLogFilePath)) {
            return;
        }

        File dir = new File(mWriteLogFilePath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return;
            }
        }

        String appendMessage = "\r\n"
                + Util.getNowMDHMSTime()
                + "\r\n"
                + Util.mapLogPriority(priority)
                + "    "
                + tag
                + "\r\n"
                + message;
        File file = new File(dir, LOG_FILE_NAME);
        try {
            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(appendMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getStackTraceString(Throwable t) {
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public boolean isLoggable(String tag, int priority) {
        return isLoggable(priority);
    }

    public boolean isLoggable(int priority) {
        return BuildConfig.DEBUG;
    }

    private String getTag() {
        String tag = explicitTag.get();
        if (tag != null) {
            explicitTag.remove();
        }

        if (TextUtils.isEmpty(tag)) {
            StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
            for (StackTraceElement element : stackTraceElements) {
                if (!Arrays.asList(fqcnIgnore).contains(element.getClassName().split("\\$")[0])) {
                    return createStackElementTag(element);
                }
            }
        } else {
            return tag;
        }

        return "MyLogger";
    }

    private String createStackElementTag(StackTraceElement element) {
        String className = element.getClassName();
        String tag = className.substring(className.lastIndexOf('.') + 1);
        Matcher matcher = ANONYMOUS_CLASS.matcher(tag);
        if (matcher.find()) {
            tag = matcher.replaceAll("");
        }

        if (tag.length() > MAX_TAG_LENGTH || Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            tag = tag.substring(0, MAX_TAG_LENGTH);
        }

        return tag;
    }

    void tag(String tag) {
        explicitTag.set(tag);
    }

    void setLogFilePath(String logFilePath) {
        this.mWriteLogFilePath = logFilePath;
    }
}
