package com.anantdevelopers.swipesinalpha.Main;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InternetConnectionViewModel extends ViewModel {

     private MutableLiveData<Boolean> isConnected = new MutableLiveData<>(true);

     public LiveData<Boolean> getIsConnected() {
          return isConnected;
     }

     public void startConnectivityCheck() {
          InternetConnectionAsyncTask asyncTask = new InternetConnectionAsyncTask(isConnected);
          asyncTask.execute();
     }

}
