package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.net.ContentHandler;

@Database(entities = {PreviousOrderEntity.class}, version = 1, exportSchema = false)
public abstract class PreviousOrderDatabase extends RoomDatabase {

     private static PreviousOrderDatabase instance;

     public abstract PreviousOrderDao previousOrderDao();

     static synchronized PreviousOrderDatabase getInstance(Context context){
          if(instance == null){
               instance = Room.databaseBuilder(context.getApplicationContext(), PreviousOrderDatabase.class,
                       "previous_orders_database")
                       .fallbackToDestructiveMigration()
                       .build();
          }
          return instance;
     }
}
