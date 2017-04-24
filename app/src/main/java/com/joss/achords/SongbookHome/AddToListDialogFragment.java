package com.joss.achords.SongbookHome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.joss.achords.AchordsActivity;
import com.joss.achords.Models.Songlist;
import com.joss.achords.R;
import com.joss.utils.AbstractDialog.AbstractDialogFragment;

import java.util.ArrayList;
import java.util.UUID;

/*
 * Created by joss on 23/02/17.
 */

public class AddToListDialogFragment extends AbstractDialogFragment {
    public static final String SONG_ID_KEY = "song_id";

    RecyclerView mRecyclerView;
    EditText newListEditText;
    UUID songId;
    DialogSonglistAdapter adapter;

    public static AddToListDialogFragment newInstance(UUID id){
        Bundle args = new Bundle();
        args.putSerializable(SONG_ID_KEY, id);
        AddToListDialogFragment fr = new AddToListDialogFragment();
        fr.setArguments(args);
        return fr;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        this.songId = (UUID) getArguments().getSerializable(SONG_ID_KEY);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.add_to_list_dialog;
    }

    @Override
    public void findViews(View v){
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        newListEditText = (EditText)v.findViewById(R.id.new_list);
    }

    @Override
    public void setViews(){
        setTitle(getString(R.string.add_to_list_dialog_title));
        adapter = new DialogSonglistAdapter(AchordsActivity.SONGBOOK.getLists());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public boolean callback(){
        ArrayList<String> lists = new ArrayList<>();
        String newListName;
        newListName = newListEditText.getText().toString();
        if(!newListEditText.getText().toString().isEmpty()){
            for(Songlist songlist : AchordsActivity.SONGBOOK.getLists()){
                if(songlist.getName().equals(newListName)){
                    Toast.makeText(getContext(), "There is already a list with this name", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            lists.add(newListName);
        }

        for(Songlist list : adapter.getSelected()){
            lists.add(list.getName());
        }

        if (!lists.isEmpty()) {
            listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK, lists, songId);
            return true;
        } else {
            Toast.makeText(getContext(), "Please select a list", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
