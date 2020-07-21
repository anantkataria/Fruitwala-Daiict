package com.anantdevelopers.swipesinalpha.UserProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.Main.MainActivity;
import com.anantdevelopers.swipesinalpha.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

     private RelativeLayout parentLayout;

     private TextView savingDataTextView;
     private TextView changeNumberTextView;

     private LinearLayout linearLayout;
     private EditText nameEditText;
     private EditText phoneNumber2;
     private EditText roomNumber;
     private Spinner wingLetterSpinner, buildingLetterSpinner;
     private Button saveAndContinueBtn;

     private String userName, phoneNum1, phoneNum2, wing, room, building;

     private ArrayList<String> buildingSpinnerArrayList, wingSpinnerArrayList;

     private ProgressBar progressBar;

     private DatabaseReference databaseReference;
     private FirebaseAuth firebaseAuth;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_user_profile);

          parentLayout = findViewById(R.id.parent_layout);
          linearLayout = findViewById(R.id.linear_layout);
          nameEditText = findViewById(R.id.name_edit_text);
          changeNumberTextView = findViewById(R.id.change_number_text_view);
          EditText authenticatedPhoneNumber = findViewById(R.id.phone1_edit_text);
          phoneNumber2 = findViewById(R.id.phone2_edit_text);
          roomNumber = findViewById(R.id.room_no_edit_text);
          wingLetterSpinner = findViewById(R.id.wing_letter_spinner);
          buildingLetterSpinner = findViewById(R.id.building_selection_spinner);
          saveAndContinueBtn = findViewById(R.id.save_and_continue_button);
          progressBar = findViewById(R.id.progress_bar);
          savingDataTextView = findViewById(R.id.saving_data_text_view);

          Intent intent = getIntent();
          phoneNum1 = intent.getStringExtra("authenticatedPhoneNumber");
          authenticatedPhoneNumber.setText(phoneNum1);

          FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();
          firebaseAuth = FirebaseAuth.getInstance();

     }

     @Override
     protected void onStart() {
          super.onStart();

          buildingSpinnerArrayList = new ArrayList<>();
          wingSpinnerArrayList = new ArrayList<>();

          setSpinnerOptions(); //set up spinner

          changeNumberTextView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    firebaseAuth.signOut();
               }
          });

          saveAndContinueBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    userName = nameEditText.getText().toString();
                    room = roomNumber.getText().toString();//use regex and stuff to make sure valid room number is entered
                    phoneNum2 = phoneNumber2.getText().toString();
                    wing = wingLetterSpinner.getSelectedItem().toString();
                    building =Integer.toString( buildingLetterSpinner.getSelectedItemPosition());
                    if (phoneNum2.isEmpty()) {
                         phoneNum2 = "num2 not given";
                    }
                    if (userName.isEmpty()) {
                         //Toast.makeText(UserProfile.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                         Snackbar.make(parentLayout, "Please Enter Your Name", Snackbar.LENGTH_SHORT).show();
                    }
                    else if(userName.matches("\\d+")){
//                         Toast.makeText(UserProfile.this, "Enter Valid Name", Toast.LENGTH_SHORT).show();
                         Snackbar.make(parentLayout, "Enter Valid Name", Snackbar.LENGTH_SHORT).show();
                    }
                    else if (room.isEmpty()) {
//                         Toast.makeText(UserProfile.this, "Please Enter Your Room/Office number", Toast.LENGTH_SHORT).show();
                         Snackbar.make(parentLayout, "Please Enter Your Room/Office Number", Snackbar.LENGTH_SHORT).show();
                    }
                    else if(room.matches("[0]+")){
//                         Toast.makeText(UserProfile.this, "Enter Valid Room-Number", Toast.LENGTH_SHORT).show();
                         Snackbar.make(parentLayout, "Enter Valid Room-Number", Snackbar.LENGTH_SHORT).show();
                    }
                    else if (wingLetterSpinner.getSelectedItemPosition() == 0) {
//                         Toast.makeText(UserProfile.this, "Please Select Your Wing Letter", Toast.LENGTH_SHORT).show();
                         Snackbar.make(parentLayout, "Please Select You Wing/Block Letter", Snackbar.LENGTH_SHORT).show();
                    }
                    else if (buildingLetterSpinner.getSelectedItemPosition() == 0) {
//                         Toast.makeText(UserProfile.this, "Please Select Your Building", Toast.LENGTH_SHORT).show();
                         Snackbar.make(parentLayout, "Please Select Your Building", Snackbar.LENGTH_SHORT).show();
                    }
                    else {
                         //now user has given every information needed so proceed to sending data at server and take user back to MainActivity.
                         hideKeyboard(UserProfile.this);
                         Map<String, Object> map = new HashMap<>();
                         map.put("userName", userName);
                         map.put("phoneNum1", phoneNum1);
                         map.put("phoneNum2", phoneNum2);
                         map.put("wing", wing);
                         map.put("room", room);
                         map.put("building", building);

                         progressBar.setVisibility(View.VISIBLE);
                         savingDataTextView.setVisibility(View.VISIBLE);
                         linearLayout.setVisibility(View.INVISIBLE);
                         saveAndContinueBtn.setEnabled(false);
                         databaseReference.child("Users").child(phoneNum1).updateChildren(map)
                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                           Toast.makeText(UserProfile.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                                           progressBar.setVisibility(View.GONE);
                                           savingDataTextView.setVisibility(View.GONE);
                                           Intent intent = new Intent(UserProfile.this, MainActivity.class);
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
                                           else {
                                                Toast.makeText(UserProfile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                           }
                                           progressBar.setVisibility(View.GONE);
                                           savingDataTextView.setVisibility(View.GONE);
                                           linearLayout.setVisibility(View.VISIBLE);
                                           saveAndContinueBtn.setEnabled(true);
                                           //todo consider other possible reasons for failure.
                                      }
                                 });

                    }
               }
          });


     }

     public static void hideKeyboard(Activity activity) {
          InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
          //Find the currently focused view, so we can grab the correct window token from it.
          View view = activity.getCurrentFocus();
          //If no view currently has focus, create a new one, just so we can grab a window token from it
          if (view == null) {
               view = new View(activity);
          }
          imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
     }

     private void setSpinnerOptions() {
          buildingSpinnerArrayList.add("---");
          buildingSpinnerArrayList.add("Hor-Men");
          buildingSpinnerArrayList.add("Hor-Men New");
          buildingSpinnerArrayList.add("Hor-Women");
          buildingSpinnerArrayList.add("Faculty-Block");

          wingSpinnerArrayList.add("---");
          wingSpinnerArrayList.add("A");
          wingSpinnerArrayList.add("B");
          wingSpinnerArrayList.add("C");
          wingSpinnerArrayList.add("D");
          wingSpinnerArrayList.add("E");
          wingSpinnerArrayList.add("F");
          wingSpinnerArrayList.add("G");
          wingSpinnerArrayList.add("H");
          wingSpinnerArrayList.add("FB-1");
          wingSpinnerArrayList.add("FB-2");
          wingSpinnerArrayList.add("FB-3");
          wingSpinnerArrayList.add("FB-4");

          ArrayAdapter adapterBuildingSpinner = new ArrayAdapter(this, R.layout.item_spinnet, buildingSpinnerArrayList);
          ArrayAdapter adapterWingSpinner = new ArrayAdapter(this, R.layout.item_spinnet, wingSpinnerArrayList);

          buildingLetterSpinner.setAdapter(adapterBuildingSpinner);
          wingLetterSpinner.setAdapter(adapterWingSpinner);

          buildingLetterSpinner.setSelection(0);
          wingLetterSpinner.setSelection(0);
     }
}
