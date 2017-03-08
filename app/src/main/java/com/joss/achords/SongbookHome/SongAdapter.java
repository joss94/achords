package com.joss.achords.SongbookHome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.joss.achords.AchordsTypefaces;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.Models.Songlist;
import com.joss.achords.R;
import com.joss.achords.SelectAdapter.SelectAdapter;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by joss on 08/02/17.
 */

public class SongAdapter extends SelectAdapter<Song> implements Filterable{
    private List<Song> songs;
    private List<Song> songsFiltered;
    private Context context;

    public SongAdapter(Context context, List<Song> songs) {
        super(songs);
        this.songs = new ArrayList<>();
        this.songsFiltered = new ArrayList<>();
        this.songs.addAll(songs);
        this.songsFiltered.addAll(songs);
        this.context = context.getApplicationContext();
        items = this.songs;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Song song = songsFiltered.get(position);
        ((SongViewHolder)holder).name.setTypeface(AchordsTypefaces.SONG_TITLE_FONT.typeface);
        ((SongViewHolder)holder).name.setText(song.getName());
        ((SongViewHolder)holder).artist.setText(song.getArtist());
        if(holder.getAdapterPosition()==songsFiltered.size()-1){
            ((SongViewHolder) holder).separator.setVisibility(View.GONE);
        }
        else{
            ((SongViewHolder) holder).separator.setVisibility(View.VISIBLE);
        }

        ((SongViewHolder) holder).addToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Songbook.get(context).getLists().get(0) == null){
                    Songbook.get(context).getLists().add(new Songlist());
                }
                Songlist list = Songbook.get(context).getLists().get(0);
                list.getSongsIds().add(song.getId());
                Songbook.get(context).saveSonglists();
            }
        });
    }

    @Override
    public int getItemCount(){
        return songsFiltered.size();
    }

    public void refresh(ArrayList<Song> songs){
        this.songs.clear();
        this.songsFiltered.clear();
        this.songs.addAll(songs);
        this.songsFiltered.addAll(songs);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Song> resultsArray = new ArrayList<>();

                if(constraint.toString().isEmpty()){
                    resultsArray = songs;
                }
                else{
                    for(Song song: songs){
                        if(song.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                                || song.getArtist().toLowerCase().contains(constraint.toString().toLowerCase())){
                            resultsArray.add(song);
                        }
                    }
                }

                results.count=resultsArray.size();
                results.values=resultsArray;

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                songsFiltered = (ArrayList<Song>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    protected class SongViewHolder extends RecyclerView.ViewHolder {

        TextView artist;
        TextView name;
        ImageView addToList;
        View separator;

        public SongViewHolder(View itemView) {
            super(itemView);
            artist = (TextView)itemView.findViewById(R.id.song_item_artist);
            name = (TextView)itemView.findViewById(R.id.song_item_title);
            separator = itemView.findViewById(R.id.song_item_separator);
            addToList = (ImageView) itemView.findViewById(R.id.add_to_list);
        }
    }
}
