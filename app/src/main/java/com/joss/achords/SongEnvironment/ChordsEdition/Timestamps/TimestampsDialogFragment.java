package com.joss.achords.SongEnvironment.ChordsEdition.Timestamps;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.joss.achords.Models.Lyrics;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;
import com.joss.achords.SongbookHome.SongbookActivity;
import com.joss.utils.AbstractDialog.AbstractDialogFragment;

import java.util.UUID;

public class TimestampsDialogFragment extends AbstractDialogFragment {

    private Context mContext;
    Songbook songbook;
    Lyrics lyrics;
    Song mEditedSong;
    int mCurrentLine = 0;
    boolean recording = false;
    long previousClick;

    TextView text;
    TextView next;

    public TimestampsDialogFragment() {
        setLayoutId(R.layout.fragment_timestamps_dialog);
    }

    public static TimestampsDialogFragment newInstance(UUID song_id) {
        TimestampsDialogFragment fragment = new TimestampsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(SongbookActivity.EXTRA_SONG_ID, song_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        songbook = Songbook.get(mContext);
        UUID song_id = (UUID) getArguments().getSerializable(SongbookActivity.EXTRA_SONG_ID);
        mEditedSong = songbook.getById(song_id).copy();
        lyrics = mEditedSong.getLyrics();
        if (lyrics.isEmpty()) {
            Toast.makeText(mContext, R.string.no_lyrics, Toast.LENGTH_LONG).show();
            dismiss();
        }
    }


    @Override
    protected void findViews(View v){
        super.findViews(v);
        text = (TextView) v.findViewById(R.id.timestamps_text);
        next = (TextView) v.findViewById(R.id.next_line);
    }

    @Override
    protected void setViews(){
        super.setViews();
        setTitle(getContext().getResources().getString(R.string.timestamps_dialog_title));
        next.setText(lyrics.get(0).getText());
    }


    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.OK_button:
                Button ok_button = (Button)v;
                if (!recording) {
                    recording = true;
                    previousClick = System.currentTimeMillis();
                    ok_button.setText(R.string.ok);
                    text.setText(next.getText());
                    if (mCurrentLine + 1 < lyrics.size()) {
                        next.setText(lyrics.get(mCurrentLine+1).getText().isEmpty()?"...":lyrics.get(mCurrentLine+1).getText());
                    } else {
                        next.setText(R.string.end_timestamps);
                    }
                } else if (mCurrentLine <= lyrics.size() - 1) {
                    lyrics.get(mCurrentLine).setDuration((int) (System.currentTimeMillis() - previousClick));
                    previousClick = System.currentTimeMillis();
                    mCurrentLine++;
                    if (mCurrentLine < lyrics.size()) {
                        text.setText(next.getText());
                    } else {
                        text.setText(next.getText());
                        next.setText("");
                    }
                    if (mCurrentLine + 1 < lyrics.size()) {
                        next.setText(lyrics.get(mCurrentLine+1).getText().isEmpty()?"...":lyrics.get(mCurrentLine+1).getText());
                    } else {
                        next.setText(R.string.end_timestamps);
                    }
                }

                if (mCurrentLine == lyrics.size() - 1) {
                    ((Button) v).setText(R.string.finish);
                }

                if (mCurrentLine >= lyrics.size()) {
                    saveModifications();
                    dismiss();
                }
                break;

            case R.id.cancel_button:
                dismiss();
                break;
        }
    }

    public void saveModifications() {
        songbook.updateSong(mEditedSong);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
