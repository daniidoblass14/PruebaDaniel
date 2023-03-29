package com.example.prueba;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{
    public TextView tvNombre, tvFecha;
    public ImageView imageView;
    public CardView cardView;
    private RecycleInterface.RecycleViewOnClick recycleViewOnClick;

    public MyViewHolder(View itemView) {
        super(itemView);
        tvNombre = itemView.findViewById(R.id.tvNombre);
        tvFecha = itemView.findViewById(R.id.tvFecha);
        imageView = itemView.findViewById(R.id.imageView);
        cardView = itemView.findViewById(R.id.cardView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recycleViewOnClick.onItemClick(getAdapterPosition());
            }
        });
    }
}
