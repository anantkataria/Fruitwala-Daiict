package com.anantdevelopers.swipesinalpha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.widget.Toast;

import com.anantdevelopers.swipesinalpha.CartFragment.CartFragment;
import com.anantdevelopers.swipesinalpha.HomeFragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, CartFragment.OnFragmentInteractionListener {

     private ArrayList<FruitItem> receivedItems;

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
          receivedItems.add(item);
          Toast.makeText(this, "Added to received items", Toast.LENGTH_SHORT).show();
     }

     @Override
     public ArrayList<FruitItem> getFruitsFromMainToCartFragment() {
          return receivedItems;
     }
}
