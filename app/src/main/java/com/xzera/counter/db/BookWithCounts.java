package com.xzera.counter.db;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class BookWithCounts {
    @Embedded
    public Book book;
    @Relation(
            parentColumn = "book_id",
            entityColumn = "book_id"
    )

    public List<Count> counts;

    public BookWithCounts(Book book, List<Count> counts){
        this.book = book;
        this.counts = counts;
    }

}
