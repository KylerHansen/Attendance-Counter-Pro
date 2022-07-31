package com.xzera.counter;

import android.app.Dialog;
import android.content.Context;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.xzera.counter.db.AllCountViewModel;
import com.xzera.counter.db.Count;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditBookDialog extends DialogFragment {

    View view;
    Toolbar toolbar;
    private long bookID;
    private String bookTitle;
    private RecyclerView recyclerView;
    private CountRecyclerViewAdapter countRecyclerViewAdapter;
    private int lineCount = 1;
    FragmentManager fm;
    private int sortType = 0; //TODO MAKE THIS GLOBAL VARIABLE THAT WAY IF YOU LEAVE AND COME BACK TO THE PAGE IT STAYS SORTED.
    private List<Count> countList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_book_dialog, container, false);
        toolbar = view.findViewById(R.id.toolbar_editBook);
        recyclerView = view.findViewById(R.id.recyclerView_CountList);
        fm = getParentFragmentManager();
        Bundle bundle = this.getArguments();

        if(bundle != null){
            this.bookID = bundle.getLong("bookID");
            this.bookTitle = bundle.getString("bookTitle");
        }

        toolbar.setTitle("Edit "+ bookTitle);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true); //sets x button on dialog left in toolbar.
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        actionBar.setDisplayHomeAsUpEnabled(true); //this takes it back one level instead of going all the way to homepage.
        setHasOptionsMenu(true);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = getContext();
        countRecyclerViewAdapter = new CountRecyclerViewAdapter(new ArrayList<Count>());

        if(lineCount <= 1){
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }else{
            recyclerView.setLayoutManager(new GridLayoutManager(context, lineCount));
        }

        recyclerView.setAdapter(countRecyclerViewAdapter);
        recyclerView.setHasFixedSize(false);

        new ViewModelProvider(this)
                .get(AllCountViewModel.class)
                .getCountList(context,this.bookID)
                .observe(this, new Observer<List<Count>>() {
                    @Override
                    public void onChanged(List<Count> counts) {
                        if(counts != null){
                            if(sortType == 1){
                                Collections.sort(counts, Count.SortCountTitleAtoZ);
                            }else if( sortType == 2){
                                Collections.sort(counts, Count.SortCountTitleZtoA);
                            }else if( sortType == 3){
                                Collections.sort(counts, Count.SortCountDateASC);
                            }else if( sortType == 4){
                                Collections.sort(counts, Count.SortCountDateDESC);
                            }else if( sortType == 5){
                                Collections.sort(counts, Count.SortCountTotalASC);
                            }else if( sortType == 6){
                                Collections.sort(counts, Count.SortCountTotalDESC);
                            }else{
                                Collections.sort(counts, Count.SortCountDateASC);
                            }

                            countList = counts;

                            countRecyclerViewAdapter.addItems(counts);
                        }
                    }
                });



    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.nav_edit_book, menu);

    }

    @NonNull

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnu_createCount:
                fm.beginTransaction()
                        .replace(android.R.id.content, new CountScreenDialog())
                        .addToBackStack(null)
                        .commit();

                break;
            case R.id.mnu_createReport:
                fm.beginTransaction()
                        .replace(android.R.id.content, new CreateReportDialog())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.sortTitleAZ:
                sortType = 1;
                sortCounts(1);
                break;
            case R.id.sortTitleZA:
                sortType = 2;
                sortCounts(2);
                break;
            case R.id.sortDateASC:
                sortType = 3;
                sortCounts(3);
                break;
            case R.id.sortDateDSC:
                sortType = 4;
                sortCounts(4);
                break;
            case R.id.sortTotalASC:
                sortType = 5;
                sortCounts(5);
                break;
            case R.id.sortTotalDSC:
                sortType = 6;
                sortCounts(6);
                break;
            case android.R.id.home:
                fm.beginTransaction()
                        .replace(android.R.id.content, new BookListDialog())
                        .addToBackStack(null)
                        .commit();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    public void sortCounts(int type){

        switch(type){
            case 1:
                Collections.sort(countList, Count.SortCountTitleAtoZ);
                countRecyclerViewAdapter.addItems(countList);
                countRecyclerViewAdapter.notifyDataSetChanged();
                break;
            case 2:
                Collections.sort(countList, Count.SortCountTitleZtoA);
                countRecyclerViewAdapter.addItems(countList);
                countRecyclerViewAdapter.notifyDataSetChanged();
                break;
            case 4:
                Collections.sort(countList, Count.SortCountDateDESC);
                countRecyclerViewAdapter.addItems(countList);
                countRecyclerViewAdapter.notifyDataSetChanged();
                break;
            case 5:
                Collections.sort(countList, Count.SortCountTotalASC);
                countRecyclerViewAdapter.addItems(countList);
                countRecyclerViewAdapter.notifyDataSetChanged();
                break;
            case 6:
                Collections.sort(countList, Count.SortCountTotalDESC);
                countRecyclerViewAdapter.addItems(countList);
                countRecyclerViewAdapter.notifyDataSetChanged();
                break;
            default:
                Collections.sort(countList, Count.SortCountDateASC);
                countRecyclerViewAdapter.addItems(countList);
                countRecyclerViewAdapter.notifyDataSetChanged();

        }


    }

}