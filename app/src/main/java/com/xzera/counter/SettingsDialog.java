package com.xzera.counter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.prefs.Preferences;

public class SettingsDialog extends DialogFragment {

    View view;
    Toolbar toolbar;
    FragmentManager fm;
    Switch ToggleSound, ToggleVibration;
    ImageView ButtonDarkTheme, ButtonFreshTheme;
    AudioManager audioManager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings_dialog, container, false);
        fm = getParentFragmentManager();
        toolbar = view.findViewById(R.id.toolbar_settings);
        toolbar.setTitle(R.string.title_settings_toolbar);
        ToggleSound = view.findViewById(R.id.btn_toggleSound);
        ToggleVibration = view.findViewById(R.id.btn_toggleVibration);
        ButtonDarkTheme = view.findViewById(R.id.themeButton_dark);
        ButtonFreshTheme = view.findViewById(R.id.themeButton_fresh);
        audioManager = (AudioManager)view.getContext().getSystemService(Context.AUDIO_SERVICE);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true); //sets x button on dialog left in toolbar.
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        actionBar.setDisplayHomeAsUpEnabled(true); //this takes it back one level instead of going all the way to homepage.
        setHasOptionsMenu(true);

        ToggleSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //TODO: FIGURE OUT HOW TO TURN OFF SOUNDS AND VIBRATION.
                    audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_DTMF,AudioManager.ADJUST_MUTE,0);
                }
                    audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
                audioManager.adjustStreamVolume(AudioManager.STREAM_DTMF,AudioManager.ADJUST_UNMUTE,0);
            }
        });
//TODO: Didn't get this working decided to just turn off the button access to it.
        ButtonDarkTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              saveTheme("DarkTheme", v);
            }
        });

        ButtonFreshTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTheme("FreshTheme", v);
            }
        });

        return view;
    }

    public void saveTheme(String themeName, View v){
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(v.getContext());
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.clear().commit();
//        editor.putString("THEME_NAME", themeName);
//        editor.commit();

//        Toast toast_BookSaved = Toast.makeText(getContext(), themeName + " Theme selected", Toast.LENGTH_SHORT);
//        toast_BookSaved.show();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                fm.beginTransaction().show(fm.findFragmentById(R.id.container_mainButtons)).commit();
                dismiss();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }
}