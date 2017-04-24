package com.joss.achords.SongEnvironment.SongEdit;

import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.joss.achords.R;
import com.joss.utils.AbstractDialog.AbstractDialogFragment;

@TargetApi(15)
public class URLDialogFragment extends AbstractDialogFragment {

    private EditText urlEdit;

    @Override
    public int getLayoutId() {
        return R.layout.url_dialog;
    }

    public static URLDialogFragment newInstance(){
        return new URLDialogFragment();
    }

    @Override
    public void findViews(View v){
        urlEdit = (EditText)v.findViewById(R.id.url);
    }

    @Override
    public void setViews(){
        setTitle(getContext().getResources().getString(R.string.url_dialog_title));
    }

    @Override
    public boolean callback(){
        listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK, urlEdit.getText().toString());
        return true;
    }
}
