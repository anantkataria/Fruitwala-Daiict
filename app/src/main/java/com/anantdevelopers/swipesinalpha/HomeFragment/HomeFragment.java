package com.anantdevelopers.swipesinalpha.HomeFragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.anantdevelopers.swipesinalpha.HomeFragment.CustomDialogFragment.CustomDialogFragment;
import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.FruitItem2;
import com.anantdevelopers.swipesinalpha.Main.InternetConnectionViewModel;
import com.anantdevelopers.swipesinalpha.R;
import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.RecyclerItemClickListener;
import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.RecyclerViewAdapterForHomeFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

     private DatabaseReference databaseReference;

     private ArrayList<FruitItem2> fruits;

     private RecyclerView recyclerView;
     private RecyclerViewAdapterForHomeFragment adapter;
     private ProgressBar progressBar;
     private RelativeLayout parentLayout;

     private Map<String, Integer> photoMapOfFruits;

     private OnFragmentInteractionListener mListener;

     private ValueEventListener valueEventListener;

     private InternetConnectionViewModel internetConnectionViewModel;
     private Snackbar noInternetSnackbar;

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
          photoMapOfFruits = new HashMap<>();

          FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();

          setupPhotoMapOfFruits();

     }

     private void setupPhotoMapOfFruits() {
          photoMapOfFruits.put("Apple", R.drawable.ic_apple);
          photoMapOfFruits.put("Bananas", R.drawable.ic_bananas);
          photoMapOfFruits.put("Chikoo", R.drawable.ic_chiku);
          photoMapOfFruits.put("Grapes", R.drawable.ic_grapes);
          photoMapOfFruits.put("Guava", R.drawable.ic_guava);
          photoMapOfFruits.put("Kiwifruit", R.drawable.ic_kiwifruit);
          photoMapOfFruits.put("Oranges", R.drawable.ic_oranges);
          photoMapOfFruits.put("Pomegranate", R.drawable.ic_pomegranate);
          photoMapOfFruits.put("Strawberries", R.drawable.ic_strawberry);
          photoMapOfFruits.put("Watermelon", R.drawable.ic_watermelon);
          photoMapOfFruits.put("Pears", R.drawable.ic_pears);
          photoMapOfFruits.put("Mango", R.drawable.ic_mangoes);
          photoMapOfFruits.put("Papaya", R.drawable.ic_papaya);
          photoMapOfFruits.put("Pineapple", R.drawable.ic_pineapple);
          photoMapOfFruits.put("CustardApple", R.drawable.ic_custard_apple);
          photoMapOfFruits.put("Black Plum", R.drawable.ic_black_plum);

     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
          View v = inflater.inflate(R.layout.fragment_home, container, false);

          progressBar = v.findViewById(R.id.progressBar);
          recyclerView = v.findViewById(R.id.recycler_view_home);
          parentLayout = v.findViewById(R.id.parent_layout);

          noInternetSnackbar = Snackbar.make(parentLayout, "NO INTERNET CONNECTION", Snackbar.LENGTH_INDEFINITE);
          noInternetSnackbar.setAction("RETRY", new MyRetryListener());
          noInternetSnackbar.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));

          internetConnectionViewModel = new ViewModelProvider(this).get(InternetConnectionViewModel.class);
          observeConnectivity();
          startCheckingNetworkConnectivity();

          getFruitsFromDatabase(new getFruitsInterface() {
               @Override
               public void afterFetching() {
                    //after fetching, we will show the recycler view and hide the progress bar
                    Collections.sort(fruits, new Comparator<FruitItem2>() {
                         @Override
                         public int compare(FruitItem2 o1, FruitItem2 o2) {
                              return o1.getFruitRank().compareTo(o2.getFruitRank());
                         }
                    });

                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    adapter = new RecyclerViewAdapterForHomeFragment(getContext(), fruits, photoMapOfFruits);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    handleClickEvents();
               }
          });

          return v;
     }


     private void startCheckingNetworkConnectivity() {
          internetConnectionViewModel.startConnectivityCheck();
     }

     private void observeConnectivity() {
          internetConnectionViewModel.getIsConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
               @Override
               public void onChanged(Boolean isConnected) {
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

     private void getFruitsFromDatabase(final getFruitsInterface fruitsInterface) {

          valueEventListener = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // now for each fruit, make a new FruitItem2 object and add it in the
                    // fruits arraylist.

                    if(fruits.isEmpty()) {

                         for (DataSnapshot d : dataSnapshot.getChildren()) {
                              FruitItem2 newFruit = new FruitItem2();
                              ArrayList<String> quantities = new ArrayList<>();
                              ArrayList<String> prices = new ArrayList<>();


                              newFruit.setFruitName(d.getKey());
                              newFruit.setAvailability(d.child("Availability").getValue(String.class));
                              newFruit.setFruitRank(d.child("rank").getValue(Integer.class));
                              for (DataSnapshot d1 : d.child("qty").getChildren()) {
                                   quantities.add(d1.getValue(String.class));
                              }
                              for (DataSnapshot d2 : d.child("prices").getChildren()) {
                                   prices.add(d2.getValue(String.class));
                              }
                              if(!quantities.isEmpty() && !prices.isEmpty()) {
                                   newFruit.setQuantities(quantities);
                                   newFruit.setPrices(prices);

                                   fruits.add(newFruit);
                              }
                         }
                    }

                    fruitsInterface.afterFetching();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                    parentLayout.setVisibility(View.INVISIBLE);

                    switch(databaseError.getCode()) {
                         case DatabaseError.DISCONNECTED :
                         case DatabaseError.NETWORK_ERROR :
                              Snackbar mySnackbar = Snackbar.make(parentLayout, "Check your INTERNET Connection", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar.setAction("RETRY", new MyRetryListener());
                              mySnackbar.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));
                              mySnackbar.show();
                              break;
                         case DatabaseError.OPERATION_FAILED :
                         case DatabaseError.UNKNOWN_ERROR:
                              Snackbar mySnackbar1 = Snackbar.make(parentLayout, "Unknown Error Occurred", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar1.setAction("RETRY", new MyRetryListener());
                              mySnackbar1.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));
                              mySnackbar1.show();
                              break;
                         case DatabaseError.PERMISSION_DENIED:
                              Snackbar mySnackbar2 = Snackbar.make(parentLayout, "Permission Denied", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar2.setAction("RETRY", new MyRetryListener());
                              mySnackbar2.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));
                              mySnackbar2.show();
                              break;
                         case DatabaseError.MAX_RETRIES:
                              Snackbar mySnackbar3 = Snackbar.make(parentLayout, "Max tries reached, Try again after some time", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar3.setAction("RETRY", new MyRetryListener());
                              mySnackbar3.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));
                              mySnackbar3.show();
                              break;
                         default:
                              Snackbar mySnackbar4 = Snackbar.make(parentLayout, "Error Occurred", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar4.setAction("RETRY", new MyRetryListener());
                              mySnackbar4.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));
                              mySnackbar4.show();
                              break;
                    }
               }
          };

          FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
          if (user != null) {
               databaseReference.child("Fruits").addListenerForSingleValueEvent(valueEventListener);
          }
     }

     private void handleClickEvents() {
          recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
               @Override
               public void onItemClick(View view, int position) {
                    FruitItem2 selectedFruit = fruits.get(position);
                    selectedFruit.setImage_resource(photoMapOfFruits.get(selectedFruit.getFruitName()));
                    if(selectedFruit.getAvailability().equals("Available")) {
                         mListener.sendToActivityfromHomeFragment(selectedFruit);

                         FragmentTransaction ft = getFragmentManager().beginTransaction();
                         CustomDialogFragment customDialogFragment = mListener.sendFruitInfoToDialog();
                         customDialogFragment.show(ft, "position of fruit is" + position);
                    }
                    else {
                         Snackbar.make(parentLayout, "Sorry,That Fruit Is Not Available", Snackbar.LENGTH_SHORT).show();
//                         Toast toast = Toast.makeText(getContext(), "Sorry, Fruit not Available", Toast.LENGTH_SHORT);
//                         toast.setGravity(Gravity.CENTER, 0, 0);
//                         toast.show();
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

     private class MyRetryListener implements View.OnClickListener {
          @Override
          public void onClick(View v) {
               //recreate the fragment
               HomeFragment fragment = (HomeFragment) getParentFragmentManager().findFragmentById(R.id.nav_host_fragment);
               getParentFragmentManager().beginTransaction()
                       .detach(fragment)
                       .attach(fragment)
                       .commit();
          }
     }

     @Override
     public void onDestroy() {
          super.onDestroy();
          if (valueEventListener != null) databaseReference.child("Fruits").removeEventListener(valueEventListener);
     }
}
