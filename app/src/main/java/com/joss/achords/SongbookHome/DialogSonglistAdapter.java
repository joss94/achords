package com.joss.achords.SongbookHome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.joss.achords.AchordsTypefaces;
import com.joss.achords.Models.Songlist;
import com.joss.achords.R;
import com.joss.utils.SelectAdapter.SelectAdapter;

import java.util.List;

/*
 * Created by joss on 08/02/17.
 */

class DialogSonglistAdapter extends SelectAdapter<Songlist> {

    public DialogSonglistAdapter(List<Songlist> lists) {
        super(lists);
    }

    @Override
    public SonglistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_songlist_item, parent, false);
        return new SonglistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((SonglistViewHolder) holder).checkBox.setOnCheckedChangeListener(null);
        if(selected.contains(position)){
            ((SonglistViewHolder) holder).checkBox.setChecked(true);
        }
        else{
            ((SonglistViewHolder) holder).checkBox.setChecked(false);
        }
        super.onBindViewHolder(holder, position);
        Songlist list = items.get(position);
        ((SonglistViewHolder)holder).listName.setTypeface(AchordsTypefaces.SONG_TITLE_FONT.typeface);
        ((SonglistViewHolder)holder).listName.setText(list.getName());
        holder.itemView.setOnTouchListener(null);
        ((SonglistViewHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    addToSelected(holder.getAdapterPosition());
                }
                else{
                    removeFromSelected(holder.getAdapterPosition());
                }
            }
        });

    }

    private class SonglistViewHolder extends RecyclerView.ViewHolder {

        TextView listName;
        CheckBox checkBox;

        SonglistViewHolder(View itemView) {
            super(itemView);
            listName = (TextView)itemView.findViewById(R.id.list_name);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkbox);
        }
    }
}
