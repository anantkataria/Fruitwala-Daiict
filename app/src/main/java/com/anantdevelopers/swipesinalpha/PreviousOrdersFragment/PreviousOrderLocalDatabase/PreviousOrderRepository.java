package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

class PreviousOrderRepository {
     private PreviousOrderDao previousOrderDao;
//     private LiveData<List<PreviousOrderEntity>> allPreviousOrders;
     private LiveData<List<PreviousOrderEntity>> allPreviousOrdersOldestFirst;
     private LiveData<List<PreviousOrderEntity>> allPreviousOrdersNewestFirst;

     PreviousOrderRepository(Application application){
          PreviousOrderDatabase database = PreviousOrderDatabase.getInstance(application);
          previousOrderDao = database.previousOrderDao();
//          allPreviousOrders = previousOrderDao.getAllOrders();
          allPreviousOrdersOldestFirst = previousOrderDao.getAllOrdersOldestFirst();
          allPreviousOrdersNewestFirst = previousOrderDao.getAllOrdersNewestFirst();
     }

     void insert(PreviousOrderEntity poe){
          new InsertPreviousOrderAsyncTask(previousOrderDao).execute(poe);
     }

     void delete(PreviousOrderEntity poe){
          new DeletePreviousOrderAsyncTask(previousOrderDao).execute(poe);
     }

     void update(PreviousOrderEntity poe){
          new UpdatePreviousOrderAsyncTask(previousOrderDao).execute(poe);
     }

     void deleteAllNotes() {
          new DeleteAllPreviousOrdersAsyncTask(previousOrderDao).execute();
     }

//     LiveData<List<PreviousOrderEntity>> getAllPreviousOrders() {
//          return allPreviousOrders;
//     }

     LiveData<List<PreviousOrderEntity>> getAllPreviousOrdersOldestFirst() {
          return allPreviousOrdersOldestFirst;
     }

     LiveData<List<PreviousOrderEntity>> getAllPreviousOrdersNewestFirst() {
          return allPreviousOrdersNewestFirst;
     }

     private static class UpdatePreviousOrderAsyncTask extends AsyncTask<PreviousOrderEntity, Void, Void> {
          private PreviousOrderDao previousOrderDao;

          private UpdatePreviousOrderAsyncTask(PreviousOrderDao previousOrderDao){
               this.previousOrderDao = previousOrderDao;
          }


          @Override
          protected Void doInBackground(PreviousOrderEntity... previousOrderEntities) {
               previousOrderDao.updateOrder(previousOrderEntities[0]);
               return null;
          }
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
