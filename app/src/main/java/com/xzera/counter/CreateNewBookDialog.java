package com.xzera.counter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.xzera.counter.db.AppDatabase;
import com.xzera.counter.db.Book;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateNewBookDialog extends DialogFragment {

    String bookTitle;
    String bookDate;
    String callerType;
    long newBookID;
    TextInputEditText bookTitleField;
    View view;



    public CreateNewBookDialog(String callerType){
        this.callerType = callerType;
    }



    public interface CreateNewBookDialogDismissed{
        void CreateNewBookDialogClosed(long newBookID);

    }

    private CreateNewBookDialog.CreateNewBookDialogDismissed listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof CreateNewBookDialogDismissed){
            listener = (CreateNewBookDialog.CreateNewBookDialogDismissed) context;
        }else{
            throw new ClassCastException(context.toString() + " Must implement CreateNewBookDialogDismissed");
        }
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.CreateNewBookDialogClosed(newBookID);
    }


    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);

        view = getLayoutInflater().inflate(R.layout.fragment_create_new_book_dialog, null);
        bookTitleField = (TextInputEditText) view.findViewById(R.id.txtbx_NewBookTitle);
        newBookID = -1;

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(view)
                .setTitle("Create New Book")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {

                           if(bookTitleField.getText().toString().trim().length() > 0){

                               if(bookTitleField.getText().toString().trim().length() < 16) {

                                   bookTitle = bookTitleField.getText().toString().trim();
                                   Date date = Calendar.getInstance().getTime();
                                   DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                   bookDate = dateFormat.format(date);


                                   new Thread(new Runnable() {
                                       @Override
                                       public void run() {
                                           try {

                                               Book newBook = new Book(bookTitle, bookDate);
                                               newBookID = AppDatabase.getInstance(getContext()).bookDAO().insertBook(newBook);

                                               if(callerType != "SaveCount"){
                                                   newBookID = -1;
                                               }

                                           } catch (Exception e) {
                                               Log.e("CreateNewBookDialog", "Save New Book Error " + e.getMessage());
                                           }
                                       }
                                   }).start();

                                   Toast toast_BookSaved = Toast.makeText(getContext(), bookTitle + " Successfully Saved!", Toast.LENGTH_SHORT);
                                   toast_BookSaved.show();

                               }else{
                                   Toast toast_BookNotSaved = Toast.makeText(getContext(), "Error: To Many Characters", Toast.LENGTH_SHORT);
                                   toast_BookNotSaved.show();

                               }

                            }else{
                               Toast toast_BookNotSaved = Toast.makeText(getContext(), "Error: Empty Title Did Not Save", Toast.LENGTH_SHORT);
                               toast_BookNotSaved.show();
                            }


                        }catch(Exception e){
                            Log.e("CreateNewBookDialog: ", "Yes Button Click "+e.toString());
                        }
                    }
                });



        return builder.create();
    }
}