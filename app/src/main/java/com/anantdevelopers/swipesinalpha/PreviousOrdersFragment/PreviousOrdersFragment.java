//TODO : show current orders fetching from the database
//TODO : show previous orders fetching from shared preferences
//TODO : Once the current order is delivered, remove it from the database and add it into the shared preferences previous orderes

//first thing we will do is try fetching the data from database, and we will be needing active
//listener (not one time listener) and we will be working with the list of childs so we will use
//child event listener.

package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.CheckoutFlow.CheckoutUser;
import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase.PreviousOrderEntity;
import com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase.PreviousOrderViewModel;
import com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase.RecyclerViewAdapterForPreviousOrders;
import com.anantdevelopers.swipesinalpha.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;


public class PreviousOrdersFragment extends Fragment {

     private RecyclerView currentOrdersRecyclerView, previousOrdersRecyclerView;

     private FirebaseDatabase firebaseDatabase;
     private DatabaseReference databaseReference;
     private FirebaseAuth firebaseAuth;

     private ArrayList<CheckoutUser> currentOrdersList;
     private ArrayList<CheckoutUser> previousOrdersList; //for local database

     private ProgressBar currentOrderProgressBar;
     private TextView noCurrentOrdersTextView;

     private ProgressBar previousOrderProgressBar;
     private TextView noPreviousOrdersTextView;

     private RecyclerViewAdapterForCurrentOrders adapterForCurrentOrders;

     private View v;

     private PreviousOrderViewModel previousOrderViewModel;

     private ValueEventListener valueEventListener;

     public PreviousOrdersFragment() {
          // Required empty public constructor
     }

     private interface DatabaseCallbackInterface { //interface is used to handle asynchronous calls of firebase
          void fromOnChildManipulated(ArrayList<CheckoutUser> currentOrdersList);
     }

     private interface LocalDatabaseCallbackInterface {
          void fromOnChildManipulated(ArrayList<CheckoutUser> previousOrdersList);
     }

     @Override
     public void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();
          firebaseAuth = FirebaseAuth.getInstance();

          currentOrdersList = new ArrayList<>();
          previousOrdersList = new ArrayList<>();

          previousOrderViewModel = new ViewModelProvider(this).get(PreviousOrderViewModel.class);
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
          // Inflate the layout for this fragment
          v = inflater.inflate(R.layout.fragment_previous_orders, container, false);
          currentOrderAffairs(v);
          previousOrderAffairs(v);
          return v;
     }

     @Override
     public void onDestroy() {
          super.onDestroy();
          String authPhoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();
          databaseReference.child("Delivered or Cancelled").child(authPhoneNumber).removeEventListener(valueEventListener);
     }

     private void previousOrderAffairs(View v) {
          previousOrderProgressBar = v.findViewById(R.id.previousOrdersProgressBar);
          noPreviousOrdersTextView = v.findViewById(R.id.noPreviousOrdersTextView);

          previousOrdersRecyclerView = v.findViewById(R.id.previousOrdersRecyclerView);
          final RecyclerViewAdapterForPreviousOrders adapter = new RecyclerViewAdapterForPreviousOrders();

          //setting the recycler view
          previousOrdersRecyclerView.setAdapter(adapter);
          previousOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


          previousOrderViewModel.getAllPreviousOrders().observe(getViewLifecycleOwner(), new Observer<List<PreviousOrderEntity>>() {
               @Override
               public void onChanged(List<PreviousOrderEntity> previousOrderEntities) {
                    if (previousOrderEntities.isEmpty()){
                         noPreviousOrdersTextView.setVisibility(View.VISIBLE);
                    }
                    else {

                         adapter.setPreviousOrders(previousOrderEntities);
//                         Log.e("****", "Number of previous orders : "+previousOrderEntities.size());
                         noPreviousOrdersTextView.setVisibility(GONE);
                    }
               }
          });

          fetchPreviousOrders(new LocalDatabaseCallbackInterface() {
               @Override
               public void fromOnChildManipulated(ArrayList<CheckoutUser> previousOrdersList) {
//                    Log.e("fetchPreviousOrders", "previousOrdersList.size() = " + previousOrdersList.size());
                    ArrayList<PreviousOrderEntity> ordersToAddInRoom = new ArrayList<>();
//                    int i = 0;
                    for(CheckoutUser u : previousOrdersList){
                         String orderFruitList = "";
                         String status = "";
                         int grandTotal = 0;
                         status = u.getStatus();
                         for(FruitItem f : u.getFruits()){
                              orderFruitList += f.getFruitName() + ", " + f.getFruitQty() + ", " + f.getFruitPrice() + "\n";
                              grandTotal += Integer.valueOf(f.getFruitPrice().replaceAll("[Rs.\\s]", ""));
                         }

                         ordersToAddInRoom.add(new PreviousOrderEntity(orderFruitList, status, "GrandTotal : " + grandTotal + " Rs."));
//                         i += 1;
                    }

//                    Log.e("fetchPreviousOrders", "i = " + i);

//                    i = 0;
                    for(PreviousOrderEntity poe : ordersToAddInRoom){
                         previousOrderViewModel.insert(poe);
//                         i += 1;
                    }

//                    Log.e("afterInserting" ,  "i = " + i);
                    previousOrderProgressBar.setVisibility(GONE);

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String authPhoneNumber = user.getPhoneNumber();

                    databaseReference.child("Delivered or Cancelled").child(authPhoneNumber).removeValue();
               }
          });
     }

     private void fetchPreviousOrders(final LocalDatabaseCallbackInterface Interface) {
          //here two things are there
          //1) check if there is any Delivered orders are there in the database if there
          //   are any then first remove them from the database and add them in the shared
          //   preferences
          //2) fetch orders from the shared preferences (if there are any)
          FirebaseUser user = firebaseAuth.getCurrentUser();
          String authPhoneNumber = user.getPhoneNumber();

          previousOrderProgressBar.setVisibility(View.VISIBLE);

          valueEventListener = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                         previousOrdersList.clear();

                         for(DataSnapshot data: dataSnapshot.getChildren()){
                              previousOrdersList.add(data.getValue(CheckoutUser.class));
                              //Log.e("fetchPreOrders" , ""+data.getValue());
                         }

                         //todo update the room database
                         Interface.fromOnChildManipulated(previousOrdersList);

                    }
                    else{
                         previousOrderProgressBar.setVisibility(GONE);
                         //no delivered/cancelled orders remaining to read from database
                    }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
          };

          databaseReference.child("Delivered or Cancelled").child(authPhoneNumber).addValueEventListener(valueEventListener);

     }

     private void currentOrderAffairs(View v) {
          currentOrderProgressBar = v.findViewById(R.id.currentOrdersProgressBar);
          noCurrentOrdersTextView = v.findViewById(R.id.noCurrentOrdersTextView);

          currentOrdersRecyclerView = v.findViewById(R.id.currentOrdersRecyclerView);

          fetchCurrentOrders(new DatabaseCallbackInterface() {
               @Override
               public void fromOnChildManipulated(ArrayList<CheckoutUser> currentOrdersList) {
                    if(currentOrdersList.isEmpty()){
                         noCurrentOrdersTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                         currentOrdersRecyclerView.setVisibility(View.VISIBLE);
                         adapterForCurrentOrders = new RecyclerViewAdapterForCurrentOrders(getContext(), currentOrdersList);
                         currentOrdersRecyclerView.setAdapter(adapterForCurrentOrders);
                         currentOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                         noCurrentOrdersTextView.setVisibility(GONE);

                         handleCurrentOrderButtonClicks();
                    }

                    currentOrderProgressBar.setVisibility(GONE);
               }
          });
     }

     private void handleCurrentOrderButtonClicks() {
          adapterForCurrentOrders.setOnButtonClickListener(new RecyclerViewAdapterForCurrentOrders.onButtonClickListener() {
               @Override
               public void onButtonClick(final int position) {
                    //TODO in click of this button, show an alert dialog showing are you sure?.
                    //TODO then change the order status to cancel requested , and in allOrdersFragment in the
                    //TODO AdminSwipesInAlpha2, add flow that if status equals cancel requested, then show that in
                    //TODO listItem For All current orders in red mark
                    //TODO then if Admin presses the cancel order button, than there will be several cases:
                    //TODO if payment is by cash, then simply delete the order from orders in database and
                    //TODO change status to order cancelled and put it into the local database and send notification to user that your order of so and so is cancelled and check the flow to save in local database of user side(SwipesInAlpha)
                    //TODO if payment id by UPI, then send notification that your payment will be initiated soon
                    //TODO and think about showing "payment will be initiated to you soon" in that particular order
                    //TODO item.
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure you want to cancel this order?");
                    builder.setPositiveButton("Yes please", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                              CheckoutUser currentOrderDetails =  currentOrdersList.get(position);
                              currentOrderDetails.setStatus("CANCELLATION REQUESTED");
                              currentOrderProgressBar.setVisibility(View.VISIBLE);
                              String firebaseKey = currentOrderDetails.getFirebaseDatabaseKey();

                              String authPhoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();

                              databaseReference.child("Orders").child(authPhoneNumber).child(firebaseKey).setValue(currentOrderDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                        currentOrderProgressBar.setVisibility(GONE);

                                        Toast.makeText(getContext(), "Requested cancellation successfully", Toast.LENGTH_SHORT).show();
                                   }
                              }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                        currentOrderProgressBar.setVisibility(GONE);
                                        Toast.makeText(getContext(), "Something went wrong! try again", Toast.LENGTH_SHORT).show();
                                   }
                              });                         }
                    });
                    builder.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                              dialog.dismiss();
                         }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
               }
          });
     }

     private void changeStatusInDatabase(String firebaseKey, CheckoutUser currentOrderDetails) {
          String authPhoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();

          databaseReference.child("Users").child(authPhoneNumber).child(firebaseKey).setValue(currentOrderDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {
                    currentOrderProgressBar.setVisibility(GONE);
                    Toast.makeText(getContext(), "Requested cancellation successfully", Toast.LENGTH_SHORT).show();
               }
          }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                    currentOrderProgressBar.setVisibility(GONE);
                    Toast.makeText(getContext(), "Something went wrong! try again", Toast.LENGTH_SHORT).show();
               }
          });
     }

     private void fetchCurrentOrders(final DatabaseCallbackInterface Interface) {

          FirebaseUser user = firebaseAuth.getCurrentUser();
          String authPhoneNumber = user.getPhoneNumber();

          currentOrderProgressBar.setVisibility(View.VISIBLE);

          databaseReference.child("Orders").child(authPhoneNumber).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    currentOrderProgressBar.setVisibility(View.VISIBLE);
                    currentOrdersList.clear();
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                         currentOrdersList.add(data.getValue(CheckoutUser.class));
                    }

                    Interface.fromOnChildManipulated(currentOrdersList);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                    //TODO implement this

               }
          });


     }

}
