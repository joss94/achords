package com.joss.achords.SongEnvironment.SongEdit;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.joss.achords.AbstractDialogFragment;
import com.joss.achords.R;

import java.util.Calendar;

/*
 * Created by Joss on 21/12/2016.
 */

@TargetApi(15)
public class URLDialogFragment extends AbstractDialogFragment {

    private EditText urlEdit;

    public static URLDialogFragment newInstance(){
        Bundle args=new Bundle();
        URLDialogFragment fragment = new URLDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){

        View v = getActivity().getLayoutInflater().inflate(R.layout.url_dialog, null);

        urlEdit = (EditText)v.findViewById(R.id.url);

        setDialogButtons(v);
        setTitle(v, getContext().getResources().getString(R.string.url_dialog_title));

        return v;
    }

    @Override
    public boolean callback(){
        listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK, urlEdit.getText().toString());
        return true;
    }
}
