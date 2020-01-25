package com.anantdevelopers.swipesinalpha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.CartFragment.CartFragment;
import com.anantdevelopers.swipesinalpha.CustomDialogFragment.CustomDialogFragment;
import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.HomeFragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, CartFragment.OnFragmentInteractionListener, CustomDialogFragment.OnFragmentInteractionListener {

     private ArrayList<FruitItem> receivedItems;
     private String selectedFruitName, selectedFruitQty, selectedFruitPrice;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);

          //setting automatic handling of bottom navigation by navigation architecture
          BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNaigationView);

          AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                  R.id.HomeFragment, R.id.CartFragment, R.id.PreviousOrdersFragment
          ).build();

          NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

          NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

          NavigationUI.setupWithNavController(bottomNavigationView, navController);

          receivedItems = new ArrayList<>();
     }

     @Override
     public void sendToActivityfromHomeFragment(FruitItem item) {
          this.selectedFruitName = item.getFruitName();
          this.selectedFruitPrice = item.getFruitPrice();
          this.selectedFruitQty = item.getFruitQty();
          //Toast.makeText(this, "Added to received items", Toast.LENGTH_SHORT).show();
     }

     @Override
     public ArrayList<FruitItem> getFruitsFromMainToCartFragment() {
          return receivedItems;
     }

     @Override
     public CustomDialogFragment sendFruitInfoToDialog() {
          CustomDialogFragment customDialogFragment = new CustomDialogFragment();
          Bundle bundle = new Bundle();
          bundle.putString("fruitName", this.selectedFruitName);
          bundle.putString("fruitQty", this.selectedFruitQty);
          bundle.putString("fruitPrice", this.selectedFruitPrice);
          //Log.e("MainActivity", this.selectedFruitName);
          customDialogFragment.setArguments(bundle);
          return customDialogFragment;
     }

     @Override
     public void getItemFromDialogToMainActivity(String receivedFruitName, String receivedFruitQty, String receivedFruitPrice) {
          receivedItems.add(new FruitItem(receivedFruitName, receivedFruitQty, receivedFruitPrice));
     }
}
