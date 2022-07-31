package com.xzera.counter.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Count.class, Book.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context){
        if(instance != null){
            return instance;
        }else{
            instance = Room.databaseBuilder(context, AppDatabase.class, "AppDatabase_database")
                    .build();
            return instance;
        }
    }

    public abstract BookDAO bookDAO();
    public abstract CountDAO countDAO();

}
