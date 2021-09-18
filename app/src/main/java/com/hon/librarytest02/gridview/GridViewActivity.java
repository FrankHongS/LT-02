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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank_Hon on 10/25/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class GridViewActivity extends AppCompatActivity {

    @BindView(R.id.rv_grid)
    RecyclerView staggeredGridView;

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
//        staggeredGridView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if (position == 1) {
//                    return 1;
//                }
//                return 2;
//            }
//        });
        staggeredGridView.setLayoutManager(layoutManager);
        staggeredGridView.setAdapter(new GridAdapter());
    }

    static class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            switch (viewType){
//                case 0:
//                    return new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered_grid_01, parent, false));
//                case 1:
//                    return new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered_grid_02, parent, false));
//                default:
//                    break;
//            }
//
//            return null;
            return new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered_grid_03, parent, false));
//            return new GridViewHolder(
//                    new SinaRelativeLayout(parent.getContext())
//            );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            GridViewHolder gridViewHolder = (GridViewHolder) holder;
//            gridViewHolder.bindView(position);
            holder.itemView.post(
                    () -> {
                        if (position == 1) {
                            Log.d("frankhon", "onBindViewHolder: width=" + Util.px2dp(
                                    gridViewHolder.itemView.getWidth()
                            )+", "
                                    + Util.px2dp(
                                    ((ViewGroup)gridViewHolder.itemView).getChildAt(0).getWidth()
                            )+","
                                    + Util.px2dp(
                                    gridViewHolder.itemView.getWidth()
                            )
                                    + ", height=" + Util.px2dp(
                                    gridViewHolder.itemView.getHeight()
                            ));
                        }
                    }
            );
        }

        @Override
        public int getItemCount() {
            MyLogger.d("getItemCount");
            return 10;
        }

        @Override
        public int getItemViewType(int position) {
            return position % 2;
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {

//        @BindView(R.id.tv_staggered_item)
//        TextView text;
//
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);
        }
//
//        public void bindView(int position) {
//            text.setText(String.valueOf(position));
//        }
    }
}
