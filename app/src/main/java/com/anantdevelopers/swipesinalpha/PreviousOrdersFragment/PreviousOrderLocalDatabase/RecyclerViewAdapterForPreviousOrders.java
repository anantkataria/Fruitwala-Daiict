package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anantdevelopers.swipesinalpha.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterForPreviousOrders extends RecyclerView.Adapter<RecyclerViewAdapterForPreviousOrders.ViewHolder> {

     private List<PreviousOrderEntity> previousOrders = new ArrayList<>();

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View itemView = LayoutInflater.from(parent.getContext())
                  .inflate(R.layout.list_item_previous_orders, parent, false);
          return new ViewHolder(itemView);
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          PreviousOrderEntity current = previousOrders.get(position);
          holder.listOfFruitsTextView.setText(current.getOrderFruitList());
          holder.statusTextView.setText(current.getStatus());
          holder.grandTotalTextView.setText(current.getGrandTotal());
     }

     @Override
     public int getItemCount() {
          return previousOrders.size();
     }

     public void setPreviousOrders(List<PreviousOrderEntity> previousOrders){
          this.previousOrders = previousOrders;
          notifyDataSetChanged();
          //todo implement better than notifydatasetchanged from coding in flow
     }

     public class ViewHolder extends RecyclerView.ViewHolder {
          private TextView listOfFruitsTextView;
          private TextView statusTextView;
          private TextView grandTotalTextView;

          public ViewHolder(@NonNull View itemView) {
               super(itemView);
               listOfFruitsTextView = itemView.findViewById(R.id.listOfFruitsTextView);
               statusTextView = itemView.findViewById(R.id.PreviousOrderStatus);
               grandTotalTextView = itemView.findViewById(R.id.grandTotalTextView);
          }
     }
}
