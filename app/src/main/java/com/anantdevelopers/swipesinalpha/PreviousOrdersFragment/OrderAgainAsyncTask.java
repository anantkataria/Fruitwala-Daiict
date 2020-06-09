package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem.FruitItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderAgainAsyncTask extends AsyncTask<Void, Integer, Void> {

     private MutableLiveData<List<FruitItem>> cartItems;
     private ArrayList<FruitItem> cartItemsList;
     private Map<String, Integer> actualPriceMap;
     private Map<String, ArrayList<String>> latestQtyMap;
     private Map<String, ArrayList<Integer>> latestPriceMap;

     public OrderAgainAsyncTask(MutableLiveData<List<FruitItem>> cartItems, Map<String, Integer> actualPriceMap, Map<String, ArrayList<String>> latestQtyMap, Map<String, ArrayList<Integer>> latestPriceMap) {
          Log.e("OrderAgainAsyncTask", "inside constructor");
          this.cartItems = cartItems;
          this.actualPriceMap = actualPriceMap;
          this.latestQtyMap = latestQtyMap;
          this.latestPriceMap = latestPriceMap;
          cartItemsList = new ArrayList<>();
     }

     @Override
     protected Void doInBackground(Void... voids) {
          Log.e("OrderAgainAsyncTask", "inside doInBackground");
          for(Map.Entry<String, Integer> entry : actualPriceMap.entrySet()){

               String fruitName = entry.getKey();
               int fruitPriceOld = entry.getValue();

               ArrayList<Integer> fruitPriceNew = latestPriceMap.get(fruitName);
               ArrayList<String> fruitQtyNew = latestQtyMap.get(fruitName);

               if (fruitPriceNew.contains(fruitPriceOld)){
                    int index = fruitPriceNew.indexOf(fruitPriceOld);
                    String qty = fruitQtyNew.get(index);
                    String price = "Rs. " + fruitPriceOld;
                    FruitItem fruitItem = new FruitItem(fruitName, qty, price);
                    cartItemsList.add(fruitItem);
               }else {
                    int index = -1;
                    int minimum_difference = Integer.MAX_VALUE;

                    for (int i=0; i<fruitPriceNew.size(); i++) {
                         int current_min = Math.abs(fruitPriceNew.get(i) - fruitPriceOld);
                         if (current_min < minimum_difference) {
                              minimum_difference = current_min;
                              index = i;
                         }
                    }

                    String qty = fruitQtyNew.get(index);
                    String price = "Rs. " + fruitPriceNew.get(index);
                    cartItemsList.add(new FruitItem(fruitName, qty, price));
               }
          }

          return null;
     }

     @Override
     protected void onPostExecute(Void aVoid) {
          super.onPostExecute(aVoid);
          cartItems.setValue(cartItemsList);
          Log.e("OrderAgainAsyncTask", "inside onPostExecute()");
          //take this arraylist and set it to the livedata
     }
}