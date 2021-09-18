package com.hon.librarytest02.dialog;

import android.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by shuaihua on 2021/6/23 5:18 PM
 * Email: shuaihua@staff.sina.com.cn
 * <p>
 * Parameters for {@link CommonDialog}
 */
public class CommonDialogParams {
    public int themeId;
    public int layoutId;
    //新增回调，里面带有当前dialog对象
    public CreateViewDelegate createViewDelegate;
    //window参数
    public WindowManager.LayoutParams lp;
    //点击外部是否关闭对话框
    public boolean cancelOutside;
    public boolean cancelable;

    private CommonDialogParams(Builder builder) {
        themeId = builder.themeId;
        layoutId = builder.layoutId;
        createViewDelegate = builder.createViewDelegate;
        lp = builder.lp;
        cancelOutside = builder.cancelOutside;
        cancelable = builder.cancelable;
    }

    public static class Builder {
        private int themeId;
        private int layoutId;
        private CreateViewDelegate createViewDelegate;
        private WindowManager.LayoutParams lp;
        private boolean cancelOutside;
        private boolean cancelable;

        public Builder() {

        }

        public Builder setThemeId(int themeId) {
            this.themeId = themeId;
            return this;
        }

        public Builder setLayoutId(int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        public Builder setCreateViewDelegate(CreateViewDelegate delegate) {
            this.createViewDelegate = delegate;
            return this;
        }

        public Builder setWindowLayoutParams(WindowManager.LayoutParams lp) {
            this.lp = lp;
            return this;
        }

        public Builder setCancelOutside(boolean cancelOutside) {
            this.cancelOutside = cancelOutside;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public CommonDialogParams build() {
            return new CommonDialogParams(this);
        }
    }

    public interface CreateViewDelegate {
        void initView(View root, DialogFragment dialogFragment);
    }
}
