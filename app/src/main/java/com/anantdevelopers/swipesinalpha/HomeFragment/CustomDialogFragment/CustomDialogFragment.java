package com.anantdevelopers.swipesinalpha.HomeFragment.CustomDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.anantdevelopers.swipesinalpha.R;

import java.util.ArrayList;

public class CustomDialogFragment extends DialogFragment {
     private TextView fruitNameTxtView;
     private TextView fruitPriceTxtView;
     private Spinner fruitQtySpinner;
     private ImageView fruitImageView;

     private OnFragmentInteractionListener mListener;

     private String receivedFruitName;
     private ArrayList<String> receivedFruitQtys, receivedFruitPrices;
     private int receivedFruitImage;

     private Vibrator vibrator;

     private String qty, updatedPrice; //used inside the spinner

     public CustomDialogFragment() {
          // Required empty public constructor
     }

     @Override
     public void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          if(getActivity() != null)
          vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

          Bundle bundle = this.getArguments();
          if(bundle != null){
               this.receivedFruitName = bundle.getString("fruitName");
               this.receivedFruitPrices = bundle.getStringArrayList("fruitPrice");
               this.receivedFruitQtys = bundle.getStringArrayList("fruitQty");
               this.receivedFruitImage = bundle.getInt("fruitImage");
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
          fruitImageView = v.findViewById(R.id.fruit_image_view);

          setFruitParams();
          setSpinnerOptions();

          fruitQtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    qty = parent.getItemAtPosition(position).toString();

                    for(String q: receivedFruitQtys){
                         if(q.equals(qty)){
                              int index = receivedFruitQtys.indexOf(q);
                              updatedPrice = receivedFruitPrices.get(index);
                              fruitPriceTxtView.setText(updatedPrice);
                              break;
                         }
                    }
               }

               @Override
               public void onNothingSelected(AdapterView<?> parent) {

               }
          });

          Button addToCartButton = v.findViewById(R.id.button);

          addToCartButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    if (vibrator != null) {
                         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                              VibrationEffect vibrationEffect = VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE);
                              vibrator.vibrate(vibrationEffect);
                         }
                         else {
                              vibrator.vibrate(15);
                         }
                    }
                    mListener.getItemFromDialogToMainActivity(receivedFruitName, qty, updatedPrice);
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
               fruitImageView.setImageResource(receivedFruitImage);
          }
     }

     private void setSpinnerOptions() {
          ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.item_spinnet, receivedFruitQtys);
          fruitQtySpinner.setAdapter(adapter);
          fruitQtySpinner.setSelection(0);
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

     public interface OnFragmentInteractionListener {
          void getItemFromDialogToMainActivity(String receivedFruitName, String receivedFruitQty, String receivedFruitPrice);
     }

}
