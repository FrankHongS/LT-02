package com.hon.mylogger;

import android.text.TextUtils;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Frank_Hon on 6/18/2019.
 * E-mail: v-shhong@microsoft.com
 */
public abstract class Tree {
    private final ThreadLocal<String> explicitTag = new ThreadLocal<>();

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
    }

    public boolean isLoggable(String tag, int priority) {
        return isLoggable(priority);
    }

    public boolean isLoggable(int priority) {
        return true;
    }

    private String getTag() {
        String temp = explicitTag.get();
        if (temp != null) {
            explicitTag.remove();
        }
        return temp;
    }

    private String getStackTraceString(Throwable t) {
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    /**
     * Write a log message to its destination. Called for all level-specific methods by default.
     *
     * @param priority Log level. See [Log] for constants.
     * @param tag      Explicit or inferred tag. May be `null`.
     * @param message  Formatted log message. May be `null`, but then `t` will not be.
     * @param t        Accompanying exceptions. May be `null`, but then `message` will not be.
     */
    protected abstract void log(int priority, String tag, String message, Throwable t);

}
