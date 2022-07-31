package com.xzera.counter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xzera.counter.db.AppDatabase;
import com.xzera.counter.db.Book;
import com.xzera.counter.db.Count;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CreateReportDialog extends DialogFragment {
FragmentManager fm;
    View view;
    Toolbar toolbar;
    Spinner startSpinner;
    Spinner endSpinner;
    Spinner graphSpinner;
    ArrayAdapter str_adapter;
    ArrayAdapter end_adapter;
    ArrayAdapter grph_adapter;
    String[] existingBooksList;
    List<String> existingCountDates;
    List<String> existingCountDates2;
    List<String> graphTypes;
    Button btnGenerateGraph;
    boolean isReadyToPopulateDates;
    Handler handler;
    TextView selectBooksToGraph;
    boolean[] selectedBooks;
    ArrayList<Integer> bookList;
    List<Book> booksToGraph;
    RadioGroup radioGroup;
    int xAxisFlag;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_report_dialog, container, false);
        fm = getParentFragmentManager();
        toolbar = view.findViewById(R.id.toolbar_createReport);
        toolbar.setTitle(R.string.create_report);
        existingCountDates = new ArrayList<>();
        existingCountDates2 = new ArrayList<>();
        graphTypes = new ArrayList<>();
        btnGenerateGraph = view.findViewById(R.id.btnGenerateGraph);
        bookList = new ArrayList<>();
        booksToGraph = new ArrayList<>();
        selectBooksToGraph = view.findViewById(R.id.checkbox_dialogSelectBook);
        isReadyToPopulateDates = false;
        handler = new Handler(getContext().getMainLooper());
        radioGroup = view.findViewById(R.id.radioGroup_xAxis);
        radioGroup.clearCheck();
        xAxisFlag = -1;

        graphTypes.add("Line Graph");
        graphTypes.add("Bar Graph");

        startSpinner = view.findViewById(R.id.spinner_selectStartDate);
        endSpinner = view.findViewById(R.id.spinner_selectEndDate);
        graphSpinner = view.findViewById(R.id.spinner_selectGraphType);

        str_adapter = new ArrayAdapter(getContext(),R.layout.select_date_spinner, existingCountDates);
        str_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(str_adapter);

        end_adapter = new ArrayAdapter(getContext(),R.layout.select_date_spinner, existingCountDates2);
        end_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endSpinner.setAdapter(end_adapter);

        grph_adapter = new ArrayAdapter(getContext(),R.layout.select_existingbook_spinner, graphTypes);
        grph_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        graphSpinner.setAdapter(grph_adapter);


        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true); //sets x button on dialog left in toolbar.
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        actionBar.setDisplayHomeAsUpEnabled(true); //this takes it back one level instead of going all the way to homepage.
        setHasOptionsMenu(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> strings = AppDatabase.getInstance(getContext()).bookDAO().getAllBookTitlesNL();
                existingBooksList = strings.toArray(new String[0]);
                selectedBooks = new boolean[existingBooksList.length];
            }
        }).start();

        btnGenerateGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(existingCountDates.size() <= 0 || graphSpinner.getSelectedItemId() == AdapterView.INVALID_ROW_ID || xAxisFlag == -1){
                    Toast errorMessage = Toast.makeText(getContext(), "Error: All Fields Must Be Selected", Toast.LENGTH_LONG);
                    errorMessage.show();
                }else {
                    List<Count> tempCountList = new ArrayList<>();
                    List<Count> countList = new ArrayList<>();
                    List<String> dateRange = new ArrayList<>();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = (int) startSpinner.getSelectedItemId(); i <= (int) endSpinner.getSelectedItemId(); i++) {
                                    dateRange.add(existingCountDates.get(i).toString());
                                }

                                for (Book book : booksToGraph) {
                                    tempCountList.addAll(AppDatabase.getInstance(getContext()).countDAO().getCountsByBookIDNL(book.getBook_id()));
                                }

                                for (Count count : tempCountList) {
                                    if (dateRange.contains(count.getDate().toString())) {
                                        countList.add(count);
                                    }
                                }

                                fm.beginTransaction()
                                        .replace(android.R.id.content, new GraphReportDialog(countList, booksToGraph, graphSpinner.getSelectedItem().toString(), xAxisFlag))
                                        .addToBackStack(null)
                                        .commit();

                            } catch (Exception e) {
                                Log.e("CreateReportDialog", "btnGenerateGraph OnClick Listener getting data to graph" + e.getMessage());
                            }

                        }
                    }).start();
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch(checkedId){
                    case R.id.rb_CountDates:
                        xAxisFlag = 1;
                        break;
                    case R.id.rb_CountTitle:
                        xAxisFlag = 2;
                        break;
                }
            }
        });

        selectBooksToGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.alertDialogStyle);
                builder.setTitle("Select Books");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(existingBooksList, selectedBooks, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            bookList.add(which);
                            Collections.sort(bookList);
                        }else{
                            int atIndex = bookList.indexOf(which);
                            bookList.remove(atIndex);
                        }
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(int i = 0; i < bookList.size(); i++){
                            stringBuilder.append(existingBooksList[bookList.get(i)]);
                            if(i != bookList.size()-1){
                                stringBuilder.append(", ");
                            }
                        }
                        selectBooksToGraph.setText(stringBuilder.toString());

                        new Thread (new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                try {
                                    if (bookList.size() > 0) {
                                        List<String> tempDates = new ArrayList<>();
                                        List<String> tempDatesNoDuplicates = new ArrayList<>();

                                        for (int i = 0; i < bookList.size(); i++) {
                                            Book myBook = AppDatabase.getInstance(getContext()).bookDAO().getBookByTitle(existingBooksList[bookList.get(i)]);
                                            booksToGraph.add(myBook);
                                            tempDates.addAll(AppDatabase.getInstance(getContext()).countDAO().getCountDatesInBook(myBook.getBook_id()));
                                        }

                                        for(int i = 0; i < tempDates.size(); i++){
                                            String date = tempDates.get(i);
                                            if(!tempDatesNoDuplicates.contains(date)){
                                                tempDatesNoDuplicates.add(date);
                                            }
                                        }

                                        List<Date> realDateList = new ArrayList<>();
                                        for(int i = 0; i < tempDatesNoDuplicates.size(); i++){
                                         Date tempDate = new SimpleDateFormat("dd/MM/yyyy").parse(tempDatesNoDuplicates.get(i));
                                         realDateList.add(tempDate);
                                       }

                                        tempDatesNoDuplicates.sort(new Comparator<String>() {
                                            @Override
                                            public int compare(String c1, String c2) {
                                                try {
                                                    String[] arrA = c1.split("/");
                                                    String[] arrB = c2.split("/");

                                                    int monthA = Integer.parseInt(arrA[0]);
                                                    int dayA = Integer.parseInt(arrA[1]);
                                                    int yearA = Integer.parseInt(arrA[2]);

                                                    int monthB = Integer.parseInt(arrB[0]);
                                                    int dayB = Integer.parseInt(arrB[1]);
                                                    int yearB = Integer.parseInt(arrB[2]);

                                                    if(yearA < yearB){ //if A comes before B
                                                        return -1;  //A appears first its oldest date
                                                    }else if(yearA > yearB){ //if B comes before A
                                                        return 1; //B appears first because its older
                                                    }else{
                                                        if(monthA < monthB){
                                                            return -1; //A appears first
                                                        }else if (monthA > monthB){
                                                            return 1; //B appears first
                                                        }else{
                                                            if(dayA < dayB){
                                                                return -1; //A appears first
                                                            }else if(dayA > dayB){
                                                                return 1; //B appears first
                                                            }else{
                                                                return 0;
                                                            }
                                                        }
                                                    }
                                                }
                                                catch(Exception e){
                                                    Log.e("CreateReportDialog", "Comparator Sort TempDatesNoDuplicates"+ e.getMessage());
                                                }
                                                return 0;
                                            }
                                        });

                                         existingCountDates = tempDatesNoDuplicates;
                                         existingCountDates2 = tempDatesNoDuplicates;

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(existingCountDates.size() > 0) {
                                                    str_adapter.clear();
                                                    str_adapter.addAll(existingCountDates);
                                                    str_adapter.notifyDataSetChanged();

                                                    end_adapter.clear();
                                                    end_adapter.addAll(existingCountDates2);
                                                    end_adapter.notifyDataSetChanged();
                                                }
                                            }
                                        });

                                    }else{
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                str_adapter.clear();
                                                end_adapter.clear();
                                                str_adapter.notifyDataSetChanged();
                                                end_adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }catch (Exception e){
                                    Log.e("CreateReportDialog","SelectBooksToGraph>OnOkayClick>RunnableThread - "+e.getMessage());
                                }
                            }
                        }).start();

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < selectedBooks.length; i++){
                            selectedBooks[i] = false;
                            bookList.clear();
                            selectBooksToGraph.setText("");
                        }
                    }
                });

                builder.show();
            }
        });

        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
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
            case android.R.id.home:
                fm.beginTransaction().show(fm.findFragmentById(R.id.container_mainButtons)).commit();
                dismiss();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }
}