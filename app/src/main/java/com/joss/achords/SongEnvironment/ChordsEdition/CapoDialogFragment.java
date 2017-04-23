package com.joss.achords.SongEnvironment.ChordsEdition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;

import com.joss.achords.R;
import com.joss.utils.AbstractDialog.AbstractDialogFragment;

/*
 * Created by joss on 19/02/17.
 */

public class CapoDialogFragment extends AbstractDialogFragment {

    private static final String CAPO_KEY = "capo";

    private int capo;
    private NumberPicker capoPicker;

    public CapoDialogFragment() {
        setLayoutId(R.layout.capo_dialog);
    }

    public static CapoDialogFragment newInstance(int capo){
        CapoDialogFragment fr = new CapoDialogFragment();
        Bundle args = new Bundle();
        args.putInt(CAPO_KEY, capo);
        fr.setArguments(args);
        return fr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        capo = getArguments().getInt(CAPO_KEY);
    }

    @Override
    public void findViews(View v){
        capoPicker = (NumberPicker) v.findViewById(R.id.capo_number_picker);
    }

    @Override
    public void setViews(){
        setTitle(getContext().getResources().getString(R.string.capo_dialog_title));
        capoPicker.setMinValue(0);
        capoPicker.setMaxValue(15);
        capoPicker.setWrapSelectorWheel(false);
        capoPicker.setValue(capo);
    }

    @Override
    public boolean callback(){
        listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK, capoPicker.getValue());
        return true;
    }

}
