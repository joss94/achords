package com.joss.achords.SongEnvironment.ChordsEdition;


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
import android.util.Log;
import android.util.TypedValue;
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

import com.joss.achords.OnDialogFragmentInteractionListener;
import com.joss.achords.Models.Chord;
import com.joss.achords.Models.Lyrics;
import com.joss.achords.Models.LyricsLine;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;
import com.joss.achords.LyricsDisplay.ChordSpan;
import com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords.ChordButton;
import com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords.ChordButtonAdapter;
import com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords.ChordDragShadowBuilder;
import com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords.OnChordButtonClickListener;
import com.joss.achords.SongEnvironment.ChordsEdition.Timestamps.TimestampsDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class ChordFragment extends Fragment implements View.OnDragListener,
        OnChordButtonClickListener,
        View.OnClickListener,
        View.OnFocusChangeListener,
        OnEditTextChordDoubleTapListener,
        OnDialogFragmentInteractionListener {

    private static final String EXTRA_SONG_ID = "song_id";
    private static final String EXTRA_CHORD = "chord";
    private static final int SELECT_CHORD_REQUEST_CODE = 1;
    private static final int CAPO_REQUEST_CODE = 2;

    private Song mSong;
    private Song mEditedSong;
    private UUID mSongID;
    private Lyrics mLyrics;
    private LinearLayout mLyricsLayout;
    private int mCurrentLine=0;
    private int mCurrentIndex;
    private Chord mCurrentChord;
    private Context mContext;
    private ImageButton mAddChordButton;
    private List<EditTextChords> mLyricsLineViews;
    private ChordButtonAdapter mRecentChordsAdapter;
    private ScrollView mScrollView;
    private RecyclerView mRecentChords;
    private TextView mCapo;
    private ImageButton mSetTimestampsButton;
    private RelativeLayout chordBin;
    private boolean chordInBin=false;

    private int cursorOffset;

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

        cursorOffset = 100;

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
        mCurrentChord = new Chord();
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

        findViews(v);
        setViews();
        setActions();

        return v;
    }

    public void findViews(View v){
        mAddChordButton = (ImageButton)v.findViewById(R.id.add_chord_button);
        mSetTimestampsButton = (ImageButton) v.findViewById(R.id.set_timestamps_button);
        mRecentChords = (RecyclerView) v.findViewById(R.id.recent_chords);
        mLyricsLayout = (LinearLayout)v.findViewById(R.id.chord_lyrics);
        mScrollView = (ScrollView)v.findViewById(R.id.scroll_view);
        chordBin = (RelativeLayout)v.findViewById(R.id.chord_bin);
        mCapo = (TextView) v.findViewById(R.id.display_capo);
    }

    @SuppressWarnings("deprecation")
    public void setViews(){
        ((GradientDrawable) mSetTimestampsButton.getBackground()).setColor(mContext.getResources().getColor(R.color.colorPrimary));
        mCapo.setText(String.format(Locale.ENGLISH, mContext.getString(R.string.capo), mSong.getCapo()));

        displayLyrics();
    }

    public void setActions(){
        mAddChordButton.setOnClickListener(this);
        mSetTimestampsButton.setOnClickListener(this);
        mCapo.setOnClickListener(this);
        mScrollView.setOnDragListener(this);

        mRecentChordsAdapter = new ChordButtonAdapter(mContext, new ArrayList<Chord>(), this);
        mRecentChords.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecentChords.setAdapter(mRecentChordsAdapter);

    }

    private void displayLyrics(){
        //Clean everything
        mLyricsLayout.removeAllViewsInLayout();
        mLyricsLineViews.clear();

        for (int i=0; i<mLyrics.size();i++){
            final LyricsLine lyricsLine = mLyrics.get(i);
            //<editor-fold desc="CREATING LYRICS VIEWS">
            final EditTextChords lineView = new EditTextChords(mContext, i);
            mLyricsLineViews.add(lineView);
            lineView.setId(i);
            lineView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.size_lyrics));
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
                    spannable.setSpan(new ChordSpan(chord, mContext), chord.getPosition(), chord.getPosition()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else if (chord.getPosition()>0){
                    spannable.setSpan(new ChordSpan(chord, mContext), chord.getPosition()-1, chord.getPosition(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else{
                    spannable.setSpan(new ChordSpan(chord, mContext), chord.getPosition(), chord.getPosition(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            lineView.setText(spannable, TextView.BufferType.SPANNABLE);
            //</editor-fold>

            mLyricsLayout.addView(lineView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mLyricsLayout.invalidate();
        //</editor-fold>
        }
    }

    public void refresh(){
        mSong = Songbook.get(getContext()).getById(mSongID);
        mEditedSong=mSong.copy();
        mLyrics=mEditedSong.getLyrics();

        setViews();
    }

    public void addChord(){
        if(!mLyricsLineViews.get(mCurrentLine).hasFocus()){
            Toast.makeText(getContext(), R.string.cursor_not_focused, Toast.LENGTH_SHORT).show();
            return;
        }

        String result = mEditedSong.addChord(mCurrentChord, mCurrentLine, mCurrentIndex);
        if(result.equals("success")){
            mRecentChordsAdapter.addChord(mCurrentChord);
        }
        else {
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
        Songbook.get(getActivity()).updateSong(mEditedSong);
    }

    public void changeID(UUID id){
        mSongID = id;
        refresh();
    }

    public int getFocusedLine(float y){
        y=y-(float) cursorOffset;
        int i=0;
        while(i< mLyricsLayout.getChildCount() && (mLyricsLayout.getChildAt(i).getTop()+ mLyricsLayout.getTop()+mScrollView.getChildAt(0).getTop()-mScrollView.getScrollY()<y)){
            i++;
        }
        if(y< mLyricsLayout.getChildAt(mLyricsLayout.getChildCount()-1).getBottom()+ mLyricsLayout.getTop()+mScrollView.getChildAt(0).getTop()-mScrollView.getScrollY()){
            i=i-1;
        }
        return Math.max(i,0);
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
        mEditedSong.deleteChord(mCurrentLine, mCurrentIndex);
        Songbook.get(getActivity()).updateSong(mEditedSong);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_chord_button:
                ChordDialogFragment chooseChordFragment = ChordDialogFragment.newInstance(mCurrentChord.getNote(), mCurrentChord.getMode());
                chooseChordFragment.setOnFragmentInteractionListener(this);
                chooseChordFragment.setRequestCode(SELECT_CHORD_REQUEST_CODE);
                chooseChordFragment.show(getFragmentManager(), mContext.getString(R.string.select_chord));
                break;

            case R.id.set_timestamps_button:
                TimestampsDialogFragment fr = TimestampsDialogFragment.newInstance(mSong.getId());
                fr.show(getFragmentManager(), mContext.getString(R.string.timestamps_dialog_title));
                break;

            case R.id.display_capo:
                CapoDialogFragment capoFragment = CapoDialogFragment.newInstance(mEditedSong.getCapo());
                capoFragment.setOnFragmentInteractionListener(this);
                capoFragment.setRequestCode(CAPO_REQUEST_CODE);
                capoFragment.show(getFragmentManager(), mContext.getString(R.string.capo_dialog_fragment));
                break;

            default:
                break;
        }
    }

    @Override
    public void onChordButtonClicked(Chord chord) {
        addChord();
    }

    @Override
    public void onChordButtonLongClicked(View v, Chord chord) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CHORD, ((ChordButton)v).getChord());

        ClipData.Item item = new ClipData.Item(intent);
        ClipData dragData = new ClipData(v.toString(), new String[] {},item);

        View.DragShadowBuilder chordShadow = new ChordDragShadowBuilder(v);
        //noinspection deprecation
        v.startDrag(dragData, chordShadow, null, 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();

        switch(action){
            case DragEvent.ACTION_DRAG_STARTED:
                chordBin.setVisibility(View.VISIBLE);
                return true;


            case DragEvent.ACTION_DRAG_LOCATION:

                if(event.getY()<chordBin.getBottom()){
                    chordBin.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_black_dark));
                    chordInBin = true;
                } else{
                    chordBin.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_black));
                    chordInBin = false;
                    int index = getFocusedLine(event.getY());

                    v = mLyricsLayout.getChildAt(index);
                    if (v instanceof EditTextChords) {
                        v.requestFocus();
                        int[] vAbsolute = {0,0};
                        v.getLocationOnScreen(vAbsolute);
                        ((EditTextChords) v).setSelection(((EditTextChords) v).getOffsetForPosition(event.getX()-mScrollView.getPaddingStart(), event.getY()-vAbsolute[1]+cursorOffset));
                        mCurrentLine = index;
                        mCurrentIndex = ((EditTextChords) v).getSelectionStart();
                    }
                }

                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                if(chordInBin){
                    chordBin.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_black));
                    chordInBin = false;
                }
                return true;

            case DragEvent.ACTION_DROP:
                if(!chordInBin){
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    Intent dragdata = item.getIntent();
                    mCurrentChord = (Chord)dragdata.getSerializableExtra(EXTRA_CHORD);
                    addChord();
                }
                chordBin.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_black));
                chordInBin = false;
                chordBin.setVisibility(View.GONE);
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                chordBin.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_black));
                chordInBin = false;
                chordBin.setVisibility(View.GONE);
                return true;

            default:
                break;
        }

        return false;
    }

    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Object... args) {
        switch(requestCode){
            case SELECT_CHORD_REQUEST_CODE:
                if(resultCode== AppCompatActivity.RESULT_OK){
                    mCurrentChord = (Chord)args[0];
                    addChord();
                }
                break;

            case CAPO_REQUEST_CODE:
                if(resultCode==AppCompatActivity.RESULT_OK){
                    mEditedSong.setCapo((int)args[0]);
                    mCapo.setText(String.format(Locale.ENGLISH, mContext.getString(R.string.capo), (int)args[0]));
                    Songbook.get(getActivity()).updateSong(mEditedSong);
                }
                break;
        }
    }
}