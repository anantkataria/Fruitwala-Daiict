//TODO : show current orders fetching from the database
//TODO : show previous orders fetching from shared preferences
//TODO : Once the current order is delivered, remove it from the database and add it into the shared preferences previous orderes

//first thing we will do is try fetching the data from database, and we will be needing active
//listener (not one time listener) and we will be working with the list of childs so we will use
//child event listener.

package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anantdevelopers.swipesinalpha.CheckoutFlow.CheckoutUser;
import com.anantdevelopers.swipesinalpha.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviousOrdersFragment extends Fragment {

     private RecyclerView currentOrdersRecyclerView, previousOrdersRecyclerView;

     private FirebaseDatabase firebaseDatabase;
     private DatabaseReference databaseReference;
     private FirebaseAuth firebaseAuth;

     private ArrayList<CheckoutUser> currentOrdersList;

     private ProgressBar currentOrderProgressBar;
     private TextView noCurrentOrdersTextView;

     private ProgressBar previousOrderProgressBar;
     private TextView noPreviousOrdersTextView;

     private RecyclerViewAdapterForCurrentOrders adapterForCurrentOrders;

     public PreviousOrdersFragment() {
          // Required empty public constructor
     }

     private interface DatabaseCallbackInterface { //interface is used to handle asynchronous calls of firebase
          void fromOnChildManipulated(ArrayList<CheckoutUser> currentOrdersList);
     }

     @Override
     public void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();
          firebaseAuth = FirebaseAuth.getInstance();

          currentOrdersList = new ArrayList<>();
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
          // Inflate the layout for this fragment
          View v = inflater.inflate(R.layout.fragment_previous_orders, container, false);

          currentOrderProgressBar = v.findViewById(R.id.currentOrdersProgressBar);
          noCurrentOrdersTextView = v.findViewById(R.id.noCurrentOrdersTextView);

          //previousOrderProgressBar = v.findViewById(R.id.previousOrdersProgressBar);
          //noPreviousOrdersTextView = v.findViewById(R.id.noPreviousOrdersTextView);

          currentOrdersRecyclerView = v.findViewById(R.id.currentOrdersRecyclerView);
          //previousOrdersRecyclerView = v.findViewById(R.id.previousOrdersRecyclerView);


          //previousOrderProgressBar.setVisibility(View.VISIBLE);
          //fetchPreviousOrders();
          fetchCurrentOrders(new DatabaseCallbackInterface() {
               @Override
               public void fromOnChildManipulated(ArrayList<CheckoutUser> currentOrdersList) {
                    noCurrentOrdersTextView.setVisibility(View.GONE);
                    currentOrdersRecyclerView.setVisibility(View.VISIBLE);
                    adapterForCurrentOrders = new RecyclerViewAdapterForCurrentOrders(getContext(), currentOrdersList);
                    currentOrdersRecyclerView.setAdapter(adapterForCurrentOrders);
                    currentOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    currentOrderProgressBar.setVisibility(View.GONE);
               }
          });

          if(currentOrdersList.isEmpty()){
               noCurrentOrdersTextView.setVisibility(View.VISIBLE);
          }

          return v;
     }

     private void fetchPreviousOrders() {
     }

     private void fetchCurrentOrders(final DatabaseCallbackInterface Interface) {

          FirebaseUser user = firebaseAuth.getCurrentUser();
          String authPhoneNumber = user.getPhoneNumber();

          //currentOrderProgressBar.setVisibility(View.VISIBLE);

          databaseReference.child("Orders").child(authPhoneNumber).addChildEventListener(new ChildEventListener() {
               @Override
               public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    currentOrderProgressBar.setVisibility(View.VISIBLE);
                    //add child in the maintained current orders arraylist.
                    CheckoutUser child = dataSnapshot.getValue(CheckoutUser.class);
                    currentOrdersList.add(child);
                    Log.e("fetchCurrentOrders", "currentOrderList.size() = " + currentOrdersList.size());

                    Interface.fromOnChildManipulated(currentOrdersList);
               }

               @Override
               public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    currentOrderProgressBar.setVisibility(View.VISIBLE);
                    //status of the child may get changed
                    //in that case update the status of that child in the maintained current orders arraylist
                    //and if the child's status is changed to order cancelled or delivered then remove it from
                    //the database and add it to the shared preferences
                    //if the order is cancelled then send notification to the user about the cancel is successful


               }

               @Override
               public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    currentOrderProgressBar.setVisibility(View.VISIBLE);

                    //find the removed child in the curentOrdersList and remove that child and again render
                    //the updated list
                    //child will be removed when order is delivered order order is cancelled
               }

               @Override
               public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    currentOrderProgressBar.setVisibility(View.VISIBLE);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }

          });

//          databaseReference.child("Orders").child(authPhoneNumber).addValueEventListener(new ValueEventListener() {
//               @Override
//               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//               }
//
//               @Override
//               public void onCancelled(@NonNull DatabaseError databaseError) {
//
//               }
//          });

          //Log.e("fetchCurrentOrders", ""+currentOrdersList.isEmpty());

     }

}
