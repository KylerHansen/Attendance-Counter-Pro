package com.xzera.counter.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface BookWithCountsDAO {
    @Transaction
    @Insert
    long insertBook(Book book);

    @Insert
    void insertCounts(List<Count> counts);
}
