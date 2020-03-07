//add a progress bar which will start loading from the start of the app
//it will stop if user is not connected to internet
//or when halfway exit is false and user is signed in properly
//other two scenarios are 1) user is not authenticated
//                    or  2) user is authenticated halfway
//                    in both cases above, the user will get shifted to other activity via intent, so disable progressbar here too.


//add fade screen when progressbar is running functionality.
package com.anantdevelopers.swipesinalpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.Authentication.AuthActivity;
import com.anantdevelopers.swipesinalpha.CartFragment.CartFragment;
import com.anantdevelopers.swipesinalpha.CustomDialogFragment.CustomDialogFragment;
import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.HomeFragment.HomeFragment;
import com.anantdevelopers.swipesinalpha.UserProfile.UserProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, CartFragment.OnFragmentInteractionListener, CustomDialogFragment.OnFragmentInteractionListener, PaymentResultListener {

     private static final String TAG = "MainActivity";

     private FirebaseAuth firebaseAuth;
     private FirebaseAuth.AuthStateListener authStateListener;

     private FirebaseDatabase firebaseDatabase;
     private DatabaseReference databaseReference;

     private ArrayList<FruitItem> receivedItems;
     private String selectedFruitName, selectedFruitQty, selectedFruitPrice;

     private BottomNavigationView bottomNavigationView;
     private AppBarConfiguration appBarConfiguration;

     private boolean isHalfwayExit = false;
     private boolean isSavingSuccessful = false;

     private ProgressBar progressBar;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);

          Intent intent = getIntent();
          isSavingSuccessful = intent.getBooleanExtra("isSavingSuccessful", false);

          progressBar = findViewById(R.id.progressBar);
          progressBar.setVisibility(View.VISIBLE);
          if(progressBar.getVisibility() == View.VISIBLE){
               getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
          }

          firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();

          bottomNavigationView = findViewById(R.id.bottomNaigationView);
          appBarConfiguration = new AppBarConfiguration.Builder(
                  R.id.HomeFragment, R.id.CartFragment, R.id.PreviousOrdersFragment
          ).build();

          firebaseAuth = FirebaseAuth.getInstance();

          authStateListener = new FirebaseAuth.AuthStateListener() {
               @Override
               public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user != null){  //means user is signed in
                         final NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                         NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
                         NavigationUI.setupWithNavController(bottomNavigationView, navController);

                         if(!isSavingSuccessful) {
                              final String phoneNo = user.getPhoneNumber();
                              databaseReference.child("halfWayExit").addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        //enable progress bar in main activity while this is being checked.

                                        //should rather use phone memory for seamless experience
                                        if (dataSnapshot.child(phoneNo).exists()) {
                                             Intent intent = new Intent(MainActivity.this, UserProfile.class);
                                             intent.putExtra("authenticatedPhoneNumber", phoneNo);
                                             startActivity(intent);
                                             finish();
                                             Log.e("line117MainActivity", "isHalfwayExit = " + isHalfwayExit);
                                        }
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {
                                        //Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                                   }
                              });
                         }
                         progressBar.setVisibility(View.GONE);
                         if (progressBar.getVisibility() == View.GONE) {
                              getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                         }

                    }
                    else{
                         //user is signed out
                         //navController.navigate(R.id.AuthFragment);   //this will inflate the fragment in navhostfragment and bottom navigation will still be accessible so instead i will create new auth activity.
                         Intent Authintent = new Intent(MainActivity.this, AuthActivity.class);
                         startActivity(Authintent);
                         finish();
                    }
               }
          };

          firebaseAuth.addAuthStateListener(authStateListener);
          receivedItems = new ArrayList<>();
     }

     @Override
     protected void onStart() {
          super.onStart();
          //setting automatic handling of bottom navigation by navigation architecture
     }

     @Override
     protected void onResume() {
          super.onResume();
     }

     @Override
     public void sendToActivityfromHomeFragment(FruitItem item) {
          this.selectedFruitName = item.getFruitName();
          this.selectedFruitPrice = item.getFruitPrice();
          this.selectedFruitQty = item.getFruitQty();
          //Toast.makeText(this, "Added to received items", Toast.LENGTH_SHORT).show();
     }

     @Override
     public ArrayList<FruitItem> getFruitsFromMainToCartFragment() {
          return receivedItems;
     }

     //@Override
     //public void startPaymentInMain() {
     //     startPayment();
     //}


     @Override
     public CustomDialogFragment sendFruitInfoToDialog() {
          CustomDialogFragment customDialogFragment = new CustomDialogFragment();
          Bundle bundle = new Bundle();
          bundle.putString("fruitName", this.selectedFruitName);
          bundle.putString("fruitQty", this.selectedFruitQty);
          bundle.putString("fruitPrice", this.selectedFruitPrice);
          //Log.e("MainActivity", this.selectedFruitName);
          customDialogFragment.setArguments(bundle);
          return customDialogFragment;
     }

     @Override
     public void getItemFromDialogToMainActivity(String receivedFruitName, String receivedFruitQty, String receivedFruitPrice) {
          receivedItems.add(new FruitItem(receivedFruitName, receivedFruitQty, receivedFruitPrice));
     }

     @Override
     public void onPaymentSuccess(String s) {
          try {
               //Toast.makeText(this, "Payment Successful: " + s, Toast.LENGTH_SHORT).show();
               Toast.makeText(this, "Payment successful: " + s, Toast.LENGTH_SHORT).show();
          } catch (Exception e) {
               Log.e(TAG, "Exception in onPaymentSuccess!!!!!", e);
          }
     }

     @Override
     public void onPaymentError(int i, String s) {
          try {
               Toast.makeText(this, "payment failed: " + s, Toast.LENGTH_LONG).show();
               Log.e(TAG, "payment failed: " + s);
          }catch (Exception e){
               Log.e(TAG, "Exception in onPayment Error!!!!", e);
          }
     }

//     public void startPayment() {
////          checkout.setKeyID("<YOUR_KEY_ID>");
//          /*
//           * Instantiate Checkout
//           */
//          final Checkout checkout = new Checkout();
//          checkout.setKeyID("rzp_test_YqOc9S9XbjhpzM");
//
//          /*
//           * Set your logo here
//           */
//          //checkout.setImage(R.drawable.logo);
//
//          /*
//           * Reference to current activity
//           */
//          Activity activity = this;
//
//          /*
//           * Pass your payment options to the Razorpay Checkout as a JSONObject
//           */
//          try {
//               JSONObject options = new JSONObject();
//
//               /*
//                * Merchant Name
//                * eg: ACME Corp || HasGeek etc.
//                */
//               options.put("name", "Anant Kataria");
//
//               /*
//                * Description can be anything
//                * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
//                *     Invoice Payment
//                *     etc.
//                */
//               options.put("description", "checking api");
//               options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//               options.put("order_id", "order_9A33XWu170gUtm");
//               options.put("currency", "INR");
//               //options.put("key", "rzp_test_YqOc9S9XbjhpzM");
//
//               /*
//                * Amount is always passed in currency subunits
//                * Eg: "500" = INR 5.00
//                */
//               options.put("amount", "500");
//
//               checkout.open(activity, options);
//          } catch(Exception e) {
//               Log.e(TAG, "Error in starting Razorpay Checkout", e);
//               Toast.makeText(activity, "Error in payment : " + e.getMessage(), Toast.LENGTH_SHORT).show();
//               e.printStackTrace();
//          }
//     }

}
