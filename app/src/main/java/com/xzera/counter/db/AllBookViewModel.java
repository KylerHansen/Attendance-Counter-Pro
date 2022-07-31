package com.xzera.counter.db;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class AllBookViewModel extends ViewModel {
    private LiveData<List<Book>> bookList;
    private LiveData<List<String>> bookTitles;

    public LiveData<List<Book>> getBookList(Context c){
        if(bookList != null){
            return bookList;
        }else{
            return bookList = AppDatabase.getInstance(c).bookDAO().getAllBooks();
        }
    }

    public LiveData<List<String>> getBookTitles(Context c){
        if(bookTitles != null){
            return bookTitles;
        }else{
            return bookTitles = AppDatabase.getInstance(c).bookDAO().getAllBookTitles();
        }
    }
}
