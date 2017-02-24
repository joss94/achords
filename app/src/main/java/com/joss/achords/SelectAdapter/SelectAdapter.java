package com.joss.achords.SelectAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.joss.achords.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by joss on 09/02/17.
 */

public abstract class SelectAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<Integer> selected = new ArrayList<>();
    protected List<T> items = new ArrayList<>();
    protected OnAdapterSelectModeChangeListener listener;
    private OnItemClickListener clickListener;
    private boolean selectMode;
    private boolean selectionUnique;

    protected SelectAdapter(List<T> items) {
        selected = new ArrayList<>();
        this.items = items;
        selectMode = false;
        selectionUnique = false;
    }

    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(selected.contains(position)){
            holder.itemView.setSelected(true);
        }else{
            holder.itemView.setSelected(false);
        }

        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                if(!selectMode){
                    selectMode=true;
                    if(listener !=null){
                        listener.onAdapterSelectModeChange(true);
                    }
                }
                addToSelected(holder.getAdapterPosition());
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e){
                if(!selectMode){
                    if(clickListener!=null){
                        clickListener.onClick(holder.getAdapterPosition());
                    }
                }
                else {
                    changeSelectedState(holder.getAdapterPosition());
                }
                return true;
            }
        });

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void addToSelected(Integer position){
        if(selectionUnique && !selected.isEmpty()){
            notifyItemChanged(selected.remove(0));
        }
        if(!selected.contains(position)){
            selected.add(position);
        }
        notifyItemChanged(position);
    }

    private void removeFromSelected(Integer position){
        selected.remove(position);
        if(selected.isEmpty()){
            selectMode=false;
            if(listener!=null){
                listener.onAdapterSelectModeChange(false);
            }
        }
        notifyItemChanged(position);
    }

    public void changeSelectedState(Integer position){
        if(selected.contains(position)){
            removeFromSelected(position);
        }
        else{
            addToSelected(position);
        }
        notifyItemChanged(position);
    }

    public void resetSelected(){
        selected = new ArrayList<>();
        selectMode=false;
        if(listener!=null){
            listener.onAdapterSelectModeChange(false);
        }
    }

    public List<T> getSelected(){
        List<T> r = new ArrayList<>();
        for(Integer i:selected){
            r.add(items.get(i));
        }
        return r;
    }

    public List<T> getItems() {
        return items;
    }

    public void setOnAdapterSelectModeChangeListener(OnAdapterSelectModeChangeListener listener){
        this.listener = listener;
    }

    public void setSelectionUnique(boolean selectionUnique) {
        this.selectionUnique = selectionUnique;
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
