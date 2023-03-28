package com.example.prueba;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{
    public TextView tvNombre, tvFecha;
    public ImageView imageView;

    public MyViewHolder(View itemView) {
        super(itemView);
        tvNombre = itemView.findViewById(R.id.tvNombre);
        tvFecha = itemView.findViewById(R.id.tvFecha);
        imageView = itemView.findViewById(R.id.imageView);
    }
}
