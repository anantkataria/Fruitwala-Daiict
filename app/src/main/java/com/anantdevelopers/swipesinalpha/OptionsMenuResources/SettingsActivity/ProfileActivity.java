package com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.anantdevelopers.swipesinalpha.R;

public class ProfileActivity extends AppCompatActivity {

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_profile);

          setTitle("Profile");

          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     }

     @Override
     public boolean onSupportNavigateUp() {
          finish();
          return true;
     }
}
