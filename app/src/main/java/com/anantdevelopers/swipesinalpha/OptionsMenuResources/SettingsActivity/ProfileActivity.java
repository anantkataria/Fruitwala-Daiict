package com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.R;
import com.anantdevelopers.swipesinalpha.UserProfile.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements TextWatcher, AdapterView.OnItemSelectedListener {

     private User user;
     private String authPhone;

     private EditText nameEditText, phone1EditText, phone2EditText, roomNoEditText;
     private Spinner buildingSpinner, wingSpinner;
     private Button saveChangesButton;
     private RelativeLayout parentLayout;
     private ProgressBar progressBar;

     private ArrayList<String> buildingSpinnerArrayList, wingSpinnerArrayList;

     private FirebaseDatabase firebaseDatabase;
     private DatabaseReference databaseReference;

     private interface DatabaseInterface{
          void afterFetch();
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_profile);

          setTitle("Profile");
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          Intent intent = getIntent();
          authPhone = intent.getStringExtra("authPhone");

          user = new User();

          buildingSpinnerArrayList = new ArrayList<>();
          wingSpinnerArrayList = new ArrayList<>();

          firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();

          setIds();
          setSpinners();
          getUserFromDatabase(new DatabaseInterface() {
               @Override
               public void afterFetch() {
                    setTexts();
                    addTextChangeListenersToEditTexts(); //this method will ensure that save changes button is enabled when user changes anything in the edittext
                    addItemSelectedListenersToSpinners();//above method for spinners
                    progressBar.setVisibility(View.GONE);
                    if (progressBar.getVisibility() == View.GONE) {
                         getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
               }
          });


          //saveChangesButton.setVisibility(View.GONE);

          saveChangesButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    //TODO save the new user to the database and show progressbar during process
                    //TODO disable screen functioning, and on success or failure, show SnackBar!
                    //TODO on success close the profile activity
                    if(phone2EditText.getText().toString().length() < 10){
                         Snackbar.make(parentLayout, "Enter valid phone number", Snackbar.LENGTH_SHORT).show();
                    }else{
                         progressBar.setVisibility(View.VISIBLE);
                         if(progressBar.getVisibility() == View.VISIBLE){
                              getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                         }

                         setNewValuesToUserClass();
                         saveChangesInDatabase();
                    }
               }
          });
     }

     private void getUserFromDatabase(final DatabaseInterface databaseInterface) {
          progressBar.setVisibility(View.VISIBLE);
          if(progressBar.getVisibility() == View.VISIBLE){
               getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
          }

          databaseReference.child("Users").child(authPhone).addListenerForSingleValueEvent(
                  new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(User.class);

                            databaseInterface.afterFetch();
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                  }
          );
     }

     private void setIds() {
          nameEditText = findViewById(R.id.name_edit_text);
          phone1EditText = findViewById(R.id.phone1_edit_text);
          phone2EditText = findViewById(R.id.phone2_edit_text);
          roomNoEditText = findViewById(R.id.room_no_edit_text);
          buildingSpinner = findViewById(R.id.building_selection_spinner);
          wingSpinner = findViewById(R.id.wing_letter_spinner);
          saveChangesButton = findViewById(R.id.save_changes_button);
          parentLayout = findViewById(R.id.parent_view);
          progressBar = findViewById(R.id.progress_bar);
     }

     private void setTexts() {
          nameEditText.setText(user.getUserName());
          phone1EditText.setText(user.getPhoneNum1());
          phone2EditText.setText(user.getPhoneNum2());
          roomNoEditText.setText(user.getRoom());
          wingSpinner.setSelection(wingSpinnerArrayList.indexOf(user.getWing()));
          buildingSpinner.setSelection(buildingSpinnerArrayList.indexOf(Integer.parseInt(user.getBuilding())-1));
     }

     private void setSpinners() {
          buildingSpinnerArrayList.add("Hor-Men");
          buildingSpinnerArrayList.add("Hor-Men New");
          buildingSpinnerArrayList.add("Hor-Women");
          buildingSpinnerArrayList.add("Faculty-Block");

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

          buildingSpinner.setAdapter(adapterBuildingSpinner);
          wingSpinner.setAdapter(adapterWingSpinner);
     }

     private void addTextChangeListenersToEditTexts() {
          nameEditText.addTextChangedListener(this);
          phone1EditText.setEnabled(false);
          phone2EditText.addTextChangedListener(this);
          roomNoEditText.addTextChangedListener(this);
     }

     private void addItemSelectedListenersToSpinners() {
          buildingSpinner.setOnItemSelectedListener(this);
          wingSpinner.setOnItemSelectedListener(this);
     }

     private void setNewValuesToUserClass() {
          user.setUserName(nameEditText.getText().toString());
          user.setPhoneNum2(phone2EditText.getText().toString());
          user.setBuilding(Integer.toString(buildingSpinnerArrayList.indexOf(buildingSpinner.getSelectedItem())+1));
          user.setRoom(roomNoEditText.getText().toString());
          user.setWing(wingSpinner.getSelectedItem().toString());
     }

     private void saveChangesInDatabase() {
          databaseReference.child("Users").child(user.getPhoneNum1()).setValue(user).addOnSuccessListener(
                  new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                            Snackbar.make(parentLayout, "Saved Successfully", Snackbar.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            if (progressBar.getVisibility() == View.GONE) {
                                 getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                       }
                  }
          ).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                    Snackbar.make(parentLayout, "Something went wrong, Try again", Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    if (progressBar.getVisibility() == View.GONE) {
                         getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
               }
          });
     }

     @Override
     public boolean onSupportNavigateUp() {
          finish();
          return true;
     }

     @Override
     public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

     @Override
     public void onTextChanged(CharSequence s, int start, int before, int count) {
          saveChangesButton.setVisibility(View.VISIBLE);
     }

     @Override
     public void afterTextChanged(Editable s) { }

     @Override
     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          saveChangesButton.setVisibility(View.VISIBLE);
     }

     @Override
     public void onNothingSelected(AdapterView<?> parent) { }
}
