package com.joss.achords.Fragments;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.joss.achords.Adapters.ChordButtonAdapter;
import com.joss.achords.Interfaces.OnChordButtonClickListener;
import com.joss.achords.Interfaces.OnEditTextChordDoubleTapListener;
import com.joss.achords.Interfaces.OnDialogFragmentInteractionListener;
import com.joss.achords.Models.Chord;
import com.joss.achords.Models.Lyrics;
import com.joss.achords.Models.LyricsLine;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;
import com.joss.achords.Utils.ChordDragShadowBuilder;
import com.joss.achords.Utils.ChordSpan;
import com.joss.achords.Views.ChordButton;
import com.joss.achords.Views.EditTextChords;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ChordFragment extends Fragment implements View.OnDragListener,
        OnChordButtonClickListener,
        View.OnClickListener,
        View.OnFocusChangeListener,
        OnEditTextChordDoubleTapListener,
        OnDialogFragmentInteractionListener {

    private static final String EXTRA_SONG_ID = "song_id";
    private static final String EXTRA_CHORD = "chord";

    private final double cursorPadding = 50;

    private Song mSong;
    private Song mEditedSong;
    private UUID mSongID;
    private Lyrics mLyrics;
    private LinearLayout mLayout;
    private int mCurrentLine=0;
    private int mCurrentIndex;
    private Chord mCurrentChord;
    private Context mContext;
    private ImageButton mSetTimestampsButton;
    private ChordButton mAddChordButton;
    private TextView mSongTitleTextView;
    private TextView mSongArtistDateTextView;
    private List<EditTextChords> mLyricsLineViews;
    private ChordButtonAdapter mRecentChordsAdapter;
    private RecyclerView mRecentChords;
    private ScrollView mScrollView;
    private RelativeLayout chordBin;
    boolean chordInBin=false;

    public ChordFragment() {

    }

    public static ChordFragment newInstance(UUID song_id) {
        ChordFragment fragment = new ChordFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SONG_ID, song_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLyricsLineViews = new ArrayList<>();

        Songbook.get(getActivity()).setOnSongbookChangeListener(new Songbook.OnSongbookChangeListener() {
            @Override
            public void onDBChange() {
                refresh();
            }
        });

        mSongID = (UUID)getArguments().getSerializable(EXTRA_SONG_ID);
        mSong = Songbook.get(getActivity()).getById(mSongID);
        mEditedSong=mSong.copy();
        mLyrics=mEditedSong.getLyrics();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_chord, container, false);

        //<editor-fold desc="FIND AND SET BUTTONS">
        mAddChordButton = (ChordButton)v.findViewById(R.id.add_chord_button);
        mAddChordButton.setOnClickListener(this);
        mSetTimestampsButton = (ImageButton)v.findViewById(R.id.set_timestamps_button);
        ((GradientDrawable)mSetTimestampsButton.getBackground()).setColor(getResources().getColor(R.color.DarkBlue));
        //</editor-fold>

        //<editor-fold desc="FIND AND SET TITLES">
        mSongTitleTextView=(TextView)v.findViewById(R.id.chords_song_title);
        mSongTitleTextView.setText(mSong.getName());
        mSongArtistDateTextView = (TextView)v.findViewById(R.id.chords_artist_date);
        String artist;
        if(mSong.getArtist()==null || mSong.getArtist().isEmpty()){
            artist = "Unknown";
        }
        else{
            artist = mSong.getArtist();
        }
        String date = (mSong.getReleaseYear()==0)?"":(" - "+String.valueOf(mSong.getReleaseYear()));
        mSongArtistDateTextView.setText(String.format(getString(R.string.artist_display_title),artist, date));
        //</editor-fold>

        mRecentChordsAdapter = new ChordButtonAdapter(mContext, new ArrayList<Chord>(), this);
        mRecentChords = (RecyclerView)v.findViewById(R.id.recent_chords);
        mRecentChords.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecentChords.setAdapter(mRecentChordsAdapter);

        mLayout = (LinearLayout)v.findViewById(R.id.chord_lyrics);

        mCurrentChord = new Chord();
        updateAddChordButton(mCurrentChord);
        displayLyrics();

        mAddChordButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                chooseChord();
                return true;
            }
        });

        mScrollView = (ScrollView)v.findViewById(R.id.scroll_view);
        mScrollView.setOnDragListener(this);

        chordBin = (RelativeLayout)v.findViewById(R.id.chord_bin);

        return v;
    }

    private void displayLyrics(){
        //Clean everything
        mLayout.removeAllViewsInLayout();
        mLyricsLineViews.clear();

        for (int i=0; i<mLyrics.size();i++){
            final int lineNumber=i;
            final LyricsLine lyricsLine = mLyrics.get(i);
            //<editor-fold desc="CREATING LYRICS VIEWS">
            final EditTextChords lineView = new EditTextChords(mContext, lineNumber);
            mLyricsLineViews.add(lineView);
            lineView.setId(i);
            lineView.setTextSize(16);
            lineView.setBackground(null);
            lineView.setPadding(0,0,0,0);
            lineView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            lineView.setTextIsSelectable(true);
            lineView.setLongClickable(false);
            lineView.setOnFocusChangeListener(this);
            lineView.setOnDoubleTapListener(this);

            //<editor-fold desc="SET TEXT AND CHORDS">
            final SpannableString spannable  = new SpannableString(lyricsLine.getText());
            for(Chord chord:lyricsLine.getChords()){
                if(chord.getPosition()<spannable.length()){
                    spannable.setSpan(new ChordSpan(chord), chord.getPosition(), chord.getPosition()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else if (chord.getPosition()>0){
                    spannable.setSpan(new ChordSpan(chord), chord.getPosition()-1, chord.getPosition(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else{
                    spannable.setSpan(new ChordSpan(chord), chord.getPosition(), chord.getPosition(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            lineView.setText(spannable, TextView.BufferType.SPANNABLE);
            //</editor-fold>

            mLayout.addView(lineView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mLayout.invalidate();
        //</editor-fold>
        }
    }

    public void refresh(){
        mSong = Songbook.get(getContext()).getById(mSongID);
        mEditedSong=mSong.copy();
        mLyrics=mEditedSong.getLyrics();

        mSongTitleTextView.setText(mSong.getName());
        String artist;
        if(mSong.getArtist()==null || mSong.getArtist().isEmpty()){
            artist = "Unknown";
        }
        else{
            artist = mSong.getArtist();
        }
        String date = (mSong.getReleaseYear()==0)?"":(" - "+String.valueOf(mSong.getReleaseYear()));
        mSongArtistDateTextView.setText(String.format(mContext.getString(R.string.artist_display_title),artist, date));
        displayLyrics();
    }

    public void chooseChord(){
        ChordDialogFragment chooseChordFragment = ChordDialogFragment.newInstance(mCurrentChord.getNote(), mCurrentChord.getMode());
        chooseChordFragment.setOnFragmentInteractionListener(this);
        chooseChordFragment.show(getFragmentManager(), "CHORD");

    }

    public void addChord(){
        if(!mLyricsLineViews.get(mCurrentLine).hasFocus()){
            Toast.makeText(getContext(), "Cursor not focused", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean existsChord=false;
        ArrayList<Chord> chordsInLine = mLyrics.get(mCurrentLine).getChords();
        for(int i=0; i<chordsInLine.size(); i++){
            Chord chord = chordsInLine.get(i);
            if(Math.abs(chord.getPosition()-mCurrentIndex)<=Chord.CHORD_MARGIN){
                Toast.makeText(getActivity(), "There is already a chord here...", Toast.LENGTH_SHORT).show();
                existsChord=true;
            }
        }
        if(mLyrics.get(mCurrentLine).getText().isEmpty()){
            Toast.makeText(getActivity(), "Can't add a chord to an empty line", Toast.LENGTH_SHORT).show();
        }
        else if(!existsChord){
            mCurrentChord.setPosition(mCurrentIndex);
            mLyrics.get(mCurrentLine).addChord(mCurrentChord);
            mRecentChordsAdapter.addChord(mCurrentChord);
        }
        Songbook.get(getActivity()).updateSong(mEditedSong);
        //((Refreshable)getActivity()).refresh();
    }

    public void deleteChord(){
        ArrayList<Chord> chordsInLine = mLyrics.get(mCurrentLine).getChords();
        for(int i=0;i<chordsInLine.size();i++){
            Chord chord = chordsInLine.get(i);
            if(Math.abs(chord.getPosition()-mCurrentIndex)<=3){
                mLyrics.get(mCurrentLine).getChords().remove(chord);
            }
        }
        Songbook.get(getActivity()).updateSong(mEditedSong);
    }

    public void updateAddChordButton(Chord chord){
        mCurrentChord = chord;
        mAddChordButton.setChord(mCurrentChord);
    }

    public void changeID(UUID id){
        mSongID = id;
        refresh();
    }

    public int getFocusedLine(float y){
        y=y-(float)cursorPadding;
        int i=0;
        while(i<mLayout.getChildCount() && (mLayout.getChildAt(i).getTop()+mLayout.getTop()+mScrollView.getPaddingTop()-mScrollView.getScrollY()<y)){
            i++;
        }
        if(y<mLayout.getChildAt(mLayout.getChildCount()-1).getBottom()+mLayout.getTop()+mScrollView.getPaddingTop()-mScrollView.getScrollY()){
            i=i-1;
        }
        return Math.max(i-1,0);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            mCurrentLine=((EditTextChords) v).getLineNumber();
            mCurrentIndex=((EditText)v).getSelectionStart();
        }
    }

    @Override
    public void onDoubleTap(int lineNumber, int index) {
        deleteChord();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_chord_button:
                addChord();
                break;

            case R.id.set_timestamps_button:
                TimestampsDialogFragment fr = TimestampsDialogFragment.newInstance(mSong.getId());
                fr.show(getFragmentManager(), "TIMESTAMPS");
                break;

            default:
                break;
        }
    }

    @Override
    public void onChordButtonClicked(Chord chord) {
        updateAddChordButton(chord);
        addChord();
    }

    @Override
    public void onChordButtonLongClicked(View v, Chord chord) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CHORD, ((ChordButton)v).getChord());

        ClipData.Item item = new ClipData.Item(intent);
        ClipData dragData = new ClipData(v.toString(), new String[] {},item);

        View.DragShadowBuilder chordShadow = new ChordDragShadowBuilder(v);
        v.startDrag(dragData, chordShadow, null, 0);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();

        switch(action){
            case DragEvent.ACTION_DRAG_STARTED:
                chordBin.setVisibility(View.VISIBLE);
                return true;


            case DragEvent.ACTION_DRAG_LOCATION:
                if(event.getY()-mScrollView.getScrollY()<chordBin.getBottom()){
                    chordBin.setBackgroundColor(getResources().getColor(R.color.transparent_black_dark));
                    chordInBin = true;
                } else{
                    int index = getFocusedLine(event.getY());
                    v = mLayout.getChildAt(index);
                    if (v instanceof EditTextChords) {
                        v.requestFocus();
                        ((EditTextChords) v).setSelection(((EditTextChords) v).getOffsetForPosition(event.getX(), event.getY()));
                    }
                }

                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                if(chordInBin){
                    chordBin.setBackgroundColor(getResources().getColor(R.color.transparent_black));
                }
                return true;

            case DragEvent.ACTION_DROP:
                if(!chordInBin){
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    Intent dragdata = item.getIntent();
                    mCurrentChord = (Chord)dragdata.getSerializableExtra(EXTRA_CHORD);
                    addChord();
                }
                chordBin.setVisibility(View.GONE);
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                return true;

            default:
                break;
        }

        return false;
    }

    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Object... args) {
        switch(requestCode){
            case ChordDialogFragment.SELECT_CHORD_REQUEST_CODE:
                if(resultCode== AppCompatActivity.RESULT_OK){
                    Chord newChord = (Chord)args[0];
                    updateAddChordButton(newChord);
                }
                break;
        }
    }
}
