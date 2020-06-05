package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment.PreviousOrderLocalDatabase;

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

public class OrderAgainDialog extends DialogFragment {

     private int position;

     public OrderAgainDialog(int position) {
          this.position = position;
     }

     public interface OrderAgainDialogListener {
          void onDialogPositiveClickForOrderAgain(int position);
     }

     private OrderAgainDialogListener listener;

     @NonNull
     @Override
     public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
          LayoutInflater inflater = requireActivity().getLayoutInflater();

          View v = inflater.inflate(R.layout.dialog_order_again, null);

          builder.setView(v)
                  .setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                            listener.onDialogPositiveClickForOrderAgain(position);
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
               listener = (OrderAgainDialogListener) getTargetFragment();
          } catch (ClassCastException e) {
               throw new ClassCastException(getTargetFragment().toString() + " must implement OrderAgainDialogListener.");
          }
     }
}
