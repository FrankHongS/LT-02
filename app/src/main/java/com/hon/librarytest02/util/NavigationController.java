package com.hon.librarytest02.util;

import android.util.Pair;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by Frank Hon on 2019-08-19 00:05.
 * E-mail: frank_hon@foxmail.com
 */
public final class NavigationController {

    private FragmentManager fragmentManager;
    private int containerId;

    public NavigationController(AppCompatActivity activity, int containerId) {
        this.containerId = containerId;
        fragmentManager = activity.getSupportFragmentManager();
    }

    public void init(Fragment target) {
        fragmentManager.beginTransaction()
                .add(containerId, target)
                .commit();
    }

    public void navigateTo(Fragment target) {
        fragmentManager.beginTransaction()
                .replace(containerId, target)
                .addToBackStack(null)
                .commit();
    }

    public void navigateTo(Fragment target, Pair<View, String> transitionPair) {
        fragmentManager.beginTransaction()
                .addSharedElement(transitionPair.first, transitionPair.second)
                .replace(containerId, target)
                .addToBackStack(null)
                .commit();
    }

}
