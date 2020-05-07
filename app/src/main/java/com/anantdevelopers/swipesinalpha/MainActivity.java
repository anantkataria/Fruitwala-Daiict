//add a progress bar which will start loading from the start of the app
//it will stop if user is not connected to internet
//or when halfway exit is false and user is signed in properly
//other two scenarios are 1) user is not authenticated
//                    or  2) user is authenticated halfway
//                    in both cases above, the user will get shifted to other activity via intent, so disable progressbar here too.


//add fade screen when progressbar is running functionality.

//add fruit price updation check functionality which will check wether fruit prices are changed
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
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.Authentication.AuthActivity;
import com.anantdevelopers.swipesinalpha.CartFragment.CartFragment;
import com.anantdevelopers.swipesinalpha.CustomDialogFragment.CustomDialogFragment;
import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem2;
import com.anantdevelopers.swipesinalpha.HomeFragment.HomeFragment;
import com.anantdevelopers.swipesinalpha.OptionsMenuResources.AboutActivity;
import com.anantdevelopers.swipesinalpha.OptionsMenuResources.FruitsAreHealthyActivity;
import com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity.SettingsActivity;
import com.anantdevelopers.swipesinalpha.StoreClosed.StoreClosedActivity;
import com.anantdevelopers.swipesinalpha.UserProfile.UserProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

     private BottomNavigationView bottomNavigationView;
     private FragmentContainerView navHostFragment;
     private AppBarConfiguration appBarConfiguration;

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
          databaseReference.child("OpenOrClose").addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isOpen = dataSnapshot.child("isOpen").getValue(Boolean.class);
                    String openingAgainTimeString = dataSnapshot.child("openingAgainTime").getValue(String.class);

                    Interface.afterChecked(isOpen, openingAgainTimeString);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
          });
     }

     private void checkInDatabase(final afterDatabaseFetchingWorkInterface Interface) {
          if(!isSavingSuccessful) {
               //
               databaseReference.child("halfWayExit").addListenerForSingleValueEvent(new ValueEventListener() {
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
                         //Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                         Toast.makeText(MainActivity.this, "Something went really wrong!", Toast.LENGTH_SHORT).show();
                    }
               });
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
          databaseReference.child("Users").child(authPhone).child("userName").addListenerForSingleValueEvent(
                  new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            user = dataSnapshot.getValue(User.class);
                              userName = dataSnapshot.getValue(String.class);
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                  }
          );
     }

     @Override
     public void sendToActivityfromHomeFragment(FruitItem2 item) {
          this.selectedFruitName = item.getFruitName();
          this.selectedFruitPrices = item.getPrices();
          this.selectedFruitQtys = item.getQuantities();
     }

     @Override
     public CustomDialogFragment sendFruitInfoToDialog() {
          CustomDialogFragment customDialogFragment = new CustomDialogFragment();
          Bundle bundle = new Bundle();
          bundle.putString("fruitName", this.selectedFruitName);
          bundle.putStringArrayList("fruitQty", this.selectedFruitQtys);
          bundle.putStringArrayList("fruitPrice", this.selectedFruitPrices);
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

}
