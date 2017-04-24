package com.joss.achords.SongbookHome;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joss.achords.AchordsActivity;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;
import com.joss.utils.SelectAdapter.OnItemClickListener;

import java.util.List;

public class ByArtistFragment extends SongbookFragment implements OnItemClickListener {

    private ArtistAdapter adapter;
    private RecyclerView recyclerView;
    private OnArtistClickedListener onArtistClickedListener;

    public ByArtistFragment() {
        // Required empty public constructor
    }

    public static ByArtistFragment newInstance() {
        return new ByArtistFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_by_artist, container, false);

        //<editor-fold desc="SET ADAPTER">
        List<String> mArtists = AchordsActivity.SONGBOOK.getArtists();
        adapter = new ArtistAdapter(context, mArtists);
        adapter.setOnItemClickListener(this);

        recyclerView = (RecyclerView) v.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        //</editor-fold>

        return v;
    }

    @Override
    public void onItemClick(int position) {
        String clickedArtist = adapter.getItems().get(position);
        if(onArtistClickedListener != null){
            onArtistClickedListener.onArtistClicked(clickedArtist);
        }
    }

    public void setOnArtistClickedListener(OnArtistClickedListener listener) {
        this.onArtistClickedListener = listener;
    }

    @Override
    public void refresh() {
        adapter.setItems(Songbook.get(context).getArtists());
    }


    @Override
    public void filter(String s) {
        adapter.getFilter().filter(s);
    }

    @Override
    public void deleteSelected() {

    }

    @Override
    void exitSelectionMode() {

    }

    interface OnArtistClickedListener{
        void onArtistClicked(String artist);
    }
}
