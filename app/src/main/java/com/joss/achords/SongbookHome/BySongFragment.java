package com.joss.achords.SongbookHome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joss.achords.AchordsActivity;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.Models.Songlist;
import com.joss.achords.R;
import com.joss.achords.SongEnvironment.SongActivity;
import com.joss.utils.AbstractDialog.OnDialogFragmentInteractionListener;
import com.joss.utils.SelectAdapter.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BySongFragment extends SongbookFragment implements
        OnAddToListListener,
        OnDialogFragmentInteractionListener,
        OnItemClickListener{

    private static final int ADD_TO_LIST_REQUEST_CODE =1;

    private List<Song> mSongs;
    private Songbook songbook;
    private SongAdapter adapter;
    public static final String ARTIST_KEY = "artist";
    public static final String SONGLIST_KEY = "songlist";

    public BySongFragment() {
        // Required empty public constructor
    }

    public static BySongFragment newInstance() {
        return new BySongFragment();
    }

    public static BySongFragment newInstance(String artist) {
        BySongFragment fragment = new BySongFragment();
        Bundle args = new Bundle();
        args.putString(ARTIST_KEY, artist);
        fragment.setArguments(args);
        return fragment;
    }

    public static BySongFragment newInstance(Songlist list){
        BySongFragment fragment = new BySongFragment();
        Bundle args = new Bundle();
        args.putString(SONGLIST_KEY, list.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songbook = AchordsActivity.SONGBOOK;
        mSongs = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_by_song, container, false);

        //<editor-fold desc="SET ADAPTER">
        adapter = new SongAdapter(mSongs);
        adapter.setOnItemClickListener(this);
        adapter.setOnAdapterSelectModeChangeListener(this);
        adapter.setOnAddToListListener(this);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        refresh();
        //</editor-fold>

        return v;
    }

    @Override
    public void onItemClick(int position) {
        Song clickedSong = adapter.getItems().get(position);
        Intent i = new Intent (getActivity(), SongActivity.class);
        i.putExtra(AchordsActivity.EXTRA_SONG_ID, clickedSong.getId());
        i.putExtra(SongActivity.EXTRA_MODE, SongActivity.EXTRA_DISPLAY_MODE);
        startActivity(i);
    }

    public void deleteSelected(){
        if(getArguments() != null && getArguments().containsKey(SONGLIST_KEY)) {
            Songlist list = songbook.getSonglistFromName(getArguments().getString(SONGLIST_KEY));
            for(Song song : adapter.getSelected()){
                list.remove(song);
                Songbook.get(context).saveSonglists();
            }
        }
        else {
            for(Song song : adapter.getSelected()){
                Songbook.get(context).deleteSong(song.getId());
            }
        }
        exitSelectionMode();
    }

    @Override
    void exitSelectionMode() {
        adapter.resetSelected();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void refresh() {
        mSongs = new ArrayList<>();

        if (getArguments() != null && getArguments().containsKey(ARTIST_KEY)) {
            mSongs = songbook.getSongsOfArtist(getArguments().getString(ARTIST_KEY));
        }
        else if(getArguments() != null && getArguments().containsKey(SONGLIST_KEY)){
            mSongs = songbook.getSongsOfList(songbook.getSonglistFromName(getArguments().getString(SONGLIST_KEY)));
        }
        else {
            mSongs = Songbook.get(context).getSongs();
        }
        adapter.setItems(mSongs);
    }

    @Override
    public void filter(String s) {
        adapter.getFilter().filter(s);
    }

    @Override
    public void onAddToList(Song song) {
        AddToListDialogFragment fr = AddToListDialogFragment.newInstance(song.getId());
        fr.setRequestCode(ADD_TO_LIST_REQUEST_CODE);
        fr.setOnFragmentInteractionListener(this);
        fr.show(getFragmentManager(), "ADD TO LIST");
    }

    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Object... args) {
        switch(requestCode){
            case ADD_TO_LIST_REQUEST_CODE:
                if(resultCode == AppCompatActivity.RESULT_OK){
                    @SuppressWarnings("unchecked")
                    List<String> listsNames = (List<String>) args[0];
                    for(String listName : listsNames) {
                        boolean newList =true;
                        Songlist list = new Songlist();
                        for(Songlist existingList : songbook.getLists()){
                            if(existingList.getName().equals(listName)){
                                newList = false;
                                list = existingList;
                            }
                        }
                        if(newList){
                            list = new Songlist();
                            list.setName(listName);
                            list.setUser("JOSS");
                            songbook.getLists().add(list);
                        }
                        UUID songId = (UUID)args[1];
                        list.getSongsIds().add(songId);
                    }
                    songbook.saveSonglists();
                    if(getActivity() instanceof SongbookActivity){
                        ((SongbookActivity)getActivity()).goToLists();
                    }
                }
                break;
        }
    }
}
