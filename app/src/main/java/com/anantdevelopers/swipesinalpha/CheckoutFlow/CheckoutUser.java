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

     public CheckoutUser() {
     }

     public CheckoutUser(User user, ArrayList<FruitItem> fruits, String paymentMethod) {
          this.user = user;
          this.fruits = fruits;
          this.paymentMethod = paymentMethod;
     }

     protected CheckoutUser(Parcel in) {
          fruits = in.createTypedArrayList(FruitItem.CREATOR);
          paymentMethod = in.readString();
     }

     @Override
     public void writeToParcel(Parcel dest, int flags) {
          dest.writeTypedList(fruits);
          dest.writeString(paymentMethod);
     }

     public String getPaymentMethod() {
          return paymentMethod;
     }

     public void setPaymentMethod(String paymentMethod) {
          this.paymentMethod = paymentMethod;
     }

     @Override
     public int describeContents() {
          return 0;
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

     public User getUser() {
          return user;
     }

     public void setUser(User user) {
          this.user = user;
     }

     public ArrayList<FruitItem> getFruits() {
          return fruits;
     }

     public void setFruits(ArrayList<FruitItem> fruits) {
          this.fruits = fruits;
     }
}
