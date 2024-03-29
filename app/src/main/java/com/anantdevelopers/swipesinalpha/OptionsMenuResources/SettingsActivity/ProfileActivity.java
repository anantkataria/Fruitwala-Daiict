package com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.anantdevelopers.swipesinalpha.Main.InternetConnectionViewModel;
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
     private ScrollView parentLayout2;
     private ProgressBar progressBar;

     private ArrayList<String> buildingSpinnerArrayList, wingSpinnerArrayList;

     private DatabaseReference databaseReference;

     private ValueEventListener listener;

     private InternetConnectionViewModel internetConnectionViewModel;
     private Snackbar noInternetSnackbar;

     private interface DatabaseInterface{
          void afterFetch();
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_profile);

          setTitle("PROFILE");
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          Intent intent = getIntent();
          authPhone = intent.getStringExtra("authPhone");

          user = new User();

          buildingSpinnerArrayList = new ArrayList<>();
          wingSpinnerArrayList = new ArrayList<>();

          FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();

          setIds();
          setSpinners();

          noInternetSnackbar = Snackbar.make(parentLayout, "NO INTERNET CONNECTION", Snackbar.LENGTH_INDEFINITE);
          noInternetSnackbar.setAction("RETRY", new MyRetryListener());
          noInternetSnackbar.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));

          internetConnectionViewModel = new ViewModelProvider(this).get(InternetConnectionViewModel.class);
          observeConnectivity();
          startCheckingNetworkConnectivity();

          getUserFromDatabase(new DatabaseInterface() {
               @Override
               public void afterFetch() {
                    parentLayout2.setVisibility(View.VISIBLE);
                    saveChangesButton.setVisibility(View.VISIBLE);
                    setTexts();
                    addTextChangeListenersToEditTexts(); //this method will ensure that save changes button is enabled when user changes anything in the edittext
                    addItemSelectedListenersToSpinners();//above method for spinners
                    progressBar.setVisibility(View.GONE);
//                    if (progressBar.getVisibility() == View.GONE) {
//                         getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    }
               }
          });


          //saveChangesButton.setVisibility(View.GONE);
          saveChangesButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                    String name = nameEditText.getText().toString();
                    String phone2 = phone2EditText.getText().toString();
                    String roomNo = roomNoEditText.getText().toString();

                    if(phone2.isEmpty()){
                         phone2 = "num2 not given";
                    }


                    if(name.isEmpty()){
                         Snackbar.make(parentLayout, "Enter Your Name", Snackbar.LENGTH_SHORT).show();
                    }
                    else if(name.matches("\\d+")){
                         Snackbar.make(parentLayout, "Enter Valid Name", Snackbar.LENGTH_SHORT).show();
                    }
                    else if(roomNo.isEmpty()){
                         Snackbar.make(parentLayout, "Enter Room-Number", Snackbar.LENGTH_SHORT).show();
                    }
                    else if(roomNo.matches("[0]+")){
                         Snackbar.make(parentLayout, "Enter Valid Room-Number", Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                         hideKeyboard(ProfileActivity.this);
                         progressBar.setVisibility(View.VISIBLE);
//                         if(progressBar.getVisibility() == View.VISIBLE){
//                              getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                         }

                         setNewValuesToUserClass(name, phone2, roomNo);
                         saveChangesInDatabase();
                    }
               }
          });
     }

     private void startCheckingNetworkConnectivity() {
          internetConnectionViewModel.startConnectivityCheck();
     }

     private void observeConnectivity() {
          internetConnectionViewModel.getIsConnected().observe(this, new Observer<Boolean>() {
               @Override
               public void onChanged(Boolean isConnected) {
                    if(!isConnected){
                         //not connected, show SnackBar of retry
                         //and retry is recreate the activity here
                         noInternetSnackbar.show();
                    }
                    else {
                         noInternetSnackbar.dismiss();
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

     private void getUserFromDatabase(final DatabaseInterface databaseInterface) {
          progressBar.setVisibility(View.VISIBLE);
          parentLayout2.setVisibility(View.INVISIBLE);
          saveChangesButton.setVisibility(View.INVISIBLE);
//          if(progressBar.getVisibility() == View.VISIBLE){
//               getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//          }

          listener = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);

                    databaseInterface.afterFetch();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                    parentLayout.setVisibility(View.INVISIBLE);
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    switch(databaseError.getCode()) {
                         case DatabaseError.DISCONNECTED :
                         case DatabaseError.NETWORK_ERROR :
                              Snackbar mySnackbar = Snackbar.make(parentLayout, "Check your INTERNET Connection", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar.setAction("RETRY", new MyRetryListener());
                              mySnackbar.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));
                              mySnackbar.show();
                              break;
                         case DatabaseError.OPERATION_FAILED :
                         case DatabaseError.UNKNOWN_ERROR:
                              Snackbar mySnackbar1 = Snackbar.make(parentLayout, "Unknown Error Occurred", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar1.setAction("RETRY", new MyRetryListener());
                              mySnackbar1.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));
                              mySnackbar1.show();
                              break;
                         case DatabaseError.PERMISSION_DENIED:
                              Snackbar mySnackbar2 = Snackbar.make(parentLayout, "Permission Denied", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar2.setAction("RETRY", new MyRetryListener());
                              mySnackbar2.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));
                              mySnackbar2.show();
                              break;
                         case DatabaseError.MAX_RETRIES:
                              Snackbar mySnackbar3 = Snackbar.make(parentLayout, "Max tries reached, Try again after some time", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar3.setAction("RETRY", new MyRetryListener());
                              mySnackbar3.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));
                              mySnackbar3.show();
                              break;
                         default:
                              Snackbar mySnackbar4 = Snackbar.make(parentLayout, "Error Occurred", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar4.setAction("RETRY", new MyRetryListener());
                              mySnackbar4.setActionTextColor(getResources().getColor(R.color.snackbarTextColor));
                              mySnackbar4.show();
                              break;
                    }
               }
          };

          databaseReference.child("Users").child(authPhone).addListenerForSingleValueEvent(listener);
     }

     private void setIds() {
          nameEditText = findViewById(R.id.name_edit_text);
          phone1EditText = findViewById(R.id.phone1_edit_text);
          phone2EditText = findViewById(R.id.phone2_edit_text);
          roomNoEditText = findViewById(R.id.room_no_edit_text);
          buildingSpinner = findViewById(R.id.building_selection_spinner);
          wingSpinner = findViewById(R.id.wing_letter_spinner);
          saveChangesButton = findViewById(R.id.save_changes_button);
          parentLayout = findViewById(R.id.parent_layout);
          parentLayout2 = findViewById(R.id.parent_layout_2);
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

     private void setNewValuesToUserClass(String name, String phone2, String roomNo) {
          user.setUserName(name);
          user.setPhoneNum2(phone2);
          user.setBuilding(Integer.toString(buildingSpinner.getSelectedItemPosition() + 1));
          user.setRoom(roomNo);
          user.setWing(wingSpinner.getSelectedItem().toString());
     }

     private void saveChangesInDatabase() {
          databaseReference.child("Users").child(user.getPhoneNum1()).setValue(user).addOnSuccessListener(
                  new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                            Snackbar.make(parentLayout2, "Saved Successfully", Snackbar.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
//                            if (progressBar.getVisibility() == View.GONE) {
//                                 getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                            }
                       }
                  }
          ).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                    Snackbar.make(parentLayout2, "Something went wrong, Try again", Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
//                    if (progressBar.getVisibility() == View.GONE) {
//                         getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    }
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

     private class MyRetryListener implements View.OnClickListener {
          @Override
          public void onClick(View v) {
               ProfileActivity.this.recreate();
          }
     }

     @Override
     protected void onDestroy() {
          super.onDestroy();
          if (listener != null) databaseReference.child("Users").child(authPhone).removeEventListener(listener);
     }
}
