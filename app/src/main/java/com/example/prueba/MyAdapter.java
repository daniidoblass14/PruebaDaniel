package com.example.prueba;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private List<MyData> mDataList;

    private RecycleInterface.RecycleViewOnClick recycleViewOnClick;

    private onItemClick onClick;
    private onItemLongClick onLongClick;

    public interface onItemClick {
        void onItemClick(MyData data);
    }

    public interface onItemLongClick{
        void onItemLongClick(MyData data);
    }

    public MyAdapter(List<MyData> mDataList, RecycleInterface.RecycleViewOnClick recycleViewOnClick) {
        this.mDataList = mDataList;
        this.recycleViewOnClick = recycleViewOnClick;
    }

    public MyAdapter(List<MyData> dataList){
        mDataList = dataList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyData data = mDataList.get(position);
        holder.tvNombre .setText(data.getNombre());
        holder.tvFecha .setText(data.getFecha());
        holder.imageView.setImageBitmap(data.getImageBitmap());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onItemClick(data);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onLongClick.onItemLongClick(data);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setOnClick(onItemClick onClick) {
        this.onClick = onClick;
    }

    public void setOnLongClick(onItemLongClick onLongClick) {
        this.onLongClick = onLongClick;
    }
}
