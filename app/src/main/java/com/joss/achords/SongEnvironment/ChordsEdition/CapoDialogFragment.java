package com.joss.achords.SongEnvironment.ChordsEdition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.joss.achords.AbstractDialogFragment;
import com.joss.achords.R;

/*
 * Created by joss on 19/02/17.
 */

public class CapoDialogFragment extends AbstractDialogFragment {

    public static final int CAPO_REQUEST_CODE = 68543;

    private int capo;
    private NumberPicker capoPicker;

    public static CapoDialogFragment newInstance(int capo){
        CapoDialogFragment fr = new CapoDialogFragment();
        Bundle args = new Bundle();
        args.putInt("capo", capo);
        fr.setArguments(args);
        return fr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        capo = getArguments().getInt("capo");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.capo_dialog, container, false);
        findViews(v);
        setViews();
        setDialogButtons(v);
        setTitle(v, getContext().getResources().getString(R.string.capo_dialog_title));
        return v;
    }

    public void findViews(View v){
        capoPicker = (NumberPicker) v.findViewById(R.id.capo_number_picker);
    }

    public void setViews(){
        capoPicker.setMinValue(0);
        capoPicker.setMaxValue(15);
        capoPicker.setWrapSelectorWheel(false);
        capoPicker.setValue(capo);
    }

    @Override
    public boolean callback(){
        listener.onFragmentInteraction(CAPO_REQUEST_CODE, AppCompatActivity.RESULT_OK, capoPicker.getValue());
        return true;
    }

}
