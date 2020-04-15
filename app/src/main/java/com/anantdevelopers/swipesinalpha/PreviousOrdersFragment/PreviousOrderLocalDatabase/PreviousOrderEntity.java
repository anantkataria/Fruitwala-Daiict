package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "previous_orders_table")
public class PreviousOrderEntity {

     @PrimaryKey(autoGenerate = true)
     private int id;

     private String orderFruitList;
     private String status;
     private String grandTotal;

     public PreviousOrderEntity(String orderFruitList, String status, String grandTotal){
          this.orderFruitList = orderFruitList;
          this.status = status;
          this.grandTotal = grandTotal;
     }

     public void setId(int id){
          this.id = id;
     }

     public int getId() {
          return id;
     }

     String getOrderFruitList() {
          return orderFruitList;
     }

     String getStatus() {
          return status;
     }

     String getGrandTotal() {
          return grandTotal;
     }
}
