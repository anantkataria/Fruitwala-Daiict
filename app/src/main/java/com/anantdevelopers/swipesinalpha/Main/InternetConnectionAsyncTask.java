package com.anantdevelopers.swipesinalpha.Main;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

class InternetConnectionAsyncTask extends AsyncTask<Void, Void, Boolean> {

     private MutableLiveData<Boolean> isConnected;

     public InternetConnectionAsyncTask(MutableLiveData<Boolean> isConnected){
          this.isConnected = isConnected;
     }

     @Override
     protected Boolean doInBackground(Void... voids) {
          try {
               Socket sock = new Socket();
               sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
               sock.close();
               return true;
          } catch (IOException e) {
               return false;
          }
     }

     @Override
     protected void onPostExecute(Boolean aBoolean) {
          super.onPostExecute(aBoolean);
          isConnected.setValue(aBoolean);
     }
}
