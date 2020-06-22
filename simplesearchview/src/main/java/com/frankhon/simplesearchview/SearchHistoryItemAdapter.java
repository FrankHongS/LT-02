package com.frankhon.simplesearchview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.frankhon.simplesearchview.db.entity.SearchItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Frank Hon on 2020-06-23 01:05.
 * E-mail: frank_hon@foxmail.com
 */
public class SearchHistoryItemAdapter extends ListAdapter<SearchItem, SearchHistoryItemAdapter.SearchHistoryViewHolder> {

    protected SearchHistoryItemAdapter(Executor executor) {
        super(new AsyncDifferConfig.Builder<SearchItem>(
                new DiffUtil.ItemCallback<SearchItem>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull SearchItem oldItem, @NonNull SearchItem newItem) {
                        return oldItem.getText().equals(newItem.getText());
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull SearchItem oldItem, @NonNull SearchItem newItem) {
                        return oldItem.getText().equals(newItem.getText());
                    }
                }
        )
                .setBackgroundThreadExecutor(executor)
                .build());
    }

    @NonNull
    @Override
    public SearchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
        return new SearchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryViewHolder holder, final int position) {
        SearchItem history = getItem(position);
        holder.bindView(history, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SearchItem> temp = new ArrayList<>(getCurrentList());
                temp.remove(position);
                submitList(temp);
            }
        });
    }

    static class SearchHistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
        private ImageButton deleteButton;

        SearchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tv_search_history);
            deleteButton = itemView.findViewById(R.id.ib_delete);
        }

        void bindView(SearchItem history, View.OnClickListener onClickListener) {
            text.setText(history.getText());
            deleteButton.setOnClickListener(onClickListener);
        }
    }
}
