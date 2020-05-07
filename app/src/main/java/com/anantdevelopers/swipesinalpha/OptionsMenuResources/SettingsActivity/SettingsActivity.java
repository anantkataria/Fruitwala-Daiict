package com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.anantdevelopers.swipesinalpha.OptionsMenuResources.AboutActivity;
import com.anantdevelopers.swipesinalpha.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

//     private User user;
     private String userName = "User";
     private String authPhone = "";

     private FirebaseAuth firebaseAuth;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_settings);

          Intent intent = getIntent();
          userName = intent.getStringExtra("userName");
          authPhone = intent.getStringExtra("authPhone");

          Button profileBtn = findViewById(R.id.profile_btn);
          Button feedbackBtn = findViewById(R.id.feedback_btn);
          Button aboutBtn = findViewById(R.id.about_btn);
          Button logoutBtn = findViewById(R.id.logout_btn);

          setTitle("Settings");

          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          firebaseAuth = FirebaseAuth.getInstance();

          profileBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                    //intent.putExtra("user", user);
                    intent.putExtra("authPhone", authPhone);
                    startActivity(intent);
               }
          });

          feedbackBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    Intent intent1 = new Intent(SettingsActivity.this, FeedbackActivity.class);
                    intent1.putExtra("authPhone", authPhone);
                    startActivity(intent1);
               }
          });

          aboutBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    Intent intent1 = new Intent(SettingsActivity.this, AboutActivity.class);
                    intent1.putExtra("userName", userName);
                    startActivity(intent1);
               }
          });

          logoutBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    initiateLogoutProcess();
               }
          });

     }

     private void initiateLogoutProcess() {
          AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
          builder.setMessage("Are you sure you want to logout?");
          builder.setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    firebaseAuth.signOut();
               }
          });
          builder.setNegativeButton("No, I want to buy more", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
               }
          });

          AlertDialog dialog = builder.create();

          dialog.show();
     }

     @Override
     public boolean onSupportNavigateUp() {
          finish();
          return true;
     }
}
