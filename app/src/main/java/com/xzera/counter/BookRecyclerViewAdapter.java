package com.xzera.counter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.xzera.counter.db.AppDatabase;
import com.xzera.counter.db.Book;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder> {

    public final List<Book> books;

    public BookRecyclerViewAdapter(List<Book> books) {
        this.books = books;
    }

    public void addItems(List<Book> books) {
        this.books.clear();
        this.books.addAll(books);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        //public Book book;
        public TextView txtBookTitle, txtBookDate;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            view = itemView;
            txtBookTitle = view.findViewById(R.id.ri_textBookTitle);
            txtBookDate = view.findViewById(R.id.ri_textBookDate);
        }

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_book,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        final Book book = books.get(position);
        if(book != null){
            holder.txtBookTitle.setText(book.getBook_title());
            holder.txtBookDate.setText(book.getDate().toString());

            holder.view.findViewById(R.id.btn_deleteBook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.alertDialogStyle);
                    builder.setTitle("Are you sure you want to delete this book?")
                            .setMessage("All counts stored in this book will be permanently deleted.")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                 AppDatabase.getInstance(v.getContext()).bookDAO().deleteBook(book);
                                            } catch (Exception e) {
                                                Log.e("BookRecyclerViewAdapter", "Delete Book Error " + e.getMessage());
                                            }
                                        }
                                    }).start();

                                    dialog.dismiss();
                                }
                            }).show();

                }
            });

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("bookTitle", book.getBook_title());
                    bundle.putLong("bookID", book.getBook_id());
                    EditBookDialog editBookDialog = new EditBookDialog();
                    editBookDialog.setArguments(bundle);

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, editBookDialog)
                            .addToBackStack(null)
                            .commit();
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
