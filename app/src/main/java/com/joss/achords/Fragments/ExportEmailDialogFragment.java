package com.joss.achords.Fragments;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.joss.achords.Interfaces.OnDialogFragmentInteractionListener;
import com.joss.achords.R;

public class ExportEmailDialogFragment extends AbstractDialogFragment {

    public static final int EMAIL_REQUEST_CODE = 234;
    private EditText emailEditText;

    public ExportEmailDialogFragment() {
        // Required empty public constructor
    }

    public static ExportEmailDialogFragment newInstance() {
        ExportEmailDialogFragment fragment = new ExportEmailDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (OnDialogFragmentInteractionListener)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_export_email, container, false);
        emailEditText = (EditText)v.findViewById(R.id.export_email);
        setDialogButtons(v);
        return v;
    }

    @Override
    public boolean callback(){
        String email = emailEditText.getText().toString();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid e-mail address", Toast.LENGTH_LONG).show();
            return false;
        }
        else{
            listener.onFragmentInteraction(EMAIL_REQUEST_CODE, AppCompatActivity.RESULT_OK, email);
            return true;
        }
    }

}