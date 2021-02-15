package com.anantdevelopers.swipesinalpha.OptionsMenuResources;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity.FeedbackActivity;
import com.anantdevelopers.swipesinalpha.R;
import com.google.firebase.auth.FirebaseAuth;

public class AboutActivity extends AppCompatActivity {

     private FirebaseAuth firebaseAuth;

     private String authPhone;
     private String userName = "User";

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_about);

          firebaseAuth = FirebaseAuth.getInstance();

          Intent intent = getIntent();
          userName = intent.getStringExtra("userName");
          //authPhone = intent.getStringExtra("authPhone");
          authPhone = firebaseAuth.getCurrentUser().getPhoneNumber();

          TextView nameTextView = findViewById(R.id.name_text_view);
          nameTextView.setText(userName);

          setTitle("ABOUT");

          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          Button feedbackButton = findViewById(R.id.feedback_btn);
          feedbackButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    //send to feedback activity
                    Intent intent1 = new Intent(AboutActivity.this, FeedbackActivity.class);
                    intent1.putExtra("authPhone", authPhone);
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
