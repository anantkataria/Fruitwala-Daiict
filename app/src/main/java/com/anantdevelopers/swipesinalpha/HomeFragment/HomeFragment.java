package com.anantdevelopers.swipesinalpha.HomeFragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.CustomDialogFragment.CustomDialogFragment;
import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem2;
import com.anantdevelopers.swipesinalpha.R;
import com.anantdevelopers.swipesinalpha.FruitItem.RecyclerItemClickListener;
import com.anantdevelopers.swipesinalpha.FruitItem.RecyclerViewAdapterForHomeFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

     private FirebaseDatabase firebaseDatabase;
     private DatabaseReference databaseReference;

//     private ArrayList<FruitItem> fruits;
     private ArrayList<FruitItem2> fruits;
     private ArrayList<String> quantities, prices;

     private RecyclerView recyclerView;
     private RecyclerViewAdapterForHomeFragment adapter;
     private ProgressBar progressBar;

     private OnFragmentInteractionListener mListener;

     public HomeFragment() {
          // Required empty public constructor
     }

     private interface getFruitsInterface {
          void afterFetching();
     }

     @Override
     public void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          fruits = new ArrayList<>();

          firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();

     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
          View v = inflater.inflate(R.layout.fragment_home, container, false);

          progressBar = v.findViewById(R.id.progressBar);
          recyclerView = v.findViewById(R.id.recycler_view_home);

          getFruitsFromDatabase(new getFruitsInterface() {
               @Override
               public void afterFetching() {
                    //after fetching, we will show the recycler view and hide the progress bar
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    adapter = new RecyclerViewAdapterForHomeFragment(getContext(), fruits);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    handleClickEvents();
               }
          });

          return v;
     }

     private void getFruitsFromDatabase(final getFruitsInterface fruitsInterface) {

          ValueEventListener valueEventListener = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // now for each fruit, make a new FruitItem2 object and add it in the
                    // fruits arraylist.

                    for(DataSnapshot d : dataSnapshot.getChildren()){
                         FruitItem2 newFruit = new FruitItem2();
                         ArrayList<String> quantities = new ArrayList<>();
                         ArrayList<String> prices = new ArrayList<>();

                         newFruit.setFruitName(d.getKey());
                         newFruit.setAvailability(d.child("Availability").getValue(String.class));
                         for(DataSnapshot d1: d.child("qty").getChildren()){
                              quantities.add(d1.getValue(String.class));
                         }
                         for(DataSnapshot d2: d.child("prices").getChildren()){
                              prices.add(d2.getValue(String.class));
                         }
                         newFruit.setQuantities(quantities);
                         newFruit.setPrices(prices);

                         fruits.add(newFruit);
                    }

                    fruitsInterface.afterFetching();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
          };

          databaseReference.child("Fruits").addListenerForSingleValueEvent(valueEventListener);
     }

     private void handleClickEvents() {
          recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
               @Override
               public void onItemClick(View view, int position) {
                    //Log.e("HomeFragment", fruits.get(position).getFruitName());
                    FruitItem2 selectedFruit = fruits.get(position);
                    if(selectedFruit.getAvailability().equals("Available")) {
                         mListener.sendToActivityfromHomeFragment(selectedFruit);

                         FragmentTransaction ft = getFragmentManager().beginTransaction();
                         CustomDialogFragment customDialogFragment = mListener.sendFruitInfoToDialog();
                         customDialogFragment.show(ft, "position of fruit is" + position);
                    }
                    else {
                         //Snackbar.make(getView(), "Fruit Not available!", Snackbar.LENGTH_SHORT);
                         Toast.makeText(getContext(), "Sorry, Fruit not Available", Toast.LENGTH_SHORT).show();
                    }
               }
          }));
     }

     public interface OnFragmentInteractionListener {
          void sendToActivityfromHomeFragment(FruitItem2 item);
          CustomDialogFragment sendFruitInfoToDialog();
     }

     @Override
     public void onAttach(Context context) {
          super.onAttach(context);
          if (context instanceof OnFragmentInteractionListener) {
               mListener = (OnFragmentInteractionListener) context;
          } else {
               throw new RuntimeException(context.toString()
                       + " must implement HomeFragment.OnFragmentInteractionListener");
          }
     }

     @Override
     public void onDetach() {
          super.onDetach();
          mListener = null;
     }

}
