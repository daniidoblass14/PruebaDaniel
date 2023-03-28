package com.example.prueba;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private List<MyData> mDataList;

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
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
