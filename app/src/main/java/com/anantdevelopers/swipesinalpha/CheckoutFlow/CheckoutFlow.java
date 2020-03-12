package com.anantdevelopers.swipesinalpha.CheckoutFlow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.MainActivity;
import com.anantdevelopers.swipesinalpha.R;
import com.anantdevelopers.swipesinalpha.UserProfile.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.UnknownServiceException;
import java.util.ArrayList;

public class CheckoutFlow extends AppCompatActivity {

     private static final int REQUEST_CODE = 123;
     private static final String CASH_ON_DELIVERY = "cod";
     private static final String UPI_PAYMENT = "upiPayment";
     private static final String INITIAL_ORDER_STATUS = "ORDER PROCESSING";

     private Button payWithUpiButton, codButton, placeOrderButton;

     private ProgressBar progressBar;

     private TextView progressBarTextView;

     private FirebaseDatabase firebaseDatabase;
     private DatabaseReference databaseReference;
     private FirebaseAuth firebaseAuth;

     private String grandTotal, grandTotalPrice;  //grandTotal will be of form "Rs. 300", and grandTotalPrice will be of form "300"

     private ArrayList<FruitItem> fruits;

     private int paymentMethodNumber = 0; //should either be 1 or 2  1-indicates upi payment, 2-indicates cod payment

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_checkout_flow);

          Intent intent = getIntent();
          fruits = intent.getParcelableArrayListExtra("Fruits");
          grandTotal = intent.getStringExtra("grandTotal");

          grandTotalPrice = grandTotal.replace("[Rs.\\s]", "");

          payWithUpiButton = findViewById(R.id.upiPaymentButton);
          codButton = findViewById(R.id.codButton);
          placeOrderButton = findViewById(R.id.placeOrderButton);
          progressBar = findViewById(R.id.progressBar);
          progressBarTextView = findViewById(R.id.progressBarTextView);

          firebaseAuth = FirebaseAuth.getInstance();

          firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();

          payWithUpiButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    paymentMethodNumber = 1;
               }
          });

          codButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    paymentMethodNumber = 2;
               }
          });

          placeOrderButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    if(paymentMethodNumber == 0) Toast.makeText(CheckoutFlow.this, "Choose payment method first", Toast.LENGTH_SHORT).show();

                    if(paymentMethodNumber == 1) {
                         //start upi payment flow
                         Log.e("placeorderbutton", "payment method 1");
                         payUsingUPI();
                    }

                    if(paymentMethodNumber == 2) {
                         //start cod payment flow
//                         openDialog();
                         AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutFlow.this);
                         builder.setMessage("Do you want to continue using Cash on delivery?");
                         builder.setPositiveButton("Yes, Do it", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                   progressBar.setVisibility(View.VISIBLE);
                                   progressBarTextView.setVisibility(View.VISIBLE);
                                   getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); //touch input disabled.
                                   //now save the order in the database under "Orders"
                                   //save username, phone, roomno, wing, building and his order(fruits and quantity), and payment status
                                   //save feature will be common in both payment methods
                                   placeOrder(CASH_ON_DELIVERY);
                              }
                         });
                         builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                              }
                         });
                         AlertDialog dialog = builder.create();

                         dialog.show();
                    }
               }
          });
     }

//     private void openDialog() {
//          CODdialog dialog = new CODdialog();
//          dialog.show(getSupportFragmentManager(), "Cod dialog");
//     }

     private void payUsingUPI() {
          Uri uri = new Uri.Builder()
                  .scheme("upi")
                  .authority("pay")
                  .appendQueryParameter("pa", "katariaanant4@oksbi")
                  .appendQueryParameter("pn", "Merchant-Name")
                  //.appendQueryParameter("mc", "your-merchant-code")
                  //.appendQueryParameter("tr", "your-transaction-ref-id")
                  //.appendQueryParameter("tn", "your-transaction-note")
                  .appendQueryParameter("am", "1")
                  .appendQueryParameter("cu", "INR")
                  //.appendQueryParameter("url", "your-transaction-url")
                  .build();

          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setData(uri);
//          intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
//          startActivityForResult(intent, REQUEST_CODE);

          Intent chooser = Intent.createChooser(intent, "Pay with");
          startActivityForResult(chooser, REQUEST_CODE);

     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);
          if(requestCode == REQUEST_CODE){
               if (resultCode == RESULT_OK){
                    if(data == null){
                         Log.e("onActivityResult", "data intent is null");
                    }
                    else {

                         String status = data.getStringExtra("Status");
                         Log.e("onActivityResult", "data intent is = " + data.getStringExtra("Status"));
                         if(status.equals("SUCCESS")){
                              placeOrder(UPI_PAYMENT);
                         }
                         else if(status.equals("FAILURE")){
                              //payment is failed
                         }
                    }
               }
          }
     }

     private void placeOrder(final String paymentMethod) {
          FirebaseUser user = firebaseAuth.getCurrentUser();
          final String authPhoneNumber = user.getPhoneNumber();

          databaseReference.child("Users").child(authPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User userDetails = dataSnapshot.getValue(User.class);
                    Log.e("placeOrder", userDetails.getUserName());
                    CheckoutUser checkoutUser = new CheckoutUser();
                    checkoutUser.setFruits(fruits);
                    checkoutUser.setUser(userDetails);
                    checkoutUser.setPaymentMethod(paymentMethod);
                    checkoutUser.setStatus(INITIAL_ORDER_STATUS);
                    String key = databaseReference.child("Orders").child(authPhoneNumber).push().getKey();
                    checkoutUser.setFirebaseDatabaseKey(key);
                    databaseReference.child("Orders").child(authPhoneNumber).child(key).setValue(checkoutUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void aVoid) {
                              Toast.makeText(CheckoutFlow.this, "Order Sent Successfully!", Toast.LENGTH_SHORT).show();
                              //TODO : if order is successful then finish the checkout flow and empty the cart again
                              //so to achieve this, we will use --intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);--  which will clear the whole activity stack and we will start new main activity
                              Intent newMainActivity = new Intent(CheckoutFlow.this, MainActivity.class);
                              newMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                              startActivity(newMainActivity);

                              //TODO : and add the order in current orders
                              //to achieve this, we will work on PreviousOrdersFragment

                              //TODO : once the order is completed then put it in the previous orders in shared resources
                              //and remove it from the firebase
                         }
                    }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                              //TODO : implement this
                              //if order is a failure then close the progressbar and progressbarTetview and tell
                              //user to try again
                         }
                    });
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                    //TODO : implement this
               }
          });
     }


}
