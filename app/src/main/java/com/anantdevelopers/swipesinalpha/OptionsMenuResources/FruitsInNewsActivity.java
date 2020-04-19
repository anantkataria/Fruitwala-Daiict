package com.anantdevelopers.swipesinalpha.OptionsMenuResources;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.anantdevelopers.swipesinalpha.R;

public class FruitsInNewsActivity extends AppCompatActivity {

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_fruits_in_news);

          setTitle("Fruit NEWS");

          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     }

     @Override
     public boolean onSupportNavigateUp() {
          finish();
          return true;
     }
}
