package com.joss.achords.SongEnvironment.SongEdit;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.joss.achords.AbstractDialogFragment;
import com.joss.achords.R;

import java.util.Calendar;

/*
 * Created by Joss on 21/12/2016.
 */

@TargetApi(15)
public class YearDialogFragment extends AbstractDialogFragment {
    public static final String EXTRA_DATE="Extra_date";

    private int mCurrentYear;
    private NumberPicker mNumberPicker;

    public static YearDialogFragment newInstance(int year){
        Bundle args=new Bundle();
        args.putInt(EXTRA_DATE, year);
        YearDialogFragment fragment = new YearDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentYear=getArguments().getInt(EXTRA_DATE);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){

        View yearPickerView = getActivity().getLayoutInflater().inflate(R.layout.date_dialog, null);
        findViews(yearPickerView);
        setViews();
        setDialogButtons(yearPickerView);
        setTitle(yearPickerView, getContext().getResources().getString(R.string.year_dialog_title));
        return yearPickerView;
    }

    public void findViews(View v){
        mNumberPicker=(NumberPicker)v.findViewById(R.id.year_number_picker);
    }

    public void setViews(){
        mNumberPicker.setMinValue(1900);
        mNumberPicker.setMaxValue(Calendar.getInstance().get(Calendar.YEAR));
        mNumberPicker.setWrapSelectorWheel(false);
        mNumberPicker.setValue(mCurrentYear);
    }

    @Override
    public boolean callback(){
        listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK, mNumberPicker.getValue());
        return true;
    }
}
