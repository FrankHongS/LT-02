package com.hon.librarytest02.saveInstance;

/**
 * Created by Frank_Hon on 7/11/2019.
 * E-mail: v-shhong@microsoft.com
 */
public final class SubmitEvent extends SubmitUIEvent{

    final String name;

    SubmitEvent(String name) {
        this.name = name;
    }
}
