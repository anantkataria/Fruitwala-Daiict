package com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.R;

public class SettingsActivity extends AppCompatActivity {

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_settings);

          Button profileBtn = findViewById(R.id.profile_btn);
          Button feedbackBtn = findViewById(R.id.feedback_btn);
          Button aboutBtn = findViewById(R.id.about_btn);
          Button logoutBtn = findViewById(R.id.logout_btn);

          setTitle("Settings");

          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          profileBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    Toast.makeText(SettingsActivity.this, "Profile Button clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                    startActivity(intent);
               }
          });

          feedbackBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    Toast.makeText(SettingsActivity.this, "Feedback Button clicked", Toast.LENGTH_SHORT).show();
               }
          });

          aboutBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    Toast.makeText(SettingsActivity.this, "About Button clicked", Toast.LENGTH_SHORT).show();
               }
          });

          logoutBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    Toast.makeText(SettingsActivity.this, "Logout Button clicked", Toast.LENGTH_SHORT).show();
               }
          });

     }

     @Override
     public boolean onSupportNavigateUp() {
          finish();
          return true;
     }
}
