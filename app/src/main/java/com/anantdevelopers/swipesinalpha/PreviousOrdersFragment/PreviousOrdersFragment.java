package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.CartFragment.CheckoutFlow.CheckoutUser;
import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase.PreviousOrderEntity;
import com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase.PreviousOrderViewModel;
import com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase.RecyclerViewAdapterForPreviousOrders;
import com.anantdevelopers.swipesinalpha.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.view.View.GONE;


public class PreviousOrdersFragment extends Fragment implements DeletePreviousOrdersDialog.DeletePreviousOrdersDialogListener, CancelCurrentOrderDialog.cancelCurrentOrderDialogListener, OrderAgainDialog.OrderAgainDialogListener {

     private static final String SORTING_ORDER_1 = "newest_first";
     private static final String SORTING_ORDER_2 = "oldest_first";
     private static final String SORTING_ORDER_TYPE_STORAGE_KEY = "sorting_type";

     private RecyclerView currentOrdersRecyclerView;

     private DatabaseReference databaseReference;

     private ArrayList<CheckoutUser> currentOrdersList;
     private ArrayList<CheckoutUser> previousOrdersList; //for local database

     private ProgressBar currentOrderProgressBar, previousOrderProgressBar, parentParentProgressBar;
     private TextView noCurrentOrdersTextView;
     private LinearLayout parentLayout;
     private TextView noPreviousOrdersTextView;

     private RecyclerViewAdapterForCurrentOrders adapterForCurrentOrders;
     private RecyclerViewAdapterForPreviousOrders adapter;

     private PreviousOrderViewModel previousOrderViewModel;
     private OrderAgainViewModel orderAgainViewModel;

     private ValueEventListener valueEventListener, valueEventListener2;

     private String authPhoneNumber;
     private String token;

     private SharedPreferences sharedPreferences;
     private String sortingOrderType;

     private Observer<List<PreviousOrderEntity>> observer;

     private sendOrderAgainItemsToMainActivityListener mListener;

     public PreviousOrdersFragment() {
          // Required empty public constructor
     }

     private interface FruitsFromdatabase {
          void afterFetch(Map<String, ArrayList<String>> latestQtyMap, Map<String, ArrayList<Integer>> latestPriceMap);
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

          setHasOptionsMenu(true);

          sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

          FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();
          FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

          authPhoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();

          currentOrdersList = new ArrayList<>();
          previousOrdersList = new ArrayList<>();

          previousOrderViewModel = new ViewModelProvider(this).get(PreviousOrderViewModel.class);
          orderAgainViewModel = new ViewModelProvider(this).get(OrderAgainViewModel.class);
          getToken();
     }

     private void getToken() {
          FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
               @Override
               public void onSuccess(InstanceIdResult instanceIdResult) {
                    token = instanceIdResult.getToken();
               }
          });
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
          // Inflate the layout for this fragment
          View v = inflater.inflate(R.layout.fragment_previous_orders, container, false);

          parentLayout = v.findViewById(R.id.parent_layout);
          parentParentProgressBar = v.findViewById(R.id.parent_parent_progress_bar);

          currentOrderAffairs(v);
          previousOrderAffairs(v);
          return v;
     }

     @Override
     public void onDestroy() {
          super.onDestroy();
          if(valueEventListener != null) databaseReference.child("Delivered or Cancelled").child(authPhoneNumber).removeEventListener(valueEventListener);
          if(valueEventListener2 != null) databaseReference.child("Orders").child(authPhoneNumber).removeEventListener(valueEventListener2);
     }



     private void previousOrderAffairs(final View v) {
          previousOrderProgressBar = v.findViewById(R.id.previousOrdersProgressBar);
          noPreviousOrdersTextView = v.findViewById(R.id.noPreviousOrdersTextView);

          RecyclerView previousOrdersRecyclerView = v.findViewById(R.id.previousOrdersRecyclerView);
          adapter = new RecyclerViewAdapterForPreviousOrders(getContext());

          //setting the recycler view
          previousOrdersRecyclerView.setAdapter(adapter);
          previousOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

          sortingOrderType = sharedPreferences.getString(SORTING_ORDER_TYPE_STORAGE_KEY, SORTING_ORDER_1);

          switch(sortingOrderType) {
               case SORTING_ORDER_2:
                    observeOldestFirst();
                    break;
               case SORTING_ORDER_1:
               default:
                    observeNewestFirst();
                    break;
          }

          orderAgainViewModel.getCartItems().observe(getViewLifecycleOwner(), new Observer<List<FruitItem>>() {
               @Override
               public void onChanged(List<FruitItem> fruitItems) {
                    if(fruitItems.isEmpty()) {
                         //Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                         //Toast.makeText(getContext(), "Fruit may not be available!", Toast.LENGTH_LONG).show();
                         Snackbar.make(parentLayout, "Fruit May Not Be Available!", Snackbar.LENGTH_SHORT).show();
                         Log.e("observe", "fruitItems are empty");
                    }
                    else {
                         //now make an interface that will send this items to the main activity and
                         //in main activity, clear the arraylist that we will send to the cart and
                         //set items of that arrayList to this arrayList. thats it.

                         //after that interface,
                         //set a toast that items added to the cart.
                         Log.e("observe", "fruitItems are not empty");
                         mListener.sendToMainFromPreviousOrderFragment(fruitItems);
                         //Toast.makeText(getContext(), "Fruits added to cart!", Toast.LENGTH_SHORT).show();
                         Snackbar.make(parentLayout, "Fruits Added To Cart!", Snackbar.LENGTH_SHORT).show();
                    }
                    previousOrderProgressBar.setVisibility(View.INVISIBLE);
               }
          });



          fetchPreviousOrders(new LocalDatabaseCallbackInterface() {
               @Override
               public void fromOnChildManipulated(ArrayList<CheckoutUser> previousOrdersList) {
                    ArrayList<PreviousOrderEntity> ordersToAddInRoom = new ArrayList<>();
                    for(CheckoutUser u : previousOrdersList){
                         StringBuilder orderFruitList = new StringBuilder();
                         int grandTotal = 0;
                         String status = u.getStatus();
                         for(FruitItem f : u.getFruits()){
                              orderFruitList.append(f.getFruitName()).append(", ").append(f.getFruitQty()).append(", ").append(f.getFruitPrice()).append("\n");
                              grandTotal += Integer.parseInt(f.getFruitPrice().replaceAll("[Rs.\\s]", ""));
                         }

                         Long orderPlacedDate =Long.parseLong(u.getOrderPlacedDate());
                         Long orderDeliveredOrCancelledDate = Long.parseLong(u.getOrderDeliveredOrCancelledDate());

                         ordersToAddInRoom.add(new PreviousOrderEntity(orderFruitList.toString(), status, "GrandTotal : " + grandTotal + " Rs.", orderPlacedDate, orderDeliveredOrCancelledDate,"false"));
                    }

                    for(PreviousOrderEntity poe : ordersToAddInRoom){
                         previousOrderViewModel.insert(poe);
                    }

                    previousOrderProgressBar.setVisibility(View.INVISIBLE);

                    databaseReference.child("Delivered or Cancelled").child(authPhoneNumber).removeValue().addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                              Toast.makeText(getContext(), "Operation Failed : " + e.getMessage(), Toast.LENGTH_LONG).show();
                         }
                    });
               }
          });
     }

     private void observeOldestFirst() {
          observer = new Observer<List<PreviousOrderEntity>>() {
               @Override
               public void onChanged(List<PreviousOrderEntity> previousOrderEntities) {
                    if (previousOrderEntities.isEmpty()){
                         noPreviousOrdersTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                         noPreviousOrdersTextView.setVisibility(GONE);
                         handleClicksForPreviousOrders();
                    }
                    adapter.setPreviousOrders(previousOrderEntities);
               }
          };

          previousOrderViewModel.getAllPreviousOrdersOldestFirst().observe(getViewLifecycleOwner(), observer);
     }

     private void observeNewestFirst() {
          observer = new Observer<List<PreviousOrderEntity>>() {
               @Override
               public void onChanged(List<PreviousOrderEntity> previousOrderEntities) {
                    if (previousOrderEntities.isEmpty()){
                         noPreviousOrdersTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                         noPreviousOrdersTextView.setVisibility(GONE);
                         handleClicksForPreviousOrders();
                    }
                    adapter.setPreviousOrders(previousOrderEntities);
               }
          };

          previousOrderViewModel.getAllPreviousOrdersNewestFirst().observe(getViewLifecycleOwner(), observer);
     }

     private void handleClicksForPreviousOrders() {
          adapter.setOnItemClickListener(new RecyclerViewAdapterForPreviousOrders.OnItemClickListener() {
               @Override
               public void onItemTouchHold(int position) {
                    PreviousOrderEntity poe = adapter.getOrderAtPosition(position);
                    String isStarred = poe.getIsStarred();

                    if(isStarred.equals("true")){
                         poe.setIsStarred("false");
                    }
                    else {
                         poe.setIsStarred("true");
                    }
                    previousOrderViewModel.update(poe);

               }

               @Override
               public void onOrderAgainButtonClick(int position) {
                    OrderAgainDialog dialog2 = new OrderAgainDialog(position);
                    dialog2.setTargetFragment(PreviousOrdersFragment.this, 0);
                    dialog2.show(getParentFragmentManager(), "ordering again from position " + position);
               }
          });
     }

     private void fetchPreviousOrders(final LocalDatabaseCallbackInterface Interface) {

          previousOrderProgressBar.setVisibility(View.VISIBLE);

          valueEventListener = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                         previousOrdersList.clear();

                         for(DataSnapshot data: dataSnapshot.getChildren()){
                              previousOrdersList.add(data.child("Order").getValue(CheckoutUser.class));
                         }

                         Interface.fromOnChildManipulated(previousOrdersList);

                    }
                    else{
                         previousOrderProgressBar.setVisibility(View.INVISIBLE);
                         //no delivered/cancelled orders remaining to read from database
                    }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                    handleDatabaseError(databaseError);
               }
          };

          databaseReference.child("Delivered or Cancelled").child(authPhoneNumber).addValueEventListener(valueEventListener);

     }

     private void handleDatabaseError(DatabaseError databaseError) {
          parentLayout.setVisibility(View.INVISIBLE);

          switch(databaseError.getCode()) {
               case DatabaseError.DISCONNECTED :
               case DatabaseError.NETWORK_ERROR :
                    Snackbar mySnackbar = Snackbar.make(parentLayout, "Check your INTERNET Connection", Snackbar.LENGTH_INDEFINITE);
                    mySnackbar.setAction("RETRY", new MyRetryListener());
                    mySnackbar.show();
                    break;
               case DatabaseError.OPERATION_FAILED :
               case DatabaseError.UNKNOWN_ERROR:
                    Snackbar mySnackbar1 = Snackbar.make(parentLayout, "Unknown Error Occurred", Snackbar.LENGTH_INDEFINITE);
                    mySnackbar1.setAction("RETRY", new MyRetryListener());
                    mySnackbar1.show();
                    break;
               case DatabaseError.PERMISSION_DENIED:
                    Snackbar mySnackbar2 = Snackbar.make(parentLayout, "Permission Denied", Snackbar.LENGTH_INDEFINITE);
                    mySnackbar2.setAction("RETRY", new MyRetryListener());
                    mySnackbar2.show();
                    break;
               case DatabaseError.MAX_RETRIES:
                    Snackbar mySnackbar3 = Snackbar.make(parentLayout, "Max tries reached, Try again after some time", Snackbar.LENGTH_INDEFINITE);
                    mySnackbar3.setAction("RETRY", new MyRetryListener());
                    mySnackbar3.show();
                    break;
               default:
                    Snackbar mySnackbar4 = Snackbar.make(parentLayout, "Error Occurred", Snackbar.LENGTH_INDEFINITE);
                    mySnackbar4.setAction("RETRY", new MyRetryListener());
                    mySnackbar4.show();
                    break;
          }
     }

     private void currentOrderAffairs(View v) {
          currentOrderProgressBar = v.findViewById(R.id.currentOrdersProgressBar);
          noCurrentOrdersTextView = v.findViewById(R.id.noCurrentOrdersTextView);

          currentOrdersRecyclerView = v.findViewById(R.id.currentOrdersRecyclerView);

          fetchCurrentOrders(new DatabaseCallbackInterface() {
               @Override
               public void fromOnChildManipulated(ArrayList<CheckoutUser> currentOrdersList) {
                    Collections.sort(currentOrdersList, new Comparator<CheckoutUser>() {
                         @Override
                         public int compare(CheckoutUser o1, CheckoutUser o2) {
                              return -1*o1.getOrderPlacedDate().compareTo(o2.getOrderPlacedDate());
                         }
                    });
                    parentLayout.setVisibility(View.VISIBLE);
                    parentParentProgressBar.setVisibility(GONE);
                    if(currentOrdersList.isEmpty()){
                         noCurrentOrdersTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                         adapterForCurrentOrders = new RecyclerViewAdapterForCurrentOrders(getContext(), currentOrdersList);
                         currentOrdersRecyclerView.setAdapter(adapterForCurrentOrders);
                         currentOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                         noCurrentOrdersTextView.setVisibility(GONE);

                         handleCurrentOrderButtonClicks();
                    }
                    currentOrderProgressBar.setVisibility(View.INVISIBLE);
               }
          });
     }

     private void handleCurrentOrderButtonClicks() {
          adapterForCurrentOrders.setOnButtonClickListener(new RecyclerViewAdapterForCurrentOrders.onButtonClickListener() {
               @Override
               public void onButtonClick(final int position) {
                    CheckoutUser currentOrderDetails =  currentOrdersList.get(position);

                    if(currentOrderDetails.getStatus().equals("ORDER ON WAY!")){
//                         Toast toast = Toast.makeText(getContext(), "Sorry, Cannot CANCEL order when it is On The Way!", Toast.LENGTH_LONG);
//                         toast.setGravity(Gravity.CENTER, 0, 0);
//                         toast.show();
                         Snackbar.make(parentLayout, "Unable To Cancel Order When It Is On The Way", Snackbar.LENGTH_LONG).show();
                    }
                    else {
                         CancelCurrentOrderDialog dialog1 = new CancelCurrentOrderDialog(position);
                         dialog1.setTargetFragment(PreviousOrdersFragment.this, 0);
                         dialog1.show(getParentFragmentManager(), "cancelling order at position " + position);
                    }
               }
          });
     }

     @Override
     public void onDialogPositiveClickForCancelOrder(int position) {
          CheckoutUser currentOrderDetails =  currentOrdersList.get(position);
          //CheckoutUser curr = new CheckoutUser(currentOrderDetails.getUser(), currentOrderDetails.getFruits(), currentOrderDetails.getPaymentMethod(), currentOrderDetails.getStatus(), currentOrderDetails.getFirebaseDatabaseKey(), currentOrderDetails.getOrderPlacedDate());

          currentOrderDetails.setStatus("CANCELLATION REQUESTED");
          currentOrderProgressBar.setVisibility(View.VISIBLE);
          String firebaseKey = currentOrderDetails.getFirebaseDatabaseKey();

          Map<String, Object> map = new HashMap<>();
          map.put("/Orders/" + authPhoneNumber + "/" + firebaseKey, currentOrderDetails);
          map.put("/tokens/" + authPhoneNumber, token);

          databaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {
                    currentOrderProgressBar.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getContext(), "Requested cancellation successfully", Toast.LENGTH_LONG).show();
                    Snackbar.make(parentLayout, "Cancellation Requested Successfully", Snackbar.LENGTH_SHORT).show();
               }
          }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                    currentOrderProgressBar.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getContext(), "Something went wrong! try again", Toast.LENGTH_LONG).show();
                    Snackbar.make(parentLayout, "Something Went Wrong! Try Again", Snackbar.LENGTH_SHORT).show();
               }
          });
     }

     private void fetchCurrentOrders(final DatabaseCallbackInterface Interface) {

          currentOrderProgressBar.setVisibility(View.VISIBLE);

          valueEventListener2 = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    currentOrdersList.clear();
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                         currentOrdersList.add(data.getValue(CheckoutUser.class));
                    }

                    Interface.fromOnChildManipulated(currentOrdersList);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                    handleDatabaseError(databaseError);
               }
          };

          databaseReference.child("Orders").child(authPhoneNumber).addValueEventListener(valueEventListener2);


     }

     @Override
     public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
          inflater.inflate(R.menu.options_menu_previous_orders_fragment, menu);
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {

          SharedPreferences.Editor editor = sharedPreferences.edit();

          switch(item.getItemId()){

               case R.id.sort_by_latest_first:
                    if(!sortingOrderType.equals(SORTING_ORDER_1)) {
                         editor.putString(SORTING_ORDER_TYPE_STORAGE_KEY, SORTING_ORDER_1);
                         editor.apply();
                         // remove observer of current sorting type by if else chain
                         if(sortingOrderType.equals(SORTING_ORDER_2)){
                              //then remove the observer for SORTING_ORDER_2
                              //previousOrderViewModel.getAllPreviousOrdersOldestFirst().removeObserver(observerForOldestFirst);
                              LiveData<List<PreviousOrderEntity>> observable = previousOrderViewModel.getAllPreviousOrdersOldestFirst();
                              observable.removeObserver(observer);
                         }
                         //else if (sortingOrderType.equals(some other sorting type)){}

                         observeNewestFirst();
                         sortingOrderType = SORTING_ORDER_1;
                    }
                    return true;
               case R.id.sort_by_oldest_first:
                    if(!sortingOrderType.equals(SORTING_ORDER_2)) {
                         editor.putString(SORTING_ORDER_TYPE_STORAGE_KEY, SORTING_ORDER_2);
                         editor.apply();

                         // remove observer of current sorting type by if else chain
                         if(sortingOrderType.equals(SORTING_ORDER_1)){
                              //then remove the observer for SORTING_ORDER_2
                              //previousOrderViewModel.getAllPreviousOrdersOldestFirst().removeObserver(observerForNewestFirst);
                              LiveData<List<PreviousOrderEntity>> observable = previousOrderViewModel.getAllPreviousOrdersNewestFirst();
                              observable.removeObserver(observer);
                         }
                         //else if (sortingOrderType.equals(some other sorting type)){}

                         observeOldestFirst();
                         sortingOrderType = SORTING_ORDER_2;
                    }
                    return true;
               case R.id.delete_all_previous_orders:
                    DeletePreviousOrdersDialog dialog = new DeletePreviousOrdersDialog();
                    dialog.setTargetFragment(this, 0);
                    dialog.show(getParentFragmentManager(), "delete previous orders from the storage");
                    return true;
               default:
                    return super.onOptionsItemSelected(item);
          }

     }

     @Override
     public void onDialogPositiveClickForDeletePreviousOrders(boolean keepStarredOrders) {
          if(keepStarredOrders){
               previousOrderViewModel.deleteAllNotStarredPreviousOrders();
          }
          else {
               previousOrderViewModel.deleteAllPreviousOrders();
          }
     }

     @Override
     public void onDialogPositiveClickForOrderAgain(int position) {

          // first thing is we make map<string fruitname, int price> from the listoffruitsTextview of the
          // current order item.
          // Then we get map1<string fruitname, arraylist<string> fruitquantity> and map2<string fruitname, arraylist<int> fruitprices>
          // from the database.
          // Then pass all three maps to the viewmodel where we will start work in AsyncTask to calculate
          // and make arraylist of FruitItem
          // return this arraylist into this fragment from the viewmodel,
          // and then send this array list into the mainActivity via interface.
          previousOrderProgressBar.setVisibility(View.VISIBLE);

          final Map<String, Integer> actualPriceMap = getActualPriceMap(position);

          //pleaseWaitTextView.setVisibility(View.VISIBLE);


          fetchFruits(new FruitsFromdatabase() {
               @Override
               public void afterFetch(Map<String, ArrayList<String>> latestQtyMap, Map<String, ArrayList<Integer>> latestPriceMap) {
                    //pleaseWaitTextView.setText("FEW SECONDS LEFT...");

                    Log.e("afterfetch", "after ondatachange()");
                    orderAgainViewModel.setActualPriceMap(actualPriceMap);
                    orderAgainViewModel.setLatestPriceMap(latestPriceMap);
                    orderAgainViewModel.setLatestQtyMap(latestQtyMap);

                    orderAgainViewModel.startCalculation();

               }
          });

     }

     private Map<String, Integer> getActualPriceMap(int position) {
          Map<String, Integer> actualPriceMap = new HashMap<>();
          PreviousOrderEntity poe = adapter.getOrderAtPosition(position);
          String text = poe.getOrderFruitList();

          String[] substrings = text.split("\\n");
          for (String s : substrings){
               String[] substringsForAFruit = s.split("\\s*,\\s*");
               int price = Integer.parseInt(substringsForAFruit[2].replaceAll("[Rs.\\s]", ""));
               actualPriceMap.put(substringsForAFruit[0], price);
          }

          return actualPriceMap;
     }

     private void fetchFruits(final FruitsFromdatabase fruitsFromdatabase) {
          databaseReference.child("Fruits").addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Map<String, ArrayList<String>> latestQtyMap = new HashMap<>();
                    Map<String, ArrayList<Integer>> latestPriceMap = new HashMap<>();
                    Log.e("onDatachange", "method starting");

                    for (DataSnapshot d: dataSnapshot.getChildren()){
                         if(Objects.equals(d.child("Availability").getValue(String.class), "Available")){

                              ArrayList<String> qtys = new ArrayList<>();
                              ArrayList<Integer> prices = new ArrayList<>();

                              for(DataSnapshot d1: d.child("prices").getChildren()){
                                   String priceString = d1.getValue(String.class);
                                   prices.add(Integer.parseInt(priceString.replaceAll("[Rs.\\s]", "")));
                              }

                              for (DataSnapshot d2: d.child("qty").getChildren()){
                                   qtys.add(d2.getValue(String.class));
                              }

                              latestPriceMap.put(d.getKey(), prices);
                              latestQtyMap.put(d.getKey(), qtys);
                         }
                    }

                    fruitsFromdatabase.afterFetch(latestQtyMap, latestPriceMap);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                    handleDatabaseError(databaseError);
               }
          });
     }

     private class MyRetryListener implements View.OnClickListener {
          @Override
          public void onClick(View v) {
               //recreate the fragment
               PreviousOrdersFragment fragment = (PreviousOrdersFragment) getParentFragmentManager().findFragmentById(R.id.nav_host_fragment);
               getParentFragmentManager().beginTransaction()
                       .detach(fragment)
                       .attach(fragment)
                       .commit();
          }
     }

     public interface sendOrderAgainItemsToMainActivityListener {
          void sendToMainFromPreviousOrderFragment(List<FruitItem> cartItems);
     }

     @Override
     public void onAttach(@NonNull Context context) {
          super.onAttach(context);

          if (context instanceof sendOrderAgainItemsToMainActivityListener) {
               mListener = (sendOrderAgainItemsToMainActivityListener) context;
          } else {
               throw new RuntimeException(context.toString()
                       + " must implement PreviousOrdersFragment.sendOrderAgainItemsToMainActivityListener");
          }
     }

     @Override
     public void onDetach() {
          super.onDetach();
          mListener = null;
     }
}
