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

import com.xzera.counter.db.AllBookViewModel;
import com.xzera.counter.db.Book;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BookListDialog extends DialogFragment {
    FragmentManager fm;
    View view;
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private BookRecyclerViewAdapter bookRecyclerViewAdapter;
    private int lineCount = 1;
    private int sortType = 0;  //TODO: MAKE THIS GLOBAL VARIABLE
    private List<Book> bookList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_book_list_dialog, container, false);
        fm = getParentFragmentManager();
        toolbar = view.findViewById(R.id.toolbar_bookList);
        toolbar.setTitle("Book List");
        recyclerView = view.findViewById(R.id.recyclerView_BookList);


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
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
        bookRecyclerViewAdapter = new BookRecyclerViewAdapter(new ArrayList<Book>());

        if (lineCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, lineCount));
        }

        recyclerView.setAdapter(bookRecyclerViewAdapter);
        recyclerView.setHasFixedSize(false);

        new ViewModelProvider(this)
                .get(AllBookViewModel.class)
                .getBookList(context)
                .observe(this, new Observer<List<Book>>() {
                    @Override
                    public void onChanged(List<Book> books) {
                        if (books != null) {
                            if (sortType == 1) {
                                Collections.sort(books, Book.SortBookTitleAtoZ);
                            } else if (sortType == 2) {
                                Collections.sort(books, Book.SortBookTitleZtoA);
                            } else if (sortType == 3) {
                                Collections.sort(books, Book.SortBookDateASC);
                            } else if (sortType == 4) {
                                Collections.sort(books, Book.SortBookDateDESC);
                            } else {
                                Collections.sort(books, Book.SortBookDateASC);
                            }
                            bookList = books;
                            bookRecyclerViewAdapter.addItems(books);
                        }
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.nav_book_list, menu);

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
        switch (item.getItemId()) {
            case R.id.mnu_createBook:
                CreateNewBookDialog newBook = new CreateNewBookDialog("BookList");
                fm.beginTransaction();
                newBook.show(fm, "NewBookAlertDialog");
                break;

            case R.id.sortBookTitleAZ:
                sortType = 1;
                sortBooks(1);
                break;
            case R.id.sortBookTitleZA:
                sortType = 2;
                sortBooks(2);
                break;
            case R.id.sortBookDateASC:
                sortType = 3;
                sortBooks(3);
                break;
            case R.id.sortBookDateDSC:
                sortType = 4;
                sortBooks(4);
                break;

            case android.R.id.home:
                fm.beginTransaction().show(fm.findFragmentById(R.id.container_mainButtons)).commit();
                dismiss();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    public void sortBooks(int type) {
        switch (type) {
            case 1:
                Collections.sort(bookList, Book.SortBookTitleAtoZ);
                bookRecyclerViewAdapter.addItems(bookList);
                bookRecyclerViewAdapter.notifyDataSetChanged();
                break;
            case 2:
                Collections.sort(bookList, Book.SortBookTitleZtoA);
                bookRecyclerViewAdapter.addItems(bookList);
                bookRecyclerViewAdapter.notifyDataSetChanged();
                break;
            case 4:
                Collections.sort(bookList, Book.SortBookDateDESC);
                bookRecyclerViewAdapter.addItems(bookList);
                bookRecyclerViewAdapter.notifyDataSetChanged();
                break;
            default:
                Collections.sort(bookList, Book.SortBookDateASC);
                bookRecyclerViewAdapter.addItems(bookList);
                bookRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

}