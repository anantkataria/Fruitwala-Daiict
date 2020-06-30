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

import com.anantdevelopers.swipesinalpha.CartFragment.CheckoutFlow.CheckoutUser;
import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.R;

import java.util.ArrayList;

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

//          holder.parentLayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation_2));

          ArrayList<FruitItem> fruitsForAnOrder = listItems.get(position).getFruits();
          String status = listItems.get(position).getStatus();
          StringBuilder FruitsList = new StringBuilder();
          int totalPrice = 0;
          for(FruitItem f: fruitsForAnOrder){
               FruitsList.append(f.getFruitName()).append(", ").append(f.getFruitQty()).append(", ").append(f.getFruitPrice()).append("\n");
               totalPrice += Integer.parseInt(f.getFruitPrice().replaceAll("[Rs.\\s]", ""));
          }
          holder.listOfFruitsTextView.setText(FruitsList.toString());
          holder.grandTotalTextView.setText("GrandTotal : "+totalPrice+" Rs.");
          holder.currentOrderStatus.setText(status);

          if(status.equals("CANCELLATION REQUESTED")){
               holder.requestForCancelButton.setVisibility(View.GONE);
               holder.currentOrderStatus.setTextColor(Color.parseColor("#cc0000"));
          }
          if (status.equals("ORDER ON WAY!")){
               holder.requestForCancelButton.setBackgroundResource(R.drawable.button_design_3);
               holder.currentOrderStatus.setTextColor(Color.parseColor("#3498db"));
          }
     }

     @Override
     public int getItemCount() {
          return listItems.size();
     }

     static class ViewHolder extends RecyclerView.ViewHolder {
          private TextView listOfFruitsTextView;
          private TextView grandTotalTextView;
          private TextView currentOrderStatus;
          private Button requestForCancelButton;

          public ViewHolder(@NonNull View itemView, final onButtonClickListener listener) {
               super(itemView);
               listOfFruitsTextView = itemView.findViewById(R.id.listOfFruitsTextView);
               grandTotalTextView = itemView.findViewById(R.id.grandTotalTextView);
               currentOrderStatus = itemView.findViewById(R.id.CurrentOrderStatus);
               requestForCancelButton = itemView.findViewById(R.id.requestCancellationButton);

               //LinearLayout parentLayout = itemView.findViewById(R.id.parent_layout);

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
