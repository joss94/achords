package com.joss.achords.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Chord implements Serializable{

    private static final long serialVersionUID = -3431305021382424832L;

    static final int CHORD_MARGIN = 3;
    private static final String JSON_KEY_CHORD = "Chord";
    private static final String JSON_KEY_MODE = "Mode";
    private static final String JSON_KEY_POSITION = "Position";
    private static final String JSON_KEY_ATTRIBUTE = "Attribute";

    public static final String[] scale = {"A", "B", "C", "D", "E", "F", "G"};
    private static final String[] attributes = {"", "#", "b"};
    private static final String[] modes = {"","m", "7", "7M", "4", "6"};
    private static final int[] toneOffsets = {2, 1, 2, 2, 1, 2, 2};
    private int note;
    private int mode;
    private int position;
    private int attribute;

    public Chord() {
        note =0;
        mode=0;
        position=0;
        attribute=0;
    }

    public Chord(int note, int mode, int position, int attribute) {
        this.note = note;
        this.mode = mode;
        this.position = position;
        this.attribute = attribute;
    }

    public Chord(int note, int mode, int position) {
        this(note, mode, position, 0);
    }

    public Chord(int note, int position){
        this(note, 0, position);
    }

    public Chord (JSONObject jsonObject) {
        try {
            this.note = (int)jsonObject.get(JSON_KEY_CHORD);
            this.mode = (int)jsonObject.get(JSON_KEY_MODE);
            this.position = (int)jsonObject.get(JSON_KEY_POSITION);
            this.attribute = (int)jsonObject.get(JSON_KEY_ATTRIBUTE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note%scale.length;
        if(this.note<0){
            this.note += scale.length;
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

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public String toString(){
        return ""+ ((note == -1)?"":scale[note])+attributes[attribute]+modes[mode];
    }

    JSONObject toJSON() throws JSONException {
        JSONObject r=new JSONObject();
        r.put(JSON_KEY_CHORD, note);
        r.put(JSON_KEY_MODE, mode);
        r.put(JSON_KEY_POSITION, position);
        r.put(JSON_KEY_ATTRIBUTE, attribute);
        return r;
    }

    public Chord copy(){
        int newChord = note;
        int newMode = mode;
        int newPosition = position;
        int newAttribute = attribute;
        return new Chord(newChord, newMode, newPosition, newAttribute);
    }

    public boolean equals(Chord chord){
        boolean result = false;
        if(mode == chord.getMode() && note == chord.getNote() && attribute == chord.getAttribute()){
            result = true;
        }
        return result;
    }

    public Chord adjustTone(int toneOffset) {
        int initialAttribute = attribute;
        int i = 0;
        while(i<toneOffset){
            switch (attribute){
                case 0:
                    if(note != 1 && note != 4){
                        attribute = (initialAttribute == 0)?1:initialAttribute;
                        note = (attribute==2)?(note+1)%scale.length:note;
                    }
                    else{
                        note = (note+1)%scale.length;
                    }
                    break;

                case 1:
                    attribute = 0;
                    note = (note+1)%scale.length;
                    break;

                case 2:
                    attribute = 0;
            }
            i++;
        }
        return this;
    }

    public static int getToneOffset(Chord chord1, Chord chord2){
        int value1 = 0, value2 = 0;

        int i =0;
        while(i<chord1.getNote()){
            value1 += toneOffsets[i];
            i++;
        }
        if(chord1.getAttribute() == 1){
            value1 += 1;
        }
        else if(chord1.getAttribute() == 2){
            value1 -= 1;
        }


        int j =0;
        while(j<chord2.getNote()){
            value2 += toneOffsets[j];
            j++;
        }
        if(chord2.getAttribute() == 1){
            value2 += 1;
        }
        else if(chord2.getAttribute() == 2){
            value2 -= 1;
        }

        int offset = value2 - value1;
        if(offset<0){
            offset = (offset+12)%12;
        }
        return offset;
    }
}
