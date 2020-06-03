package com.anantdevelopers.swipesinalpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.anantdevelopers.swipesinalpha.Authentication.AuthActivity;
import com.anantdevelopers.swipesinalpha.CartFragment.CartFragment;
import com.anantdevelopers.swipesinalpha.HomeFragment.CustomDialogFragment.CustomDialogFragment;
import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.FruitItem2;
import com.anantdevelopers.swipesinalpha.HomeFragment.HomeFragment;
import com.anantdevelopers.swipesinalpha.OptionsMenuResources.AboutActivity;
import com.anantdevelopers.swipesinalpha.OptionsMenuResources.FruitsAreHealthyActivity;
import com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity.SettingsActivity;
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

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, CartFragment.OnFragmentInteractionListener, CustomDialogFragment.OnFragmentInteractionListener {

     private DatabaseReference databaseReference;

     private ArrayList<FruitItem> receivedItems;
     private String selectedFruitName;
     private ArrayList<String> selectedFruitQtys, selectedFruitPrices;
     private int selectedFruitImage;

     private BottomNavigationView bottomNavigationView;
     private FragmentContainerView navHostFragment;
     private AppBarConfiguration appBarConfiguration;
     private RelativeLayout parentLayout;

     private ValueEventListener listener1, listener2, listener3;

     private boolean isSavingSuccessful = false;

     private ProgressBar progressBar;

     private String userName = "User";
     private String authPhone = "";
//     private User user;

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

          Intent intent = getIntent();
          isSavingSuccessful = intent.getBooleanExtra("isSavingSuccessful", false);

          progressBar = findViewById(R.id.progressBar);
          FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();
//          user = new User();


          bottomNavigationView = findViewById(R.id.bottomNavigationView);
          navHostFragment = findViewById(R.id.nav_host_fragment);
          parentLayout = findViewById(R.id.parent_layout);

          appBarConfiguration = new AppBarConfiguration.Builder(
                  R.id.HomeFragment, R.id.CartFragment, R.id.PreviousOrdersFragment
          ).build();

          FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

          FirebaseAuth.AuthStateListener authStateListener = buildAuthStateListener();
          firebaseAuth.addAuthStateListener(authStateListener);
          receivedItems = new ArrayList<>();

          progressBar.setVisibility(View.VISIBLE);
          if(progressBar.getVisibility() == View.VISIBLE){
               getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
          }

     }

     FirebaseAuth.AuthStateListener buildAuthStateListener(){

          FirebaseAuth.AuthStateListener authStateListener;

          authStateListener = new FirebaseAuth.AuthStateListener() {
               @Override
               public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
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
                                   if (progressBar.getVisibility() == View.GONE) {
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                   }

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
                         finish();
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
          getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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

     private void checkInDatabase(final afterDatabaseFetchingWorkInterface Interface) {
          if(!isSavingSuccessful) {
               //
               listener2 = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         //enable progress bar in main activity while this is being checked.

                         //should rather use phone memory for seamless experience
                         if (dataSnapshot.child(authPhone).exists()) {
                              Intent intent = new Intent(MainActivity.this, UserProfile.class);
                              intent.putExtra("authenticatedPhoneNumber", authPhone);
                              startActivity(intent);
                              finish();
                         }
                         else {
                              final NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                              NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
                              NavigationUI.setupWithNavController(bottomNavigationView, navController);

                              getUser();
                         }

                         //call the interface
                         Interface.afterFetch();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                         handleDatabaseError(databaseError);
                    }
               };

               databaseReference.child("halfWayExit").addListenerForSingleValueEvent(listener2);
          }
          else {
               //final String phoneNumber = user.getPhoneNumber();
               final NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
               NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
               NavigationUI.setupWithNavController(bottomNavigationView, navController);

               getUser();
               Interface.afterFetch();
          }

     }

     private void getUser() {

          listener3 = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            user = dataSnapshot.getValue(User.class);
                    userName = dataSnapshot.getValue(String.class);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                    handleDatabaseError(databaseError);
               }
          };

          databaseReference.child("Users").child(authPhone).child("userName").addListenerForSingleValueEvent(listener3);
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
          //Log.e("MainActivity", this.selectedFruitName);
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
                    intent.putExtra("authPhone", authPhone);
                    startActivity(intent);
                    return true;
               case R.id.fruits_are_healthy_dest:
                    Intent intent1 = new Intent(MainActivity.this, FruitsAreHealthyActivity.class);
                    startActivity(intent1);
                    return true;
               case R.id.about_dest:
                    Intent intent3 = new Intent(MainActivity.this, AboutActivity.class);
                    intent3.putExtra("userName", userName);
                    intent3.putExtra("authPhone", authPhone);
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