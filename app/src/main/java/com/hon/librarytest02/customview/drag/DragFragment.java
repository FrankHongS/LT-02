package com.hon.librarytest02.customview.drag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hon.librarytest02.R;

import java.util.Arrays;

/**
 * Created by Frank Hon on 2023/7/15 7:03 下午.
 * E-mail: frank_hon@foxmail.com
 */
public class DragFragment extends Fragment {

    private static final String[] strArray = {
            "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L"
    };
    private static final String TAG = "DragFragment";

    private RecyclerView targetRecyclerView;
    private DragAdapter<String> dragAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
    }

    private void initView(View view) {
        targetRecyclerView = view.findViewById(R.id.rv_target);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        targetRecyclerView.setLayoutManager(layoutManager);
        dragAdapter = new DragAdapter<>();
        targetRecyclerView.setAdapter(dragAdapter);
        dragAdapter.setData(Arrays.asList(strArray));

        Button insertBtn = view.findViewById(R.id.btn_insert);
        Button removeBtn = view.findViewById(R.id.btn_remove);
        DragLayer dragLayer = view.findViewById(R.id.dragLayer);

        insertBtn.setOnClickListener(v -> {
            insert(0, "Z");
        });
        removeBtn.setOnClickListener(v -> {
            dragAdapter.remove(2);
        });

        CrossDragHelper dragHelper = new CrossDragHelper(dragLayer);
        dragHelper.attachToDragTarget(targetRecyclerView);

    }

    private void insert(int position, String value) {
        dragAdapter.insert(position, value);
        if (position == 0) {
            targetRecyclerView.smoothScrollToPosition(0);
        }
    }

}
