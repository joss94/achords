package com.joss.achords.Activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import com.joss.achords.Fragments.SongbookFragment;
import com.joss.achords.R;

public class SongbookActivity extends AbstractParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songbook);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.activity_songbook);
        if (fragment == null) {
            fragment = SongbookFragment.newInstance();
            fm.beginTransaction().add(R.id.activity_songbook, fragment).commit();
        }
    }
}
