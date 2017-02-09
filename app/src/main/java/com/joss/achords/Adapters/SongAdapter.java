package com.joss.achords.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joss.achords.Interfaces.OnSongItemClickListener;
import com.joss.achords.Models.Song;
import com.joss.achords.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joss on 08/02/17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songs;
    private Context mContext;
    private OnSongItemClickListener listener;
    private ArrayList<Integer> selected;

    public SongAdapter(Context context, List<Song> songs, OnSongItemClickListener listener) {
        mContext = context.getApplicationContext();
        this.listener = listener;
        this.songs=songs;
        selected=new ArrayList<>();
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        SongViewHolder holder = new SongViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.name.setText(song.getName());
        holder.artist.setText(song.getArtist());
        if(selected.contains(position)){
            holder.songItemLayout.setSelected(true);
        }else{
            holder.songItemLayout.setSelected(false);
        }

        final GestureDetector songItemLayoutGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                listener.onSongLongClick(holder.songItemLayout, holder.getAdapterPosition());
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e){
                listener.onSongClick(holder.songItemLayout, holder.getAdapterPosition());
                return true;
            }
        });

        holder.songItemLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return songItemLayoutGestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void refresh(ArrayList<Song> songs){
        this.songs.clear();
        for(Song song:songs){
            this.songs.add(song);
        }
        notifyDataSetChanged();
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void addToSelection(Integer position){
        if(!selected.contains(position)){
            selected.add(position);
        }
        notifyItemChanged(position);
    }

    public void changeSelectedState(Integer position){
        if(selected.contains(position)){
            selected.remove(position);
        }
        else{
            selected.add(position);
        }
        notifyItemChanged(position);
    }

    public ArrayList<Song> getSelected(){
        ArrayList<Song> r = new ArrayList<>();
        for(Integer i:selected){
            r.add(songs.get(i));
        }
        return r;
    }

    protected class SongViewHolder extends RecyclerView.ViewHolder{

        TextView artist;
        TextView name;
        LinearLayout songItemLayout;

        public SongViewHolder(View itemView) {
            super(itemView);
            artist = (TextView)itemView.findViewById(R.id.song_item_artist);
            name = (TextView)itemView.findViewById(R.id.song_item_title);
            songItemLayout = (LinearLayout) itemView.findViewById(R.id.song_item_layout);
        }
    }
}
