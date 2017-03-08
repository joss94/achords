package com.joss.achords.SongEnvironment.ChordsEdition;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.joss.achords.AbstractDialogFragment;
import com.joss.achords.Models.Chord;
import com.joss.achords.R;


public class ChordDialogFragment extends AbstractDialogFragment {
    public static final String EXTRA_CHORD_NOTE = "chord_note";
    public static final String EXTRA_CHORD_MODE = "chord_mode";

    Spinner mNoteSpinner;
    Spinner mModeSpinner;
    int mCurrentNote;
    int mCurrentMode;

    public ChordDialogFragment() {

    }

    public static ChordDialogFragment newInstance(int currentNote, int currentMode){
        Bundle args = new Bundle();
        args.putInt(EXTRA_CHORD_NOTE, currentNote);
        args.putInt(EXTRA_CHORD_MODE, currentMode);
        ChordDialogFragment fr=new ChordDialogFragment();
        fr.setArguments(args);
        return fr;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        View chordPickerView = getActivity().getLayoutInflater().inflate(R.layout.fragment_chord_dialog, null);
        findViews(chordPickerView);
        setViews();

        setDialogButtons(chordPickerView);
        setTitle(chordPickerView, getString(R.string.chord_dialog_title));

        return chordPickerView;
    }

    public void findViews(View v){
        mNoteSpinner = (Spinner)v.findViewById(R.id.note_spinner);
        mModeSpinner = (Spinner)v.findViewById(R.id.mode_spinner);
    }

    public void setViews(){
        //<editor-fold desc="NOTE SPINNER">
        ArrayAdapter<String> noteAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Chord.invertedScale);
        noteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNoteSpinner.setAdapter(noteAdapter);
        mNoteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentNote=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCurrentNote=0;
            }
        });
        mNoteSpinner.setSelection(getArguments().getInt(EXTRA_CHORD_NOTE));
        //</editor-fold>

        //<editor-fold desc="MODE SPINNER">
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Chord.modesDisplay);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mModeSpinner.setAdapter(modeAdapter);
        mModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentMode=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCurrentMode=0;
            }
        });
        if(getArguments().getInt(EXTRA_CHORD_MODE)==-1){
            mModeSpinner.setSelection(0);
            mModeSpinner.setEnabled(false);
            mModeSpinner.setVisibility(View.GONE);
        }else{
            mModeSpinner.setSelection(getArguments().getInt(EXTRA_CHORD_MODE));
        }

        //</editor-fold>
    }

    @Override
    public boolean callback(){
        Chord chord = new Chord(mCurrentNote, mCurrentMode, 0);
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

}
