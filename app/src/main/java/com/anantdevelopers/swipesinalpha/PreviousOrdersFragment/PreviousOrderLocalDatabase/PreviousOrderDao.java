package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PreviousOrderDao {

     @Insert
     void insert(PreviousOrderEntity poe);

     @Delete
     void delete(PreviousOrderEntity poe);

     @Query("DELETE FROM previous_orders_table")
     void deleteAllOrders();

     @Query("SELECT * FROM previous_orders_table")
     LiveData<List<PreviousOrderEntity>> getAllOrders();

}
