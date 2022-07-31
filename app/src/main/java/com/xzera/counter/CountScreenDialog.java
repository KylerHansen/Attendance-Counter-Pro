package com.xzera.counter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xzera.counter.db.AppDatabase;
import com.xzera.counter.db.Count;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

public class CountScreenDialog extends DialogFragment {

    View view;
    Toolbar toolbar;
    FragmentManager fm;
    ImageButton btnPlus, btnMinus;
    int CountBy;
    int TotalCount;
    TextInputEditText incrementField;
    EditText totalCountField;
    InputMethodManager imm;
    LinearLayout countScreenLL;
    boolean isEditMode;
    String editCountTitle;
    long editCountId;
    long editCountBookID;
    String editCountDate;
    Toast toast_countUpdated;
    CountRecyclerViewAdapter countRecyclerViewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_count_screen_dialog, container, false);
        fm = getParentFragmentManager();
        toolbar = view.findViewById(R.id.toolbar_countScreen);
        toolbar.setTitle(R.string.title_newCount_toolbar);
        imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        countScreenLL = view.findViewById(R.id.ll_countScreen);
        toast_countUpdated = Toast.makeText(getActivity(), "Count Updated", Toast.LENGTH_LONG);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true); //sets x button on dialog left in toolbar
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        actionBar.setDisplayHomeAsUpEnabled(true); //this takes it back one level instead of going all the way to homepage.
        setHasOptionsMenu(true);

        btnPlus = view.findViewById(R.id.btn_countPlus);
        btnMinus = view.findViewById(R.id.btn_countMinus);
        incrementField = view.findViewById(R.id.txtbx_incrementBy);
        totalCountField = view.findViewById(R.id.lbl_countTotal);
        isEditMode = false;

        TotalCount = 0;
        CountBy = 1;

        Bundle bundle = this.getArguments();
        if(bundle != null){
            isEditMode = true;
            TotalCount = bundle.getInt("editCount_total");
            totalCountField.setText(TotalCount+"");
            editCountTitle = bundle.getString("editCount_title");
            toolbar.setTitle(editCountTitle);
            editCountId = bundle.getLong("editCount_id");
            editCountBookID = bundle.getLong("editCount_bookID");
            editCountDate = bundle.getString("editCount_date");
        }

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CountBy > 0){
                    view.clearFocus();
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    TotalCount += CountBy;
                    adjustTextSize(TotalCount);
                    totalCountField.setText(TotalCount+"");
                }
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CountBy > 0){
                    view.clearFocus();
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    TotalCount -= CountBy;
                    adjustTextSize(TotalCount);
                    totalCountField.setText(TotalCount+"");
                }
            }
        });

        countScreenLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.clearFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        totalCountField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    totalCountField.setText("");
                }else{
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    try{
                        TotalCount = Integer.parseInt(totalCountField.getText().toString());

                    }catch(Exception e){
                        TotalCount = 0;
                    }
                }
            }
        });

        incrementField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    incrementField.setText("");
                }else{
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    try {
                        CountBy = Integer.parseInt(incrementField.getText().toString());
                    }catch(Exception e){
                        CountBy = 1;
                    }
                }
            }
        });



        return view;
    }


    private void adjustTextSize(int total){
        if(total >= 1000 || total <= -900) {
            totalCountField.setTextSize(90);
        }else{
            totalCountField.setTextSize(130);
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.nav_count, menu);

    }

    @NonNull

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  //removes the title from the navbar.
        return dialog;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.mnu_saveCount:
                try{
                    TotalCount = Integer.parseInt(totalCountField.getText().toString());

                }catch(Exception e){
                    TotalCount = 0;
                   Log.e("CountScrenDialog", "SaveButton Click "+ e.getMessage());
                }

                if(!isEditMode) {

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, new SaveCountDialog(TotalCount), "tag_SaveCountDialog")
                            .addToBackStack(null)
                            .commit();
                }else if(isEditMode){

                    Count count = new Count(editCountBookID, editCountTitle, editCountDate, TotalCount);
                    count.setCount_id(editCountId);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                AppDatabase.getInstance(getContext()).countDAO().updateCount(count);
                            }catch(Exception e){
                                Log.e("CountScrenDialog", "SaveButton Click Edit Count Error"+ e.getMessage());
                            }
                        }
                    }).start();

                    toast_countUpdated.show();

                    Bundle editBundle = new Bundle();
                    editBundle.putLong("bookID", editCountBookID);

                    EditBookDialog editBookDialog = new EditBookDialog();
                    editBookDialog.setArguments(editBundle);

                    fm.beginTransaction()
                            .replace(android.R.id.content, editBookDialog)
                            .addToBackStack(null)
                            .commit();

                }
            break;

            case R.id.mnu_ResetCount:

                AlertDialog.Builder reset_builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
                reset_builder.setTitle("Are you sure you want to reset?")
                        .setMessage("Everything will reset back to 0.")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TotalCount = 0;
                                totalCountField.setText(TotalCount+"");
                                CountBy = 1;
                                incrementField.setText("");
                                dialog.dismiss();
                            }
                        }).show();

                break;
            case android.R.id.home:


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
                builder.setTitle("Are you sure you want to leave?")
                        .setMessage("Changes to this count will not be saved.")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                 dismiss();
                                fm.beginTransaction().show(fm.findFragmentById(R.id.container_mainButtons)).commit();
                            }
                        }).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }



}