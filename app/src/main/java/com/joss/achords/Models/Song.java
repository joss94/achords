package com.joss.achords.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Song implements Serializable{


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    //VALUES
    private UUID id;
    private String name;
    private String artist;
    private int releaseYear;
    private String editor;
    private Date lastEditionDate;
    private Lyrics lyrics;

    //CONSTRUCTOR


    public Song() {
        this.id = UUID.randomUUID();
        lastEditionDate= Calendar.getInstance().getTime();
        lyrics=new Lyrics();
    }

    public Song(JSONObject json){
        try {
            this.id = UUID.randomUUID();
            this.name=json.getString("name");
            this.artist=json.getString("artist");
            this.releaseYear=json.getInt("releaseYear");
            this.editor=json.getString("editor");
            this.lastEditionDate = sdf.parse(json.getString("lastEditionDate"));
            this.lyrics=new Lyrics(json.getJSONObject("lyrics"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //GETTERS
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getEditor() {
        return editor;
    }

    public Date getLastEditionDate() {
        return lastEditionDate;
    }

    public Lyrics getLyrics(){
        return lyrics;
    }

    public ArrayList<Chord> getChords() {
        ArrayList<Chord> chords = new ArrayList<>();
        for(LyricsLine line : lyrics){
            for(Chord chord : line.getChords()){
                chords.add(chord);
            }
        }
        return chords;
    }

    //SETTERS
    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public void setLastEditionDate(Date lastEditionDate) {
        this.lastEditionDate = lastEditionDate;
    }

    public void setLyrics(Lyrics lyrics){
        this.lyrics=lyrics;
    }


    //METHODS

    public boolean addChord (Chord chord, int line){
        boolean added = false;
        if(line>lyrics.size()){
            line = lyrics.size()-1;
        }

        lyrics.get(line).addChord(chord);
        return added;
    }

    public String printLyrics(){
        String r="";
        if (lyrics!=null){
            r=lyrics.toString();
        }
        return r;
    }

    public void resetChords(){
        //chords = new ArrayList<>();
    }

    public Song copy(){
        Song song = new Song();
        song.setId(this.id);
        song.setName(this.name);
        song.setArtist(this.artist);
        song.setReleaseYear(this.releaseYear);
        song.setEditor(this.editor);
        song.setLastEditionDate(this.lastEditionDate);
        song.setLyrics(this.lyrics.copy());
        return song;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject song_obj = new JSONObject();
        song_obj.put("name", this.name);
        song_obj.put("artist", this.artist);
        song_obj.put("releaseYear", this.releaseYear);
        song_obj.put("editor", this.editor);
        song_obj.put("lastEditionDate", sdf.format(this.lastEditionDate));
        song_obj.put("lyrics", this.lyrics.ToJSON());
        return song_obj;
    }
}
