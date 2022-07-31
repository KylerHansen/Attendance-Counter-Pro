package com.xzera.counter.db;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;


@Entity
public class Book {

    @PrimaryKey(autoGenerate = true)
    private long book_id;

    private String book_title;
    private String date;

    public Book(String book_title, String date) {
        this.book_title = book_title;
        this.date = date;
    }

    public long getBook_id() {
        return book_id;
    }

    public void setBook_id(long book_id) {
        this.book_id = book_id;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Book{" +
                "book_id=" + book_id +
                ", book_title='" + book_title + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public static Comparator<Book> SortBookTitleAtoZ = new Comparator<Book>() {
        @Override
        public int compare(Book c1, Book c2) {
            return c1.getBook_title().compareTo(c2.getBook_title());
        }
    };

    public static Comparator<Book> SortBookTitleZtoA = new Comparator<Book>() {
        @Override
        public int compare(Book c1, Book c2) {
            return c2.getBook_title().compareTo(c1.getBook_title());
        }
    };


    public static Comparator<Book>SortBookDateASC = new Comparator<Book>() {
        @Override
        public int compare(Book c1, Book c2) {
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
                Log.e("Book.java", "Comparator SortBookDate"+ e.getMessage());
            }
            return 0;
        }
    };

    public static Comparator<Book>SortBookDateDESC = new Comparator<Book>() {
        @Override
        public int compare(Book c1, Book c2) {
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
                Log.e("Book.java", "Comparator SortBookDate"+ e.getMessage());
            }
            return 0;
        }
    };
}
