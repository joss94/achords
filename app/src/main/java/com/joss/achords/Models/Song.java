package com.joss.achords.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Song implements Serializable{

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);

    //VALUES
    private UUID id;
    private String name;
    private String artist;
    private int releaseYear;
    private String editor;
    private Date lastEditionDate;
    private Lyrics lyrics;

    private int capo;

    //CONSTRUCTOR
    public Song() {
        this.id = UUID.randomUUID();
        lastEditionDate= Calendar.getInstance().getTime();
        lyrics=new Lyrics();
        capo = 0;
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
            if (json.has("capo")) {
                this.capo = json.getInt("capo");
            }
        } catch (JSONException | ParseException e) {
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

    Date getLastEditionDate() {
        return lastEditionDate;
    }

    public Lyrics getLyrics(){
        return lyrics;
    }

    public int getCapo() {
        return capo;
    }

    public Chord getFirstChord(){
        for(LyricsLine line : lyrics){
            if(!line.getChords().isEmpty()){
                Chord r = line.getChords().get(0).copy();
                for(Chord chord : line.getChords()){
                    if(r.getPosition()>chord.getPosition()){
                        r=chord.copy();
                    }
                }
                return r;
            }
        }
        return null;
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

    public void setCapo(int capo) {
        this.capo = capo;
    }

    public void deleteChord(int line, int index){
        List<Chord> chords = new ArrayList<>();
        chords.addAll(lyrics.get(line).getChords());
        for(Chord chord : chords){
            if(Math.abs(chord.getPosition()-index)<=Chord.CHORD_MARGIN){
                lyrics.get(line).getChords().remove(chord);
            }
        }
    }

    public String addChord(Chord chord, int line, int index){
        for(Chord existingChord : lyrics.get(line).getChords()){
            if(Math.abs(existingChord.getPosition()-index)<=Chord.CHORD_MARGIN){
                return("There is already a chord here...");
            }
        }
        if(index == lyrics.get(line).getText().length()){
            lyrics.get(line).setText(lyrics.get(line).getText() + "    ");
        }

        chord.setPosition(index);
        lyrics.get(line).addChord(chord);
        return "success";
    }

    public String printLyrics(){
        String r="";
        if (lyrics!=null){
            r=lyrics.toString();
        }
        return r;
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
        song.setCapo(this.capo);
        return song;
    }

    JSONObject toJSON() throws JSONException {
        JSONObject song_obj = new JSONObject();
        song_obj.put("name", this.name);
        song_obj.put("artist", this.artist);
        song_obj.put("releaseYear", this.releaseYear);
        song_obj.put("editor", this.editor);
        song_obj.put("lastEditionDate", sdf.format(this.lastEditionDate));
        song_obj.put("lyrics", this.lyrics.ToJSON());
        song_obj.put("capo", this.capo);
        return song_obj;
    }
}
