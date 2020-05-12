package com.anantdevelopers.swipesinalpha.OptionsMenuResources;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.anantdevelopers.swipesinalpha.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FruitsAreHealthyActivity extends AppCompatActivity {

     private WebView webView;
     private ProgressBar progressBar1, progressBarMain;
     private RelativeLayout mainLayout;

     private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
     private ValueEventListener listener;

     private interface AfterFetchFromDatabase{
          void afterFetch(String url);
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_fruits_are_healthy);

          setTitle("Health tips");
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          webView = findViewById(R.id.web_view);
          progressBar1 = findViewById(R.id.progress_bar);
          progressBarMain = findViewById(R.id.progress_bar_main);
          mainLayout = findViewById(R.id.main_layout);

          getUrlFromDatabase(new AfterFetchFromDatabase() {
               @Override
               public void afterFetch(String url) {
                    progressBarMain.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    loadUrlToWebview(url);
               }
          });

     }

     private void getUrlFromDatabase(final AfterFetchFromDatabase Interface){

          listener = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String url = dataSnapshot.getValue(String.class);
                    Interface.afterFetch(url);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
          };

          databaseReference.child("URLs").child("FruitsAreHealthy").addListenerForSingleValueEvent(listener);
     }

     private void loadUrlToWebview(String url){
          WebSettings webSettings = webView.getSettings();
          webSettings.setJavaScriptEnabled(true);
          webView.setWebViewClient(new WebViewClient());
          webView.loadUrl(url);
     }

     public class WebViewClient extends android.webkit.WebViewClient {
          @Override
          public void onPageStarted(WebView view, String url, Bitmap favicon) {
               super.onPageStarted(view, url, favicon);
          }

          @Override
          public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
               view.loadUrl(request.getUrl().toString());
               return super.shouldOverrideUrlLoading(view, request);
          }

          @Override
          public void onPageFinished(WebView view, String url) {
               super.onPageFinished(view, url);
               progressBar1.setVisibility(View.GONE);
          }
     }

     @Override
     public boolean onSupportNavigateUp() {
          finish();
          return true;
     }

     @Override
     public boolean onKeyDown(int keyCode, KeyEvent event) {
          // Check if the key event was the Back button and if there's history
          if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
               webView.goBack();
               return true;
          }
          // If it wasn't the Back key or there's no web page history, bubble up to the default
          // system behavior (probably exit the activity)
          return super.onKeyDown(keyCode, event);
     }

     @Override
     protected void onDestroy() {
          super.onDestroy();
          databaseReference.child("URLs").child("FruitsAreHealthy").removeEventListener(listener);
     }
}
