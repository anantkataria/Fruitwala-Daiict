package com.anantdevelopers.swipesinalpha.UserProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.MainActivity;
import com.anantdevelopers.swipesinalpha.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

     private TextView greetingName, savingDataTextView;

     private LinearLayout parentLayout;
     private EditText nameEditText;
     private EditText phoneNumber2;
     private EditText roomNumber;
     private Spinner wingLetter;
     private Button btnHorMen, btnHorMenNew, btnHorWomen, btnFB, saveAndContinueBtn;

     private String userName, phoneNum1, phoneNum2, wing, room, building;

     private String defaultSpinnerItem;

     private ProgressBar progressBar;

     private boolean isSavingSuccessful = false;

     private DatabaseReference databaseReference;
     private ValueEventListener listener;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_user_profile);

          parentLayout = findViewById(R.id.parent_layout);
          greetingName = findViewById(R.id.userNameTextView);
          nameEditText = findViewById(R.id.nameEditText);
          EditText authenticatedPhoneNumber = findViewById(R.id.authenticatedPhoneNumber);
          phoneNumber2 = findViewById(R.id.phoneNumber2);
          roomNumber = findViewById(R.id.roomNumber);
          wingLetter = findViewById(R.id.wingLetter);
          btnHorMen = findViewById(R.id.btnHorMen);
          btnHorMenNew = findViewById(R.id.btnHorMenNew);
          btnHorWomen = findViewById(R.id.btnHorWomen);
          btnFB = findViewById(R.id.btnFB);
          saveAndContinueBtn = findViewById(R.id.saveAndContinueBtn);
          progressBar = findViewById(R.id.progressBar);
          savingDataTextView = findViewById(R.id.savingDataTextView);

          Intent intent = getIntent();
          phoneNum1 = intent.getStringExtra("authenticatedPhoneNumber");
          authenticatedPhoneNumber.setText(phoneNum1);

          FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();

     }

     @Override
     protected void onStart() {
          super.onStart();

          building = "-1";//this is default value, but will add onclicklistener to decide which button is clicked
          setSpinnerOptions(); //set up spinner

          wing = defaultSpinnerItem;

          wingLetter.setOnItemSelectedListener(
                  new AdapterView.OnItemSelectedListener() {
                       @Override
                       public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            wing = parent.getItemAtPosition(position).toString();
                       }

                       @Override
                       public void onNothingSelected(AdapterView<?> parent) {

                       }
                  }
          );

          btnHorMen.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    building = "1";
               }
          });

          btnHorMenNew.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    building = "2";
               }
          });

          btnHorWomen.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    building = "3";
               }
          });

          btnFB.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    building = "4";
               }
          });



          saveAndContinueBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    userName = nameEditText.getText().toString();
                    room = roomNumber.getText().toString();//use regex and stuff to make sure valid room number is entered
                    phoneNum2 = phoneNumber2.getText().toString();
                    if (phoneNum2.isEmpty()) {
                         phoneNum2 = "num2 not given";
                    }
                    if (userName.isEmpty()) {
                         Toast.makeText(UserProfile.this, "Please Enter your name", Toast.LENGTH_SHORT).show();
                    }
                    else if (room.isEmpty()) {
                         Toast.makeText(UserProfile.this, "Please Enter your room/office number", Toast.LENGTH_SHORT).show();
                    }
                    else if (wing.equals(defaultSpinnerItem)) {
                         Toast.makeText(UserProfile.this, "Please select your wing letter", Toast.LENGTH_SHORT).show();
                    }
                    else if (building.equals("-1")) {
                         Toast.makeText(UserProfile.this, "Please select your building", Toast.LENGTH_SHORT).show();
                    }
                    else {
                         //now user has given every information needed so proceed to sending data at server and take user back to MainActivity.
                         Map<String, Object> map = new HashMap<>();
                         map.put("userName", userName);
                         map.put("phoneNum1", phoneNum1);
                         map.put("phoneNum2", phoneNum2);
                         map.put("wing", wing);
                         map.put("room", room);
                         map.put("building", building);

                         progressBar.setVisibility(View.VISIBLE);
                         savingDataTextView.setVisibility(View.VISIBLE);
                         databaseReference.child("Users").child(phoneNum1).updateChildren(map)
                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                           Toast.makeText(UserProfile.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                                           //take back to main activity
                                           isSavingSuccessful = true;
                                           progressBar.setVisibility(View.GONE);
                                           savingDataTextView.setVisibility(View.GONE);
                                           Intent intent = new Intent(UserProfile.this, MainActivity.class);
                                           intent.putExtra("isSavingSuccessful", isSavingSuccessful);
                                           startActivity(intent);
                                           finish();
                                      }
                                 })
                                 .addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                           if (e instanceof FirebaseNetworkException) {
                                                Toast.makeText(UserProfile.this, "check your Internet Connection", Toast.LENGTH_SHORT).show();
                                           }
                                      }
                                 });

                         listener = new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   if(dataSnapshot.exists()){
                                        databaseReference.child("halfWayExit").child(phoneNum1).setValue(null);
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

                         databaseReference.child("halfWayExit").child(phoneNum1).addListenerForSingleValueEvent(listener);
                    }
               }
          });


     }

     private void setSpinnerOptions() {
          ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(UserProfile.this, R.array.wingLetter, android.R.layout.simple_spinner_item);
          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          wingLetter.setAdapter(adapter);
          wingLetter.setSelection(0);
          defaultSpinnerItem = wingLetter.getSelectedItem().toString();
     }

     @Override
     protected void onDestroy() {
          super.onDestroy();
          if(!isSavingSuccessful){
               //saving has not taken place in database and user presses home and then clears the
               //app then firebase auth is successful but we haven't got user data. In that case, we first
               //call this activity instead of directly giving access to mainactivity

               //so we need to have some kind of flag which shows that user has done halfway exit.
               //for that we will store user number in halfway exit node in firebase database.

               //should rather user phone storage for seamless experience.
               Log.e("UserProfile.onDestroy", "isSavingSuccessful = " + isSavingSuccessful);
               databaseReference.child("halfWayExit").child(phoneNum1).setValue(true);
               databaseReference.child("halfWayExit").keepSynced(true);
               //now we will check particular number in database in mainactivity and if it is
               //halfway sign in then we send it to this activity.
          }
          if (listener != null) databaseReference.child("halfWayExit").child(phoneNum1).removeEventListener(listener);
     }

     private class MyRetryListener implements View.OnClickListener {
          @Override
          public void onClick(View v) {
               UserProfile.this.recreate();
          }
     }
}
