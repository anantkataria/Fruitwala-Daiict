package com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem;

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

public class RecyclerViewAdapterForHomeFragment extends RecyclerView.Adapter<RecyclerViewAdapterForHomeFragment.ViewHolder>{

     private ArrayList<FruitItem2> fruits;
     private Context context;

     public RecyclerViewAdapterForHomeFragment(Context context, ArrayList<FruitItem2> fruits) {
          this.fruits = fruits;
          this.context = context;
     }

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(context).inflate(R.layout.list_item_fruit
          , parent, false);
          return new ViewHolder(view);
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          FruitItem2 currentItem = fruits.get(position);
          holder.fruitName.setText(currentItem.getFruitName());
          if(currentItem.getAvailability().equals("Available")){
               holder.fruitQty.setText(currentItem.getQuantities().get(0));
               holder.fruitPrice.setText(currentItem.getPrices().get(0));
          }
          else {
               holder.fruitPrice.setVisibility(View.GONE);
               holder.fruitQty.setText(" - - ");
               holder.notAvailableTxtView.setVisibility(View.VISIBLE);
          }
     }

     @Override
     public int getItemCount() {
          return fruits.size();
     }

     class ViewHolder extends RecyclerView.ViewHolder{
          TextView fruitName;
          TextView fruitQty;
          TextView fruitPrice;
          TextView notAvailableTxtView;

          RelativeLayout parentLayout;
          ViewHolder(@NonNull View itemView) {
               super(itemView);
               fruitName = itemView.findViewById(R.id.fruit_name);
               fruitQty = itemView.findViewById(R.id.fruit_qty);
               fruitPrice = itemView.findViewById(R.id.fruit_price);
               notAvailableTxtView = itemView.findViewById(R.id.not_available_text_view);

               parentLayout = itemView.findViewById(R.id.parent_layout);
          }
     }
}
