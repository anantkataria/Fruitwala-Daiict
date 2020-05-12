package com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.R;
import com.anantdevelopers.swipesinalpha.UserProfile.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

     private EditText concernEditText;
     private ImageView imageView1, imageView2, imageView3;
     private ScrollView scrollView;
     private ProgressBar progressBar;

     private static final int REQUEST_IMAGE1_GET = 1;
     private static final int REQUEST_IMAGE2_GET = 2;
     private static final int REQUEST_IMAGE3_GET = 3;

     private Uri uri1, uri2, uri3;

     private String authPhone;

     private DatabaseReference databaseReference;

     private User user;

     private interface fetchFromDatabase{
          void afterFetch();
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_feedback);

          setTitle("Feedback");

          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          Intent intent = getIntent();
          authPhone = intent.getStringExtra("authPhone");

          user = new User();

          concernEditText = findViewById(R.id.concern_edit_text);
          imageView1 = findViewById(R.id.image1_image_view);
          imageView2 = findViewById(R.id.image2_image_view);
          imageView3 = findViewById(R.id.image3_image_view);
          Button nextButton = findViewById(R.id.next_button);
          scrollView = findViewById(R.id.scroll_view);
          progressBar = findViewById(R.id.progress_bar);

          imageView1.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                            selectImage1();
                       }
                  });

          imageView2.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                            selectImage2();
                       }
                  });

          imageView3.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    selectImage3();
               }
          });

          imageView1.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                    if(uri1 != null){
                         uri1 = null;
                         imageView1.setImageResource(R.drawable.ic_add);
                         return true;
                    }
                    else {
                         return false;
                    }
               }
          });

          imageView2.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                    if(uri2 != null){
                         uri2 = null;
                         imageView2.setImageResource(R.drawable.ic_add);
                         return true;
                    }
                    else {
                         return false;
                    }
               }
          });

          imageView3.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                    if(uri3 != null){
                         uri3 = null;
                         imageView3.setImageResource(R.drawable.ic_add);
                         return true;
                    }
                    else {
                         return false;
                    }
               }
          });

          nextButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    final String concern = concernEditText.getText().toString();
                    if(concern.isEmpty()){
                         Snackbar.make(scrollView, "Enter your concern first", Snackbar.LENGTH_SHORT).show();
                    }else if(concern.length() <= 7){
                         Snackbar.make(scrollView, "Write more", Snackbar.LENGTH_SHORT).show();
                    }else {
                         progressBar.setVisibility(View.VISIBLE);
                         if(progressBar.getVisibility() == View.VISIBLE){
                              getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                         }

                         getUserFromDatabase(new fetchFromDatabase() {
                              @Override
                              public void afterFetch() {
                                   progressBar.setVisibility(View.GONE);
                                   if (progressBar.getVisibility() == View.GONE) {
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                   }

                                   String userdetails = getUserDetails();
                                   String[] emails = {"savage9ishere@gmail.com", "katariaanant4@gmail.com"};
                                   String subject = "FruitWala Customer Feedback";
                                   String body = concern + "\n\n" + userdetails;
                                   composeEmail(emails, subject, body, uri1, uri2, uri3);
                              }
                         });
                    }
               }
          });
     }

     private String getUserDetails() {
          String userDetails = "";

          String[] buildings = {"Hor-Men", "Hor-Men New", "Hor-Women", "Faculty Block"};

          userDetails += "\nname : " + user.getUserName();
          userDetails += "\nphone1 : " + user.getPhoneNum1();
          userDetails += "\nphone2 : " + user.getPhoneNum2();
          userDetails += "\nbuilding : " + buildings[Integer.parseInt(user.getBuilding())-1];
          userDetails += "\nWing : " + user.getWing();
          userDetails += "\nRoom : " + user.getRoom();

          return userDetails;
     }

     private void getUserFromDatabase(final fetchFromDatabase Interface) {
          databaseReference = FirebaseDatabase.getInstance().getReference();
          databaseReference.child("Users").child(authPhone).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);

                    Interface.afterFetch();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
          });
     }

     private void composeEmail(String[] emails, String subject, String body, Uri uri1, Uri uri2, Uri uri3) {

          ArrayList<Uri> uris = new ArrayList<>();
          if(uri1 != null) {
               uris.add(uri1);
          }
          if(uri2 != null) {
               uris.add(uri2);
          }
          if(uri3 != null) {
               uris.add(uri3);
          }

          Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
          //intent.setDataAndType(Uri.parse("mailto:"),"*/*");
          selectorIntent.setData(Uri.parse("mailto:"));
          //intent.setType("message/rfc822");
          //intent.setType("text/plain");

          final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
          emailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
          emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
          emailIntent.putExtra(Intent.EXTRA_TEXT, body);
          emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
          emailIntent.setSelector(selectorIntent);


          if(emailIntent.resolveActivity(getPackageManager()) != null){
               startActivity(emailIntent);
          }
          else {
               Snackbar.make(scrollView, "Sorry, We couldn't find any email client apps!", Snackbar.LENGTH_SHORT).show();
          }
     }

     private void selectImage3() {
          Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
          intent.setType("image/*");
          if(intent.resolveActivity(getPackageManager()) != null){
               startActivityForResult(intent, REQUEST_IMAGE3_GET);
          }else {
               Snackbar.make(scrollView, "No Application to perform this task!", Snackbar.LENGTH_SHORT).show();
          }
     }

     private void selectImage2() {
          Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
          intent.setType("image/*");
          if(intent.resolveActivity(getPackageManager()) != null){
               startActivityForResult(intent, REQUEST_IMAGE2_GET);
          }else {
               Snackbar.make(scrollView, "No Application to perform this task!", Snackbar.LENGTH_SHORT).show();
          }
     }

     private void selectImage1() {
          Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
          intent.setType("image/*");
          if(intent.resolveActivity(getPackageManager()) != null){
               startActivityForResult(intent, REQUEST_IMAGE1_GET);
          }else {
               Snackbar.make(scrollView, "No Application to perform this task!", Snackbar.LENGTH_SHORT).show();
          }
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);

          if(requestCode == REQUEST_IMAGE1_GET && resultCode==RESULT_OK){
               uri1 = data.getData();
               imageView1.setImageURI(uri1);
          }else if(requestCode == REQUEST_IMAGE2_GET && resultCode == RESULT_OK){
               uri2 = data.getData();
               imageView2.setImageURI(uri2);
          }else if(requestCode == REQUEST_IMAGE3_GET && resultCode==RESULT_OK){
               uri3 = data.getData();
               imageView3.setImageURI(uri3);
          }
     }

     @Override
     public boolean onSupportNavigateUp() {
          finish();
          return true;
     }
}
