package com.joss.achords.Fragments;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.joss.achords.AbstractDialogFragment;
import com.joss.achords.R;

public class UserDialogFragment extends AbstractDialogFragment {

    private View v;

    public UserDialogFragment() {
        // Required empty public constructor
    }


    public static UserDialogFragment newInstance() {
        UserDialogFragment fragment = new UserDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_user_dialog, container, false);

        setDialogButtons(v);
        setTitle(v, getContext().getResources().getString(R.string.user_dialog_title));

        return v;
    }

    @Override
    public boolean callback(){
        EditText userNameEditText = (EditText)v.findViewById(R.id.user_edit_name);
        String userName = userNameEditText.getEditableText().toString();
        if(!userName.isEmpty()){
            listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK, userName);
            return true;
        }
        else{
            Toast.makeText(getContext(), "Please enter a user name", Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
