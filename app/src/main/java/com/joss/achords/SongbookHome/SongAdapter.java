package com.joss.achords.SongbookHome;

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
import com.joss.achords.R;
import com.joss.utils.SelectAdapter.SelectAdapter;

import java.util.ArrayList;
import java.util.List;

class SongAdapter extends SelectAdapter<Song> implements Filterable{
    private OnAddToListListener mOnAddToListListener;

    SongAdapter(List<Song> songs) {
        super(songs);
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Song song = items.get(position);
        ((SongViewHolder)holder).name.setTypeface(AchordsTypefaces.SONG_TITLE_FONT.typeface);
        ((SongViewHolder)holder).name.setText(song.getName());
        ((SongViewHolder)holder).artist.setText(song.getArtist());
        if(holder.getAdapterPosition()== allItems.size()-1){
            ((SongViewHolder) holder).separator.setVisibility(View.GONE);
        }
        else{
            ((SongViewHolder) holder).separator.setVisibility(View.VISIBLE);
        }

        ((SongViewHolder) holder).addToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnAddToListListener!=null){
                    mOnAddToListListener.onAddToList(song);
                }
            }
        });
    }

    void setOnAddToListListener(OnAddToListListener mOnAddToListListener) {
        this.mOnAddToListListener = mOnAddToListListener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Song> resultsArray = new ArrayList<>();

                if(constraint.toString().isEmpty()){
                    resultsArray = allItems;
                }
                else{
                    for(Song song: allItems){
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
                items = (ArrayList<Song>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    private class SongViewHolder extends RecyclerView.ViewHolder {

        TextView artist;
        TextView name;
        ImageView addToList;
        View separator;

        SongViewHolder(View itemView) {
            super(itemView);
            artist = (TextView)itemView.findViewById(R.id.song_item_artist);
            name = (TextView)itemView.findViewById(R.id.song_item_title);
            separator = itemView.findViewById(R.id.song_item_separator);
            addToList = (ImageView) itemView.findViewById(R.id.add_to_list);
        }
    }
}
