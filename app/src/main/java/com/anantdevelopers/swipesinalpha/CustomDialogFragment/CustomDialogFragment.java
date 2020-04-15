package com.anantdevelopers.swipesinalpha.CustomDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.anantdevelopers.swipesinalpha.R;

import java.util.ArrayList;

public class CustomDialogFragment extends DialogFragment {
     private TextView fruitNameTxtView;
     private TextView fruitPriceTxtView;
     private Spinner fruitQtySpinner;

     private OnFragmentInteractionListener mListener;

     private String receivedFruitName;
     private ArrayList<String> receivedFruitQtys, receivedFruitPrices;

     private String qty, updatedPrice; //used inside the spinner
     private String defaultSpinnerItem;
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
               this.receivedFruitPrices = bundle.getStringArrayList("fruitPrice");
               this.receivedFruitQtys = bundle.getStringArrayList("fruitQty");
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

          fruitQtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    qty = parent.getItemAtPosition(position).toString();
//                    int defaultQty = Integer.valueOf(defaultSpinnerItem.substring(0, 3).replaceAll("[a-z\\s]", ""));
//                    int selectedQty = Integer.valueOf(qty.substring(0, 3).replaceAll("[a-z\\s]", ""));
//                    int defaultprice = Integer.valueOf(receivedFruitPrices.substring(0, 3).replaceAll("[a-z\\s]", ""));
//                    //Log.e("###", "defaultQty = " + defaultQty + ", selectedQty = " + selectedQty + ", defaultPrice = " + defaultprice);
//                    //Integer.valueOf(qty.substring(0, 3).replaceAll("\\s", ""))*Integer.valueOf(receivedFruitPrice.substring(0, 3).replaceAll("\\s", ""))/Integer.valueOf(defaultSpinnerItem.substring(0, 3).replaceAll("\\s", ""))
//                    updatedPrice = Integer.toString((selectedQty*defaultprice)/defaultQty);
//                    fruitPriceTxtView.setText(updatedPrice+" Rs.");
//                    //Log.e("spinnerItemSelected", qty);
               }

               @Override
               public void onNothingSelected(AdapterView<?> parent) {

               }
          });

          Button addToCartButton = v.findViewById(R.id.button);

          addToCartButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    mListener.getItemFromDialogToMainActivity(receivedFruitName, qty, updatedPrice+" Rs.");
                    dismiss();
               }
          });

          builder.setView(v);

          return builder.create();

     }

     private void setFruitParams() {
          if (!receivedFruitQtys.isEmpty() && !receivedFruitPrices.isEmpty() && !receivedFruitName.equals("")){
               fruitNameTxtView.setText(receivedFruitName);
               fruitPriceTxtView.setText(receivedFruitPrices.get(0));
          }
     }

     private void setSpinnerOptions() {
//          selectSpinnerArrayFromGivenFruitName(receivedFruitName);
//          ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), SpinnerArrayTypeId, android.R.layout.simple_spinner_item);
//          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//          fruitQtySpinner.setAdapter(adapter);
//          fruitQtySpinner.setSelection(0);
//          defaultSpinnerItem = fruitQtySpinner.getSelectedItem().toString();
          ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.item_spinnet, receivedFruitQtys);
          fruitQtySpinner.setAdapter(adapter);
          fruitQtySpinner.setSelection(0);
          defaultSpinnerItem = fruitQtySpinner.getSelectedItem().toString();

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

//     private void selectSpinnerArrayFromGivenFruitName(String fruitName) {
//          switch(fruitName){
//               case "Bananas":
//                    SpinnerArrayTypeId = R.array.quantityTypesForBananas;
//                    break;
//               case "Oranges":
//                    SpinnerArrayTypeId = R.array.quantityTypesForOranges;
//                    break;
//               case "Apple":
//                    SpinnerArrayTypeId = R.array.quantityTypesForApple;
//                    break;
//               case "Chico":
//                    SpinnerArrayTypeId = R.array.quantityTypesForChico;
//                    break;
//               case "Guava":
//                    SpinnerArrayTypeId = R.array.quantityTypesForGuava;
//                    break;
//               case "Kiwi":
//                    SpinnerArrayTypeId = R.array.quantityTypesForKiwi;
//                    break;
//               case "Papaya":
//                    SpinnerArrayTypeId = R.array.quantityTypesForPapaya;
//                    break;
//               case "Pears":
//                    SpinnerArrayTypeId = R.array.quantityTypesForPears;
//                    break;
//               case "Strawberry":
//                    SpinnerArrayTypeId = R.array.quantityTypesForStrawberry;
//                    break;
//               case "Mangoes":
//                    SpinnerArrayTypeId = R.array.quantityTypesForMangoes;
//                    break;
//          }
//     }

     public interface OnFragmentInteractionListener {
          void getItemFromDialogToMainActivity(String receivedFruitName, String receivedFruitQty, String receivedFruitPrice);
     }

}
