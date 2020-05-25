package com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem;

import android.os.Parcel;
import android.os.Parcelable;

public class FruitItem implements Parcelable {
     private String fruitName;
     private String fruitQty;
     private String fruitPrice;

     FruitItem() {
     }

     public FruitItem(String name, String qty, String price){
          this.fruitName = name;
          this.fruitQty = qty;
          this.fruitPrice = price;
     }

     FruitItem(Parcel in) {
          fruitName = in.readString();
          fruitQty = in.readString();
          fruitPrice = in.readString();
     }

     public static final Creator<FruitItem> CREATOR = new Creator<FruitItem>() {
          @Override
          public FruitItem createFromParcel(Parcel in) {
               return new FruitItem(in);
          }

          @Override
          public FruitItem[] newArray(int size) {
               return new FruitItem[size];
          }
     };

     public String getFruitName() {
          return fruitName;
     }

     public void setFruitName(String fruitName) {
          this.fruitName = fruitName;
     }

     public String getFruitQty() {
          return fruitQty;
     }

     public void setFruitQty(String fruitQty) {
          this.fruitQty = fruitQty;
     }

     public String getFruitPrice() {
          return fruitPrice;
     }

     public void setFruitPrice(String fruitPrice) {
          this.fruitPrice = fruitPrice;
     }

     @Override
     public int describeContents() {
          return 0;
     }

     @Override
     public void writeToParcel(Parcel dest, int flags) {
          dest.writeString(fruitName);
          dest.writeString(fruitQty);
          dest.writeString(fruitPrice);
     }
}
