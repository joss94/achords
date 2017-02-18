package com.joss.achords;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;

public abstract class AbstractDialogFragment extends DialogFragment implements View.OnClickListener {

    protected OnDialogFragmentInteractionListener listener;

    public AbstractDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
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
