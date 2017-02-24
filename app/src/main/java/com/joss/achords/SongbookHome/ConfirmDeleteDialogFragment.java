package com.joss.achords.SongbookHome;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.joss.achords.AbstractDialogFragment;
import com.joss.achords.R;
import com.joss.achords.SongEnvironment.SongEdit.URLDialogFragment;

/**
 * Created by joss on 23/02/17.
 */

public class ConfirmDeleteDialogFragment extends AbstractDialogFragment{
    public static final int DELETE_SONG_CONFIRM_REQUEST_CODE = 6541;

    private TextView warningMessage;

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

        warningMessage = (TextView) v.findViewById(R.id.warning_message);

        setDialogButtons(v);
        setTitle(v, getContext().getResources().getString(R.string.confirm_delete_dialog_title));

        return v;
    }

    @Override
    public boolean callback(){
        listener.onFragmentInteraction(DELETE_SONG_CONFIRM_REQUEST_CODE, AppCompatActivity.RESULT_OK);
        return true;
    }
}
