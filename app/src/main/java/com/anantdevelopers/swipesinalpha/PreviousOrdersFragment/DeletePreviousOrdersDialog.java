package com.anantdevelopers.swipesinalpha.PreviousOrdersFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.anantdevelopers.swipesinalpha.R;

public class DeletePreviousOrdersDialog extends DialogFragment {

     private CheckBox checkBox;

     public interface DeletePreviousOrdersDialogListener {
          void onDialogPositiveClick(boolean keepStarredOrders);
     }

     private DeletePreviousOrdersDialogListener listener;

     @NonNull
     @Override
     public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
          LayoutInflater inflater = requireActivity().getLayoutInflater();

          View v = inflater.inflate(R.layout.dialog_delete_previous_orders, null);

          checkBox = v.findViewById(R.id.keep_starred_orders_checkbox);

          builder.setView(v)
                  .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                            boolean keepStarred = checkBox.isChecked();
                            listener.onDialogPositiveClick(keepStarred);
                            dialog.dismiss();
                       }
                  })
                  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
               listener = (DeletePreviousOrdersDialogListener) getTargetFragment();
          } catch(ClassCastException e){
               throw new ClassCastException(getTargetFragment().toString() + " must implement DeletePreviousOrdersDialogListener");
          }
     }
}
