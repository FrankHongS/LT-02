package com.hon.librarytest02.transition.navigation;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hon.librarytest02.AppExecutors;
import com.hon.librarytest02.R;
import com.hon.librarytest02.transition.FragmentB;
import com.hon.librarytest02.transition.ItemEntity;
import com.hon.librarytest02.transition.TransitionListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank Hon on 2019-08-18 19:45.
 * E-mail: frank_hon@foxmail.com
 */
public class FragmentA extends Fragment {

    private static final int[] IMAGE_ARRAY = {
            R.mipmap.test01, R.mipmap.test02, R.mipmap.test03, R.mipmap.test04,
            R.mipmap.test01, R.mipmap.test02, R.mipmap.test03, R.mipmap.test04,
            R.mipmap.test01, R.mipmap.test02, R.mipmap.test03, R.mipmap.test04
    };

    @BindView(R.id.rv_transition)
    RecyclerView list;

    private TransitionListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transition_a, container, false);
        ButterKnife.bind(this, view);

        setSharedElementReturnTransition(
                TransitionInflater.from(getContext()).inflateTransition(R.transition.move)
        );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new TransitionListAdapter(AppExecutors.getInstance());
        adapter.setOnItemClickListener((position, entity, sharedView) -> {
//            if (getActivity() != null) {
//                TransitionActivity transitionActivity = (TransitionActivity) getActivity();
//                Fragment fragmentB = new FragmentB();
//                if (entity != null) {
//                    Bundle args = new Bundle();
//                    args.putInt(FragmentB.KEY_AVATAR_URL, entity.getImageId());
//                    args.putInt(FragmentB.KEY_AVATAR_POSITION, position);
//                    fragmentB.setArguments(args);
//                }
//                Pair<View, String> pair = new Pair<>(sharedView, "avatar" + position);
//                transitionActivity.navigateTo(fragmentB, pair);
//            }

            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(sharedView, "avatar" + position)
                    .build();
            Bundle args = null;
            if (entity != null) {
                args = new Bundle();
                args.putInt(FragmentB.KEY_AVATAR_URL, entity.getImageId());
                args.putInt(FragmentB.KEY_AVATAR_POSITION, position);
            }
            NavHostFragment.findNavController(this)
                    .navigate(
                            R.id.fragmentB,
                            args,
                            null,
                            extras);
        });
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        postponeEnterTransition();
        list.getViewTreeObserver().addOnPreDrawListener(() -> {
            startPostponedEnterTransition();
            return true;
        });

        adapter.submitList(getItemEntities());
    }

    private List<ItemEntity> getItemEntities() {
        List<ItemEntity> itemEntities = new ArrayList<>();
        for (int imageId : IMAGE_ARRAY) {
            ItemEntity entity = new ItemEntity();
            entity.setImageId(imageId);
            itemEntities.add(entity);
        }

        return itemEntities;
    }

}
