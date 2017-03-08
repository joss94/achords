package com.joss.achords.SongbookHome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joss.achords.AbstractDialogFragment;
import com.joss.achords.R;

/*
 * Created by joss on 23/02/17.
 */

public class ConfirmDeleteDialogFragment extends AbstractDialogFragment{

    public static ConfirmDeleteDialogFragment newInstance(){
        Bundle args=new Bundle();
        ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
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

        View v = getActivity().getLayoutInflater().inflate(R.layout.confirm_delete_dialog, null);

        setDialogButtons(v);
        setTitle(v, getContext().getResources().getString(R.string.confirm_delete_dialog_title));

        return v;
    }

    @Override
    public boolean callback(){
        listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK);
        return true;
    }
}
