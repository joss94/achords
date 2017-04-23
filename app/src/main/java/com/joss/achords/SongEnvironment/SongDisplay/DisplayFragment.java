package com.joss.achords.SongEnvironment.SongDisplay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.joss.achords.LyricsDisplay.ChordSpan;
import com.joss.achords.Models.Chord;
import com.joss.achords.Models.Lyrics;
import com.joss.achords.Models.LyricsLine;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;
import com.joss.achords.SongEnvironment.ChordsEdition.ChordDialogFragment;
import com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords.ChordButton;
import com.joss.achords.SongbookHome.SongbookActivity;
import com.joss.utils.AbstractDialog.OnDialogFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;


public class DisplayFragment extends Fragment implements View.OnLongClickListener, OnDialogFragmentInteractionListener {
    private static final int SELECT_CHORD_REQUEST_CODE = 1;
    private Song mSong;
    private TextView mDisplayCapo;
    private LinearLayout mDisplayLyricsLayout;
    private ScrollView mScrollView;
    private ImageButton mScrollButton;
    private ChordButton mChordButton;
    private View mLine;
    private Lyrics mLyrics;
    private ArrayList<Integer> linesCoordinates = new ArrayList<>();
    private int toneOffset;

    long precedent=0;

    private int currentLine;
    private CountDownTimer mCountDownTimer;
    private boolean mScrolling=false;
    private boolean mDisplayChord=true;
    private Context mContext;
    private UUID id;

    private float scrollBuffer;


    public DisplayFragment() {
        // Required empty public constructor
    }

    public static DisplayFragment newInstance(UUID id) {
        DisplayFragment fragment = new DisplayFragment();
        Bundle args = new Bundle();
        args.putSerializable(SongbookActivity.EXTRA_SONG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Songbook.get(getActivity()).addOnSongbookChangeListener(new Songbook.OnSongbookChangeListener() {
            @Override
            public void onDBChange() {
                refresh();
            }
        });

        id = (UUID)getArguments().getSerializable(SongbookActivity.EXTRA_SONG_ID);
        mSong = Songbook.get(getActivity()).getById(id);
        mLyrics=(mSong.getLyrics()).copy();
        toneOffset=0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_display, container, false);
        findViews(v);
        setViews();
        setActions();

        return v;
    }

    public void findViews(View v){
        mDisplayLyricsLayout =(LinearLayout)v.findViewById(R.id.display_lyrics);
        mScrollView=(ScrollView)v.findViewById(R.id.scroll_view);
        mLine = v.findViewById(R.id.line);
        mDisplayCapo = (TextView) v.findViewById(R.id.display_capo);
        mScrollButton = (ImageButton)v.findViewById(R.id.scroll_button);
        mChordButton = (ChordButton)v.findViewById(R.id.chord_button);
    }

    @SuppressWarnings("deprecation")
    public void setViews(){
        if(mSong!=null){
            if (mSong.getCapo()!= 0) {
                mDisplayCapo.setVisibility(View.VISIBLE);
                mDisplayCapo.setText(String.format(Locale.ENGLISH, mContext.getString(R.string.capo), mSong.getCapo()));
            } else {
                mDisplayCapo.setVisibility(View.GONE);
            }
            displayLyrics();
            if(mSong.getFirstChord()!=null){
                mChordButton.setVisibility(View.VISIBLE);
                Chord chord = mSong.getFirstChord().copy();
                chord.setNote(chord.getNote()+toneOffset);
                mChordButton.setChord(chord);
            }
            else{
                mChordButton.setVisibility(View.GONE);
            }
        }

        ((GradientDrawable)mScrollButton.getBackground()).setColor(mContext.getResources().getColor(R.color.Red));

        if(mDisplayChord){
            mChordButton.setButtonColor(mContext.getResources().getColor(R.color.DarkBlue));
            mChordButton.setTextColor(Color.WHITE);
        }
        else{
            mChordButton.setButtonColor(mContext.getResources().getColor(R.color.LightGrey));
            mChordButton.setTextColor(mContext.getResources().getColor(R.color.DarkBlue));
        }
    }

    public void setActions(){
        //<editor-fold desc="SCROLL BUTTON">
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
        mChordButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                if(!mDisplayChord){
                    mDisplayChord=true;
                    setViews();
                }
                else{
                    mDisplayChord=false;
                    setViews();
                }

            }
        });
        mChordButton.setOnLongClickListener(this);
        //</editor-fold>
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mCountDownTimer!=null){
            mCountDownTimer.onFinish();
        }
    }

    private void displayLyrics(){
        mDisplayLyricsLayout.removeAllViewsInLayout();
        linesCoordinates=new ArrayList<>();

        for (int i=0; i<mLyrics.size();i++){
            //<editor-fold desc="CREATING LYRICS VIEWS">

            //<editor-fold desc="CREATE AND SET TEXT VIEW">
            final TextView lineView = new TextView(mContext);
            lineView.setId(i);
            lineView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.size_lyrics));
            lineView.setPadding(0,0,0,0);
            lineView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            lineView.setTextIsSelectable(false);
            //</editor-fold>

            //<editor-fold desc="ADD CHORDS">
            final SpannableString spannable = new SpannableString(mLyrics.get(i).getText());
            if (mDisplayChord) {
                for(Chord realChord:mLyrics.get(i).getChords()){
                    Chord chord = realChord.copy();
                    chord.setNote(realChord.getNote()+toneOffset);
                    if(chord.getPosition()<spannable.length()-1){
                        spannable.setSpan(new ChordSpan(chord, mContext), chord.getPosition(), chord.getPosition()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (chord.getPosition()>0){
                        spannable.setSpan(new ChordSpan(chord, mContext), chord.getPosition()-1, chord.getPosition(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else{
                        spannable.setSpan(new ChordSpan(chord, mContext), chord.getPosition(), chord.getPosition(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int lineHeightDp =1;
        int lineHeightPx = lineHeightDp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, lineHeightPx);
        int startOffset = 0;
        int linePos = mScrollView.getTop()
                + mScrollView.getChildAt(0).getTop()
                + mDisplayLyricsLayout.getTop()
                + mDisplayLyricsLayout.getPaddingTop()
                - startOffset;
        lineParams.setMargins(0, linePos, 0, 0);
        mLine.setLayoutParams(lineParams);
        //</editor-fold>

        mLine.setVisibility(View.VISIBLE);

        //<editor-fold desc="COMPUTE DURATION OF THE SONG AND CHECK FOR NULL TIMESTAMPS">
        int songDuration=0;
        for(LyricsLine lyricsLine:mSong.getLyrics()){
            songDuration += lyricsLine.getDuration();
            if(lyricsLine.getDuration()==0){
                Toast.makeText(getContext(), R.string.undefined_timestamps, Toast.LENGTH_LONG).show();
                stopScrolling();
                return;
            }
        }
        //</editor-fold>

        final int dt = 40;
        scrollBuffer = 0;
        long totalTime = 5*songDuration;
        precedent = totalTime;


        mCountDownTimer = new CountDownTimer(totalTime, dt) {
            public void onTick(long millisUntilFinished) {
                float timeElapsed = (float) (precedent-millisUntilFinished);
                precedent=millisUntilFinished;
                //<editor-fold desc="GET CURRENT LINE">
                int i=0;
                    while(i<linesCoordinates.size() && mScrollView.getScrollY()
                            + linesCoordinates.get(0)>=linesCoordinates.get(i)){
                        currentLine = i;
                        i++;
                }
                //</editor-fold>

                float dx = ((float)mDisplayLyricsLayout.getChildAt(currentLine).getHeight())*timeElapsed/(float)mSong.getLyrics().get(currentLine).getDuration();

                if(dx+scrollBuffer<1){
                    scrollBuffer+=dx;
                }
                else{
                    mScrollView.smoothScrollBy(0, (int)(dx+scrollBuffer));
                    scrollBuffer=dx+scrollBuffer - (int)(dx+scrollBuffer);
                }
            }
            public void onFinish() {
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
        mLyrics=mSong.getLyrics().copy();
        setViews();
    }

    public void changeID(UUID new_id) {
        id = new_id;
        refresh();
    }

    @Override
    public boolean onLongClick(View v) {
        switch(v.getId()){
            case R.id.chord_button:
                if (mSong.getFirstChord()!=null) {
                    ChordDialogFragment chooseChordFragment = ChordDialogFragment.newInstance(mChordButton.getChord().getNote(), -1);
                    chooseChordFragment.setOnFragmentInteractionListener(this);
                    chooseChordFragment.setRequestCode(SELECT_CHORD_REQUEST_CODE);
                    chooseChordFragment.show(getFragmentManager(), mContext.getString(R.string.select_chord));
                } else {
                    Toast.makeText(getContext(), R.string.change_tone_no_chords, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return false;
    }

    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Object... args) {
        switch(requestCode){
            case SELECT_CHORD_REQUEST_CODE:
                if(resultCode == AppCompatActivity.RESULT_OK){
                    Chord chord = (Chord)args[0];
                    toneOffset = chord.getNote() - mSong.getFirstChord().getNote();
                    toneOffset = (toneOffset<0)?toneOffset+Chord.scale.size():toneOffset;
                    setViews();
                }
                break;
        }
    }
}
