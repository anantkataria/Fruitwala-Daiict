package com.anantdevelopers.swipesinalpha.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.TextKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.MainActivity;
import com.anantdevelopers.swipesinalpha.R;
import com.anantdevelopers.swipesinalpha.UserProfile.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

     private ProgressBar progressBar;
     private TextView otpSentTextView, autoVerificationTextView;
     private LinearLayout phoneNumberLayout;
     private EditText phoneNumberEditText, otpEditText;
     private Button sendOtpButton, verifyOtpButton;
     private RelativeLayout parentLayout;

     private String verificationId;

     private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

     private FirebaseAuth firebaseAuth;
     private DatabaseReference databaseReference;

     private ValueEventListener listener;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_auth);

          progressBar = findViewById(R.id.progressBar);
          otpSentTextView = findViewById(R.id.otpSentTextView);
          phoneNumberLayout = findViewById(R.id.phoneNumberLayout);
          phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
          otpEditText = findViewById(R.id.otpEditText);
          sendOtpButton = findViewById(R.id.sendotpButton);
          verifyOtpButton = findViewById(R.id.verifyOtpButton);
          autoVerificationTextView = findViewById(R.id.autoVerficationTextView);
          parentLayout = findViewById(R.id.parent_layout);

          firebaseAuth = FirebaseAuth.getInstance();

          FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();

          sendOtpButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    phoneNumberEditText.setEnabled(false);
                    sendOtpButton.setEnabled(false);

                    String phoneNumber = "+91"+phoneNumberEditText.getText().toString();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            0,
                            TimeUnit.MILLISECONDS,
                            AuthActivity.this,
                            mCallbacks
                    );

                    progressBar.setVisibility(View.VISIBLE);
                    sendOtpButton.setVisibility(View.INVISIBLE);
                    phoneNumberLayout.setVisibility(View.INVISIBLE);
               }
          });

          mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
               @Override
               public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential);
               }

               @Override
               public void onVerificationFailed(@NonNull FirebaseException e) {
                    if(e instanceof FirebaseNetworkException){
                         Toast.makeText(AuthActivity.this, "Check your Internet connection", Toast.LENGTH_SHORT).show();
                    }
                    if (e instanceof FirebaseTooManyRequestsException) {
                         Toast.makeText(AuthActivity.this, "Your quota of getting otp is over because of too many requests", Toast.LENGTH_LONG).show();
                    }
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                         Toast.makeText(AuthActivity.this, "Enter valid number", Toast.LENGTH_SHORT).show();
                         TextKeyListener.clear(phoneNumberEditText.getText());//user has typed number in the wrong way, so clear it
                    }
                    phoneNumberEditText.setEnabled(true);//and since user will type number again (in right format), we have to enable it(we disabled it in onclick of 'generate otp' button)
                    sendOtpButton.setEnabled(true);//we have to enable it again since we disabled it in onclick of 'generate otp' button
                    sendOtpButton.setVisibility(View.VISIBLE);
                    autoVerificationTextView.setVisibility(View.INVISIBLE);
                    phoneNumberLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
               }

               @Override
               public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationId = s;

                    progressBar.setVisibility(View.INVISIBLE);
                    verifyOtpButton.setVisibility(View.VISIBLE);
                    otpEditText.setVisibility(View.VISIBLE);
                    otpSentTextView.setVisibility(View.VISIBLE);
               }

               @Override
               public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                    super.onCodeAutoRetrievalTimeOut(s);
               }
          };

          verifyOtpButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    String otpCode = otpEditText.getText().toString();

                    if(otpCode.isEmpty()){
                         Toast.makeText(AuthActivity.this, "Please enter the OTP sent!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                         verifyOtpButton.setEnabled(false);
                         otpEditText.setEnabled(false);

                         otpSentTextView.setVisibility(View.INVISIBLE);
                         progressBar.setVisibility(View.VISIBLE);

                         PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpCode);
                         signInWithPhoneAuthCredential(credential);
                    }
               }
          });
     }

     private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
          firebaseAuth.signInWithCredential(credential)
                  .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                 // Sign in success, update UI with the signed-in user's information
                                 FirebaseUser user = task.getResult().getUser();
                                 final String authenticatedPhoneNumber = user.getPhoneNumber();

                                 listener = new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if(dataSnapshot.exists()){

                                                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                                intent.putExtra("isSavingSuccessful", true);
                                                startActivity(intent);
                                                finish();

                                           }
                                           else {
                                                //take user first to the profile and finish this activity
                                                Intent intent = new Intent(AuthActivity.this, UserProfile.class);
                                                intent.putExtra("authenticatedPhoneNumber", authenticatedPhoneNumber);
                                                startActivity(intent);
                                                finish();//finishing AuthActivity.java
                                           }
                                      }

                                      @Override
                                      public void onCancelled(@NonNull DatabaseError databaseError) {
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
                                 };

                                 databaseReference.child("Users").child(authenticatedPhoneNumber).addListenerForSingleValueEvent(listener);

                            } else {
                                 if(task.getException() instanceof  FirebaseNetworkException){
                                      Toast.makeText(AuthActivity.this, "Check your Internet connection", Toast.LENGTH_SHORT).show();
                                      phoneNumberEditText.setEnabled(true);//and since user will type number again (in right format), we have to enable it(we disabled it in onclick of 'generate otp' button)
                                      sendOtpButton.setEnabled(true);//we have to enable it again since we disabled it in onclick of 'generate otp' button
                                      sendOtpButton.setVisibility(View.VISIBLE);
                                      phoneNumberLayout.setVisibility(View.VISIBLE);
                                      progressBar.setVisibility(View.INVISIBLE);

                                 }
                                 if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                      Toast.makeText(AuthActivity.this, "Entered OTP is wrong!", Toast.LENGTH_SHORT).show();
                                      otpEditText.setEnabled(true);
                                      verifyOtpButton.setEnabled(true);
                                 }

                                 progressBar.setVisibility(View.INVISIBLE);

                            }
                       }
                  });
     }

     private class MyRetryListener implements View.OnClickListener {
          @Override
          public void onClick(View v) {
               AuthActivity.this.recreate();
          }
     }

     @Override
     protected void onDestroy() {
          super.onDestroy();

          String authenticatedPhoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();
          if (listener != null) databaseReference.child("Users").child(authenticatedPhoneNumber).removeEventListener(listener);
     }
}