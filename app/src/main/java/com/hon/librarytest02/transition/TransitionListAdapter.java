package com.hon.librarytest02.transition;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hon.librarytest02.AppExecutors;
import com.hon.librarytest02.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank Hon on 2019-08-18 23:38.
 * E-mail: frank_hon@foxmail.com
 */
public class TransitionListAdapter extends ListAdapter<ItemEntity, RecyclerView.ViewHolder> {

    private OnItemClickListener onItemClickListener;

    public TransitionListAdapter(AppExecutors appExecutors) {
        super(new AsyncDifferConfig.Builder<ItemEntity>(new DiffUtil.ItemCallback<ItemEntity>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull ItemEntity oldItem, @NonNull ItemEntity newItem) {
                        return oldItem.getImageId() == newItem.getImageId();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull ItemEntity oldItem, @NonNull ItemEntity newItem) {
                        return oldItem.getImageId() == newItem.getImageId();
                    }
                })
                        .setBackgroundThreadExecutor(appExecutors.getDiskIO())
                        .build()
        );
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_a_item, parent, false);
        return new TransitionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TransitionViewHolder transitionViewHolder = (TransitionViewHolder) holder;
        transitionViewHolder.bindView(getItem(position), position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class TransitionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_avatar)
        ImageView avatar;

        TransitionViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(ItemEntity entity, int position) {

            avatar.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            });

            Glide.with(itemView.getContext())
                    .load(entity.getImageId())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(avatar);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

    }
}
