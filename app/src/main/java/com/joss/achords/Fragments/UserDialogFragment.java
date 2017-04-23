package com.joss.achords.Fragments;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.joss.achords.R;
import com.joss.utils.AbstractDialog.AbstractDialogFragment;

public class UserDialogFragment extends AbstractDialogFragment {

    private EditText userName;

    public UserDialogFragment() {
        setLayoutId(R.layout.fragment_user_dialog);
    }


    public static UserDialogFragment newInstance() {
        return new UserDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void findViews(View v){
        super.findViews(v);
        userName = (EditText)v.findViewById(R.id.user_edit_name);
    }

    @Override
    protected void setViews(){
        super.setViews();
        setTitle(getContext().getResources().getString(R.string.user_dialog_title));
        cancel_button.setVisibility(View.GONE);
    }

    @Override
    public boolean callback(){
        String name = userName.getEditableText().toString();
        if(!name.isEmpty()){
            listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK, name);
            return true;
        }
        else{
            Toast.makeText(getContext(), "Please enter a user name", Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
