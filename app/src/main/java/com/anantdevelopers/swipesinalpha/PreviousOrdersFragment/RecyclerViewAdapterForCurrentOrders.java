package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
     private onButtonClickListener mListener;

     public interface onButtonClickListener{
          void onButtonClick(int position);
     }

     void setOnButtonClickListener(onButtonClickListener listener) {
          this.mListener = listener;
     }

     RecyclerViewAdapterForCurrentOrders(Context context, ArrayList<CheckoutUser> listItems){
          this.listItems = listItems;
          this.context = context;
     }

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View v = LayoutInflater.from(context).inflate(R.layout.list_item_current_orders, parent, false);
          return new ViewHolder(v, mListener);
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

          if(status.equals("CANCELLATION REQUESTED")){
               holder.requestForCancelButton.setVisibility(View.GONE);
               holder.currentOrderStatus.setTextColor(Color.parseColor("#cc0000"));
          }
     }

     @Override
     public int getItemCount() {
          return listItems.size();
     }

     public class ViewHolder extends RecyclerView.ViewHolder {
          TextView listOfFruitsTextView;
          TextView grandTotalTextView;
          TextView currentOrderStatus;
          Button requestForCancelButton;

          public ViewHolder(@NonNull View itemView, final onButtonClickListener listener) {
               super(itemView);
               listOfFruitsTextView = itemView.findViewById(R.id.listOfFruitsTextView);
               grandTotalTextView = itemView.findViewById(R.id.grandTotalTextView);
               currentOrderStatus = itemView.findViewById(R.id.CurrentOrderStatus);
               requestForCancelButton = itemView.findViewById(R.id.requestCancellationButton);

               requestForCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         if(listener != null){
                              int position = getAdapterPosition();
                              if(position != RecyclerView.NO_POSITION){
                                   listener.onButtonClick(position);
                              }
                         }
                    }
               });
          }
     }
}
