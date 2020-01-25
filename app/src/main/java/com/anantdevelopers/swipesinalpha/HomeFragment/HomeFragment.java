package com.anantdevelopers.swipesinalpha.HomeFragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anantdevelopers.swipesinalpha.CustomDialogFragment.CustomDialogFragment;
import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.R;
import com.anantdevelopers.swipesinalpha.FruitItem.RecyclerItemClickListener;
import com.anantdevelopers.swipesinalpha.FruitItem.RecyclerViewAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

     private ArrayList<FruitItem> fruits;
     private RecyclerView recyclerView;
     private RecyclerViewAdapter adapter;

     private OnFragmentInteractionListener mListener;

     public HomeFragment() {
          // Required empty public constructor
     }

     @Override
     public void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          fruits = new ArrayList<>();
          fruits.add(new FruitItem("Bananas", "1 Kg", "50 Rs."));
          fruits.add(new FruitItem("Oranges", "1 Kg", "60 Rs."));
          fruits.add(new FruitItem("Apple", "1 Kg", "100 Rs."));
          fruits.add(new FruitItem("Chico", "1 Kg", "80 Rs."));
          fruits.add(new FruitItem("Guava", "1 Kg", "80 Rs."));
          fruits.add(new FruitItem("Kiwi", "1 Kg", "200 Rs."));
          fruits.add(new FruitItem("Papaya", "1 Kg", "40 Rs."));
          fruits.add(new FruitItem("Pears", "1 Kg", "80 Rs."));
          fruits.add(new FruitItem("Strawberry", "1 Kg", "300 Rs."));
          fruits.add(new FruitItem("Mangoes", "1 kg", "200 Rs."));

          adapter = new RecyclerViewAdapter(getContext(), fruits);
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
          View v = inflater.inflate(R.layout.fragment_home, container, false);

          recyclerView = v.findViewById(R.id.recycler_view_home);
          recyclerView.setAdapter(adapter);
          recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

          recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
               @Override
               public void onItemClick(View view, int position) {
                    //Log.e("HomeFragment", fruits.get(position).getFruitName());
                    mListener.sendToActivityfromHomeFragment(fruits.get(position));

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    CustomDialogFragment customDialogFragment = mListener.sendFruitInfoToDialog();

                    customDialogFragment.show(ft, "position of fruit is" + position);
               }
          }));

          return v;
     }

     @Override
     public void onAttach(Context context) {
          super.onAttach(context);
          if (context instanceof OnFragmentInteractionListener) {
               mListener = (OnFragmentInteractionListener) context;
          } else {
               throw new RuntimeException(context.toString()
                       + " must implement HomeFragment.OnFragmentInteractionListener");
          }
     }

     @Override
     public void onDetach() {
          super.onDetach();
          mListener = null;
     }

     public interface OnFragmentInteractionListener {
          void sendToActivityfromHomeFragment(FruitItem item);
          CustomDialogFragment sendFruitInfoToDialog();
     }

}
