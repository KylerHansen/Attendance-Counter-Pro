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
import com.xzera.counter.db.Count;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CountRecyclerViewAdapter extends RecyclerView.Adapter<CountRecyclerViewAdapter.ViewHolder> {
    public final List<Count> counts;

    public CountRecyclerViewAdapter(List<Count> counts) {
        this.counts = counts;
    }

    public void addItems(List<Count> counts) {
        this.counts.clear();
        this.counts.addAll(counts);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public TextView txtCountTitle, txtCountDate, txtCountAmount;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            view = itemView;
            txtCountTitle = view.findViewById(R.id.ri_textCountTitle);
            txtCountDate = view.findViewById(R.id.ri_textCountDate);
            txtCountAmount = view.findViewById(R.id.ri_textCountTotal);
        }

    }

    @NonNull
    @NotNull
    @Override
    public CountRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_count,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CountRecyclerViewAdapter.ViewHolder holder, int position) {
        final Count count = counts.get(position);
        if(count != null){
            holder.txtCountTitle.setText(count.getCount_title());
            holder.txtCountDate.setText(count.getDate().toString());
            holder.txtCountAmount.setText(count.getTotal()+"");

            holder.view.findViewById(R.id.btn_deleteCount).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.alertDialogStyle);
                    builder.setTitle("Are you sure you want to delete this count?")
                            .setMessage("This will be permanently deleted.")
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
                                                AppDatabase.getInstance(v.getContext()).countDAO().deleteCount(count);
                                            } catch (Exception e) {
                                                Log.e("CountRecyclerViewAdpter", "Delete Count Error " + e.getMessage());
                                            }
                                        }
                                    }).start();

                                    dialog.dismiss();
                                }
                            }).show();
                }
            });

            holder.view.findViewById(R.id.btn_increment1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int total = count.getTotal();
                            total++;
                            count.setTotal(total);
                            AppDatabase.getInstance(v.getContext()).countDAO().updateCount(count);
                        }
                    }).start();
                    CountRecyclerViewAdapter.this.notifyDataSetChanged();
                }
            });

            holder.view.findViewById(R.id.btn_decrement1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int total = count.getTotal();
                            total--;
                            count.setTotal(total);
                            AppDatabase.getInstance(v.getContext()).countDAO().updateCount(count);
                        }
                    }).start();
                    CountRecyclerViewAdapter.this.notifyDataSetChanged();
                }
            });

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putLong("editCount_id", count.getCount_id());
                    bundle.putLong("editCount_bookID", count.getBook_id());
                    bundle.putString("editCount_title",count.getCount_title());
                    bundle.putInt("editCount_total", count.getTotal());
                    bundle.putString("editCount_date", count.getDate());

                    CountScreenDialog countScreenDialog = new CountScreenDialog();
                    countScreenDialog.setArguments(bundle);

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, countScreenDialog)
                            .addToBackStack(null)
                            .commit();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return counts.size();
    }
}
