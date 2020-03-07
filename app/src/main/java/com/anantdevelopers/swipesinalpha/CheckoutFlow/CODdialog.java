package com.anantdevelopers.swipesinalpha.CheckoutFlow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class CODdialog extends AppCompatDialogFragment {
     @NonNull
     @Override
     public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
          builder.setMessage("Do you want to continue using Cash on delivery?");
          builder.setPositiveButton("Yes, Do it", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
               }
          });
          builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

               }
          });
          AlertDialog dialog = builder.create();

          return dialog;
     }
}
