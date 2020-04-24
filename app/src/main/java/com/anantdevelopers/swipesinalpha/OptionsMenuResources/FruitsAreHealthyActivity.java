package com.anantdevelopers.swipesinalpha.OptionsMenuResources;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.anantdevelopers.swipesinalpha.R;

public class FruitsAreHealthyActivity extends AppCompatActivity {

     private WebView webView;
     private ProgressBar progressBar;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_fruits_are_healthy);

          webView = findViewById(R.id.web_view);
          progressBar = findViewById(R.id.progress_bar);

          WebSettings webSettings = webView.getSettings();
          webSettings.setJavaScriptEnabled(true);
          webView.setWebViewClient(new WebViewClient());
          webView.loadUrl("https://www.betterhealth.vic.gov.au/health/HealthyLiving/fruit-and-vegetables");



          setTitle("Health tips");

          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
               progressBar.setVisibility(View.GONE);
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
}
