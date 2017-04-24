package com.joss.achords.SongEnvironment.ChordsEdition;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.joss.achords.Models.Chord;
import com.joss.achords.R;
import com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords.ChordButton;
import com.joss.utils.AbstractDialog.AbstractDialogFragment;

import java.util.ArrayList;


public class ChordDialogFragment extends AbstractDialogFragment {
    public static final String EXTRA_CHORD_NOTE = "chord_note";
    private static final String EXTRA_CHORD_ATTR = "chord_attr";

    private int WHITE, BLUE;

    private int mCurrentNote, mCurrentMode, mCurrentAttr;

    private LinearLayout noteContainer, modeContainer, attrContainer;
    private ArrayList<ChordButton> noteButtons, modeButtons, attrButtons;

    public static ChordDialogFragment newInstance(){
        return new ChordDialogFragment();
    }

    public static ChordDialogFragment newInstance(int currentNote, int currentAttr){
        Bundle args = new Bundle();
        args.putInt(EXTRA_CHORD_NOTE, currentNote);
        args.putInt(EXTRA_CHORD_ATTR, currentAttr);
        ChordDialogFragment fr=new ChordDialogFragment();
        fr.setArguments(args);
        return fr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        noteButtons = new ArrayList<>();
        modeButtons = new ArrayList<>();
        attrButtons = new ArrayList<>();
        WHITE = getContext().getResources().getColor(R.color.white);
        BLUE = getContext().getResources().getColor(R.color.DarkBlue);
    }

    @Override
    public void findViews(View v){
        noteContainer = (LinearLayout) v.findViewById(R.id.note_container);
        modeContainer = (LinearLayout) v.findViewById(R.id.mode_container);
        attrContainer = (LinearLayout) v.findViewById(R.id.attribute_container);
    }

    @Override
    public void setViews(){
        setTitle(getContext().getResources().getString(R.string.chord_dialog_title));

         for(int i = 0; i<noteContainer.getChildCount(); i++){
             final ChordButton button = (ChordButton) noteContainer.getChildAt(i);
             noteButtons.add(button);
             button.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     onNoteClick(button);
                 }
             });
         }

        for(int i = 0; i<modeContainer.getChildCount(); i++){
            final ChordButton button = (ChordButton) modeContainer.getChildAt(i);
            modeButtons.add(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onModeClick(button);
                }
            });
        }

        for(int i = 0; i<attrContainer.getChildCount(); i++){
            final ChordButton button = (ChordButton) attrContainer.getChildAt(i);
            attrButtons.add(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAttrClick(button);
                }
            });
        }

        if(getArguments() != null && getArguments().containsKey(EXTRA_CHORD_NOTE)){
            modeContainer.setVisibility(View.GONE);
            onNoteClick((ChordButton) noteContainer.getChildAt(getArguments().getInt(EXTRA_CHORD_NOTE)));
            onAttrClick((ChordButton) attrContainer.getChildAt(getArguments().getInt(EXTRA_CHORD_ATTR)));
        }
    }

    private void onAttrClick(ChordButton clickedButton) {
        for(ChordButton button : attrButtons){
            button.setButtonColor(WHITE);
            button.setTextColor(BLUE);
        }
        if (mCurrentAttr != clickedButton.getChord().getAttribute()) {
            clickedButton.setButtonColor(BLUE);
            clickedButton.setTextColor(WHITE);
            mCurrentAttr = clickedButton.getChord().getAttribute();
        }
        else{
            mCurrentAttr = 0;
        }

    }

    private void onModeClick(ChordButton clickedButton) {
        for(ChordButton button : modeButtons){
            button.setButtonColor(WHITE);
            button.setTextColor(BLUE);
        }
        if (mCurrentMode != clickedButton.getChord().getMode()) {
            clickedButton.setButtonColor(BLUE);
            clickedButton.setTextColor(WHITE);
            mCurrentMode = clickedButton.getChord().getMode();
        }
        else{
            mCurrentMode=0;
        }
    }

    private void onNoteClick(ChordButton clickedButton) {
        for(ChordButton button : noteButtons){
            button.setButtonColor(WHITE);
            button.setTextColor(BLUE);
        }
        clickedButton.setButtonColor(BLUE);
        clickedButton.setTextColor(WHITE);

        mCurrentNote = clickedButton.getChord().getNote();
    }

    @Override
    public boolean callback(){
        Chord chord = new Chord(mCurrentNote, mCurrentMode, 0, mCurrentAttr);
        listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK, chord);
        return true;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chord_dialog;
    }

}
