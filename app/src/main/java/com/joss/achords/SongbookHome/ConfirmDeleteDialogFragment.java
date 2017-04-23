package com.joss.achords.SongbookHome;

import android.support.v7.app.AppCompatActivity;

import com.joss.achords.R;
import com.joss.utils.AbstractDialog.AbstractDialogFragment;

/*
 * Created by joss on 23/02/17.
 */

public class ConfirmDeleteDialogFragment extends AbstractDialogFragment {

    public ConfirmDeleteDialogFragment() {
        setLayoutId(R.layout.confirm_delete_dialog);
    }

    @Override
    protected void setViews(){
        super.setViews();
        setTitle(getContext().getResources().getString(R.string.confirm_delete_dialog_title));
    }

    @Override
    public boolean callback(){
        listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK);
        return true;
    }
}
