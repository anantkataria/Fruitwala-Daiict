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
     private String isStarred;
     private Long orderPlacedDate;
     private Long orderDeliveredOrCancelledDate;

     public PreviousOrderEntity(String orderFruitList, String status, String grandTotal, Long orderPlacedDate, Long orderDeliveredOrCancelledDate, String isStarred){
          this.orderFruitList = orderFruitList;
          this.status = status;
          this.grandTotal = grandTotal;
          this.isStarred = isStarred;
          this.orderPlacedDate = orderPlacedDate;
          this.orderDeliveredOrCancelledDate = orderDeliveredOrCancelledDate;
     }

     public void setId(int id){
          this.id = id;
     }

     public int getId() {
          return id;
     }

     public String getOrderFruitList() {
          return orderFruitList;
     }

     String getStatus() {
          return status;
     }

     String getGrandTotal() {
          return grandTotal;
     }

     Long getOrderPlacedDate() { return orderPlacedDate; }

     Long getOrderDeliveredOrCancelledDate() { return orderDeliveredOrCancelledDate; }

     public String getIsStarred() {
          return isStarred;
     }

     public void setIsStarred(String val) {
          this.isStarred = val;
     }
}
