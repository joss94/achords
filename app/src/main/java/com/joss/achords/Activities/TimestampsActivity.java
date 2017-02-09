package com.joss.achords.Activities;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;

import java.util.UUID;

public class TimestampsActivity extends AbstractParentActivity {

    Song mSong;
    Song mEditedSong;
    int mCurrentLine = 0;
    boolean recording=false;
    long previousClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timestamps);
        UUID song_id = (UUID)getIntent().getExtras().get(SongbookActivity.EXTRA_SONG_ID);
        mSong = Songbook.get(getApplicationContext()).getById(song_id);
        mEditedSong=mSong.copy();

        final TextView text = (TextView)findViewById(R.id.timestamps_text);
        final TextView next = (TextView)findViewById(R.id.next_line);
        if (mSong.getLyrics().isEmpty()) {
            Toast.makeText(getApplicationContext(), "No lyrics in the song...", Toast.LENGTH_LONG).show();
            finish();
        }

        next.setText(mSong.getLyrics().get(0).getText());

        final Button ok_button = (Button)findViewById(R.id.timestamps_OK_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!recording){
                    recording=true;
                    previousClick = System.currentTimeMillis();
                    ok_button.setText("OK");
                    text.setText(mSong.getLyrics().get(0).getText());
                    if(mCurrentLine+1<mSong.getLyrics().size()){
                        next.setText(mSong.getLyrics().get(mCurrentLine+1).getText());
                    }
                    else{
                        next.setText("Click finish to save");
                    }
                }

                else if(mCurrentLine<=mEditedSong.getLyrics().size()-1){
                    mEditedSong.getLyrics().get(mCurrentLine).setDuration((int)(System.currentTimeMillis()- previousClick));
                    previousClick = System.currentTimeMillis();
                    mCurrentLine++;
                    if(mCurrentLine<mSong.getLyrics().size()){
                        text.setText(mEditedSong.getLyrics().get(mCurrentLine).getText());
                    }
                    else{
                        text.setText("Click finish to save");
                        next.setText("");
                    }
                    if(mCurrentLine+1<mSong.getLyrics().size()){
                        next.setText(mSong.getLyrics().get(mCurrentLine+1).getText());
                    }
                    else{
                        next.setText("Click finish to save");
                    }
                }

                if(mCurrentLine==mEditedSong.getLyrics().size()-1){
                    ((Button)v).setText("FINISH");
                }

                if(mCurrentLine>=mEditedSong.getLyrics().size()){
                    saveModifications();
                    finish();
                }
            }
        });

        Button cancel_button = (Button)findViewById(R.id.timestamps_cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void saveModifications(){
        Songbook.get(getApplicationContext()).updateSong(mEditedSong);
    }
}
