package com.hon.librarytest02.transition;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.hon.librarytest02.R;
import com.hon.librarytest02.util.NavigationController;

/**
 * Created by Frank Hon on 2019-08-18 19:44.
 * E-mail: frank_hon@foxmail.com
 */
public class TransitionActivity extends AppCompatActivity {

    private NavigationController navigationController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);

        navigationController = new NavigationController(this, R.id.fragment_container);
        if (savedInstanceState == null) {
            navigationController.init(new FragmentA());
        }
    }

    public void navigateTo(Fragment target){
        navigationController.navigateTo(target);
    }

    public void navigateTo(Fragment target, Pair<View,String> transitionPair){
        navigationController.navigateTo(target, transitionPair);
    }
}
