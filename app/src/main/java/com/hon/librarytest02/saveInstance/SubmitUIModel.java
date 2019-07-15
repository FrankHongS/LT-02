package com.hon.librarytest02.saveInstance;

/**
 * Created by Frank_Hon on 7/11/2019.
 * E-mail: v-shhong@microsoft.com
 */
final class SubmitUIModel {
    final boolean inProgress;
    final boolean success;
    final String errorMessage;

    private SubmitUIModel(boolean inProgress, boolean success, String errorMessage) {
        this.inProgress = inProgress;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    static SubmitUIModel inProgress() {
        return new SubmitUIModel(true, false, "");
    }

    static SubmitUIModel success() {
        return new SubmitUIModel(false, true, "");
    }

    static SubmitUIModel failure(String errorMessage) {
        return new SubmitUIModel(false, false, errorMessage);
    }

    static SubmitUIModel idle() {
        return new SubmitUIModel(false, false, null);
    }
}
