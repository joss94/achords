package com.joss.achords.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LyricsLine {

    private static final String JSON_KEY_CHORDS = "json_key_chords";
    private static final String JSON_KEY_LYRICSLINE_TEXT = "json_key_lyricsline text";
    private static final String JSON_KEY_TIME_IN_SONG="json_key_time_in_song";

    private String text;
    private List<Chord> chords;
    private int duration;

    LyricsLine() {
        text="";
        chords = new ArrayList<>();
        duration =0;
    }

    LyricsLine(String text){
        this.text=text;
        this.chords = new ArrayList<>();
        this.duration =0;
    }

    LyricsLine(JSONObject jsonObject) {
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

    public List<Chord> getChords() {
        return chords;
    }

    public void setChords(List<Chord> chords) {
        this.chords = chords;
    }

    void addChord(Chord chord) {
        if(chord.getPosition()>text.length()){
            chord.setPosition(text.length()-1);
        }
        chords.add(chord);
    }


    JSONObject toJSON() throws JSONException {
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


    LyricsLine copy(){
        LyricsLine copy = new LyricsLine();
        copy.setText(this.text);
        for(Chord chord:this.chords){
            copy.addChord(chord);
        }
        copy.setDuration(this.duration);
        return copy;
    }
}
