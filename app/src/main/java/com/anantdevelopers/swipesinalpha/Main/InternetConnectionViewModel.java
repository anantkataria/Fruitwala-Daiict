package com.anantdevelopers.swipesinalpha.Main;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

class InternetConnectionViewModel extends ViewModel {

     private MutableLiveData<Boolean> isConnected = new MutableLiveData<>(false);

     public LiveData<Boolean> getIsConnected() {
          return isConnected;
     }


}
