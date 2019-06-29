package com.hon.mylogger;

/**
 * Created by Frank_Hon on 2/14/2019.
 * E-mail: v-shhong@microsoft.com
 */
public final class MyLogger {

    private static final MyLoggerFacade LOGGER_FACADE = new MyLoggerFacade();

    private MyLogger() {
        throw new AssertionError();
    }

    public static void v(String message, Object... args) {
        LOGGER_FACADE.v(message, args);
    }

    public static void v(Throwable t, String message, Object... args) {
        LOGGER_FACADE.v(t, message, args);
    }

    public static void v(Throwable t) {
        LOGGER_FACADE.v(t);
    }

    public static void d(String message, Object... args) {
        LOGGER_FACADE.d(message, args);
    }

    public static void d(Throwable t, String message, Object... args) {
        LOGGER_FACADE.d(t, message, args);
    }

    public static void d(Throwable t) {
        LOGGER_FACADE.d(t);
    }

    public static void i(String message, Object... args) {
        LOGGER_FACADE.i(message, args);
    }

    public static void i(Throwable t, String message, Object... args) {
        LOGGER_FACADE.i(t, message, args);
    }

    public static void i(Throwable t) {
        LOGGER_FACADE.i(t);
    }

    public static void w(String message, Object... args) {
        LOGGER_FACADE.w(message, args);
    }

    public static void w(Throwable t, String message, Object... args) {
        LOGGER_FACADE.w(t, message, args);
    }

    public static void w(Throwable t) {
        LOGGER_FACADE.w(t);
    }

    public static void e(String message, Object... args) {
        LOGGER_FACADE.e(message, args);
    }

    public static void e(Throwable t, String message, Object... args) {
        LOGGER_FACADE.e(t, message, args);
    }

    public static void e(Throwable t) {
        LOGGER_FACADE.e(t);
    }

    public static void wtf(String message, Object... args) {
        LOGGER_FACADE.wtf(message, args);
    }

    public static void wtf(Throwable t, String message, Object... args) {
        LOGGER_FACADE.wtf(t, message, args);
    }

    public static void wtf(Throwable t) {
        LOGGER_FACADE.wtf(t);
    }

    public static void log(int priority, Throwable t) {
        LOGGER_FACADE.log(priority, t);
    }

    public static void log(int priority, String message, Object... args) {
        LOGGER_FACADE.log(priority, message, args);
    }

    public static void log(int priority, Throwable t, String message, Object... args) {
        LOGGER_FACADE.log(priority, t, message, args);
    }

    public static void tag(String tag) {
        LOGGER_FACADE.tag(tag);
    }

    public static void initLogFilePath(String logFilePath){
        LOGGER_FACADE.setLogFilePath(logFilePath);
    }
}
