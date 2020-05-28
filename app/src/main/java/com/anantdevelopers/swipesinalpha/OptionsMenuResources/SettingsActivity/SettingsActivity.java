package com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.anantdevelopers.swipesinalpha.OptionsMenuResources.AboutActivity;
import com.anantdevelopers.swipesinalpha.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements LogoutDialog.logoutDialogListener {

//     private User user;
     private String userName = "User";
     private String authPhone = "";

     private FirebaseAuth firebaseAuth;

     private ListView listView;

     private ArrayList<ListItem> listItems = new ArrayList<>();

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_settings);

          listItems.add(new ListItem(R.drawable.ic_profile, "Profile", "name, address, phone number..."));
          listItems.add(new ListItem(R.drawable.ic_feedback, "Feedback", "write us your doubts, suggestions, complaints..."));
          listItems.add(new ListItem(R.drawable.ic_about, "About", "What are we..."));
          listItems.add(new ListItem(R.drawable.ic_logout, "Logout", "sign out..."));

          Intent intent = getIntent();
          userName = intent.getStringExtra("userName");
          authPhone = intent.getStringExtra("authPhone");

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
                              startActivity(intent2);
                              break;
                         case 3 :
                              initiateLogoutProcess();
                              break;

                    }
               }
          });

          setTitle("Settings");

          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          firebaseAuth = FirebaseAuth.getInstance();
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
}
