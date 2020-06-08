package com.frankhon.simplesearchview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Created by Frank Hon on 2020-06-07 10:47.
 * E-mail: frank_hon@foxmail.com
 */
public class SearchItemAdapter extends ArrayAdapter<String> {
    public SearchItemAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_suggestion, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bindView(position, getItem(position));

        return convertView;
    }

    private static class ViewHolder {

        private View borderView;
        private TextView suggestionText;

        ViewHolder(View itemView) {
            borderView = itemView.findViewById(R.id.view_border);
            suggestionText = itemView.findViewById(R.id.tv_suggestion);
        }

        void bindView(int position, String text) {
            if (position == 0) {
                borderView.setVisibility(View.VISIBLE);
            } else {
                borderView.setVisibility(View.GONE);
            }
            suggestionText.setText(text);
        }
    }
}
