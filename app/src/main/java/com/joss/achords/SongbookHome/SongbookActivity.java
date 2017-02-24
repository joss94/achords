package com.joss.achords.SongbookHome;


import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.joss.achords.AbstractParentActivity;
import com.joss.achords.AchordsTypefaces;
import com.joss.achords.OnDialogFragmentInteractionListener;
import com.joss.achords.SelectAdapter.OnAdapterSelectModeChangeListener;
import com.joss.achords.OnItemClickListener;
import com.joss.achords.SongEnvironment.SongActivity;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;

import java.util.ArrayList;

public class SongbookActivity extends AbstractParentActivity implements Songbook.OnSongbookChangeListener, OnAdapterSelectModeChangeListener, OnItemClickListener {

    private static final String TAG = "Songbook Fragment";

    private SongAdapter adapter;
    private RecyclerView recyclerView;
    private EditText editSearch;

    private ImageView deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songbook);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        ((TextView)toolbar.findViewById(R.id.title)).setTypeface(AchordsTypefaces.SONG_TITLE_FONT.typeface);
        setSupportActionBar(toolbar);
        setToolbarPadding(toolbar);
        configOverflowMenu(toolbar);
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

        deleteBtn =(ImageView)findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDeleteDialogFragment fr = ConfirmDeleteDialogFragment.newInstance();
                fr.setOnFragmentInteractionListener(new OnDialogFragmentInteractionListener() {
                    @Override
                    public void onFragmentInteraction(int requestCode, int resultCode, Object... args) {
                        if(requestCode==ConfirmDeleteDialogFragment.DELETE_SONG_CONFIRM_REQUEST_CODE && resultCode == RESULT_OK){
                            deleteSelected();
                            adapter.resetSelected();
                        }
                    }
                });
                fr.show(getSupportFragmentManager(), "DELETE_CONFIRM");
            }
        });

        Songbook.get(this).setOnSongbookChangeListener(this);

        //Set the adapter
        ArrayList<Song> mSongs = Songbook.get(this).getSongs();
        adapter = new SongAdapter(mSongs);
        adapter.setOnItemClickListener(this);
        adapter.setOnAdapterSelectModeChangeListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        editSearch = (EditText)findViewById(R.id.edit_search);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString());
            }
        });
    }

    @Override
    public void onDBChange() {
        adapter.refresh(Songbook.get(this).getSongs());
    }

    public void deleteSelected(){
        for(Song song : adapter.getSelected()){
            Songbook.get(this).deleteSong(song.getId());
        }

    }

    @Override
    public void onAdapterSelectModeChange(boolean selectMode) {
        if(selectMode){
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            deleteBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(int position) {
        Song clickedSong = adapter.getItems().get(position);
        Intent i = new Intent (this, SongActivity.class);
        i.putExtra(EXTRA_SONG_ID, clickedSong.getId());
        i.putExtra(SongActivity.EXTRA_MODE, SongActivity.EXTRA_DISPLAY_MODE);
        startActivity(i);
    }
}
