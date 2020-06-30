package com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anantdevelopers.swipesinalpha.R;

import java.util.ArrayList;
import java.util.Map;

public class RecyclerViewAdapterForHomeFragment extends RecyclerView.Adapter<RecyclerViewAdapterForHomeFragment.ViewHolder>{

     private ArrayList<FruitItem2> fruits;
     private Context context;
     private Map<String, Integer> photoMapOfFruits;

     public RecyclerViewAdapterForHomeFragment(Context context, ArrayList<FruitItem2> fruits, Map<String, Integer> photoMapOfFruits) {
          this.fruits = fruits;
          this.context = context;
          this.photoMapOfFruits = photoMapOfFruits;
     }

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(context).inflate(R.layout.list_item_home_2
          , parent, false);
          return new ViewHolder(view);
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          FruitItem2 currentItem = fruits.get(position);

          holder.fruitImageView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
          holder.parentLayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

          Integer photo = photoMapOfFruits.get(currentItem.getFruitName());
          holder.fruitName.setText(currentItem.getFruitName());
          holder.fruitImageView.setImageResource(photo);
          if(currentItem.getAvailability().equals("Available")){
               holder.fruitQty.setText(currentItem.getQuantities().get(0));
               holder.fruitPrice.setVisibility(View.VISIBLE);
               holder.fruitPrice.setText(currentItem.getPrices().get(0));
          }
          else {
               holder.fruitPrice.setVisibility(View.INVISIBLE);
               holder.fruitQty.setText(" - - N/A - - ");
          }
     }

     @Override
     public int getItemCount() {
          return fruits.size();
     }

     static class ViewHolder extends RecyclerView.ViewHolder{
          TextView fruitName;
          TextView fruitQty;
          TextView fruitPrice;
          ImageView fruitImageView;

          RelativeLayout parentLayout;
          ViewHolder(@NonNull View itemView) {
               super(itemView);
               fruitName = itemView.findViewById(R.id.fruit_name_text_view);
               fruitQty = itemView.findViewById(R.id.fruit_qty_text_view);
               fruitPrice = itemView.findViewById(R.id.fruit_price_text_view);
               fruitImageView = itemView.findViewById(R.id.fruit_image_view);

               parentLayout = itemView.findViewById(R.id.relativeLayout);
          }
     }
}
