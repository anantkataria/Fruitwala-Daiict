package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PreviousOrderViewModel extends AndroidViewModel {

     private PreviousOrderRepository repository;
     private LiveData<List<PreviousOrderEntity>> allPreviousOrdersOldestFirst;
     private LiveData<List<PreviousOrderEntity>> allPreviousOrdersNewestFirst;

     public PreviousOrderViewModel(@NonNull Application application) {
          super(application);
          repository = new PreviousOrderRepository(application);
          allPreviousOrdersOldestFirst = repository.getAllPreviousOrdersOldestFirst();
          allPreviousOrdersNewestFirst = repository.getAllPreviousOrdersNewestFirst();
     }

     public void insert(PreviousOrderEntity poe){
          repository.insert(poe);
     }

     public void update(PreviousOrderEntity poe){
          repository.update(poe);
     }

     public void delete(PreviousOrderEntity poe){
          repository.delete(poe);
     }

     public void deleteAllPreviousOrders(){
          repository.deleteAllOrders();
     }

     public void deleteAllNotStarredPreviousOrders() {
          repository.deleteAllNotStarredOrders();
     }

     public LiveData<List<PreviousOrderEntity>> getAllPreviousOrdersOldestFirst() {
          return allPreviousOrdersOldestFirst;
     }

     public LiveData<List<PreviousOrderEntity>> getAllPreviousOrdersNewestFirst() {
          return allPreviousOrdersNewestFirst;
     }
}
