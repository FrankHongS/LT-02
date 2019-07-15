package com.hon.librarytest02.saveInstance;

/**
 * Created by Frank_Hon on 7/12/2019.
 * E-mail: v-shhong@microsoft.com
 */
public enum CheckNameResult implements Result{
    IN_FLIGHT(true, false, null),
    SUCCESS(false, true, null),
    FAILURE(false, false, "");

    private boolean inProgress;
    private boolean success;
    private String errorMessage;

    CheckNameResult(boolean inProgress, boolean success, String errorMessage) {
        this.inProgress = inProgress;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    static CheckNameResult failure(String errorMessage) {
        FAILURE.errorMessage = errorMessage;
        return FAILURE;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}