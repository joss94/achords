package com.joss.achords.SongbookHome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joss.achords.Models.Song;
import com.joss.achords.R;
import com.joss.achords.SelectAdapter.SelectAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joss on 08/02/17.
 */

public class SongAdapter extends SelectAdapter<Song> {
    private List<Song> songs;

    public SongAdapter(List<Song> songs) {
        super(songs);
        this.songs=songs;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        SongViewHolder holder = new SongViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Song song = songs.get(position);
        ((SongViewHolder)holder).name.setText(song.getName());
        ((SongViewHolder)holder).artist.setText(song.getArtist());
    }

    public void refresh(ArrayList<Song> songs){
        this.songs.clear();
        for(Song song:songs){
            this.songs.add(song);
        }
        notifyDataSetChanged();
    }

    protected class SongViewHolder extends RecyclerView.ViewHolder {

        TextView artist;
        TextView name;

        public SongViewHolder(View itemView) {
            super(itemView);
            artist = (TextView)itemView.findViewById(R.id.song_item_artist);
            name = (TextView)itemView.findViewById(R.id.song_item_title);
        }
    }
}
