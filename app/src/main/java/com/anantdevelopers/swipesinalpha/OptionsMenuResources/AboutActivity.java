package com.anantdevelopers.swipesinalpha.OptionsMenuResources;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity.FeedbackActivity;
import com.anantdevelopers.swipesinalpha.R;

public class AboutActivity extends AppCompatActivity {

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_about);

          Intent intent = getIntent();
          String userName = intent.getStringExtra("userName");

          TextView nameTextView = findViewById(R.id.name_text_view);
          nameTextView.setText(userName);

          setTitle("About");

          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          Button feedbackButton = findViewById(R.id.feedback_btn);
          feedbackButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    //send to feedback activity
                    Intent intent1 = new Intent(AboutActivity.this, FeedbackActivity.class);
                    startActivity(intent1);
               }
          });
     }

     @Override
     public boolean onSupportNavigateUp() {
          finish();
          return true;
     }
}
