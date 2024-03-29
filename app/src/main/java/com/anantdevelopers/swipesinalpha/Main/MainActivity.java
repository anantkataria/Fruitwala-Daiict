package com.anantdevelopers.swipesinalpha.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anantdevelopers.swipesinalpha.Authentication.AuthActivity;
import com.anantdevelopers.swipesinalpha.BuildConfig;
import com.anantdevelopers.swipesinalpha.CartFragment.CartFragment;
import com.anantdevelopers.swipesinalpha.HomeFragment.CustomDialogFragment.CustomDialogFragment;
import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.FruitItem2;
import com.anantdevelopers.swipesinalpha.HomeFragment.HomeFragment;
import com.anantdevelopers.swipesinalpha.OptionsMenuResources.AboutActivity;
import com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity.SettingsActivity;
import com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrdersFragment;
import com.anantdevelopers.swipesinalpha.R;
import com.anantdevelopers.swipesinalpha.StoreClosed.StoreClosedActivity;
import com.anantdevelopers.swipesinalpha.UserProfile.UserProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, CartFragment.OnFragmentInteractionListener, CustomDialogFragment.OnFragmentInteractionListener, PreviousOrdersFragment.sendOrderAgainItemsToMainActivityListener {

     private DatabaseReference databaseReference;

     private ArrayList<FruitItem> receivedItems;
     private String selectedFruitName;
     private ArrayList<String> selectedFruitQtys, selectedFruitPrices;
     private int selectedFruitImage;

     private BottomNavigationView bottomNavigationView;
     private FragmentContainerView navHostFragment;
     private AppBarConfiguration appBarConfiguration;
     private RelativeLayout parentLayout;

     private ImageView noInternetImage;
     private TextView noConnectionHardCoded;
     private TextView retryTextView;

     private InternetConnectionViewModel internetConnectionViewModel;
     private Snackbar noInternetSnackbar;

     private ValueEventListener listener1, listener2, listener3;

     //private boolean isSavingSuccessful = false;

     private ProgressBar progressBar;

     private String userName = "User";
     private String authPhone = "";

     private interface afterDatabaseFetchingWorkInterface {
          void afterFetch();
     }

     private interface afterOpenOrClosedCheckedInterface {
          void afterChecked(boolean isOpen, String openingAgainTimeString);
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          setTheme(R.style.AppTheme);

          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);

          //getWindow().setNavigationBarColor(getResources().getColor(R.color.white));

          progressBar = findViewById(R.id.progressBar);
          FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();

          bottomNavigationView = findViewById(R.id.bottomNavigationView);
          navHostFragment = findViewById(R.id.nav_host_fragment);
          parentLayout = findViewById(R.id.parent_layout);

          noInternetImage = findViewById(R.id.no_internet_connection_image);
          noConnectionHardCoded = findViewById(R.id.hardcoded1);
          retryTextView = findViewById(R.id.retry_text_view);

          appBarConfiguration = new AppBarConfiguration.Builder(
                  R.id.HomeFragment, R.id.CartFragment, R.id.PreviousOrdersFragment
          ).build();

//          noInternetSnackbar = Snackbar.make(parentLayout, "NO INTERNET CONNECTION", Snackbar.LENGTH_INDEFINITE);
//          noInternetSnackbar.setAction("RETRY", new MyRetryListener());
//          noInternetSnackbar.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));

          internetConnectionViewModel = new ViewModelProvider(this).get(InternetConnectionViewModel.class);
          observeConnectivity();
          startCheckingNetworkConnectivity();

          FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

          FirebaseAuth.AuthStateListener authStateListener = buildAuthStateListener();
          firebaseAuth.addAuthStateListener(authStateListener);
          receivedItems = new ArrayList<>();

          progressBar.setVisibility(View.VISIBLE);
//          if(progressBar.getVisibility() == View.VISIBLE){
//               getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//          }

          retryTextView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    MainActivity.this.recreate();
               }
          });

     }

     private void startCheckingNetworkConnectivity() {
          internetConnectionViewModel.startConnectivityCheck();
     }

     private void observeConnectivity() {
          internetConnectionViewModel.getIsConnected().observe(this, new Observer<Boolean>() {
               @Override
               public void onChanged(Boolean isConnected) {
                    if(!isConnected){
                        //not connected, show SnackBar of retry
                        //and retry is recreate the activity here
                        //noInternetSnackbar.show();
                         progressBar.setVisibility(View.GONE);
                         noInternetImage.setVisibility(View.VISIBLE);
                         noConnectionHardCoded.setVisibility(View.VISIBLE);
                         retryTextView.setVisibility(View.VISIBLE);
                    }
                    else {
//                         noInternetSnackbar.dismiss();
                         progressBar.setVisibility(View.VISIBLE);
                         noInternetImage.setVisibility(View.GONE);
                         noConnectionHardCoded.setVisibility(View.GONE);
                         retryTextView.setVisibility(View.GONE);
                    }
               }
          });
     }

     FirebaseAuth.AuthStateListener buildAuthStateListener(){

          FirebaseAuth.AuthStateListener authStateListener;

          authStateListener = new com.google.firebase.auth.FirebaseAuth.AuthStateListener() {
               @Override
               public void onAuthStateChanged(@NonNull com.google.firebase.auth.FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user != null){  //means user is signed in

                         authPhone = user.getPhoneNumber();

                         checkThatStoreIsOpenOrClose(new afterOpenOrClosedCheckedInterface() {
                              @Override
                              public void afterChecked(boolean isOpen, String openingAgainTimeString) {
                                   if(!isOpen){
                                        //store is supposed to be closed so launch store closed activity
                                        // and clear the back stack
                                        Intent intent = new Intent(MainActivity.this, StoreClosedActivity.class);
                                        intent.putExtra("openingAgainTimeString", openingAgainTimeString);
                                        intent.putExtra("authPhone", authPhone);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                   }
                                   //other wise everything continues normal, because store is open
                              }
                         });

                         checkInDatabase(new afterDatabaseFetchingWorkInterface() {
                              @Override
                              public void afterFetch() {
                                   progressBar.setVisibility(View.GONE);
//                                   if (progressBar.getVisibility() == View.GONE) {
//                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                   }

                                   bottomNavigationView.setVisibility(View.VISIBLE);
                                   navHostFragment.setVisibility(View.VISIBLE);
                              }
                         });


                    }
                    else{
                         //user is signed out
                         //navController.navigate(R.id.AuthFragment);   //this will inflate the fragment in navhostfragment and bottom navigation will still be accessible so instead i will create new auth activity.
                         Intent Authintent = new Intent(MainActivity.this, AuthActivity.class);
                         Authintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         startActivity(Authintent);
//                         finish();
                    }
               }
          };

          return authStateListener;
     }

     private void checkThatStoreIsOpenOrClose(final afterOpenOrClosedCheckedInterface Interface) {

          listener1 = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isOpen = dataSnapshot.child("isOpen").getValue(Boolean.class);
                    String openingAgainTimeString = dataSnapshot.child("openingAgainTime").getValue(String.class);

                    Interface.afterChecked(isOpen, openingAgainTimeString);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                    handleDatabaseError(databaseError);
               }
          };

          databaseReference.child("OpenOrClose").addValueEventListener(listener1);
     }

     private void handleDatabaseError(DatabaseError databaseError) {
          parentLayout.setVisibility(View.INVISIBLE);
//          getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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

     private void checkInDatabase(final afterDatabaseFetchingWorkInterface Interface) {

          listener2 = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()){
                         Intent intent = new Intent(MainActivity.this, UserProfile.class);
                         intent.putExtra("authenticatedPhoneNumber", authPhone);
                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         startActivity(intent);
                         //finish();
                    }
                    else {
                         userName = dataSnapshot.child("userName").getValue(String.class);
                         final NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
//                         NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
//                                 .findFragmentById(R.id.nav_host_fragment);
//                         NavController navController = navHostFragment.getNavController();
                         NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
                         NavigationUI.setupWithNavController(bottomNavigationView, navController);
                    }

                    Interface.afterFetch();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                    handleDatabaseError(databaseError);
               }
          };
          databaseReference.child("Users").child(authPhone).addListenerForSingleValueEvent(listener2);
     }

     @Override
     public void sendToActivityfromHomeFragment(FruitItem2 item) {
          this.selectedFruitName = item.getFruitName();
          this.selectedFruitPrices = item.getPrices();
          this.selectedFruitQtys = item.getQuantities();
          this.selectedFruitImage = item.getImage_resource();
     }

     @Override
     public CustomDialogFragment sendFruitInfoToDialog() {
          CustomDialogFragment customDialogFragment = new CustomDialogFragment();
          Bundle bundle = new Bundle();
          bundle.putString("fruitName", this.selectedFruitName);
          bundle.putStringArrayList("fruitQty", this.selectedFruitQtys);
          bundle.putStringArrayList("fruitPrice", this.selectedFruitPrices);
          bundle.putInt("fruitImage", this.selectedFruitImage);
          customDialogFragment.setArguments(bundle);
          return customDialogFragment;
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
          MenuInflater inflater = getMenuInflater();
          inflater.inflate(R.menu.options_menu, menu);
          return true;
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
          switch(item.getItemId()){
               case R.id.settings_dest:
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    intent.putExtra("userName", userName);
                    //intent.putExtra("authPhone", authPhone);
                    startActivity(intent);
                    return true;
               case R.id.share_dest:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "FruitWala");
                    String shareMessage = "\nOrder fresh fruits online and get it delivered to your dorm number.\nTry using fruitwala to order fruits.\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share with"));
                    return true;
               case R.id.about_dest:
                    Intent intent3 = new Intent(MainActivity.this, AboutActivity.class);
                    intent3.putExtra("userName", userName);
                    //intent3.putExtra("authPhone", authPhone);
                    startActivity(intent3);
                    return true;
               default:
                    return super.onOptionsItemSelected(item);
          }
     }

     @Override
     public void getItemFromDialogToMainActivity(String receivedFruitName, String receivedFruitQty, String receivedFruitPrice) {
          receivedItems.add(new FruitItem(receivedFruitName, receivedFruitQty, receivedFruitPrice));
     }

     @Override
     public void sendToMainFromPreviousOrderFragment(List<FruitItem> cartItems) {
          receivedItems.clear();
          receivedItems = (ArrayList<FruitItem>) cartItems;
     }

     @Override
     public ArrayList<FruitItem> getFruitsFromMainToCartFragment() { return receivedItems; }

     private class MyRetryListener implements View.OnClickListener {
          @Override
          public void onClick(View v) {
               MainActivity.this.recreate();
          }
     }

     @Override
     protected void onDestroy() {
          super.onDestroy();
          if (listener3 != null) databaseReference.child("Users").child(authPhone).child("userName").removeEventListener(listener3);
          if (listener2 != null) databaseReference.child("halfWayExit").removeEventListener(listener2);
          if (listener1 != null) databaseReference.child("OpenOrClose").removeEventListener(listener1);
     }
}