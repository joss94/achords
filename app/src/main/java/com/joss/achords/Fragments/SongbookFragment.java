package com.joss.achords.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.joss.achords.Activities.SongActivity;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;

import java.util.ArrayList;

public class SongbookFragment extends ListFragment implements Songbook.OnSongbookChangeListener {
    private static final String TAG = "Songbook Fragment";
    public static final String EXTRA_SONG_ID="song_id";

    private SongAdapter adapter;

    public SongbookFragment() {
    }

    public static SongbookFragment newInstance() {
        SongbookFragment fragment = new SongbookFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Songbook.get(getContext()).setOnSongbookChangeListener(this);

        //Set the adapter
        ArrayList<Song> mSongs = Songbook.get(getContext()).getSongs();
        adapter = new SongAdapter(getContext(), mSongs);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_songbook, container, false);

        //Setting Button
        ImageButton newSongButton=(ImageButton) v.findViewById(R.id.new_song_button);
        ((GradientDrawable)newSongButton.getBackground()).setColor(getResources().getColor(R.color.Red));
        newSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), SongActivity.class);
                i.putExtra(SongActivity.EXTRA_MODE, SongActivity.EXTRA_CREATE_MODE);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Log.i(TAG, "ListItem Clicked !!");
        Intent i = new Intent (getActivity(), SongActivity.class);
        Song selected_song= (Song)getListAdapter().getItem(position);
        i.putExtra(EXTRA_SONG_ID, selected_song.getId());
        i.putExtra(SongActivity.EXTRA_MODE, SongActivity.EXTRA_DISPLAY_MODE);
        startActivity(i);
    }

    @Override
    public void onDBChange() {
        adapter.refresh(Songbook.get(getContext()).getSongs());
    }

    //CUSTOMIZED ADAPTER
    private class SongAdapter extends ArrayAdapter<Song>{

        SongAdapter(Context context, ArrayList<Song> songs) {
            super(context.getApplicationContext(), 0, songs);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView==null) {
                convertView=getActivity().getLayoutInflater().inflate(R.layout.song_item, null);
            }
            TextView artist = (TextView)convertView.findViewById(R.id.song_item_artist);
            TextView name = (TextView)convertView.findViewById(R.id.song_item_title);
            ImageButton delete = (ImageButton)convertView.findViewById(R.id.delete_button);

            final Song song = getItem(position);

            name.setText(song.getName());
            artist.setText(song.getArtist());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Songbook.get(getContext()).deleteSong(song.getId());
                    refresh(Songbook.get(getContext()).getSongs());
                }
            });

            return convertView;
        }


        void refresh(ArrayList<Song> songs){
            this.clear();
            this.addAll(songs);
        }
    }


}
