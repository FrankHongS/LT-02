package com.hon.librarytest02.transition.navigation;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hon.librarytest02.R;
import com.hon.mylogger.MyLogger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank Hon on 2019-08-18 19:45.
 * E-mail: frank_hon@foxmail.com
 */
public class FragmentB extends Fragment {

    public static final String KEY_AVATAR_URL = "avatar_url";
    public static final String KEY_AVATAR_POSITION = "avatar_position";

    @BindView(R.id.iv_avatar)
    ImageView avatar;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transition_b, container, false);
        ButterKnife.bind(this, view);

        setSharedElementEnterTransition(
                TransitionInflater.from(getContext()).inflateTransition(R.transition.move)
        );

        handler.postDelayed(()->{
            MyLogger.tag("Hong");
            MyLogger.d("startPostponedEnterTransition");
            startPostponedEnterTransition();
        }, 1000);
        postponeEnterTransition();

        Bundle args = getArguments();
        if (args != null) {
            int position = args.getInt(KEY_AVATAR_POSITION);
            int imageId = args.getInt(KEY_AVATAR_URL);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                avatar.setTransitionName("avatar" + position);
            }

            Glide.with(view)
                    .load("https://avatars3.githubusercontent.com/u/9608466?s=400&v=4")
//                    .load(imageId)
                    .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            startPostponedEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            startPostponedEnterTransition();
                            return false;
                        }
                    })
                    .into(avatar);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLogger.tag("Hong");
        MyLogger.d("onDestroy");
        handler.removeCallbacksAndMessages(null);
    }
}
