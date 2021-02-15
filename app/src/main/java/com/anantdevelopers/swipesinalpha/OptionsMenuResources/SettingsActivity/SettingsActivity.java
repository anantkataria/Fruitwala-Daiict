package com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.BuildConfig;
import com.anantdevelopers.swipesinalpha.Main.InternetConnectionViewModel;
import com.anantdevelopers.swipesinalpha.OptionsMenuResources.AboutActivity;
import com.anantdevelopers.swipesinalpha.OptionsMenuResources.FruitsAreHealthyActivity;
import com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase.PreviousOrderViewModel;
import com.anantdevelopers.swipesinalpha.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements LogoutDialog.logoutDialogListener, DeleteAccountDialog.deleteAccountDialogListener {

//     private User user;
     private String userName = "User";
     private String authPhone = "";

     private FirebaseAuth firebaseAuth;
     private DatabaseReference databaseReference;

     private RelativeLayout logoutLayout;
     private ListView listView;
     private ProgressBar progressBar;

     private ArrayList<ListItem> listItems = new ArrayList<>();

     private InternetConnectionViewModel internetConnectionViewModel;
     private boolean isItConnected;

     private PreviousOrderViewModel deletePreviousOrderViewModel;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_settings);

          logoutLayout = findViewById(R.id.logout_layout);
          progressBar = findViewById(R.id.progress_bar);

          firebaseAuth = FirebaseAuth.getInstance();
          authPhone = firebaseAuth.getCurrentUser().getPhoneNumber();
          FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
          databaseReference = firebaseDatabase.getReference();

          deletePreviousOrderViewModel = new ViewModelProvider(this).get(PreviousOrderViewModel.class);
          internetConnectionViewModel = new ViewModelProvider(this).get(InternetConnectionViewModel.class);
          observeConnectivity();
          startCheckingNetworkConnectivity();

          setTitle("SETTINGS");
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          listItems.add(new ListItem(R.drawable.ic_profile, "Profile", "Name, address, phone number..."));
          listItems.add(new ListItem(R.drawable.ic_feedback, "Feedback", "Write us your doubts, suggestions, complaints..."));
          listItems.add(new ListItem(R.drawable.ic_about, "About", "What are we..."));
          listItems.add(new ListItem(R.drawable.ic_fruits_are_healthy, "Fruits Are Healthy", "An apple a day, keeps doctor away..."));
          listItems.add(new ListItem(R.drawable.ic_share, "Share With Friends", "Let your friends know about the service..."));
          listItems.add(new ListItem(R.drawable.ic_delete_account, "Delete Account", "Completely erase my account..."));

          Intent intent = getIntent();
          userName = intent.getStringExtra("userName");
          //authPhone = intent.getStringExtra("authPhone");

          listView = findViewById(R.id.list_view);
          customListAdapter adapter = new customListAdapter(this, R.layout.list_item_settings, listItems);
          listView.setAdapter(adapter);

          listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch(position) {

                         case 0 :
                              Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                              //intent.putExtra("user", user);
                              intent.putExtra("authPhone", authPhone);
                              startActivity(intent);
                              break;
                         case 1 :
                              Intent intent1 = new Intent(SettingsActivity.this, FeedbackActivity.class);
                              intent1.putExtra("authPhone", authPhone);
                              startActivity(intent1);
                              break;
                         case 2 :
                              Intent intent2 = new Intent(SettingsActivity.this, AboutActivity.class);
                              intent2.putExtra("userName", userName);
                              intent2.putExtra("authPhone", authPhone);
                              startActivity(intent2);
                              break;
                         case 3 :
                              Intent intent3 = new Intent(SettingsActivity.this, FruitsAreHealthyActivity.class);
                              startActivity(intent3);
                              break;
                         case 4:
                              shareWithFriends();
                              break;
                         case 5:
                              if(!isItConnected){
                                   Toast.makeText(SettingsActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                              }else {


                                   DeleteAccountDialog deleteAccountDialog = new DeleteAccountDialog();
                                   deleteAccountDialog.show(getSupportFragmentManager(), "Account-Deletion process starting");
                              }
                              break;

                    }
               }
          });

          logoutLayout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    if(!isItConnected){
                         Toast.makeText(SettingsActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    else {
                         initiateLogoutProcess();
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
                    isItConnected = isConnected;
               }
          });
     }

     private void startAccountDeletionProcess() {
          // first remove orders, delivered or cancelled, then token, if any, and then delete entry from users
//          databaseReference.child("Delivered or Cancelled").child(authPhone).removeValue();
//          databaseReference.child("Orders").child(authPhone).removeValue();
          //tokens
//          databaseReference.child("Users").child(authPhone).removeValue();
//          firebaseAuth.signOut();

          listView.setVisibility(View.INVISIBLE);
          logoutLayout.setVisibility(View.INVISIBLE);
          progressBar.setVisibility(View.VISIBLE);

          deletePreviousOrderViewModel.deleteAllPreviousOrders();

          Map<String, Object> deletionMap = new HashMap<>();
          deletionMap.put("/Delivered or Cancelled/" + authPhone, null);
          deletionMap.put("/Orders/" + authPhone, null);
          deletionMap.put("/tokens/" + authPhone, null);
          deletionMap.put("/Users/" + authPhone, null);

          databaseReference.updateChildren(deletionMap).addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {
                    firebaseAuth.signOut();
                    Toast.makeText(SettingsActivity.this, "Operation Successful : Deletion complete", Toast.LENGTH_LONG).show();
               }
          }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                    listView.setVisibility(View.VISIBLE);
                    logoutLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SettingsActivity.this, "Error Occurred : " + e.getMessage(), Toast.LENGTH_SHORT).show();
               }
          });


     }

     private void shareWithFriends() {
          Intent shareIntent = new Intent(Intent.ACTION_SEND);
          shareIntent.setType("text/plain");
          shareIntent.putExtra(Intent.EXTRA_SUBJECT, "FruitWala");
          String shareMessage = "\nOrder fresh fruits online and get it delivered to your dorm number.\nTry using fruitwala to order fruits.\n\n";
          shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
          shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
          startActivity(Intent.createChooser(shareIntent, "Share with"));

     }

     private void initiateLogoutProcess() {
          LogoutDialog dialog = new LogoutDialog();
          dialog.show(getSupportFragmentManager(), "logging out dialog");
     }

     @Override
     public void onDialogPositiveClick() {
          firebaseAuth.signOut();
     }

     @Override
     public boolean onSupportNavigateUp() {
          finish();
          return true;
     }

     @Override
     public void onDeleteDialogPositiveClick() {
          startAccountDeletionProcess();
     }
}
