package com.joss.achords.SongbookHome;

/*
 * Created by joss on 28/02/17.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.joss.achords.R;
import com.joss.utils.AbstractDialog.OnDialogFragmentInteractionListener;
import com.joss.utils.SelectAdapter.OnSelectModeChangeListener;

import static com.joss.achords.AchordsActivity.DELETE_MENU_ITEM;

public abstract class SongbookFragment extends Fragment implements OnSelectModeChangeListener {

    private static final int DELETE_SONG_CONFIRM_REQUEST_CODE = 1;
    private Menu menu;
    protected Context context;

    @Override
    public void onCreate(Bundle args){
        super.onCreate(args);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context.getApplicationContext();
    }



    @Override
    public void onSelectModeChange(boolean selectMode) {
        setHasOptionsMenu(true);
        menu.findItem(R.id.delete).setVisible(selectMode);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case DELETE_MENU_ITEM:
                ConfirmDeleteDialogFragment fr = new ConfirmDeleteDialogFragment();
                fr.setOnFragmentInteractionListener(new OnDialogFragmentInteractionListener() {
                    @Override
                    public void onFragmentInteraction(int requestCode, int resultCode, Object... args) {
                        if(requestCode==DELETE_SONG_CONFIRM_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK){
                            deleteSelected();
                        }
                    }
                });
                fr.setRequestCode(DELETE_SONG_CONFIRM_REQUEST_CODE);
                fr.show(getActivity().getSupportFragmentManager(), "DELETE_CONFIRM");
                return true;

            default:
                return false;
        }
    }

    abstract void filter(String s);
    abstract void deleteSelected();
    abstract void exitSelectionMode();
    abstract void refresh();
}
