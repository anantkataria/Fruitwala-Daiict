package com.anantdevelopers.swipesinalpha.FruitItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anantdevelopers.swipesinalpha.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

     private ArrayList<FruitItem> fruits = new ArrayList<>();
     private Context context;

     public RecyclerViewAdapter(Context context, ArrayList<FruitItem> fruits) {
          this.fruits = fruits;
          this.context = context;
     }

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fruit
          , parent, false);
          ViewHolder holder = new ViewHolder(view);
          return holder;
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          holder.fruitName.setText(fruits.get(position).getFruitName());
          holder.fruitPrice.setText(fruits.get(position).getFruitPrice());
          holder.fruitQty.setText(fruits.get(position).getFruitQty());
     }

     @Override
     public int getItemCount() {
          return fruits.size();
     }

     public class ViewHolder extends RecyclerView.ViewHolder{
          TextView fruitName;
          TextView fruitQty;
          TextView fruitPrice;
          RelativeLayout parentLayout;
          public ViewHolder(@NonNull View itemView) {
               super(itemView);
               fruitName = itemView.findViewById(R.id.fruit_name);
               fruitQty = itemView.findViewById(R.id.fruit_qty);
               fruitPrice = itemView.findViewById(R.id.fruit_price);
               parentLayout = itemView.findViewById(R.id.parent_layout);
          }
     }
}
