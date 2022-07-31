package com.xzera.counter;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import java.util.List;

public class EntriesList {
    private List<Entry> entries;
    private String bookName;

    public EntriesList(List<Entry> entries, String bookName) {
        this.entries = entries;
        this.bookName = bookName;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public String getBookName() {
        return bookName;
    }

    @Override
    public String toString() {
        return "EntriesList{" +
                "entries=" + entries.toString() +
                "bookName=" + bookName +
                '}';
    }
}
