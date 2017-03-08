package com.joss.achords.SongbookHome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joss.achords.AbstractParentActivity;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.OnItemClickListener;
import com.joss.achords.R;
import com.joss.achords.SelectAdapter.OnAdapterSelectModeChangeListener;
import com.joss.achords.SongEnvironment.SongActivity;

import java.util.ArrayList;

public class BySongFragment extends Fragment implements OnItemClickListener, SongbookFragment {

    private OnAdapterSelectModeChangeListener mSelectModeListener;
    private ArrayList<Song> mSongs;
    private SongAdapter adapter;
    private RecyclerView recyclerView;
    public static final String ARTIST_KEY = "artist";

    public BySongFragment() {
        // Required empty public constructor
    }

    public static BySongFragment newInstance() {
        BySongFragment fragment = new BySongFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static BySongFragment newInstance(String artist) {
        BySongFragment fragment = new BySongFragment();
        Bundle args = new Bundle();
        args.putString(ARTIST_KEY, artist);
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
        View v =  inflater.inflate(R.layout.fragment_by_song, container, false);

        if (getArguments().containsKey(ARTIST_KEY)) {
            mSongs = Songbook.get(getContext()).getSongsOfArtist(getArguments().getString(ARTIST_KEY));
        } else {
            mSongs = Songbook.get(getContext()).getSongs();
        }

        //<editor-fold desc="SET ADAPTER">
        adapter = new SongAdapter(getContext(), mSongs);
        adapter.setOnItemClickListener(this);
        if (mSelectModeListener != null) {
            adapter.setOnAdapterSelectModeChangeListener(mSelectModeListener);
        }

        recyclerView = (RecyclerView) v.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        //</editor-fold>

        return v;
    }

    @Override
    public void onAttach(Context c){
        super.onAttach(c);
        if(getActivity() instanceof OnAdapterSelectModeChangeListener){
            mSelectModeListener = (OnAdapterSelectModeChangeListener) getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSelectModeListener = null;
    }

    @Override
    public void onClick(int position) {
        Song clickedSong = adapter.getItems().get(position);
        Intent i = new Intent (getActivity(), SongActivity.class);
        i.putExtra(AbstractParentActivity.EXTRA_SONG_ID, clickedSong.getId());
        i.putExtra(SongActivity.EXTRA_MODE, SongActivity.EXTRA_DISPLAY_MODE);
        startActivity(i);
    }

    public void deleteSelected(){
        for(Song song : adapter.getSelected()){
            Songbook.get(getContext()).deleteSong(song.getId());
        }
        adapter.resetSelected();
    }

    @Override
    public void refresh() {
        if (getArguments().containsKey(ARTIST_KEY)) {
            mSongs = Songbook.get(getContext()).getSongsOfArtist(getArguments().getString(ARTIST_KEY));
        } else {
            mSongs = Songbook.get(getContext()).getSongs();
        }
        adapter.refresh(mSongs);
    }

    @Override
    public void filter(String s) {
        adapter.getFilter().filter(s);
    }
}
