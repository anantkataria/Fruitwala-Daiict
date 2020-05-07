package com.anantdevelopers.swipesinalpha.StoreClosed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity.FeedbackActivity;
import com.anantdevelopers.swipesinalpha.R;

public class StoreClosedActivity extends AppCompatActivity {

     private TextView openingAgainTimeTextView;
     private Button feedbackButton;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_store_closed);

          Intent intent = getIntent();
          String openingAgainTimeString = intent.getStringExtra("openingAgainTimeString");

          openingAgainTimeTextView = findViewById(R.id.open_again_time_text_view);
          feedbackButton = findViewById(R.id.feedback_btn);

          openingAgainTimeTextView.setText(openingAgainTimeString);

          feedbackButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    Intent intent1 = new Intent(StoreClosedActivity.this, FeedbackActivity.class);
                    startActivity(intent1);
               }
          });
     }
}
