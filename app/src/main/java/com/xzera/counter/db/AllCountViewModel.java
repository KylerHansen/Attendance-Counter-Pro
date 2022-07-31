package com.xzera.counter.db;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class AllCountViewModel extends ViewModel {
    private LiveData<List<Count>> countList;
    //private LiveData<List<String>> countDateRange;

    public LiveData<List<Count>> getCountList(Context c, long BookID) {
        if (countList != null) {
            return countList;
        } else {
            return countList = AppDatabase.getInstance(c).countDAO().getCountsByBookID(BookID);
        }
    }

//    public LiveData<List<String>> getCountDates(Context c, long BookID){
//        if(countDateRange != null){
//            return countDateRange;
//        }else{
//            return countDateRange = AppDatabase.getInstance(c).countDAO().getCountDatesInBook(BookID);
//        }
//    }
}
