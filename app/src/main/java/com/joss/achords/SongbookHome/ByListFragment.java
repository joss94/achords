package com.joss.achords.SongbookHome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joss.achords.AchordsActivity;
import com.joss.achords.Models.Songlist;
import com.joss.achords.R;
import com.joss.utils.SelectAdapter.OnAdapterSelectModeChangeListener;
import com.joss.utils.SelectAdapter.OnItemClickListener;

import java.util.List;

public class ByListFragment extends SongbookFragment implements
        OnItemClickListener,
        OnAdapterSelectModeChangeListener {

    private OnListClickedListener listClickedListener;
    private SonglistAdapter adapter;
    private RecyclerView recyclerView;

    private OnAdapterSelectModeChangeListener mSelectModeListener;

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
        List<Songlist> mLists = AchordsActivity.SONGBOOK.getLists();
        adapter = new SonglistAdapter(mLists);
        adapter.setOnItemClickListener(this);
        adapter.setOnAdapterSelectModeChangeListener(this);

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

    public void setListClickedListener(OnListClickedListener listClickedListener) {
        this.listClickedListener = listClickedListener;
    }

    @Override
    public void onItemClick(int position) {
        if (listClickedListener!=null) {
            listClickedListener.onListClicked(adapter.getItems().get(position));
        }
    }

    @Override
    public void onAdapterSelectModeChange(boolean selectMode) {
        if(mSelectModeListener != null){
            mSelectModeListener.onAdapterSelectModeChange(selectMode);
        }
    }

    @Override
    public void filter(String s) {

    }

    @Override
    public void deleteSelected() {
        for(Songlist songlist : adapter.getSelected()){
            AchordsActivity.SONGBOOK.deleteList(songlist);
        }
        adapter.resetSelected();
    }


    @Override
    public void refresh() {
        adapter.setItems(AchordsActivity.SONGBOOK.getLists());
    }

    interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public interface OnListClickedListener{
        void onListClicked(Songlist list);
    }
}
