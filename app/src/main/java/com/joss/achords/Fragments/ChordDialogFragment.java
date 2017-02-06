package com.joss.achords.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.joss.achords.Models.Chord;
import com.joss.achords.R;


public class ChordDialogFragment extends AbstractDialogFragment {
    public static final String EXTRA_CHORD_NOTE = "chord_note";
    public static final String EXTRA_CHORD_MODE = "chord_mode";
    public static final int SELECT_CHORD_REQUEST_CODE = 3984;

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

        //<editor-fold desc="NOTE SPINNER">
        mNoteSpinner = (Spinner)chordPickerView.findViewById(R.id.note_spinner);
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
        mModeSpinner = (Spinner)chordPickerView.findViewById(R.id.mode_spinner);
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
        mModeSpinner.setSelection(getArguments().getInt(EXTRA_CHORD_MODE));
        //</editor-fold>

        setDialogButtons(chordPickerView);

        return chordPickerView;
    }

    @Override
    public boolean callback(){
        Chord chord = new Chord(mCurrentNote, mCurrentMode, 0);
        listener.onFragmentInteraction(SELECT_CHORD_REQUEST_CODE, AppCompatActivity.RESULT_OK, chord);
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
