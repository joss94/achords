package com.joss.achords.Settings;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.joss.achords.AchordsActivity;
import com.joss.achords.AchordsTypefaces;
import com.joss.achords.R;

public class MySettingsActivity extends AchordsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ((TextView)(findViewById(R.id.toolbar)).findViewById(R.id.title)).setTypeface(AchordsTypefaces.SONG_TITLE_FONT.typeface);
    }

}
