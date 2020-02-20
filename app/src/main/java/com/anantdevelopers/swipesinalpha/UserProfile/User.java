package com.anantdevelopers.swipesinalpha.UserProfile;

public class User {
     private String building;
     private String phoneNum1;
     private String phoneNum2;
     private String room;
     private String userName;
     private String wing;

     public User(String building, String phoneNum1, String phoneNum2, String room, String userName, String wing) {
          this.building = building;
          this.phoneNum1 = phoneNum1;
          this.phoneNum2 = phoneNum2;
          this.room = room;
          this.userName = userName;
          this.wing = wing;
     }

     public User(){

     }

     public String getBuilding() {
          return building;
     }

     public void setBuilding(String building) {
          this.building = building;
     }

     public String getPhoneNum1() {
          return phoneNum1;
     }

     public void setPhoneNum1(String phoneNum1) {
          this.phoneNum1 = phoneNum1;
     }

     public String getPhoneNum2() {
          return phoneNum2;
     }

     public void setPhoneNum2(String phoneNum2) {
          this.phoneNum2 = phoneNum2;
     }

     public String getRoom() {
          return room;
     }

     public void setRoom(String room) {
          this.room = room;
     }

     public String getUserName() {
          return userName;
     }

     public void setUserName(String userName) {
          this.userName = userName;
     }

     public String getWing() {
          return wing;
     }

     public void setWing(String wing) {
          this.wing = wing;
     }
}
