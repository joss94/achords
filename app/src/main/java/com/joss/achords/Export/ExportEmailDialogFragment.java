package com.joss.achords.Export;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.joss.achords.R;
import com.joss.utils.AbstractDialog.AbstractDialogFragment;
import com.joss.utils.AbstractDialog.OnDialogFragmentInteractionListener;

public class ExportEmailDialogFragment extends AbstractDialogFragment {

    private EditText emailEditText;

    public ExportEmailDialogFragment() {
        setLayoutId(R.layout.fragment_export_email);
    }

    public static ExportEmailDialogFragment newInstance() {
        return new ExportEmailDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (OnDialogFragmentInteractionListener)getActivity();
    }

    @Override
    public void findViews(View v){
        super.findViews(v);
        emailEditText = (EditText)v.findViewById(R.id.export_email);
    }

    @Override
    protected void setViews(){
        super.setViews();
        setTitle(getString(R.string.export_dialog_title));
    }

    @Override
    public boolean callback(){
        String email = emailEditText.getText().toString();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid e-mail address", Toast.LENGTH_LONG).show();
            return false;
        }
        else{
            listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK, email);
            return true;
        }
    }

}
