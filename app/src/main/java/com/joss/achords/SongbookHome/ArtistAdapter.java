package com.joss.achords.SongbookHome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.joss.achords.AchordsTypefaces;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;
import com.joss.achords.SelectAdapter.SelectAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * Created by joss on 08/02/17.
 */

public class ArtistAdapter extends SelectAdapter<String> implements Filterable{
    private List<String> artists;
    private List<String> artistsFiltered;
    private Context context;

    public ArtistAdapter(Context context, List<String> artists) {
        super(artists);
        this.context = context.getApplicationContext();
        this.artists = new ArrayList<>();
        this.artistsFiltered = new ArrayList<>();
        this.artists.addAll(artists);
        this.artistsFiltered.addAll(artists);
        items = this.artists;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        return new ArtistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String artist = artistsFiltered.get(position);
        ((ArtistViewHolder)holder).artistName.setTypeface(AchordsTypefaces.SONG_TITLE_FONT.typeface);
        ((ArtistViewHolder)holder).artistName.setText(artist);
        ((ArtistViewHolder)holder).numberOfSongs.setText(String.format(Locale.ENGLISH, context.getString(R.string.number_of_songs), Songbook.get(context).getSongsOfArtist(artist).size()));
        if(holder.getAdapterPosition()== artistsFiltered.size()-1){
            ((ArtistViewHolder) holder).separator.setVisibility(View.GONE);
        }
        else{
            ((ArtistViewHolder) holder).separator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount(){
        return artistsFiltered.size();
    }

    public void refresh(ArrayList<String> artists){
        this.artists.clear();
        this.artistsFiltered.clear();
        this.artists.addAll(artists);
        this.artistsFiltered.addAll(artists);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<String> resultsArray = new ArrayList<>();

                if(constraint.toString().isEmpty()){
                    resultsArray = artists;
                }
                else{
                    for(String artist: artists){
                        if(artist.toLowerCase().contains(constraint.toString().toLowerCase())){
                            resultsArray.add(artist);
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
                artistsFiltered = (ArrayList<String>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    protected class ArtistViewHolder extends RecyclerView.ViewHolder {

        TextView artistName;
        TextView numberOfSongs;
        View separator;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            artistName = (TextView)itemView.findViewById(R.id.artist_name);
            numberOfSongs = (TextView)itemView.findViewById(R.id.number_of_songs);
            separator = itemView.findViewById(R.id.artist_item_separator);
        }
    }
}
