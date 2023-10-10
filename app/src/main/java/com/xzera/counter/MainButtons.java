package com.xzera.counter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainButtons extends Fragment implements CreateNewBookDialog.CreateNewBookDialogDismissed {
    View view;
    FragmentManager fm;

    Button btnStartNewCount, btnViewBooks, btnCreateReport;
    public MainButtons() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_buttons, container, false);

        fm = getParentFragmentManager();

        btnStartNewCount =  view.findViewById(R.id.btnStartNewCount);
        btnCreateReport = view.findViewById(R.id.btnCreateReport);
        btnViewBooks = view.findViewById(R.id.btnViewBooks);


        btnStartNewCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions() == true) {
                    fm.clearFragmentResultListener(null);
                    fm.beginTransaction()
                            .replace(android.R.id.content, new CountScreenDialog())
                            .hide(fm.findFragmentById(R.id.container_mainButtons))
                            .addToBackStack(null)
                            .commit();
                }else{

                }
            }
        });


        btnViewBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions() == true) {
                    fm.beginTransaction()
                            .replace(android.R.id.content, new BookListDialog())
                            .hide(fm.findFragmentById(R.id.container_mainButtons))
                            .addToBackStack(null)
                            .commit();
                }else{

                }
            }
        });


        btnCreateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions() == true) {
                    fm.beginTransaction()
                            .replace(android.R.id.content, new CreateReportDialog())
                            .hide(fm.findFragmentById(R.id.container_mainButtons))
                            .addToBackStack(null)
                            .commit();
                }else{

                }
            }
        });

        return view;
    }


    @Override
    public void CreateNewBookDialogClosed(long newBookID) {
        //not called here.
    }


    public boolean checkPermissions(){


    //TODO: LEARN HOW TO REQUEST PERMISSION
    return true;

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if(this.getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
//                return false;
//            }
//
//            if(this.getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
//                return false;
//            }
//
//            return true;
//
//        }else{
//            Toast.makeText(this.getContext(), "Permission granted", Toast.LENGTH_SHORT);
//            return true;
//        }
    }


}