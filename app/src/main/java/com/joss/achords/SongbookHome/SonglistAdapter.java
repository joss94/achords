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
import com.joss.achords.Models.Songlist;
import com.joss.achords.R;
import com.joss.achords.SelectAdapter.SelectAdapter;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by joss on 08/02/17.
 */

public class SonglistAdapter extends SelectAdapter<Songlist> implements Filterable{
    private List<Songlist> lists;
    private List<Songlist> listsFiltered;
    private Context context;

    public SonglistAdapter(Context context, List<Songlist> lists) {
        super(lists);
        this.context = context.getApplicationContext();
        this.lists = new ArrayList<>();
        this.listsFiltered = new ArrayList<>();
        this.lists.addAll(this.lists);
        this.listsFiltered.addAll(this.lists);
        items = this.lists;
    }

    @Override
    public SonglistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.songlist_item, parent, false);
        return new SonglistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Songlist list = listsFiltered.get(position);
        ((SonglistViewHolder)holder).listName.setTypeface(AchordsTypefaces.SONG_TITLE_FONT.typeface);
        ((SonglistViewHolder)holder).listName.setText(list.getName());
        if(holder.getAdapterPosition()== listsFiltered.size()-1){
            ((SonglistViewHolder) holder).separator.setVisibility(View.GONE);
        }
        else{
            ((SonglistViewHolder) holder).separator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount(){
        return listsFiltered.size();
    }

    public void refresh(ArrayList<Songlist> lists){
        this.lists.clear();
        this.listsFiltered.clear();
        this.lists.addAll(lists);
        this.listsFiltered.addAll(lists);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Songlist> resultsArray = new ArrayList<>();

                if(constraint.toString().isEmpty()){
                    resultsArray = lists;
                }
                else{
                    for(Songlist list: lists){
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
                listsFiltered = (ArrayList<Songlist>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    protected class SonglistViewHolder extends RecyclerView.ViewHolder {

        TextView listName;
        View separator;

        public SonglistViewHolder(View itemView) {
            super(itemView);
            listName = (TextView)itemView.findViewById(R.id.list_name);
            separator = itemView.findViewById(R.id.songlist_item_separator);
        }
    }
}
