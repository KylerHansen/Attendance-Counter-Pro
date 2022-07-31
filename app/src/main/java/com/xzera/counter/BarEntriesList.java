package com.xzera.counter;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.List;

public class BarEntriesList {
    private List<BarEntry> barEntries;
    private String bookName;

    public BarEntriesList(List<BarEntry> barEntries, String bookName) {
        this.barEntries = barEntries;
        this.bookName = bookName;
    }

    public List<BarEntry> getEntries() {
        return barEntries;
    }

    public void setEntries(List<BarEntry> barEntries) {
        this.barEntries = barEntries;
    }

    public String getBookName() {
        return bookName;
    }

    @Override
    public String toString() {
        return "BarEntriesList{" +
                "entries=" + barEntries.toString() +
                "bookName=" + bookName +
                '}';
    }
}
