package com.joss.achords.SongbookHome;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joss.achords.Models.Songlist;
import com.joss.achords.OnItemClickListener;
import com.joss.achords.R;
import com.joss.achords.SelectAdapter.OnAdapterSelectModeChangeListener;

import java.util.ArrayList;

public class ByListFragment extends Fragment implements OnItemClickListener, OnAdapterSelectModeChangeListener, SongbookFragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<Songlist> mLists;
    private SonglistAdapter adapter;
    private RecyclerView recyclerView;

    public ByListFragment() {
        // Required empty public constructor
    }

    public static ByListFragment newInstance() {
        ByListFragment fragment = new ByListFragment();
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
        View v =  inflater.inflate(R.layout.fragment_by_list, container, false);

        //<editor-fold desc="SET ADAPTER">
        /*ArrayList<Songlist> mLists = Songbook.get(getContext()).getLists();
        adapter = new SonglistAdapter(getContext(), mLists);
        adapter.setOnItemClickListener(this);
        adapter.setOnAdapterSelectModeChangeListener(this);

        recyclerView = (RecyclerView) v.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);*/
        //</editor-fold>



        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(int position) {
    }

    @Override
    public void onAdapterSelectModeChange(boolean selectMode) {
    }

    @Override
    public void filter(String s) {

    }

    @Override
    public void deleteSelected() {

    }

    @Override
    public void refresh() {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
