package com.joss.achords.Fragments;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.joss.achords.Models.Chord;
import com.joss.achords.Models.Lyrics;
import com.joss.achords.Models.LyricsLine;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;
import com.joss.achords.Utils.ChordSpan;

import java.util.ArrayList;
import java.util.UUID;


public class DisplayFragment extends Fragment{
    Song mSong;
    TextView mDisplayName;
    TextView mDisplayArtistDate;
    private LinearLayout mDisplayLyricsLayout;
    private ScrollView mScrollView;
    private ImageButton mScrollButton;
    private Button mChordButton;
    private View mLine;
    private Lyrics mLyrics;
    private ArrayList<Integer> linesCoordinates = new ArrayList<>();

    private int currentLine;
    private CountDownTimer mCountDownTimer;
    private boolean mScrolling=false;
    private boolean mDisplayChord=true;
    private Context mContext;
    private UUID id;

    private double scrollBuffer;


    public DisplayFragment() {
        // Required empty public constructor
    }

    public static DisplayFragment newInstance(UUID id) {
        DisplayFragment fragment = new DisplayFragment();
        Bundle args = new Bundle();
        args.putSerializable(SongbookFragment.EXTRA_SONG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Songbook.get(getActivity()).setOnSongbookChangeListener(new Songbook.OnSongbookChangeListener() {
            @Override
            public void onDBChange() {
                refresh();
            }
        });

        id = (UUID)getArguments().getSerializable(SongbookFragment.EXTRA_SONG_ID);
        mSong = Songbook.get(getActivity()).getById(id);
        mLyrics=(mSong.getLyrics()).copy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_display, container, false);

        //<editor-fold desc="FIND VIEWS">
        mDisplayName=(TextView)v.findViewById(R.id.display_title);
        mDisplayArtistDate=(TextView)v.findViewById(R.id.display_artist_date);
        mDisplayLyricsLayout =(LinearLayout)v.findViewById(R.id.display_lyrics);
        mScrollView=(ScrollView)v.findViewById(R.id.scroll_view);
        mLine = v.findViewById(R.id.line);
        //</editor-fold>

        //<editor-fold desc="SETTING TITLES AND LYRICS">
        if(mSong!=null){
            mDisplayName.setText(mSong.getName());
            String artist;
            if(mSong.getArtist()==null || mSong.getArtist().isEmpty()){
                artist = "Unknown";
            }
            else{
                artist = mSong.getArtist();
            }
            String date = (mSong.getReleaseYear()==0)?"":(" - "+String.valueOf(mSong.getReleaseYear()));
            mDisplayArtistDate.setText(String.format(getString(R.string.artist_display_title),artist, date));
            displayLyrics();
        }
        else{
            mDisplayName.setText("No song....");
        }
        //</editor-fold>

        //<editor-fold desc="SCROLL BUTTON">
        mScrollButton = (ImageButton)v.findViewById(R.id.scroll_button);
        ((GradientDrawable)mScrollButton.getBackground()).setColor(getResources().getColor(R.color.Red));
        mScrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mScrolling){
                    scrollLyrics();
                }
                else{
                    stopScrolling();
                }

            }
        });
        //</editor-fold>

        //<editor-fold desc="CHORD BUTTON">
        mChordButton = (Button)v.findViewById(R.id.chord_button);
        ((GradientDrawable)mChordButton.getBackground()).setColor(getResources().getColor(R.color.DarkBlue));
        mChordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mDisplayChord){
                    mChordButton.setPaintFlags(mChordButton.getPaintFlags()&(~Paint.STRIKE_THRU_TEXT_FLAG));
                    mDisplayChord=true;
                    displayLyrics();
                }
                else{
                    mChordButton.setPaintFlags(mChordButton.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                    mDisplayChord=false;
                    displayLyrics();
                }

            }
        });
        //</editor-fold>

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    private void displayLyrics(){
        mDisplayLyricsLayout.removeAllViewsInLayout();
        linesCoordinates=new ArrayList<>();

        for (int i=0; i<mLyrics.size();i++){
            final LyricsLine lyricsLine = mLyrics.get(i);
            //<editor-fold desc="CREATING LYRICS VIEWS">

            //<editor-fold desc="CREATE AND SET TEXT VIEW">
            final TextView lineView = new TextView(mContext);
            lineView.setId(i);
            lineView.setTextSize(16);
            lineView.setPadding(0,0,0,0);
            lineView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            lineView.setTextIsSelectable(false);
            //</editor-fold>

            //<editor-fold desc="ADD CHORDS">
            final SpannableString spannable = new SpannableString(lyricsLine.getText());
            if (mDisplayChord) {
                for(Chord chord:lyricsLine.getChords()){
                    if(chord.getPosition()<spannable.length()-1){
                        spannable.setSpan(new ChordSpan(chord), chord.getPosition(), chord.getPosition()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (chord.getPosition()>0){
                        spannable.setSpan(new ChordSpan(chord), chord.getPosition()-1, chord.getPosition(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else{
                        spannable.setSpan(new ChordSpan(chord), chord.getPosition(), chord.getPosition(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            lineView.setText(spannable, TextView.BufferType.SPANNABLE);
            //</editor-fold>

            //<editor-fold desc="ADD TO LAYOUT">
            mDisplayLyricsLayout.addView(lineView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //</editor-fold>

            //</editor-fold>
            mDisplayLyricsLayout.invalidate();
        }

    }

    //Scroll lyrics at speed (in lines/s)
    public void scrollLyrics(){
        mScrolling=true;
        mScrollButton.setImageResource(android.R.drawable.ic_media_pause);

        if (linesCoordinates.isEmpty()) {
            for(int i=0; i<mDisplayLyricsLayout.getChildCount(); i++){
                linesCoordinates.add(mDisplayLyricsLayout.getChildAt(i).getTop());
            }
        }

        //<editor-fold desc="SET LINE MARKER">
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int lineHeightDp =1;
        int lineHeightPx = lineHeightDp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, lineHeightPx);
        lineParams.setMargins(0, mScrollView.getPaddingTop()+mDisplayLyricsLayout.getTop() + mDisplayLyricsLayout.getPaddingTop(), 0, 0);
        mLine.setLayoutParams(lineParams);
        //</editor-fold>

        mLine.setVisibility(View.VISIBLE);

        //<editor-fold desc="COMPUTE DURATION OF THE SONG AND CHECK FOR NULL TIMESTAMPS">
        int songDuration=0;
        for(LyricsLine lyricsLine:mSong.getLyrics()){
            songDuration += lyricsLine.getDuration();
            if(lyricsLine.getDuration()==0){
                Log.d("DISPLAY", "Line "+lyricsLine.getText()+" has a duration of 0");
                Toast.makeText(getContext(), "Some timestamps are not defined properly, please recalibrate the song", Toast.LENGTH_LONG).show();
                stopScrolling();
                return;
            }
        }
        //</editor-fold>

        final int dt = 40;
        scrollBuffer = 0;

        mCountDownTimer = new CountDownTimer(5*songDuration, dt) {
            public void onTick(long millisUntilFinished) {

                //<editor-fold desc="GET CURRENT LINE">
                for(int i=0;i<linesCoordinates.size();i++){
                    int lineCoordinate = linesCoordinates.get(i);
                    if (mScrollView.getScrollY()+linesCoordinates.get(0)>=lineCoordinate){
                        currentLine = i;
                    }
                }
                //</editor-fold>

                double speed = 1000.0/(mSong.getLyrics().get(currentLine).getDuration());

                double dx = mDisplayLyricsLayout.getChildAt(currentLine).getHeight()*speed*(dt/1000.0);
                if(dx+scrollBuffer<1){
                    scrollBuffer+=dx;
                }
                else{
                    mScrollView.smoothScrollBy(0, (int)(dx+scrollBuffer));
                    scrollBuffer=dx+scrollBuffer - (int)(dx+scrollBuffer);
                }
            }
            public void onFinish() {
                Toast.makeText(mContext, "the timer reached 5 times the duration of the song and stopped", Toast.LENGTH_SHORT).show();
                mScrolling=false;
                mCountDownTimer.cancel();
                mScrollButton.setImageResource(android.R.drawable.ic_media_play);
            }
        }.start();
    }

    public void stopScrolling(){
        mScrolling=false;
        if (mCountDownTimer!=null) {
            mCountDownTimer.cancel();
        }
        mScrollButton.setImageResource(android.R.drawable.ic_media_play);
        mLine.setVisibility(View.INVISIBLE);
    }

    public void refresh(){
        mSong = Songbook.get(getActivity()).getById(id);
        mLyrics=mSong.getLyrics().copy();;
        displayLyrics();
        mDisplayName.setText(mSong.getName());
        String artist;
        if(mSong.getArtist()==null || mSong.getArtist().isEmpty()){
            artist = "Unknown";
        }
        else{
            artist = mSong.getArtist();
        }
        String date = (mSong.getReleaseYear()==0)?"":(" - "+String.valueOf(mSong.getReleaseYear()));
        mDisplayArtistDate.setText(String.format(mContext.getResources().getString(R.string.artist_display_title),artist, date));
    }


    public void changeID(UUID new_id) {
        id = new_id;
        refresh();
    }
}
