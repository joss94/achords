package com.joss.achords.SongEnvironment.ChordsEdition.Timestamps;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.joss.achords.AbstractDialogFragment;
import com.joss.achords.SongbookHome.SongbookActivity;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;

import java.util.UUID;

public class TimestampsDialogFragment extends AbstractDialogFragment {

    private Context mContext;
    Songbook songbook;
    Song mSong;
    Song mEditedSong;
    int mCurrentLine = 0;
    boolean recording = false;
    long previousClick;

    TextView text;
    TextView next;

    public TimestampsDialogFragment() {
        // Required empty public constructor
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
        mSong = songbook.getById(song_id);
        mEditedSong = mSong.copy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timestamps_dialog, container, false);

        findViews(v);
        setViews();

        if (mSong.getLyrics().isEmpty()) {
            Toast.makeText(mContext, "No lyrics in the song...", Toast.LENGTH_LONG).show();
            dismiss();
        }

        setDialogButtons(v);
        setTitle(v, mContext.getResources().getString(R.string.timestamps_dialog_title));

        return v;
    }

    public void findViews(View v){
        text = (TextView) v.findViewById(R.id.timestamps_text);
        next = (TextView) v.findViewById(R.id.next_line);
    }

    public void setViews(){
        next.setText(mSong.getLyrics().get(0).getText());
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
                    text.setText(mSong.getLyrics().get(mCurrentLine).getText().isEmpty()?"...":mSong.getLyrics().get(mCurrentLine).getText());
                    if (mCurrentLine + 1 < mSong.getLyrics().size()) {
                        next.setText(mSong.getLyrics().get(mCurrentLine+1).getText().isEmpty()?"...":mSong.getLyrics().get(mCurrentLine+1).getText());
                    } else {
                        next.setText(R.string.end_timestamps);
                    }
                } else if (mCurrentLine <= mEditedSong.getLyrics().size() - 1) {
                    mEditedSong.getLyrics().get(mCurrentLine).setDuration((int) (System.currentTimeMillis() - previousClick));
                    Log.d("TIMESTAMPS", "Duration recorded: "+mEditedSong.getLyrics().get(mCurrentLine).getDuration());
                    previousClick = System.currentTimeMillis();
                    mCurrentLine++;
                    if (mCurrentLine < mSong.getLyrics().size()) {
                        text.setText(mEditedSong.getLyrics().get(mCurrentLine).getText());
                    } else {
                        text.setText(R.string.end_timestamps);
                        next.setText("");
                    }
                    if (mCurrentLine + 1 < mSong.getLyrics().size()) {
                        next.setText(mSong.getLyrics().get(mCurrentLine+1).getText().isEmpty()?"...":mSong.getLyrics().get(mCurrentLine+1).getText());
                    } else {
                        next.setText(R.string.end_timestamps);
                    }
                }

                if (mCurrentLine == mEditedSong.getLyrics().size() - 1) {
                    ((Button) v).setText(R.string.finish);
                }

                if (mCurrentLine >= mEditedSong.getLyrics().size()) {
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
