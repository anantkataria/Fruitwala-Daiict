package com.anantdevelopers.swipesinalpha.CustomDialogFragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.anantdevelopers.swipesinalpha.FruitItem.FruitItem;
import com.anantdevelopers.swipesinalpha.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomDialogFragment extends DialogFragment {
     private Button addToCartButton;
     private TextView fruitNameTxtView;
     private AppCompatSpinner fruitQtySpinner;
     private TextView fruitPriceTxtView;

     private OnFragmentInteractionListener mListener;

     private String receivedFruitName, receivedFruitQty, receivedFruitPrice;

     private int SpinnerArrayTypeId;

     public CustomDialogFragment() {
          // Required empty public constructor
     }

     @Override
     public void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          Bundle bundle = this.getArguments();
          if(bundle != null){
               this.receivedFruitName = bundle.getString("fruitName");
               this.receivedFruitPrice = bundle.getString("fruitPrice");
               this.receivedFruitQty = bundle.getString("fruitQty");
               //Log.e("CustomDialogfragment", receivedFruitName);
          }
     }

     @NonNull
     @Override
     public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

          LayoutInflater inflater = requireActivity().getLayoutInflater();

          View v = inflater.inflate(R.layout.fragment_custom_dialog, null);

          fruitNameTxtView = v.findViewById(R.id.fruitName);
          fruitQtySpinner = v.findViewById(R.id.spinnerForQuantity);
          fruitPriceTxtView = v.findViewById(R.id.fruitTotalPrice);

          setFruitParams();
          setSpinnerOptions();

          addToCartButton = v.findViewById(R.id.button);

          addToCartButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    mListener.getItemFromDialogToMainActivity(receivedFruitName, receivedFruitQty, receivedFruitPrice);
                    dismiss();
               }
          });

          builder.setView(v);

          return builder.create();

     }

     @Override
     public void onAttach(@NonNull Context context) {
          super.onAttach(context);
          if (context instanceof OnFragmentInteractionListener) {
               mListener = (OnFragmentInteractionListener) context;
          } else {
               throw new RuntimeException(context.toString()
                       + " must implement CustomDialogFragment.OnFragmentInteractionListener");
          }
     }

     @Override
     public void onDetach() {
          super.onDetach();
          mListener = null;
     }

     private void setFruitParams() {
          if (!receivedFruitQty.equals("") && !receivedFruitPrice.equals("") && !receivedFruitName.equals("")){
               //Log.e("setFruitParams()", receivedFruitName);
               fruitNameTxtView.setText(receivedFruitName);
               fruitPriceTxtView.setText(receivedFruitPrice);
          }
     }

     private void setSpinnerOptions() {
          selectSpinnerArrayFromGivenFruitName(receivedFruitName);
          ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), SpinnerArrayTypeId, android.R.layout.simple_spinner_item);
          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          fruitQtySpinner.setAdapter(adapter);
          fruitQtySpinner.setSelection(0);
     }

     private void selectSpinnerArrayFromGivenFruitName(String fruitName) {
          switch(fruitName){
               case "Bananas":
                    SpinnerArrayTypeId = R.array.quantityTypesForBananas;
                    break;
               case "Oranges":
                    SpinnerArrayTypeId = R.array.quantityTypesForOranges;
                    break;
               case "Apple":
                    SpinnerArrayTypeId = R.array.quantityTypesForApple;
                    break;
               case "Chico":
                    SpinnerArrayTypeId = R.array.quantityTypesForChico;
                    break;
               case "Guava":
                    SpinnerArrayTypeId = R.array.quantityTypesForGuava;
                    break;
               case "Kiwi":
                    SpinnerArrayTypeId = R.array.quantityTypesForKiwi;
                    break;
               case "Papaya":
                    SpinnerArrayTypeId = R.array.quantityTypesForPapaya;
                    break;
               case "Pears":
                    SpinnerArrayTypeId = R.array.quantityTypesForPears;
                    break;
               case "Strawberry":
                    SpinnerArrayTypeId = R.array.quantityTypesForStrawberry;
                    break;
               case "Mangoes":
                    SpinnerArrayTypeId = R.array.quantityTypesForMangoes;
                    break;
          }
     }

     public interface OnFragmentInteractionListener {
          void getItemFromDialogToMainActivity(String receivedFruitName, String receivedFruitQty, String receivedFruitPrice);
     }

}
