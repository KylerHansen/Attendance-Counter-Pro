package com.xzera.counter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity implements CreateNewBookDialog.CreateNewBookDialogDismissed  {

    Toolbar toolbar;
    FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String themeName = preferences.getString("THEME_NAME", "FreshTheme");
//
//        switch(themeName) {
//            case "FreshTheme":
//                setTheme(R.style.FreshTheme);
//            break;
//            case "DarkTheme":
//                setTheme(R.style.DarkTheme);
//            break;
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        fm = getSupportFragmentManager();

            fm.beginTransaction()
                    .add(R.id.container_mainButtons, new MainButtons(), "MainButtons")
                    .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.nav_main, menu);
     return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.mnu_main_Settings:
                fm.beginTransaction()
                        .replace(android.R.id.content, new SettingsDialog())
                        .hide(fm.findFragmentById(R.id.container_mainButtons))
                        .addToBackStack(null)
                        .commit();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void CreateNewBookDialogClosed(long newBookID) {
        if(newBookID != -1){
            ((SaveCountDialog)fm.findFragmentByTag("tag_SaveCountDialog")).SetSpinnerItem(newBookID);
        }

    }


}