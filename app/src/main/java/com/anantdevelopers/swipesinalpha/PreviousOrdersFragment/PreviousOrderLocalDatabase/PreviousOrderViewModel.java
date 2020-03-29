package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PreviousOrderViewModel extends AndroidViewModel {

     private PreviousOrderRepository repository;
     private LiveData<List<PreviousOrderEntity>> allPreviousOrders;

     public PreviousOrderViewModel(@NonNull Application application) {
          super(application);
          repository = new PreviousOrderRepository(application);
          allPreviousOrders = repository.getAllPreviousOrders();
     }

     public void insert(PreviousOrderEntity poe){
          repository.insert(poe);
     }

     public void delete(PreviousOrderEntity poe){
          repository.delete(poe);
     }

     public void deleteAllPreviousOrders(){
          repository.deleteAllNotes();
     }

     public LiveData<List<PreviousOrderEntity>> getAllPreviousOrders(){
          return repository.getAllPreviousOrders();
     }
}
