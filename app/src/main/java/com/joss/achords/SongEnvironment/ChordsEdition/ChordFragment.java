package com.joss.achords.SongEnvironment.ChordsEdition;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.joss.achords.AchordsActivity;
import com.joss.achords.Database.DBHelper;
import com.joss.achords.LyricsDisplay.ChordSpan;
import com.joss.achords.Models.Chord;
import com.joss.achords.Models.Lyrics;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;
import com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords.ChordButton;
import com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords.ChordButtonAdapter;
import com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords.ChordDragShadowBuilder;
import com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords.OnChordButtonClickListener;
import com.joss.achords.SongEnvironment.ChordsEdition.Timestamps.TimestampsDialogFragment;
import com.joss.utils.AbstractDialog.OnDialogFragmentInteractionListener;

import java.util.ArrayList;
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

    private Song mSong, mEditedSong;
    private UUID mSongID;
    private Lyrics mLyrics;
    private LinearLayout mLyricsLayout;
    private int mCurrentLine=0, mCurrentIndex;
    private Chord mCurrentChord;
    private Context mContext;
    private ImageButton mAddChordButton;
    private ChordButtonAdapter mRecentChordsAdapter;
    private ScrollView mScrollView;
    private RecyclerView mRecentChords;
    private TextView mCapo;
    private ImageButton mSetTimestampsButton;

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

        mCurrentChord = new Chord();

        mSongID = (UUID)getArguments().getSerializable(EXTRA_SONG_ID);
        Songbook.get(getActivity()).addOnSongbookChangeListener(new DBHelper.OnDBChangeListener() {
            @Override
            public void onDBChange() {
                refresh();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_chord, container, false);

        findViews(v);
        setViews();
        refresh();

        for(Chord chord : mSong.getAllChords()){
            mRecentChordsAdapter.addChord(chord);
        }

        return v;
    }

    public void findViews(View v){
        mAddChordButton = (ImageButton)v.findViewById(R.id.add_chord_button);
        mSetTimestampsButton = (ImageButton) v.findViewById(R.id.set_timestamps_button);
        mRecentChords = (RecyclerView) v.findViewById(R.id.recent_chords);
        mLyricsLayout = (LinearLayout)v.findViewById(R.id.chord_lyrics);
        mScrollView = (ScrollView)v.findViewById(R.id.scroll_view);
        mCapo = (TextView) v.findViewById(R.id.display_capo);
    }

    @SuppressWarnings("deprecation")
    public void setViews(){
        ((GradientDrawable) mSetTimestampsButton.getBackground()).setColor(mContext.getResources().getColor(R.color.colorPrimary));

        mAddChordButton.setOnClickListener(this);
        mSetTimestampsButton.setOnClickListener(this);
        mCapo.setOnClickListener(this);

        mRecentChordsAdapter = new ChordButtonAdapter(new ArrayList<Chord>(), this);
        mRecentChords.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecentChords.setAdapter(mRecentChordsAdapter);
    }


    private void displayLyrics(){
        mLyricsLayout.removeAllViewsInLayout();

        for (int i=0; i<mLyrics.size();i++){
            //<editor-fold desc="CREATING LYRICS VIEWS">
            final EditTextChords lineView = new EditTextChords(mLyricsLayout.getContext(), i);
            lineView.setId(i);
            lineView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.size_lyrics));
            lineView.setBackground(null);
            lineView.setPadding(0,0,0,0);
            lineView.setTextIsSelectable(true);
            lineView.setFocusable(true);
            lineView.setLongClickable(false);
            lineView.setOnFocusChangeListener(this);
            lineView.setOnDoubleTapListener(this);
            lineView.setOnDragListener(this);

            //<editor-fold desc="SET TEXT AND CHORDS">
            final SpannableString spannable  = new SpannableString(mLyrics.get(i).getText());
            for(Chord chord:mLyrics.get(i).getChords()){
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

            lineView.setFilters(new InputFilter[] {
                    new InputFilter() {
                        public CharSequence filter(CharSequence src, int start,
                                                   int end, Spanned dst, int dstart, int dend) {
                            return src.length() < 1 ? dst.subSequence(dstart, dend) : "";
                        }
                    }
            });
            mLyricsLayout.addView(lineView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mLyricsLayout.invalidate();
        //</editor-fold>
        }
        TextView emptyLastLine = new TextView(mContext);
        emptyLastLine.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.size_lyrics));
        emptyLastLine.setVisibility(View.INVISIBLE);
        emptyLastLine.setId(mLyricsLayout.getChildCount());
        emptyLastLine.setOnDragListener(this);
        mLyricsLayout.addView(emptyLastLine);
    }

    public void refresh(){
        mSong = Songbook.get(getContext()).getById(mSongID);
        mEditedSong=mSong.copy();
        mLyrics=mEditedSong.getLyrics();

        mCapo.setText(String.format(Locale.ENGLISH, mContext.getString(R.string.capo), mSong.getCapo()));
        displayLyrics();
    }

    public void addChord(){
        String result = mEditedSong.addChord(mCurrentChord, mCurrentLine, mCurrentIndex);
        if(result.equals("success")){
            mRecentChordsAdapter.addChord(mCurrentChord);
        }
        else {
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
        AchordsActivity.SONGBOOK.updateSong(mEditedSong);
    }

    public void changeID(UUID id){
        mSongID = id;
        refresh();
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
                ChordDialogFragment chooseChordFragment = ChordDialogFragment.newInstance();
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

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CHORD, ((ChordButton)v).getChord());
        intent.putExtra(EXTRA_CHORD, args);

        ClipData.Item item = new ClipData.Item(intent);
        ClipData dragData = new ClipData(v.toString(), new String[] {},item);

        View.DragShadowBuilder chordShadow = new ChordDragShadowBuilder(v);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            v.startDragAndDrop(dragData, chordShadow, null, 0);
        } else {
            //noinspection deprecation
            v.startDrag(dragData, chordShadow, null, 0);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onDrag(View v, DragEvent event) {

        v = mLyricsLayout.findViewById(Math.max(v.getId()-1, 0));

        switch(event.getAction()){

            case DragEvent.ACTION_DRAG_LOCATION:
                if (v instanceof EditTextChords) {
                    v.requestFocus();
                    int[] vAbsolute = {0,0};
                    v.getLocationOnScreen(vAbsolute);
                    ((EditTextChords) v).setSelection(((EditTextChords) v).getOffsetForPosition(event.getX()-mScrollView.getPaddingStart(), event.getY()-vAbsolute[1]));
                    mCurrentIndex = ((EditTextChords) v).getSelectionStart();
                    return false;
                }
                return false;

            case DragEvent.ACTION_DROP:
                ClipData.Item item = event.getClipData().getItemAt(0);
                Intent dragData = item.getIntent();
                Bundle args = dragData.getBundleExtra(EXTRA_CHORD);
                mCurrentChord = (Chord) args.getSerializable(EXTRA_CHORD);
                addChord();
                return true;

            default:
                return false;
        }
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
