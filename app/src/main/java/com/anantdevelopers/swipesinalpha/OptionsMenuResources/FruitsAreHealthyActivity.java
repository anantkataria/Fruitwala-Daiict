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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FruitsAreHealthyActivity extends AppCompatActivity {

     private WebView webView;
     private ProgressBar progressBar1, progressBarMain;
     private RelativeLayout parentLayout;

     private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
     private ValueEventListener listener;

     private interface AfterFetchFromDatabase{
          void afterFetch(String url);
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_fruits_are_healthy);

          setTitle("HEALTH TIPS");
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          webView = findViewById(R.id.web_view);
          progressBar1 = findViewById(R.id.progress_bar);
          progressBarMain = findViewById(R.id.progress_bar_main);
          parentLayout = findViewById(R.id.main_layout);

          getUrlFromDatabase(new AfterFetchFromDatabase() {
               @Override
               public void afterFetch(String url) {
                    progressBarMain.setVisibility(View.GONE);
                    parentLayout.setVisibility(View.VISIBLE);
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
                    parentLayout.setVisibility(View.INVISIBLE);

                    switch(databaseError.getCode()) {
                         case DatabaseError.DISCONNECTED :
                         case DatabaseError.NETWORK_ERROR :
                              Snackbar mySnackbar = Snackbar.make(parentLayout, "Check your INTERNET Connection", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar.setAction("RETRY", new MyRetryListener());
                              mySnackbar.show();
                              break;
                         case DatabaseError.OPERATION_FAILED :
                         case DatabaseError.UNKNOWN_ERROR:
                              Snackbar mySnackbar1 = Snackbar.make(parentLayout, "Unknown Error Occurred", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar1.setAction("RETRY", new MyRetryListener());
                              mySnackbar1.show();
                              break;
                         case DatabaseError.PERMISSION_DENIED:
                              Snackbar mySnackbar2 = Snackbar.make(parentLayout, "Permission Denied", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar2.setAction("RETRY", new MyRetryListener());
                              mySnackbar2.show();
                              break;
                         case DatabaseError.MAX_RETRIES:
                              Snackbar mySnackbar3 = Snackbar.make(parentLayout, "Max tries reached, Try again after some time", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar3.setAction("RETRY", new MyRetryListener());
                              mySnackbar3.show();
                              break;
                         default:
                              Snackbar mySnackbar4 = Snackbar.make(parentLayout, "Error Occurred", Snackbar.LENGTH_INDEFINITE);
                              mySnackbar4.setAction("RETRY", new MyRetryListener());
                              mySnackbar4.show();
                              break;
                    }
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
          if (listener != null) databaseReference.child("URLs").child("FruitsAreHealthy").removeEventListener(listener);
     }

     private class MyRetryListener implements View.OnClickListener {
          @Override
          public void onClick(View v) {
               FruitsAreHealthyActivity.this.recreate();
          }
     }
}
