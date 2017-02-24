package com.joss.achords.Models;

import android.util.SparseArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Joss on 23/12/2016.
 */

public class Chord implements Serializable{
    public static final int CHORD_MARGIN = 3;

    public static final String JSON_KEY_CHORD = "Chord";
    public static final String JSON_KEY_MODE = "Mode";
    public static final String JSON_KEY_POSITION = "Position";

    public static final HashMap<String, Integer> scale =new HashMap();
    static{
        scale.put("A", 0);
        scale.put("A#", 1);
        scale.put("B", 2);
        scale.put("C", 3);
        scale.put("C#", 4);
        scale.put("D", 5);
        scale.put("D#", 6);
        scale.put("E", 7);
        scale.put("E#", 8);
        scale.put("F", 9);
        scale.put("G", 10);
        scale.put("G#", 11);
    }
    public static final String[] invertedScale = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    public static final int MAJOR = 0;
    public static final int MINOR = 1;
    public static final int SEVEN = 2;
    public static final int SEVEN_MAJOR = 3;
    public static final int FOUR = 4;
    public static final int SIX = 5;
    public static final String[] modes = {"","m", "7", "7M", "4", "6"};
    public static final String[] modesDisplay = {"M","m", "7", "7M", "4", "6"};
    public static final SparseArray<String> modeDisplay = new SparseArray<>();


    private int note;
    private int mode;
    private int position;

    public Chord() {
        note =0;
        mode=MAJOR;
        position=0;
    }

    public Chord(int note, int mode, int position) {
        this.note = note;
        this.mode = mode;
        this.position = position;
    }

    public Chord (JSONObject jsonObject) {
        try {
            this.note = (int)jsonObject.get(JSON_KEY_CHORD);
            this.mode = (int)jsonObject.get(JSON_KEY_MODE);
            this.position = (int)jsonObject.get(JSON_KEY_POSITION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note%scale.size();
        if(this.note<0){
            this.note += scale.size();
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String toString(){
        return ""+invertedScale[note]+modes[mode];
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject r=new JSONObject();
        r.put(JSON_KEY_CHORD, note);
        r.put(JSON_KEY_MODE, mode);
        r.put(JSON_KEY_POSITION, position);
        return r;
    }

    public Chord copy(){
        int newChord = note;
        int newMode = mode;
        int newPosition = position;
        return new Chord(newChord, newMode, newPosition);
    }
}
