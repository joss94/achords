package com.joss.achords;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import static android.widget.ListPopupWindow.WRAP_CONTENT;

public abstract class AbstractDialogFragment extends DialogFragment implements View.OnClickListener {

    protected OnDialogFragmentInteractionListener listener;
    private int requestCode=0;

    public AbstractDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(final Bundle b){
        Dialog d = super.onCreateDialog(b);
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return d;
    }

    @Override
    public void onResume(){
        super.onResume();
        Window w = getDialog().getWindow();
        if (w != null) {
            w.setLayout((int) (getResources().getDisplayMetrics().widthPixels*0.8), WRAP_CONTENT);
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public void setDialogButtons(View v){
        super.onStart();
        if(v!=null){
            Button ok_button = (Button)v.findViewById(R.id.OK_button);
            if(ok_button!=null){
                ok_button.setOnClickListener(this);
            }
            Button cancel_button = (Button)v.findViewById(R.id.cancel_button);
            if(cancel_button!=null){
                cancel_button.setOnClickListener(this);
            }
        }
    }

    public void setTitle(View v, String title){
        ((TextView)v.findViewById(R.id.dialog_title)).setText(title);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.cancel_button:
                dismiss();
                break;
            case R.id.OK_button:
                if(callback()){
                    dismiss();
                }
                break;
        }
    }

    public boolean callback(){
        return true;
    }

    public void setOnFragmentInteractionListener(OnDialogFragmentInteractionListener listener){
        this.listener = listener;
    }

}
