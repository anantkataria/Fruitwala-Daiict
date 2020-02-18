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
import android.widget.TextView;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.MainActivity;
import com.anantdevelopers.swipesinalpha.R;
import com.anantdevelopers.swipesinalpha.UserProfile.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

     private ProgressBar progressBar;
     private TextView otpSentTextView, autoVerificationTextView;
     private LinearLayout phoneNumberLayout;
     private EditText phoneNumberEditText, otpEditText;
     private Button sendOtpButton, verifyOtpButton;

     private String verificationId;

     private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

     private FirebaseAuth firebaseAuth;

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

          firebaseAuth = FirebaseAuth.getInstance();

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

                    //this is when firebase will try automatic sign in without user to type otp manually.
                    progressBar.setVisibility(View.VISIBLE);
                    sendOtpButton.setVisibility(View.INVISIBLE);
                    //autoVerificationTextView.setVisibility(View.VISIBLE);
                    phoneNumberLayout.setVisibility(View.INVISIBLE);
               }
          });

          verifyOtpButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    verifyOtpButton.setEnabled(false);
                    otpEditText.setEnabled(false);

                    otpSentTextView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    String otpCode = otpEditText.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpCode);
                    signInWithPhoneAuthCredential(credential);
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
                         // The SMS quota for the project has been exceeded
                         // ...
                         Toast.makeText(AuthActivity.this, "Your quota of getting otp is over because of too many requests", Toast.LENGTH_LONG).show();


                         // Toast.makeText(AuthActivity.this, "Try again later...", Toast.LENGTH_LONG).show();
                    }
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                         // Invalid request
                         // ...
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
               }

               @Override
               public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                    super.onCodeAutoRetrievalTimeOut(s);
                    autoVerificationTextView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    verifyOtpButton.setVisibility(View.VISIBLE);
                    otpEditText.setVisibility(View.VISIBLE);
                    otpSentTextView.setVisibility(View.VISIBLE);
               }
          };
     }

     private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
          firebaseAuth.signInWithCredential(credential)
                  .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                 // Sign in success, update UI with the signed-in user's information
                                 FirebaseUser user = task.getResult().getUser();
//                                 Intent mainIntent = new Intent(AuthActivity.this, MainActivity.class);
//                                 startActivity(mainIntent);
//                                 finish();
                                 //if(/*user node exists in the firebase?*/){
                                      //then take user directly to mainActivity and finish this activity
                                 //}else {
                                      //take user first to the profile and finish this activity
                                      Intent intent = new Intent(AuthActivity.this, UserProfile.class);
                                      intent.putExtra("authenticatedPhoneNumber", user.getPhoneNumber());
                                      startActivity(intent);
                                      finish();//finishing AuthActivity.java
                                 //}

                            } else {
                                 if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                      Toast.makeText(AuthActivity.this, "Entered OTP is wrong!", Toast.LENGTH_SHORT).show();
                                      otpEditText.setEnabled(true);
                                      verifyOtpButton.setEnabled(true);
                                 }
                                 if(task.getException() instanceof  FirebaseNetworkException){
                                      Toast.makeText(AuthActivity.this, "Check your Internet connection", Toast.LENGTH_SHORT).show();
                                 }
                                 progressBar.setVisibility(View.INVISIBLE);

                            }
                       }
                  });
     }
}
