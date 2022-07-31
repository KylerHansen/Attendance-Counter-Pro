package com.xzera.counter.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDAO {

    @Query("select * from Book")
    LiveData<List<Book>> getAllBooks();

    @Query("select * from Book where book_title =:BookTitle ")
    Book  getBookByTitle(String BookTitle);

    @Query("select book_title from Book")
    LiveData<List<String>> getAllBookTitles();

    @Query("select book_title from Book")
    List<String> getAllBookTitlesNL();

    @Insert
    long insertBook(Book book);

    @Insert
    long insertCount(Count count);

    @Update
    void updateBook(Book book);

    @Delete
    void deleteBook(Book book);

}
