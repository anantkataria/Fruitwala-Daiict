package com.anantdevelopers.swipesinalpha.HomeFragment.FruitItem;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class FruitItem2 implements Parcelable {
     private String fruitName;
     private ArrayList<String> quantities;
     private ArrayList<String> prices;
     private String availability;
     private int image_resource;

     public FruitItem2(String fruitName, String availability, ArrayList<String> quantities, ArrayList<String> prices) {
          this.fruitName = fruitName;
          this.availability = availability;
          this.quantities = quantities;
          this.prices = prices;
     }

     public FruitItem2() {}

     public void addToQuantitiesAndPrices(String qty, String price){
          quantities.add(qty);
          prices.add(price);
     }

     public void removeFromQuantitiesAndPrices(int position){
          quantities.remove(position);
          prices.remove(position);
     }

     private FruitItem2(Parcel in) {
          fruitName = in.readString();
          availability = in.readString();
          quantities = in.createStringArrayList();
          prices = in.createStringArrayList();
          image_resource = in.readInt();
     }

     public static final Creator<FruitItem2> CREATOR = new Creator<FruitItem2>() {
          @Override
          public FruitItem2 createFromParcel(Parcel in) {
               return new FruitItem2(in);
          }

          @Override
          public FruitItem2[] newArray(int size) {
               return new FruitItem2[size];
          }
     };

     public String getFruitName() {
          return fruitName;
     }

     public void setFruitName(String fruitName) {
          this.fruitName = fruitName;
     }

     public ArrayList<String> getQuantities() {
          return quantities;
     }

     public void setQuantities(ArrayList<String> quantities) {
          this.quantities = quantities;
     }

     public ArrayList<String> getPrices() {
          return prices;
     }

     public void setPrices(ArrayList<String> prices) {
          this.prices = prices;
     }

     public String getAvailability() {
          return availability;
     }

     public void setAvailability(String availability) {
          this.availability = availability;
     }

     public int getImage_resource() {
          return image_resource;
     }

     public void setImage_resource(int image_resource) {
          this.image_resource = image_resource;
     }

     @Override
     public int describeContents() {
          return 0;
     }

     @Override
     public void writeToParcel(Parcel dest, int flags) {
          dest.writeString(fruitName);
          dest.writeString(availability);
          dest.writeStringList(quantities);
          dest.writeStringList(prices);
          dest.writeInt(image_resource);
     }
}
