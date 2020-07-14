package com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity;

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

public class DeleteAccountDialog extends DialogFragment {
     public interface deleteAccountDialogListener {
          void onDeleteDialogPositiveClick();
     }

     private deleteAccountDialogListener listener;

     @NonNull
     @Override
     public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
          LayoutInflater inflater = requireActivity().getLayoutInflater();

          View v = inflater.inflate(R.layout.dialog_account_delete, null);

          builder.setView(v)
                  .setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                            listener.onDeleteDialogPositiveClick();
                            dialog.dismiss();
                       }
                  })
                  .setNegativeButton("ABORT", new DialogInterface.OnClickListener() {
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
               listener = (deleteAccountDialogListener) context;
          }catch(ClassCastException e) {
               throw new ClassCastException(getActivity().toString() + " must implement deleteAccountDialogListener.");
          }
     }
}
