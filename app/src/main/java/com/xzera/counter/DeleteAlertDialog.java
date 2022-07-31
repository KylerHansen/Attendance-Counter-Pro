package com.xzera.counter;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class DeleteAlertDialog extends DialogFragment {
private String title;
    public DeleteAlertDialog(String title){
        this.title = title;
    }

    public interface DeleteAlertDialogDismissed{
        void DeleteAlertDialogClosed();
    }

    private DeleteAlertDialog.DeleteAlertDialogDismissed listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof DeleteAlertDialogDismissed){
            listener = (DeleteAlertDialog.DeleteAlertDialogDismissed) context;
        }else{
            throw new ClassCastException(context.toString() + "    Must implement DeleteAlertDialogDismissed");
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.DeleteAlertDialogClosed();
    }

    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
        builder.setTitle("Confirm Delete " + title)
                .setMessage("Are you sure you want to delete this "+title+"?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //this adds a listener to the okay button.
                        //TODO:ADD METHOD TO DELETE ITEM HERE.
                        //ADD Toast Message to show that it got deleted.
                    }
                });
        return builder.create();
    }

}