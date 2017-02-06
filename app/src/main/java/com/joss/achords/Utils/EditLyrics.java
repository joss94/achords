package com.joss.achords.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.EditText;

import com.joss.achords.Models.Chord;
import com.joss.achords.Models.Lyrics;

import java.util.ArrayList;

/**
 * Created by Joss on 23/12/2016.
 */

public class EditLyrics extends EditText {

    private Lyrics lyrics;
    private ArrayList<Chord> chords;

    public Lyrics getLyrics() {
        return lyrics;
    }
    public void setLyrics(Lyrics lyrics) {
        this.lyrics = lyrics;
    }
    public ArrayList<Chord> getChords() {
        return chords;
    }
    public void setChords(ArrayList<Chord> chords) {
        this.chords = chords;
    }

    public EditLyrics(Context context) {
        super(context);
        init();
    }

    public EditLyrics(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public EditLyrics(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        lyrics = new Lyrics();
        chords = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas){

    }
}
