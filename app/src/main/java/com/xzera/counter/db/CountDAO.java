package com.xzera.counter.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CountDAO {

    @Query("select * from Count")
    List<Count> getAllCounts();

    @Query("select * from Count where count_id =:id")
    List<Count> getCountByID(long id);

    @Query("select * from Count where book_id=:bookID")
    LiveData<List<Count>> getCountsByBookID(long bookID);

    @Query("select distinct date from Count where book_id=:bookID")
    List<String> getCountDatesInBook(long bookID);

    @Query("select * from Count where book_id=:bookID")
    List<Count> getCountsByBookIDNL(long bookID);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCount(Count count);

    @Delete
    void deleteCount(Count count);

    @Insert
    void insertCount(Count... courses);

}
