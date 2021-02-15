package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.FruitItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderAgainViewModel extends ViewModel {

     private MutableLiveData<List<FruitItem>> cartItems = new MutableLiveData<>();
     private Map<String, Integer> actualPriceMap;
     private Map<String, ArrayList<String>> latestQtyMap;
     private Map<String, ArrayList<Integer>> latestPriceMap;

     public LiveData<List<FruitItem>> getCartItems() {
          return cartItems;
     }

     public void setActualPriceMap(Map<String, Integer> actualPriceMap) {
          this.actualPriceMap = actualPriceMap;
     }

     public void setLatestQtyMap(Map<String, ArrayList<String>> latestQtyMap) {
          this.latestQtyMap = latestQtyMap;
     }

     public void setLatestPriceMap(Map<String, ArrayList<Integer>> latestPriceMap) {
          this.latestPriceMap = latestPriceMap;
     }

     public void startCalculation() {
          OrderAgainAsyncTask asyncTask = new OrderAgainAsyncTask(cartItems, actualPriceMap, latestQtyMap, latestPriceMap);
          asyncTask.execute();
     }
}
