package com.anantdevelopers.swipesinalpha.CartFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.CartFragment.CheckoutFlow.CheckoutFlow;
import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.RecyclerViewAdapterForCartfragment;
import com.anantdevelopers.swipesinalpha.HomeFragment.HomeFragment;
import com.anantdevelopers.swipesinalpha.Main.InternetConnectionViewModel;
import com.anantdevelopers.swipesinalpha.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class CartFragment extends Fragment {

     private ArrayList<FruitItem> fruits;

     private RecyclerViewAdapterForCartfragment adapter;

     private TextView grandTotal, swipeToDeleteTextView;
     private RelativeLayout parentLayout;

     private InternetConnectionViewModel internetConnectionViewModel;
     private Snackbar noInternetSnackbar;

     private OnFragmentInteractionListener mListener;

     private boolean isItConnected;

     public CartFragment() {
          // Required empty public constructor
     }

     @Override
     public void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          adapter = new RecyclerViewAdapterForCartfragment(getContext(), fruits);
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
          View v = inflater.inflate(R.layout.fragment_cart, container, false);
          grandTotal = v.findViewById(R.id.grand_total_text);
          swipeToDeleteTextView = v.findViewById(R.id.hardcoded_text_view);
          parentLayout = v.findViewById(R.id.parent_layout);

          noInternetSnackbar = Snackbar.make(parentLayout, "NO INTERNET CONNECTION", Snackbar.LENGTH_INDEFINITE);
          noInternetSnackbar.setAction("RETRY", new MyRetryListener());
          noInternetSnackbar.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));

          internetConnectionViewModel = new ViewModelProvider(this).get(InternetConnectionViewModel.class);
          observeConnectivity();
          startCheckingNetworkConnectivity();

          if(!fruits.isEmpty()){
               swipeToDeleteTextView.setVisibility(View.VISIBLE);
          }

          countGrandTotal();

          Button proceedToCheckoutButton = v.findViewById(R.id.proceed_to_checkout_button);

          proceedToCheckoutButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    if(!isItConnected){
                         Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
                    }
                    else {
                         if (fruits.isEmpty())
                              Snackbar.make(parentLayout, "Add something to cart first!", Snackbar.LENGTH_SHORT).show();

                         else {
                              Intent checkoutFlowIntent = new Intent(getContext(), CheckoutFlow.class);
                              checkoutFlowIntent.putExtra("Fruits", fruits);
                              checkoutFlowIntent.putExtra("grandTotal", grandTotal.getText());
                              startActivity(checkoutFlowIntent);
                         }
                    }
               }
          });

          RecyclerView recyclerView = v.findViewById(R.id.recycler_view_cart);
          recyclerView.setAdapter(adapter);
          new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
          recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

          return v;
     }

     private void startCheckingNetworkConnectivity() {
          internetConnectionViewModel.startConnectivityCheck();
     }

     private void observeConnectivity() {
          internetConnectionViewModel.getIsConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
               @Override
               public void onChanged(Boolean isConnected) {
                    isItConnected = isConnected;
                    if(!isConnected){
                         //not connected, show SnackBar of retry
                         //and retry is recreate the activity here
                         noInternetSnackbar.show();
                    }
                    else {
                         noInternetSnackbar.dismiss();
                    }
               }
          });
     }

     private ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
          @Override
          public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
               return false;
          }

          @Override
          public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
               int position = viewHolder.getAdapterPosition();
               FruitItem fruitItem = fruits.get(position);
               fruits.remove(position);
               countGrandTotal();
               adapter.notifyItemRemoved(position);
               showSnackbar(position, fruitItem);

               if(fruits.isEmpty()){
                    swipeToDeleteTextView.setVisibility(View.GONE);
               }
          }
     };

     private class MyUndoListener implements View.OnClickListener {

          private int position;
          private FruitItem fruit;

          MyUndoListener(int position, FruitItem fruitItem){
               this.position = position;
               this.fruit = fruitItem;
          }

          @Override
          public void onClick(View v) {
               fruits.add(position, fruit);
               adapter.notifyItemInserted(position);
               swipeToDeleteTextView.setVisibility(View.VISIBLE);
               countGrandTotal();
          }
     }

     private void showSnackbar(int position, FruitItem fruit) {
          Snackbar mySnackbar = Snackbar.make(parentLayout, "1 Item removed", Snackbar.LENGTH_SHORT);
          mySnackbar.setAction("undo", new MyUndoListener(position, fruit));
          mySnackbar.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));
          mySnackbar.show();
     }

     @Override
     public void onAttach(Context context) {
          super.onAttach(context);

          if (context instanceof OnFragmentInteractionListener) {
               mListener = (OnFragmentInteractionListener) context;
          } else {
               throw new RuntimeException(context.toString()
                       + " must implement CartFragment.OnFragmentInteractionListener");
          }

          fruits = mListener.getFruitsFromMainToCartFragment();
     }

     @Override
     public void onDetach() {
          super.onDetach();
          mListener = null;
     }

     private void countGrandTotal() {
          int Total = 0;
          for(FruitItem item: fruits){
               Total += Integer.parseInt(item.getFruitPrice().replaceAll("[Rs.\\s]", ""));
          }
          grandTotal.setText("Rs. " + Total);
     }

     public interface OnFragmentInteractionListener {
          ArrayList<FruitItem> getFruitsFromMainToCartFragment();
     }

     private class MyRetryListener implements View.OnClickListener {
          @Override
          public void onClick(View v) {
               //recreate the fragment
               CartFragment fragment = (CartFragment) getParentFragmentManager().findFragmentById(R.id.nav_host_fragment);
               getParentFragmentManager().beginTransaction()
                       .detach(fragment)
                       .attach(fragment)
                       .commit();
          }
     }

}
