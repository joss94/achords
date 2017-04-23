package com.joss.achords.SongbookHome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.joss.achords.AchordsActivity;
import com.joss.achords.AchordsTypefaces;
import com.joss.achords.Models.Songlist;
import com.joss.achords.R;
import com.joss.utils.SelectAdapter.SelectAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * Created by joss on 08/02/17.
 */

class SonglistAdapter extends SelectAdapter<Songlist> implements Filterable{

    private Context context;

    public SonglistAdapter(List<Songlist> lists) {
        super(lists);
    }

    @Override
    public SonglistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.songlist_item, parent, false);
        context = parent.getContext().getApplicationContext();
        return new SonglistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Songlist list = items.get(position);
        ((SonglistViewHolder)holder).listName.setTypeface(AchordsTypefaces.SONG_TITLE_FONT.typeface);
        ((SonglistViewHolder)holder).listName.setText(list.getName());
        ((SonglistViewHolder)holder).numberOfSongs.setText(String.format(Locale.ENGLISH, context.getString(R.string.number_of_songs), AchordsActivity.SONGBOOK.getSongsOfList(list).size()));

        if(holder.getAdapterPosition()== items.size()-1){
            ((SonglistViewHolder) holder).separator.setVisibility(View.GONE);
        }
        else{
            ((SonglistViewHolder) holder).separator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Songlist> resultsArray = new ArrayList<>();

                if(constraint.toString().isEmpty()){
                    resultsArray = allItems;
                }
                else{
                    for(Songlist list: allItems){
                        if(list.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                            resultsArray.add(list);
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
                items = (ArrayList<Songlist>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    private class SonglistViewHolder extends RecyclerView.ViewHolder {

        TextView listName;
        TextView numberOfSongs;
        View separator;

        SonglistViewHolder(View itemView) {
            super(itemView);
            listName = (TextView)itemView.findViewById(R.id.list_name);
            numberOfSongs = (TextView) itemView.findViewById(R.id.number_of_songs);
            separator = itemView.findViewById(R.id.songlist_item_separator);
        }
    }
}
