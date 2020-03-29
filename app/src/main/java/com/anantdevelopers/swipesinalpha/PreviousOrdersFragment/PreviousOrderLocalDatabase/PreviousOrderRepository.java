package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;

import java.util.List;

public class PreviousOrderRepository {
     private PreviousOrderDao previousOrderDao;
     private LiveData<List<PreviousOrderEntity>> allPreviousOrders;

     PreviousOrderRepository(Application application){
          PreviousOrderDatabase database = PreviousOrderDatabase.getInstance(application);
          previousOrderDao = database.previousOrderDao();
          allPreviousOrders = previousOrderDao.getAllOrders();
     }

     public void insert(PreviousOrderEntity poe){
          new InsertPreviousOrderAsyncTask(previousOrderDao).execute(poe);
     }

     public void delete(PreviousOrderEntity poe){
          new DeletePreviousOrderAsyncTask(previousOrderDao).execute(poe);
     }

     public void deleteAllNotes() {
          new DeleteAllPreviousOrdersAsyncTask(previousOrderDao).execute();
     }

     public LiveData<List<PreviousOrderEntity>> getAllPreviousOrders() {
          return allPreviousOrders;
     }

     private static class InsertPreviousOrderAsyncTask extends AsyncTask<PreviousOrderEntity, Void, Void> {
          private PreviousOrderDao previousOrderDao;

          private InsertPreviousOrderAsyncTask(PreviousOrderDao previousOrderDao){
               this.previousOrderDao = previousOrderDao;
          }

          @Override
          protected Void doInBackground(PreviousOrderEntity... previousOrderEntities) {
               previousOrderDao.insert(previousOrderEntities[0]);
               return null;
          }
     }

     private static class DeletePreviousOrderAsyncTask extends AsyncTask<PreviousOrderEntity, Void, Void> {
          private PreviousOrderDao previousOrderDao;

          private DeletePreviousOrderAsyncTask(PreviousOrderDao previousOrderDao){
               this.previousOrderDao = previousOrderDao;
          }

          @Override
          protected Void doInBackground(PreviousOrderEntity... previousOrderEntities) {
               previousOrderDao.delete(previousOrderEntities[0]);
               return null;
          }
     }

     private static class DeleteAllPreviousOrdersAsyncTask extends AsyncTask<Void, Void, Void> {
          private PreviousOrderDao previousOrderDao;

          private DeleteAllPreviousOrdersAsyncTask(PreviousOrderDao previousOrderDao){
               this.previousOrderDao = previousOrderDao;
          }

          @Override
          protected Void doInBackground(Void... voids) {
               previousOrderDao.deleteAllOrders();
               return null;
          }
     }
}
