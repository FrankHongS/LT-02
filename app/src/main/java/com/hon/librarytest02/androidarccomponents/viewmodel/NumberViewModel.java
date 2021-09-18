package com.hon.librarytest02.androidarccomponents.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by Frank Hon on 2020/12/8 9:53 AM.
 * E-mail: frank_hon@foxmail.com
 */
public class NumberViewModel extends ViewModel {

    public int number = 0;
    public LiveData<String> numberLiveData = new MutableLiveData<>();

    public void updateNumber() {
        number++;
    }
}
