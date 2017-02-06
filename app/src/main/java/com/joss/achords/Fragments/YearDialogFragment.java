package com.joss.achords.Fragments;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.joss.achords.Interfaces.OnFragmentInteractionListener;
import com.joss.achords.R;

import java.util.Calendar;

/**
 * Created by Joss on 21/12/2016.
 */

@TargetApi(15)
public class YearDialogFragment extends AbstractDialogFragment {
    public static final String EXTRA_DATE="Extra_date";
    public static final int RELEASE_YEAR_REQUEST_CODE = 23512;

    private int mCurrentYear;
    private NumberPicker mNumberPicker;
    private OnFragmentInteractionListener listener;

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
        listener = (OnFragmentInteractionListener)getActivity();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){

        View yearPickerView = getActivity().getLayoutInflater().inflate(R.layout.date_dialog, null);

        mCurrentYear=getArguments().getInt(EXTRA_DATE);
        mNumberPicker=(NumberPicker)yearPickerView.findViewById(R.id.year_number_picker);
        mNumberPicker.setMinValue(1900);
        mNumberPicker.setMaxValue(Calendar.getInstance().get(Calendar.YEAR));
        mNumberPicker.setWrapSelectorWheel(false);
        mNumberPicker.setValue(mCurrentYear);

        setDialogButtons(yearPickerView);

        return yearPickerView;
    }

    @Override
    public boolean callback(){
        listener.onFragmentInteraction(RELEASE_YEAR_REQUEST_CODE, AppCompatActivity.RESULT_OK, mNumberPicker.getValue());
        return true;
    }
}
