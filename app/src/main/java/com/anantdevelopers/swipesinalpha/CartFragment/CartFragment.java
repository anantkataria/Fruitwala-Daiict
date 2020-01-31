package com.anantdevelopers.swipesinalpha.CartFragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.R;
import com.anantdevelopers.swipesinalpha.FruitItem.RecyclerViewAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

     private ArrayList<FruitItem> fruits;

     private RecyclerView recyclerView;
     private RecyclerViewAdapter adapter;

     private OnFragmentInteractionListener mListener;

     public CartFragment() {
          // Required empty public constructor
     }

     @Override
     public void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          FruitItem fruitItem = fruits.get(0);
//          Log.e("CartFragment", "fruit = " + fruitItem.getFruitName()
//          + ", qty = " + fruitItem.getFruitQty() + ", price = " + fruitItem.getFruitPrice());
          adapter = new RecyclerViewAdapter(getContext(), fruits);
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
          View v = inflater.inflate(R.layout.fragment_cart, container, false);

          recyclerView = v.findViewById(R.id.recycler_view_cart);
          recyclerView.setAdapter(adapter);
          new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
          recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

          return v;
     }

     ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
          @Override
          public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
               return false;
          }

          @Override
          public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
               fruits.remove(viewHolder.getAdapterPosition());
               adapter.notifyDataSetChanged();
          }
     };

     @Override
     public void onAttach(Context context) {
          super.onAttach(context);

          if (context instanceof OnFragmentInteractionListener) {
               mListener = (OnFragmentInteractionListener) context;
          } else {
               throw new RuntimeException(context.toString()
                       + " must implement CartFragment.OnFragmentInteractionListener");
          }

          fruits = mListener.getFruitsFromMainToCartFragment();
     }

     @Override
     public void onDetach() {
          super.onDetach();
          mListener = null;
     }

     public interface OnFragmentInteractionListener {
          // TODO: Update argument type and name
          ArrayList<FruitItem> getFruitsFromMainToCartFragment();
     }

}
