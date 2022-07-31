package com.xzera.counter.db;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;

@Entity
public class Count {


    @PrimaryKey(autoGenerate = true)
    private long count_id;

    private long book_id;
    private String count_title;
    private String date;
    private int total;

    public Count(long book_id, String count_title, String date, int total) {
        this.book_id = book_id;
        this.count_title = count_title;
        this.date = date;
        this.total = total;
    }

    public long getCount_id() {
        return count_id;
    }

    public void setCount_id(long count_id) {
        this.count_id = count_id;
    }

    public long getBook_id() {
        return book_id;
    }

    public void setBook_id(long book_id) {
        this.book_id = book_id;
    }

    public String getCount_title() {
        return count_title;
    }

    public void setCount_title(String count_title) {
        this.count_title = count_title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Count{" +
                "count_id=" + count_id +
                ", book_id=" + book_id +
                ", count_title='" + count_title + '\'' +
                ", date='" + date + '\'' +
                ", total=" + total +
                '}';
    }

    public static Comparator<Count>SortCountTitleAtoZ = new Comparator<Count>() {
        @Override
        public int compare(Count c1, Count c2) {
            return c1.getCount_title().compareTo(c2.getCount_title());
        }
    };

    public static Comparator<Count>SortCountTitleZtoA = new Comparator<Count>() {
        @Override
        public int compare(Count c1, Count c2) {
            return c2.getCount_title().compareTo(c1.getCount_title());
        }
    };

    public static Comparator<Count>SortCountTotalASC = new Comparator<Count>() {
        @Override
        public int compare(Count c1, Count c2) {
            return c1.getTotal() - c2.getTotal();
        }
    };

    public static Comparator<Count>SortCountTotalDESC = new Comparator<Count>() {
        @Override
        public int compare(Count c1, Count c2) {
            return c2.getTotal() - c1.getTotal();
        }
    };

    public static Comparator<Count>SortCountDateASC = new Comparator<Count>() {
        @Override
        public int compare(Count c1, Count c2) {
            try {
                String[] arrA = c1.getDate().split("/");
                String[] arrB = c2.getDate().split("/");

                int monthA = Integer.parseInt(arrA[0]);
                int dayA = Integer.parseInt(arrA[1]);
                int yearA = Integer.parseInt(arrA[2]);

                int monthB = Integer.parseInt(arrB[0]);
                int dayB = Integer.parseInt(arrB[1]);
                int yearB = Integer.parseInt(arrB[2]);

                if(yearA < yearB){ //if A comes before B
                    return 1;  //B appears first its oldest date
                }else if(yearA > yearB){ //if B comes before A
                    return -1; //A appears first because its older
                }else{
                    if(monthA < monthB){
                        return 1; //B appears first
                    }else if (monthA > monthB){
                        return -1; //A appears first
                    }else{
                        if(dayA < dayB){
                            return 1; //B appears first
                        }else if(dayA > dayB){
                            return -1; //A appears first
                        }else{
                            return 0;
                        }
                    }
                }
            }
            catch(Exception e){
                Log.e("Count.java", "Comparator SortCountDate"+ e.getMessage());
            }
            return 0;
        }
    };

    public static Comparator<Count>SortCountDateDESC = new Comparator<Count>() {
        @Override
        public int compare(Count c1, Count c2) {
            //negative value if we want c1 to appear first
            //positive value if c2 first.
            //0 if either order
            try {
                String[] arrA = c1.getDate().split("/");
                String[] arrB = c2.getDate().split("/");

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
                Log.e("Count.java", "Comparator SortCountDate"+ e.getMessage());
            }
            return 0;
        }
    };

}

