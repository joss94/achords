package com.joss.achords.SongbookHome;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joss.achords.Models.Songbook;
import com.joss.achords.OnItemClickListener;
import com.joss.achords.R;
import com.joss.achords.SelectAdapter.OnAdapterSelectModeChangeListener;

import java.util.ArrayList;

public class ByArtistFragment extends Fragment implements OnItemClickListener, SongbookFragment {

    private OnAdapterSelectModeChangeListener mSelectModeListener;
    private ArrayList<String> mArtists;
    private ArtistAdapter adapter;
    private RecyclerView recyclerView;
    private OnArtistClickedListener onArtistClickedListener;

    public ByArtistFragment() {
        // Required empty public constructor
    }

    public static ByArtistFragment newInstance() {
        ByArtistFragment fragment = new ByArtistFragment();
        Bundle args = new Bundle();
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
        View v =  inflater.inflate(R.layout.fragment_by_artist, container, false);

        //<editor-fold desc="SET ADAPTER">
        mArtists = Songbook.get(getContext()).getArtists();
        adapter = new ArtistAdapter(getContext(), mArtists);
        adapter.setOnItemClickListener(this);

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
        String clickedArtist = adapter.getItems().get(position);
        if(onArtistClickedListener != null){
            onArtistClickedListener.onArtistClicked(clickedArtist);
        }
    }

    public void deleteSelected(){
        adapter.resetSelected();
    }

    public void setOnArtistClickedListener(OnArtistClickedListener listener) {
        this.onArtistClickedListener = listener;
    }

    @Override
    public void refresh() {
        adapter.refresh(Songbook.get(getContext()).getArtists());
    }

    @Override
    public void filter(String s) {
        adapter.getFilter().filter(s);
    }

    public interface OnArtistClickedListener{
        void onArtistClicked(String artist);
    }
}
