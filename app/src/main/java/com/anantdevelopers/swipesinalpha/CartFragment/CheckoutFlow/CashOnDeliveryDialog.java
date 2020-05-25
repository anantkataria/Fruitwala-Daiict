package com.anantdevelopers.swipesinalpha.CartFragment.CheckoutFlow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.anantdevelopers.swipesinalpha.R;

public class CashOnDeliveryDialog extends DialogFragment {

     public interface cashOnDeliveryDialogListener {
          void onDialogPositiveClick();
     }

     private cashOnDeliveryDialogListener listener;

     @NonNull
     @Override
     public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
          LayoutInflater inflater = requireActivity().getLayoutInflater();

          View v = inflater.inflate(R.layout.dialog_cash_on_delivery, null);

          builder.setView(v)
                  .setPositiveButton("YES, DO IT", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                            listener.onDialogPositiveClick();
                            dialog.dismiss();
                       }
                  })
                  .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                       }
                  });

          return builder.create();

     }

     @Override
     public void onAttach(@NonNull Context context) {
          super.onAttach(context);

          try {
               listener = (cashOnDeliveryDialogListener) context;
          }catch (ClassCastException e) {
               throw new ClassCastException(getActivity().toString() + " must implement cashOnDeliveryDialogListener.");
          }
     }
}
