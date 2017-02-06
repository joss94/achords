package com.joss.achords.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joss on 24/12/2016.
 */

public class LyricsLine {

    public static final String JSON_KEY_CHORDS = "json_key_chords";
    public static final String JSON_KEY_LYRICSLINE_TEXT = "json_key_lyricsline text";
    public static final String JSON_KEY_TIME_IN_SONG="json_key_time_in_song";

    private String text;
    private ArrayList<Chord> chords;
    private int duration;

    public LyricsLine() {
        text="";
        chords = new ArrayList<>();
        duration =0;
    }

    public LyricsLine(String text){
        this.text=text;
        this.chords = new ArrayList<>();
        this.duration =0;
    }

    public LyricsLine(JSONObject jsonObject) {
        try {
            text=jsonObject.getString(JSON_KEY_LYRICSLINE_TEXT);
            JSONArray jsonArray = jsonObject.getJSONArray(JSON_KEY_CHORDS);
            chords=new ArrayList<>();
            for(int i=0;i<jsonArray.length();i++){
                chords.add(new Chord(jsonArray.getJSONObject(i)));
            }
            duration =jsonObject.getInt(JSON_KEY_TIME_IN_SONG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getText() {
        return text;
    }

    public int getDuration() {
        return duration;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ArrayList<Chord> getChords() {
        return chords;
    }

    public void addChord(Chord chord) {
        if(chord.getPosition()>text.length()){
            chord.setPosition(text.length()-1);
        }
        chords.add(chord);
    }


    public JSONObject toJSON() throws JSONException {
        JSONObject r = new JSONObject();

        r.put(JSON_KEY_LYRICSLINE_TEXT, text);
        JSONArray jsonArray = new JSONArray();
        for (Chord chord:chords){
            jsonArray.put(chord.toJSON());
        }
        r.put(JSON_KEY_CHORDS, jsonArray);
        r.put(JSON_KEY_TIME_IN_SONG, duration);
        return r;
    }


    public LyricsLine copy(){
        LyricsLine copy = new LyricsLine();
        copy.setText(this.text);
        for(Chord chord:this.chords){
            copy.addChord(chord);
        }
        copy.setDuration(this.duration);
        return copy;
    }
}
