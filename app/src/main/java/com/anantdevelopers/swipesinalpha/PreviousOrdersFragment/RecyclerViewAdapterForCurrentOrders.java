package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anantdevelopers.swipesinalpha.CheckoutFlow.CheckoutUser;
import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.R;

import java.util.ArrayList;
import java.util.Iterator;

public class RecyclerViewAdapterForCurrentOrders extends RecyclerView.Adapter<RecyclerViewAdapterForCurrentOrders.ViewHolder> {

     private ArrayList<CheckoutUser> listItems;
     private Context context;

     RecyclerViewAdapterForCurrentOrders(Context context, ArrayList<CheckoutUser> listItems){
          this.listItems = listItems;
          this.context = context;
     }

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_current_orders, parent, false);
          ViewHolder holder = new ViewHolder(v);
          return holder;
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          ArrayList<FruitItem> fruitsForAnOrder = listItems.get(position).getFruits();
          String status = listItems.get(position).getStatus();
          String FruitsList = "";
          int totalPrice = 0;
          for(FruitItem f: fruitsForAnOrder){
               FruitsList += f.getFruitName() + ", " + f.getFruitQty() + ", " + f.getFruitPrice() + "\n";
               totalPrice += Integer.valueOf(f.getFruitPrice().replaceAll("[Rs.\\s]", ""));
          }
          holder.listOfFruitsTextView.setText(FruitsList);
          holder.grandTotalTextView.setText("GrandTotal : "+totalPrice+" Rs.");
          holder.currentOrderStatus.setText(status);
     }

     @Override
     public int getItemCount() {
          return listItems.size();
     }

     public class ViewHolder extends RecyclerView.ViewHolder {
          TextView listOfFruitsTextView;
          TextView grandTotalTextView;
          TextView currentOrderStatus;

          public ViewHolder(@NonNull View itemView) {
               super(itemView);
               listOfFruitsTextView = itemView.findViewById(R.id.listOfFruitsTextView);
               grandTotalTextView = itemView.findViewById(R.id.grandTotalTextView);
               currentOrderStatus = itemView.findViewById(R.id.CurrentOrderStatus);
          }
     }
}
