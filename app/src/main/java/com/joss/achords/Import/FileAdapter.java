package com.joss.achords.Import;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joss.achords.R;
import com.joss.utils.SelectAdapter.SelectAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by joss on 09/02/17.
 */

class FileAdapter extends SelectAdapter<File> {

    FileAdapter(List<File> items) {
        super(items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new FileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(position==0){
            ((FileViewHolder)holder).fileName.setText("..");
        } else{
            String name = items.get(holder.getAdapterPosition()-1).getName();
            ((FileViewHolder)holder).fileName.setText(name);
        }
    }

    @Override
    public List<File> getSelected(){
        ArrayList<File> r = new ArrayList<>();
        for(Integer i : selected){
            if(i>0){
                r.add(items.get(i-1));
            }
        }
        return r;
    }

    @Override
    public int getItemCount(){
        return Math.max(super.getItemCount(), 1);
    }

    private class FileViewHolder extends RecyclerView.ViewHolder{

        TextView fileName;

        FileViewHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.file_name);
        }
    }
}
