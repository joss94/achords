package com.joss.achords.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        args.putSerializable(SongbookFragment.EXTRA_SONG_ID, song_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        songbook = Songbook.get(mContext);
        UUID song_id = (UUID) getArguments().getSerializable(SongbookFragment.EXTRA_SONG_ID);
        mSong = songbook.getById(song_id);
        mEditedSong = mSong.copy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timestamps_dialog, container, false);

        text = (TextView) v.findViewById(R.id.timestamps_text);
        next = (TextView) v.findViewById(R.id.next_line);
        if (mSong.getLyrics().isEmpty()) {
            Toast.makeText(mContext, "No lyrics in the song...", Toast.LENGTH_LONG).show();
            dismiss();
        }

        next.setText(mSong.getLyrics().get(0).getText());

        setDialogButtons(v);

        return v;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.OK_button:
                Button ok_button = (Button)v;
                if (!recording) {
                    recording = true;
                    previousClick = System.currentTimeMillis();
                    ok_button.setText("OK");
                    text.setText(mSong.getLyrics().get(0).getText());
                    if (mCurrentLine + 1 < mSong.getLyrics().size()) {
                        next.setText(mSong.getLyrics().get(mCurrentLine + 1).getText());
                    } else {
                        next.setText("Click finish to save");
                    }
                } else if (mCurrentLine <= mEditedSong.getLyrics().size() - 1) {
                    mEditedSong.getLyrics().get(mCurrentLine).setDuration((int) (System.currentTimeMillis() - previousClick));
                    previousClick = System.currentTimeMillis();
                    mCurrentLine++;
                    if (mCurrentLine < mSong.getLyrics().size()) {
                        text.setText(mEditedSong.getLyrics().get(mCurrentLine).getText());
                    } else {
                        text.setText("Click finish to save");
                        next.setText("");
                    }
                    if (mCurrentLine + 1 < mSong.getLyrics().size()) {
                        next.setText(mSong.getLyrics().get(mCurrentLine + 1).getText());
                    } else {
                        next.setText("Click finish to save");
                    }
                }

                if (mCurrentLine == mEditedSong.getLyrics().size() - 1) {
                    ((Button) v).setText("FINISH");
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
