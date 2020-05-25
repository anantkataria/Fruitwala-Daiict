package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment;

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

public class CancelCurrentOrderDialog extends DialogFragment {

     private int position;

     CancelCurrentOrderDialog(int position){
          this.position = position;
     }

     public interface cancelCurrentOrderDialogListener {
          void onDialogPositiveClick(int position);
     }

     private cancelCurrentOrderDialogListener listener;

     @NonNull
     @Override
     public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
          LayoutInflater inflater = requireActivity().getLayoutInflater();

          View v = inflater.inflate(R.layout.dialog_cancel_current_order, null);

          builder.setView(v)
                  .setPositiveButton("YES PLEASE", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                            listener.onDialogPositiveClick(position);
                            dialog.dismiss();
                       }
                  })
                  .setNegativeButton("NOPE", new DialogInterface.OnClickListener() {
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
               listener = (cancelCurrentOrderDialogListener) getTargetFragment();
          } catch (ClassCastException e){
               throw new ClassCastException(getTargetFragment().toString() + " must implement cancelCurrentOrderDialogListener.");
          }
     }
}
