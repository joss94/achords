package com.joss.achords.Activities;


import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.joss.achords.Adapters.SongAdapter;
import com.joss.achords.Interfaces.OnSongItemClickListener;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;

import java.util.ArrayList;

public class SongbookActivity extends AbstractParentActivity implements Songbook.OnSongbookChangeListener, OnSongItemClickListener {

    private static final String TAG = "Songbook Fragment";

    private SongAdapter adapter;
    private RecyclerView recyclerView;

    private boolean selectMode=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songbook);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();



        //Setting Button
        ImageButton newSongButton=(ImageButton) findViewById(R.id.new_song_button);
        ((GradientDrawable)newSongButton.getBackground()).setColor(getResources().getColor(R.color.Red));
        newSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), SongActivity.class);
                i.putExtra(SongActivity.EXTRA_MODE, SongActivity.EXTRA_CREATE_MODE);
                startActivity(i);
            }
        });

        Songbook.get(this).setOnSongbookChangeListener(this);

        //Set the adapter
        ArrayList<Song> mSongs = Songbook.get(this).getSongs();
        adapter = new SongAdapter(this, mSongs, this);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.delete_menu:
                deleteSelected();
                selectMode=false;
                menu.removeItem(R.id.delete_menu);
                break;
        }
        return false;
    }

    @Override
    public void onDBChange() {
        adapter.refresh(Songbook.get(this).getSongs());
    }

    @Override
    public void onSongClick(View v, int position) {
        Song clickedSong = adapter.getSongs().get(position);
        if(!selectMode){
            Log.i(TAG, "ListItem Clicked !!");
            Intent i = new Intent (this, SongActivity.class);
            i.putExtra(EXTRA_SONG_ID, clickedSong.getId());
            i.putExtra(SongActivity.EXTRA_MODE, SongActivity.EXTRA_DISPLAY_MODE);
            startActivity(i);
        }
        else {
            adapter.changeSelectedState(position);
            if(adapter.getSelected().isEmpty()){
                selectMode=false;
                menu.removeItem(R.id.delete_menu);
            }
        }

    }


    @Override
    public void onSongLongClick(View v, int position) {
        Log.d("SONGBOOK FR", "onSongLongClick called, selectmode before = "+selectMode);
        if(!selectMode){
            selectMode=true;

            menu.add(Menu.NONE, R.id.delete_menu, Menu.NONE, "Delete")
                    .setIcon(R.drawable.ic_delete)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        }
        adapter.addToSelection(position);
    }

    public void deleteSelected(){
        for(Song song : adapter.getSelected()){
            Songbook.get(this).deleteSong(song.getId());
        }
    }
}
