package com.anantdevelopers.swipesinalpha.CheckoutFlow;

import android.os.Parcel;
import android.os.Parcelable;

import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.UserProfile.User;

import java.util.ArrayList;

public class CheckoutUser implements Parcelable {
     private User user;
     private ArrayList<FruitItem> fruits;
     private String paymentMethod;
     private String status; //status can be 'ORDER PROCESSING', 'ORDER CANCELLED', 'ORDER DELIVERED', 'ORDER ON THE WAY'
     private String firebaseDatabaseKey;
     private String orderPlacedDate;
     private String orderDeliveredOrCancelledDate = "N/A";

     CheckoutUser() {
     }

     public CheckoutUser(User user, ArrayList<FruitItem> fruits, String paymentMethod, String status, String firebaseDatabaseKey, String orderPlacedDate) {
          this.user = user;
          this.fruits = fruits;
          this.paymentMethod = paymentMethod;
          this.status = status;
          this.firebaseDatabaseKey = firebaseDatabaseKey;
          this.orderPlacedDate = orderPlacedDate;
     }

     private CheckoutUser(Parcel in) {
          user = in.readParcelable(User.class.getClassLoader());
          fruits = in.createTypedArrayList(FruitItem.CREATOR);
          paymentMethod = in.readString();
          status = in.readString();
          firebaseDatabaseKey = in.readString();
          orderPlacedDate = in.readString();
          orderDeliveredOrCancelledDate = in.readString();
     }

     public static final Creator<CheckoutUser> CREATOR = new Creator<CheckoutUser>() {
          @Override
          public CheckoutUser createFromParcel(Parcel in) {
               return new CheckoutUser(in);
          }

          @Override
          public CheckoutUser[] newArray(int size) {
               return new CheckoutUser[size];
          }
     };

     public String getFirebaseDatabaseKey() {
          return firebaseDatabaseKey;
     }

     void setFirebaseDatabaseKey(String firebaseDatabaseKey) {
          this.firebaseDatabaseKey = firebaseDatabaseKey;
     }

     public String getStatus() {
          return status;
     }

     void setStatus(String status) {
          this.status = status;
     }

     public String getPaymentMethod() {
          return paymentMethod;
     }

     void setPaymentMethod(String paymentMethod) {
          this.paymentMethod = paymentMethod;
     }

     public User getUser() {
          return user;
     }

     void setUser(User user) {
          this.user = user;
     }

     public ArrayList<FruitItem> getFruits() {
          return fruits;
     }

     void setFruits(ArrayList<FruitItem> fruits) {
          this.fruits = fruits;
     }

     public String getOrderPlacedDate() {
          return orderPlacedDate;
     }

     public void setOrderPlacedDate(String orderPlacedDate) {
          this.orderPlacedDate = orderPlacedDate;
     }

     public String getOrderDeliveredOrCancelledDate() {
          return orderDeliveredOrCancelledDate;
     }

     public void setOrderDeliveredOrCancelledDate(String orderDeliveredOrCancelledDate) {
          this.orderDeliveredOrCancelledDate = orderDeliveredOrCancelledDate;
     }

     @Override
     public int describeContents() {
          return 0;
     }

     @Override
     public void writeToParcel(Parcel dest, int flags) {
          dest.writeParcelable(user, flags);
          dest.writeTypedList(fruits);
          dest.writeString(paymentMethod);
          dest.writeString(status);
          dest.writeString(firebaseDatabaseKey);
          dest.writeString(orderPlacedDate);
          dest.writeString(orderDeliveredOrCancelledDate);
     }
}
