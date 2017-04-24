package com.joss.achords.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Created by Joss on 21/12/2016.
 */

public class Lyrics extends ArrayList<LyricsLine>{

    private static final String JSON_KEY_LYRICS = "json_lyrics";

    Lyrics (){}

    public Lyrics(String text){
        ArrayList<String> lyrics = new ArrayList<>(Arrays.asList(text.split("\n")));
        for(String line:lyrics){
            this.add(new LyricsLine(line));
        }
    }

    public Lyrics(JSONObject jsonObject)  {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(JSON_KEY_LYRICS);
            for(int i=0;i<jsonArray.length();i++){
                LyricsLine lyricsLine = new LyricsLine(jsonArray.getJSONObject(i));
                this.add(lyricsLine);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toString(){
        String r = "";
        for (LyricsLine lyricsLine:this){
            String s = lyricsLine.getText();
            r=r+s+"\n";
        }
        if(!this.isEmpty()){
            r = r.substring(0,r.length()-1);}
        return r;
    }

    public JSONObject ToJSON() throws JSONException {
        JSONObject r = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(LyricsLine lyricsLine:this){
            jsonArray.put(lyricsLine.toJSON());
        }
        r.put(JSON_KEY_LYRICS, jsonArray);
        return r;
    }

    public Lyrics copy(){
        Lyrics copy = new Lyrics();
        for (LyricsLine lyricsLine:this){
            copy.add(lyricsLine.copy());
        }
        return copy;
    }

    public void deleteChar(int lineNumber, int charPosInLine){
        LyricsLine line = this.get(lineNumber);

        //<editor-fold desc="REMOVE CHORD IF ATTACHED TO DELETED CHAR">
        for(int i=0; i<line.getChords().size(); i++){
            if(line.getChords().get(i).getPosition()==charPosInLine){
                line.getChords().remove(i);
            }
        }
        //</editor-fold>

        //If deleting '\n'
        if(charPosInLine>=line.getText().length()){
            //if not last line
            if (lineNumber<this.size()-1) {
                //<editor-fold desc="MERGE THE TWO LINES">
                LyricsLine nextLine = this.get(lineNumber+1);

                //<editor-fold desc="MERGE TEXTS">
                String firstPart = line.getText();
                line.setText(line.getText()+nextLine.getText());
                //</editor-fold>

                //<editor-fold desc="MERGE CHORDS">
                for (Chord chord:nextLine.getChords()){
                    Chord newChord = chord.copy();
                    boolean checkOtherChords=true;
                    for(Chord existingChord : line.getChords()){
                        if(Math.abs(chord.getPosition()-existingChord.getPosition())<=Chord.CHORD_MARGIN){
                            checkOtherChords=false;
                            break;
                        }
                    }
                    if(checkOtherChords){
                        newChord.setPosition(newChord.getPosition() + firstPart.length());
                    }
                    line.addChord(newChord);
                }
                //</editor-fold>

                line.setDuration(line.getDuration()+nextLine.getDuration());

                this.remove(nextLine);
                //</editor-fold>
            }
        }

        //If deleting any other char
        else{
            //<editor-fold desc="MERGE TEXTS">
            line.setText(line.getText().substring(0,charPosInLine) + line.getText().substring(charPosInLine+1));
            //</editor-fold>

            //<editor-fold desc="MERGE CHORDS">
            for(Chord chord:line.getChords()){
                if(chord.getPosition()>=charPosInLine){
                    boolean checkOtherChords=true;
                    for(Chord existingChord : line.getChords()){
                        if(Math.abs(chord.getPosition()-existingChord.getPosition())<=Chord.CHORD_MARGIN){
                            checkOtherChords=false;
                            break;
                        }
                    }
                    if(checkOtherChords){
                        chord.setPosition(chord.getPosition()-1);
                    }
                }
            }
            //</editor-fold>
        }
    }

    public void addChar(char addedChar, int lineNumber, int charPosInLine){
        LyricsLine line = this.get(lineNumber);

        if(addedChar=='\n'){
            //<editor-fold desc="ADD NEW LINE">
            LyricsLine newLine = new LyricsLine();
            LyricsLine newNextLine = new LyricsLine();
            newLine.setText(line.getText().substring(0,charPosInLine));
            newNextLine.setText(line.getText().substring(charPosInLine));

            for(Chord chord:line.getChords()){
                Chord newChord = chord.copy();
                if(chord.getPosition()>=charPosInLine){
                    newChord.setPosition(newChord.getPosition()-charPosInLine);
                    newNextLine.addChord(newChord);
                }
                else{
                    newLine.addChord(chord);
                }
            }
            this.remove(lineNumber);
            this.add(lineNumber, newLine);
            this.add(lineNumber+1, newNextLine);
            newNextLine.setDuration(0);
            //</editor-fold>
        }
        else{
            //<editor-fold desc="ADD OTHER CHAR">
            line.setText(line.getText().substring(0,charPosInLine)+addedChar+line.getText().substring(charPosInLine));

            //<editor-fold desc="RESET CHORDS">
            for(Chord chord:line.getChords()){
                if(chord.getPosition()>=charPosInLine){
                    chord.setPosition(chord.getPosition()+1);
                }
            }
            //</editor-fold>
            //</editor-fold>
        }
    }

}
