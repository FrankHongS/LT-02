package com.hon.librarytest02.customview.drag;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hon.librarytest02.R;
import com.hon.librarytest02.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank Hon on 2023/7/17 12:11 上午.
 * E-mail: frank_hon@foxmail.com
 */
public class DragAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<T> data = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drag, parent, false)
        ) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        T value = data.get(position);
        if (value == null) {
            holder.itemView.setVisibility(View.INVISIBLE);
            holder.itemView.setTag(null);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);

            ImageView imageView = holder.itemView.findViewById(R.id.iv_drag);
            TextView textView = holder.itemView.findViewById(R.id.tv_drag);

            imageView.setImageDrawable(new ColorDrawable(Util.getColor(R.color.cardview_dark_background)));
            textView.setText((CharSequence) value);
            holder.itemView.setTag(new DragInfo<>(value, position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<T> newData) {
        data.clear();
        data.addAll(newData);
    }

    public void insert(int position, T value) {
        data.add(position, value);
        notifyItemInserted(position);
    }

    public void change(int position, T value) {
        data.set(position, value);
        notifyItemChanged(position);
    }

    public void remove(int position) {
        if (position >= 0 && position < data.size()) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }
}
