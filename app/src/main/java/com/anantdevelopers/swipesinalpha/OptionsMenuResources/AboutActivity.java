package com.anantdevelopers.swipesinalpha.OptionsMenuResources;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.anantdevelopers.swipesinalpha.R;

public class AboutActivity extends AppCompatActivity {

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_about);

          setTitle("About");

          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     }

     @Override
     public boolean onSupportNavigateUp() {
          finish();
          return true;
     }
}
