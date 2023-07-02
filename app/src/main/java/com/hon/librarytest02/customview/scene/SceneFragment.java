package com.hon.librarytest02.customview.scene;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hon.librarytest02.R;

/**
 * Created by Frank Hon on 2023/6/29 11:02 下午.
 * E-mail: frank_hon@foxmail.com
 */
public class SceneFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scene, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.rv_scenes);
        RecyclerView recyclerView2 = view.findViewById(R.id.rv_scenes_2);
        TextView debugText = view.findViewById(R.id.tv_debug);
        Button debugIncBtn = view.findViewById(R.id.btn_debug_inc);
        Button debugDecBtn = view.findViewById(R.id.btn_debug_dec);
        Config config = new Config();
        config.secondaryScale = 0.8f;
        config.scaleRatio = 0.5f;
        config.maxStackCount = 3;
        config.initialStackCount = 0;
        config.space = 30;
        config.parallex = 1.5f;
        config.align = Align.LEFT;
        PileLayoutManager layoutManager = new PileLayoutManager(30, 0.8f);
        layoutManager.setOnSelectListener(position -> debugText.setText("curPos: " + position));
        recyclerView.setLayoutManager(layoutManager);
        PileAdapter adapter = new PileAdapter();
        adapter.setOnItemClickListener(position -> {
            layoutManager.scrollToPosition(position);
        });
        recyclerView.setAdapter(adapter);

        recyclerView2.setLayoutManager(new StackLayoutManager(config));
        recyclerView2.setAdapter(new PileAdapter());

        debugIncBtn.setOnClickListener(v -> {
            int selectedPosition = layoutManager.getSelectedPosition();
            layoutManager.scrollToPosition(selectedPosition + 1);
        });
        debugDecBtn.setOnClickListener(v -> {
            int selectedPosition = layoutManager.getSelectedPosition();
            layoutManager.scrollToPosition(selectedPosition - 1);
        });
    }

    private static class PileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private OnItemClickListener onItemClickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.item_pile, parent, false
                    )
            ) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView textView = holder.itemView.findViewById(R.id.tv_text);
            textView.setText(String.valueOf(position));
            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            });
            if (position % 3 == 0) {
                holder.itemView.setBackgroundColor(Color.parseColor("#009688"));
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#607D8B"));
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
    }

    private interface OnItemClickListener {

        void onItemClick(int position);

    }
}
