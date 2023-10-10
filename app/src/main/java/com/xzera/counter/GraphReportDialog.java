package com.xzera.counter;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.xzera.counter.db.Book;
import com.xzera.counter.db.Count;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class GraphReportDialog extends DialogFragment {
    FragmentManager fm;
    View view;
    Toolbar toolbar;
    LineChart ln_chart;
    BarChart bar_chart;
    List<EntriesList> listOfEntries;
    List<BarEntriesList> listOfBarEntries;
    List<ILineDataSet> listOfDataSets;
    List<IBarDataSet>  listOfBarDataSets;
    Description chartTitle;
    Legend legend;
    List<Count> countList;
    List<Book> bookList;
    String[] xAxisLabels;
    String[] columnHeadings;
    String graphType;
    int xAxisType;
    int numberOfSelectedBooks;

    public GraphReportDialog(List<Count> countList, List<Book> bookList, String selectedGraphType, int xAxisType){
        this.countList = countList;
        this.bookList = bookList;
        this.graphType = selectedGraphType;
        this.xAxisType = xAxisType;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        view = inflater.inflate(R.layout.fragment_graph_report_dialog, container, false);
        fm = getParentFragmentManager();
        toolbar = view.findViewById(R.id.toolbar_graphReport);
        toolbar.setTitle(R.string.title_graphReport_Dialog);
        ln_chart = view.findViewById(R.id.graph_LineChart);
        bar_chart = view.findViewById(R.id.graph_BarChart);
        listOfEntries = new ArrayList<>();
        listOfBarEntries = new ArrayList<>();
        listOfDataSets = new ArrayList<>();
        listOfBarDataSets = new ArrayList<>();
        xAxisLabels = new String[countList.size()];
        numberOfSelectedBooks = bookList.size();

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true); //sets x button on dialog left in toolbar.
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        actionBar.setDisplayHomeAsUpEnabled(true); //this takes it back one level instead of going all the way to homepage.
        setHasOptionsMenu(true);

        countList.sort(Count.SortCountDateDESC);

        if(Objects.equals(graphType, "Line Graph")){
            ln_chart.setVisibility(View.VISIBLE);
            bar_chart.setVisibility(View.GONE);

        }else if(Objects.equals(graphType, "Bar Graph")){
            bar_chart.setVisibility(View.VISIBLE);
            ln_chart.setVisibility(View.GONE);
        }

        for(Book book : bookList){
            List<Entry> entries = new ArrayList<Entry>();
            List<BarEntry> barEntries = new ArrayList<>();

            String bookName="";

            for(int i = 0; i<countList.size(); i++){
                if(countList.get(i).getBook_id() == book.getBook_id()) {
                    int total = countList.get(i).getTotal();
                    bookName = book.getBook_title();
                    if(Objects.equals(graphType, "Line Graph")) {

                        if(xAxisType == 1) {
                            xAxisLabels[i] = countList.get(i).getDate();
                        }else if(xAxisType == 2){
                            xAxisLabels[i] = countList.get(i).getCount_title();
                        }

                        Entry entry = new Entry(i, total);
                        entries.add(entry);

                    }else if(Objects.equals(graphType, "Bar Graph")) {
                        if(numberOfSelectedBooks > 1){
                            xAxisLabels[i] = "";
                        }else{
                            if(xAxisType == 1) {
                                xAxisLabels[i] = countList.get(i).getDate();
                            }else if(xAxisType == 2){
                                xAxisLabels[i] = countList.get(i).getCount_title();
                            }
                        }

                        BarEntry brEntry = new BarEntry(i, total);
                        barEntries.add(brEntry);
                    }
                }
            }
            if(Objects.equals(graphType, "Line Graph")) {
                EntriesList listOfEntriesList = new EntriesList(entries, bookName);
                listOfEntries.add(listOfEntriesList);
            }else if(Objects.equals(graphType, "Bar Graph")) {
                BarEntriesList listOfBarEntriesList = new BarEntriesList(barEntries, bookName);
                listOfBarEntries.add(listOfBarEntriesList);
            }
        }

        if(Objects.equals(graphType, "Line Graph")) {
            int c = 0;
            for (EntriesList countsOfBook : listOfEntries) {
                if (c == 3) {
                    c = 0;
                }
                LineDataSet dataSet = new LineDataSet(countsOfBook.getEntries(), countsOfBook.getBookName());
                dataSet.setColor(getResources().getColor(getDataSetColor(c)));
                dataSet.setValueTextColor(getResources().getColor(getDataSetColor(3)));
                dataSet.setValueTextSize(15);
                dataSet.setLineWidth(3);
                dataSet.setCircleColor(getResources().getColor(getDataSetColor(c)));
                dataSet.setCircleRadius(5);
                dataSet.setDrawCircleHole(false);
                listOfDataSets.add(dataSet);
                c++;
            }

            LineData lineData = new LineData(listOfDataSets);
            ln_chart.setData(lineData);
            ln_chart.setDragYEnabled(true);
            ln_chart.invalidate(); //refreshes the chart
            ln_chart.setBackgroundColor(getResources().getColor(R.color.backGroundColor));
            ln_chart.setBorderWidth(20);
            ln_chart.fitScreen();

            chartTitle = ln_chart.getDescription();
            chartTitle.setEnabled(false);

        }else if (graphType == "Bar Graph") {

            int c = 0;
            for (BarEntriesList countsOfBook : listOfBarEntries) {
                if (c == 3) {
                    c = 0;
                }
                BarDataSet barDataSet = new BarDataSet(countsOfBook.getEntries(), countsOfBook.getBookName());
                barDataSet.setColor(getResources().getColor(getDataSetColor(c)));
                barDataSet.setBarShadowColor(getResources().getColor(getDataSetColor(c)));
                barDataSet.setValueTextColor(getResources().getColor(getDataSetColor(c)));
                barDataSet.setValueTextSize(15);
                barDataSet.setBarBorderColor(getResources().getColor(getDataSetColor(c)));
                listOfBarDataSets.add(barDataSet);
                c++;
            }

            BarData barData = new BarData(listOfBarDataSets);
            barData.setBarWidth(0.7f);
            bar_chart.setData(barData);
            bar_chart.setFitBars(true);
            if(numberOfSelectedBooks > 1) {
                bar_chart.groupBars(0.01f, 0.5f, 0.1f);
            }
            bar_chart.setBackgroundColor(getResources().getColor(R.color.backGroundColor));
            bar_chart.invalidate();

            chartTitle = bar_chart.getDescription();
            chartTitle.setEnabled(false);
        }


        legend = ln_chart.getLegend();
        legend.setTextSize(15);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setXEntrySpace(20f);
        legend.setEnabled(true);



        String[] xLabels = xAxisLabels;
            ValueFormatter formatter = new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    int ival = (int)value;
                   if(( ival < xLabels.length && ival >= 0 )){
                        return xLabels[(int) value];
                    }
                   return "";
                }
            };


        if(graphType == "Line Graph") {
            XAxis xAxis = ln_chart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setTextSize(12f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setLabelRotationAngle(67);
            xAxis.setValueFormatter(formatter);
            xAxis.setAxisMinimum(0);

            YAxis rightYaxis = ln_chart.getAxisRight();
            rightYaxis.setEnabled(false);

            YAxis leftYaxis = ln_chart.getAxisLeft();
            leftYaxis.setGranularity(1f);
            leftYaxis.setAxisMinimum(0);
            leftYaxis.setTextSize(13);

        }else  if(graphType == "Bar Graph") {
            XAxis xAxis = bar_chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setTextSize(12f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setLabelRotationAngle(67);
            xAxis.setValueFormatter(formatter);
            xAxis.setAxisMinimum(-0.5f);
            xAxis.setAxisMaximum(countList.size()+0.5f);
            xAxis.setDrawGridLines(false);

            YAxis rightYaxis = bar_chart.getAxisRight();
            rightYaxis.setEnabled(false);

            YAxis leftYaxis = bar_chart.getAxisLeft();
            leftYaxis.setGranularity(1f);
            leftYaxis.setAxisMinimum(0);
            leftYaxis.setTextSize(13);
        }

        return view;
    }

    private int getDataSetColor(int i){
        switch(i){
            case 0:
                return R.color.colorPrimary;
            case 1:
                return R.color.colorAccent;
            case 2:
                return R.color.errorRed;
            default:
                return R.color.colorPrimaryDark;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.nav_graph_report, menu);

    }

    @NonNull

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  //removes the title from the navbar.
        return dialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnu_txtmsg_report:
                takeScreenShot();
                break;
            case R.id.mnu_make_excel:
                createCSVFile();
                break;
            case android.R.id.home:
                fm.popBackStack();
                dismiss();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }


    public void takeScreenShot(){
        View root = this.getActivity().getWindow().getDecorView().getRootView();
        root.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(root.getDrawingCache());
        root.setDrawingCacheEnabled(false);

        String filepath = Environment.getExternalStorageDirectory()+"/Download/"+ Calendar.getInstance().getTime().toString().replaceAll(":", ".")+".jpg";
        File fileScreenShot = new File(filepath);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(fileScreenShot);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            Log.e("takeScreenShot Error:", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.graph_text_message)+Calendar.getInstance().getTime().toString());
        Uri uri = FileProvider.getUriForFile(this.getContext(),"com.xzera.counter", fileScreenShot);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/jpeg");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createCSVFile(){
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet(getString(R.string.Excel_Sheet_Title));

        int rowPosition = 0;

        for(int i = 0; i < bookList.size(); i++){
            Book book = bookList.get(i);

            HSSFRow headerRow = hssfSheet.createRow(rowPosition);
            rowPosition++;
            HSSFCell bookTitleCell = headerRow.createCell(0);
            bookTitleCell.setCellValue(book.getBook_title());

            List<Count> countsInBook = countList.stream().filter(c -> c.getBook_id() == book.getBook_id()).collect(Collectors.toList());
            int bookCountTotal = 0;

            for(int j = 1; j < countsInBook.size(); j++){
                HSSFRow countRow = hssfSheet.createRow(rowPosition);
                rowPosition++;
                HSSFCell firstCountCell = countRow.createCell(0);
                if(xAxisType == 1){
                    firstCountCell.setCellValue(countsInBook.get(j-1).getDate());
                }else{
                    firstCountCell.setCellValue(countsInBook.get(j-1).getCount_title());
                }
                HSSFCell secondCountCell = countRow.createCell(1);
                secondCountCell.setCellValue(countsInBook.get(j-1).getTotal());
                bookCountTotal += countsInBook.get(j-1).getTotal();
            }

            HSSFRow footerRow = hssfSheet.createRow(rowPosition);
            rowPosition++;
            HSSFCell firstCell = footerRow.createCell(0);
            HSSFCell secondCell = footerRow.createCell(1);
            firstCell.setCellValue("Total:");
            secondCell.setCellValue(bookCountTotal);

            HSSFRow spaceRow = hssfSheet.createRow(rowPosition);
            rowPosition++;
        }

        try{
           String nowDateTime = System.currentTimeMillis()+"";
            File filePath = new File(Environment.getExternalStorageDirectory()+"/Download/"+"CounterPro_"+nowDateTime+".xls");

            if(!filePath.exists()){
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            Uri uri = FileProvider.getUriForFile(this.getContext(),"com.xzera.counter", filePath);
            emailIntent.setType("application/excel");
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.Email_Subject_Line));
            emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(emailIntent);

        }catch(Exception e){
            Log.e("GraphReportDialog", "Creating Excel Sheet File Output Stream Error "+ e.getMessage());
        }



    }


}