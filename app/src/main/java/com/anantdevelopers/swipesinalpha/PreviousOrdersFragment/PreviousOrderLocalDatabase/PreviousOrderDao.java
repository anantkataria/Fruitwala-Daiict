package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
interface PreviousOrderDao {

     @Insert
     void insert(PreviousOrderEntity poe);

     @Query("SELECT * FROM previous_orders_table ORDER BY orderPlacedDate")
     LiveData<List<PreviousOrderEntity>> getAllOrdersOldestFirst();

     @Query("SELECT * FROM previous_orders_table ORDER BY orderPlacedDate DESC")
     LiveData<List<PreviousOrderEntity>> getAllOrdersNewestFirst();

     @Delete
     void delete(PreviousOrderEntity poe);

     @Query("DELETE FROM previous_orders_table")
     void deleteAllOrders();

     @Query("DELETE FROM previous_orders_table WHERE isStarred = 'false'")
     void deleteAllNotStarredOrders();

     @Update
     void updateOrder(PreviousOrderEntity poe);

}
