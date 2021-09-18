package com.hon.librarytest02.dialog;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.hon.librarytest02.R;


/**
 * Created by conghui1 on 2016/7/8.
 */
public class CommonDialog extends DialogFragment {

    private CommonDialogParams mParams;

    public CommonDialog() {
        mParams = new CommonDialogParams.Builder().build();
    }

    private void setParams(CommonDialogParams params) {
        this.mParams = params;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            getDialog().setCanceledOnTouchOutside(mParams.cancelOutside);
            // 去掉内边距
            getDialog().getWindow().getDecorView().setPadding(0, 0, 0, 0);
            if (mParams.lp != null) {
                getDialog().getWindow().setAttributes(mParams.lp);
            } else {
                WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                getDialog().getWindow().setAttributes(lp);
            }

        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = getDialog().getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.0f;
            window.setAttributes(windowParams);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        initWindows();
        View root = inflater.inflate(mParams.layoutId, container, false);
        if (mParams.createViewDelegate != null) {
            mParams.createViewDelegate.initView(root, this);
        }
        return root;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), mParams.themeId);
        dialog.setCanceledOnTouchOutside(mParams.cancelOutside);
        return dialog;
    }

    public void showOnceSafely(FragmentManager manager, String tag) {
        if (manager == null) {
            return;
        }
        FragmentTransaction ft = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null) {
            //确保只显示一个
            ft.remove(fragment);
        }
        try {
            //切到后台后，才commit显示DialogFragment，可能发生FragmentTransaction.commit()异常
            show(ft, tag);
        } catch (Exception e) {

        }
    }

    /**
     * 初始化窗口
     */
    private void initWindows() {
        try {
            Window window = getDialog().getWindow();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return;
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //[4.4, 5.0)
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                //[5.0, +)
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        }
    }

    public static void show(FragmentManager manager, String tag, CommonDialogParams params) {
        CommonDialog dialog = new CommonDialog();
        if (params != null) {
            dialog.setParams(params);
        }
        dialog.show(manager, tag);
    }
}
