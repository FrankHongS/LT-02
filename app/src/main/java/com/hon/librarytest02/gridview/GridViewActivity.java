package com.hon.librarytest02.gridview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.hon.librarytest02.R;

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
    }

    private void initView() {
        staggeredGridView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        staggeredGridView.setAdapter(new GridAdapter());
    }

    class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType){
                case 0:
                    return new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered_grid_01,parent,false));
                case 1:
                    return new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staggered_grid_02,parent,false));
                default:
                    break;
            }

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            GridViewHolder gridViewHolder= (GridViewHolder) holder;
            gridViewHolder.bindView(position);
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        @Override
        public int getItemViewType(int position) {
            return position%2;
        }
    }

    class GridViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_staggered_item)
        TextView text;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindView(int position){
            text.setText(String.valueOf(position));
        }
    }
}
