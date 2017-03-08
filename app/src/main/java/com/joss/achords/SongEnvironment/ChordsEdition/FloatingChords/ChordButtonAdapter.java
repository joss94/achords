package com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joss.achords.Models.Chord;
import com.joss.achords.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joss on 01/02/2017.
 */

public class ChordButtonAdapter extends RecyclerView.Adapter<ChordButtonAdapter.ChordButtonViewHolder> {
    private static int MAX_LIST_SIZE = 4;

    private List<Chord> chords;
    private Context mContext;
    private OnChordButtonClickListener listener;

    public ChordButtonAdapter(Context context, List<Chord> chords, OnChordButtonClickListener listener) {
        mContext=context.getApplicationContext();
        if(!chords.isEmpty()){
            this.chords = chords.subList(0,Math.max(MAX_LIST_SIZE, chords.size()));
        }
        else{
            this.chords = new ArrayList<>();
        }
        this.listener = listener;
    }

    public void addChord(Chord chord){
        boolean alreadyExists=false;
        Chord otherChord = new Chord();
        for (Chord existingChord : chords){
            if(existingChord.getNote()==chord.getNote() && existingChord.getMode()==chord.getMode()){
                alreadyExists=true;
                otherChord = existingChord;
            }
        }

        if (alreadyExists) {
            chords.remove(otherChord);
            chords.add(otherChord);
        }
        else {
            chords.add(chord);
            while(chords.size()> MAX_LIST_SIZE){
                chords.remove(0);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public ChordButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chord_button_item, parent, false);
        ChordButtonViewHolder holder = new ChordButtonViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChordButtonViewHolder holder, int position) {
        holder.mButton.setChord(chords.get(position));
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChordButtonClicked(((ChordButton)v).getChord());
            }
        });
        holder.mButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onChordButtonLongClicked(v, ((ChordButton)v).getChord());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (chords.isEmpty()?0:chords.size());
    }

    protected class ChordButtonViewHolder extends RecyclerView.ViewHolder{

        private ChordButton mButton;

        public ChordButtonViewHolder(View v) {
            super(v);
            mButton = (ChordButton)v.findViewById(R.id.button);
        }
    }


}