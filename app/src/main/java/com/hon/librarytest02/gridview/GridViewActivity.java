package com.hon.librarytest02.gridview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.hon.librarytest02.R;
import com.hon.librarytest02.util.Util;
import com.hon.mylogger.MyLogger;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank_Hon on 10/25/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class GridViewActivity extends AppCompatActivity {

    @BindView(R.id.rv_grid)
    RecyclerView staggeredGridView;
    @BindView(R.id.gridTextView)
    GridTextView gridTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);

        ButterKnife.bind(this);
        initView();

        MyLogger.tag("shuai");
        MyLogger.d("onCreate: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLogger.d("onResume: ");
    }

    private void initView() {
        gridTextView.setData(Arrays.asList("Monica", "Chandler Bing", "Reachel Green", "Ross", "Joey Trribianni","hello worldworldworldworldyou are the best !"));
        staggeredGridView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if (position == 1) {
//                    return 1;
//                }
//                return 2;
//            }
//        });
//        staggeredGridView.setLayoutManager(layoutManager);
        staggeredGridView.setAdapter(new GridAdapter());
    }

    static class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered_grid_01, parent, false));
                case 1:
                    return new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered_grid_02, parent, false));
                default:
                    break;
            }

            throw new RuntimeException("Not support type");
//            return new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered_grid_03, parent, false));
//            return new GridViewHolder(
//                    new SinaRelativeLayout(parent.getContext())
//            );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            GridViewHolder gridViewHolder = (GridViewHolder) holder;
            gridViewHolder.bindView(position);
//            holder.itemView.post(
//                    () -> {
//                        if (position == 1) {
//                            Log.d("frankhon", "onBindViewHolder: width=" + Util.px2dp(
//                                    gridViewHolder.itemView.getWidth()
//                            ) + ", "
//                                    + Util.px2dp(
//                                    ((ViewGroup) gridViewHolder.itemView).getChildAt(0).getWidth()
//                            ) + ","
//                                    + Util.px2dp(
//                                    gridViewHolder.itemView.getWidth()
//                            )
//                                    + ", height=" + Util.px2dp(
//                                    gridViewHolder.itemView.getHeight()
//                            ));
//                        }
//                    }
//            );
        }

        @Override
        public int getItemCount() {
            MyLogger.d("getItemCount");
            return 10;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_staggered_item)
        TextView text;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(int position) {
            switch (position % 3) {
                case 0:
                    text.setText("Monica Monica Monica " + position);
                    break;
                case 1:
                    text.setText("Chandler " + position);
                    break;
                case 2:
                    text.setText("Joey " + position);
                    break;
                default:
                    break;
            }
        }
    }
}
