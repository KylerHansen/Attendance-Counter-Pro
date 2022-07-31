package com.xzera.counter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xzera.counter.db.AllBookViewModel;
import com.xzera.counter.db.AppDatabase;
import com.xzera.counter.db.Book;
import com.xzera.counter.db.Count;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class SaveCountDialog extends DialogFragment implements CreateNewBookDialog.CreateNewBookDialogDismissed {

    public SaveCountDialog(int totalCount){
        this.totalCount = totalCount;
    }
    FragmentManager fm;
    View view;
    Toolbar toolbar;
    TextView txtCountDate, errlbl_CountName;
    TextInputEditText txtCountTitle;
    android.app.DatePickerDialog.OnDateSetListener CountDateListener;
    Spinner spinner;
    ArrayAdapter adapter;
    List<String> existingBooks;
    Button btnCreateNewBook;
    int totalCount;
    String date;
    int year;
    int month;
    int day;
    Toast toast_countSaved;
    InputMethodManager imm;
    ViewModelProvider vmBookTitles;
    long book_Id;
    String countDate;
    LinearLayout chooseBookLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_save_count_dialog, container, false);
        fm = getParentFragmentManager();
        toolbar = view.findViewById(R.id.toolbar_saveCount);
        toolbar.setTitle(R.string.title_saveCount_toolbar);
        errlbl_CountName = view.findViewById(R.id.lbl_error_CountNameRequired);
        errlbl_CountName.setVisibility(View.INVISIBLE);
        toast_countSaved = Toast.makeText(getActivity(), "Count Saved", Toast.LENGTH_LONG);
        imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        chooseBookLayout = view.findViewById(R.id.ll_chooseBook);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        actionBar.setDisplayHomeAsUpEnabled(true); //sets x button on dialog left in toolbar.
        setHasOptionsMenu(true);

        txtCountTitle = view.findViewById(R.id.txtbx_countTitle);
        btnCreateNewBook = view.findViewById(R.id.btn_createNewBook);
        txtCountDate = view.findViewById(R.id.txtbx_saveCountDate);
        existingBooks = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = month+1;
        date = month + "/" + day + "/" + year;
        txtCountDate.setText(date);


        txtCountDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                view.clearFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                DatePickerDialog dialog = new DatePickerDialog(
                        v.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        CountDateListener,
                        year,month-1,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        txtCountTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    view.clearFocus();
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

    chooseBookLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            view.clearFocus();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    });

        CountDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                txtCountDate.setText(date);
            }
        };


        btnCreateNewBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.clearFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                CreateNewBookDialog newBook = new CreateNewBookDialog("SaveCount");
                fm.beginTransaction();
                newBook.show(fm,"NewBookAlertDialog");


            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        spinner = view.findViewById(R.id.spinner_selectBookToSave);
        adapter = new ArrayAdapter(getContext(), R.layout.select_existingbook_spinner, existingBooks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        new ViewModelProvider(this)
                .get(AllBookViewModel.class)
                .getBookTitles(getContext())
                .observe(this, new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> strings) {
                        if(strings != null){
                            existingBooks = strings;
                            adapter.clear();
                            adapter.addAll(existingBooks);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.nav_save_count, menu);

    }

    @NonNull

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  //removes the title from the navbar.
        return dialog;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        switch(item.getItemId()){

            case R.id.mnu_saveCount_toBook:

                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if(validationCheck()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                SimpleDateFormat date_count = new SimpleDateFormat("MM/dd/yyyy");

                               countDate = txtCountDate.getText().toString();

                               String bookName = existingBooks.get((int)spinner.getSelectedItemId());

                                Book myBook = AppDatabase.getInstance(getContext()).bookDAO().getBookByTitle(bookName);

                                book_Id = myBook.getBook_id();

                                String CountName = txtCountTitle.getText().toString();

                                Count count = new Count(book_Id, CountName, countDate, totalCount);

                                AppDatabase.getInstance(getContext()).countDAO().insertCount(count);

                                Bundle bundle = new Bundle();
                                bundle.putLong("bookID", book_Id);
                                bundle.putString("bookTitle", myBook.getBook_title());

                                EditBookDialog editBookDialog = new EditBookDialog();
                                editBookDialog.setArguments(bundle);

                                fm.beginTransaction()
                                        .replace(android.R.id.content, editBookDialog)
                                        .addToBackStack(null)
                                        .commit();

                            } catch (Exception e) {
                                Log.e("SaveCount2Book Error: ", e.getMessage());
                            }
                        }
                    }).start();
                    toast_countSaved.show();

                    dismiss();



                }
                break;
            case android.R.id.home:
                fm.popBackStack();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }


        return super.onOptionsItemSelected(item);
    }


    private boolean validationCheck(){
        try {
            if(txtCountTitle.getText().toString().trim().length() > 0) {
                if(txtCountDate.getText().toString().trim().length() > 0){
                    if(spinner.getSelectedItem().toString().trim().length() > 0){
                        errlbl_CountName.setVisibility(View.INVISIBLE);
                        return true;
                    }
                }
            }else{
                errlbl_CountName.setVisibility(View.VISIBLE);
            }
        }catch(Exception e){
            errlbl_CountName.setVisibility(View.VISIBLE);
          return false;
        }

        return false;
    }

    @Override
    public void CreateNewBookDialogClosed(long newBookID) {
        //not called here
    }


    public void SetSpinnerItem(long id){
        spinner.setSelection((int)id);
    }

}