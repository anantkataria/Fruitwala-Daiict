package com.anantdevelopers.swipesinalpha.UserProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

     private TextView savingDataTextView;

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

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_user_profile);

          linearLayout = findViewById(R.id.linear_layout);
          nameEditText = findViewById(R.id.name_edit_text);
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

     }

     @Override
     protected void onStart() {
          super.onStart();

          buildingSpinnerArrayList = new ArrayList<>();
          wingSpinnerArrayList = new ArrayList<>();

          setSpinnerOptions(); //set up spinner

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
                         Toast.makeText(UserProfile.this, "Please Enter your name", Toast.LENGTH_SHORT).show();
                    }
                    else if (room.isEmpty()) {
                         Toast.makeText(UserProfile.this, "Please Enter your room/office number", Toast.LENGTH_SHORT).show();
                    }
                    else if (wingLetterSpinner.getSelectedItemPosition() == 0) {
                         Toast.makeText(UserProfile.this, "Please select your wing letter", Toast.LENGTH_SHORT).show();
                    }
                    else if (buildingLetterSpinner.getSelectedItemPosition() == 0) {
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
