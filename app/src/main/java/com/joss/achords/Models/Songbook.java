package com.joss.achords.Models;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.joss.achords.AchordsActivity;
import com.joss.achords.Database.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Songbook {

    public static final String EXPORT_FORMAT_JSON = "json";
    public static final String IMPORT_OPTION_ADD = "Add";
    private static final String IMPORT_OPTION_REPLACE = "Replace";

    private static final String SONGBOOK_JSON_KEY = "songbook";
    private static final String LISTS_JSON_KEY = "lists";

    public static final String EXPORT_FORMAT_JSON_TOKEN="IE876RTEZ87YGDSD67";
    private static final String SONGLISTS_SHARED_PREFS_KEY = "shared_prefs_songlists";

    private static Songbook songbook;
    private DBHelper dbHelper;
    private List<Songlist> lists;
    private SharedPreferences sharedPrefs;

    private List<DBHelper.OnDBChangeListener> listeners = new ArrayList<>();

    private Songbook(Context context)  {
        dbHelper = DBHelper.getInstance(context);
        lists = new ArrayList<>();
        sharedPrefs = context.getSharedPreferences(AchordsActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        getSonglistsFromSharedPrefs();
    }

    public static Songbook get(Context context) {
        songbook = songbook == null ? new Songbook(context.getApplicationContext()) : songbook;
        return songbook;
    }

    public void addOnSongbookChangeListener(DBHelper.OnDBChangeListener listener) {
        listeners.add(listener);
    }

    public void updateSong(Song song) {
        dbHelper.updateSong(song);
        for(DBHelper.OnDBChangeListener listener : listeners) {
            listener.onDBChange();
        }
    }

    public void addSong(Song song){
        dbHelper.addSong(song);
        for(DBHelper.OnDBChangeListener listener : listeners) {
            listener.onDBChange();
        }
    }

    public void deleteSong(UUID id){
        dbHelper.deleteSong(id);
        for(DBHelper.OnDBChangeListener listener : listeners) {
            listener.onDBChange();
        }
    }

    public void deleteList(Songlist songlist){
        lists.remove(songlist);
        saveSonglists();
    }

    private List<String> getNames(){
        ArrayList<String> names = new ArrayList<>();
        for (Song song : dbHelper.getAllSongs()){
            names.add(song.getName());
        }
        return names;
    }

    public List<String> getArtists(){
        ArrayList<String> r = new ArrayList<>();
        for(Song song : dbHelper.getAllSongs()){
            String artist = song.getArtist();
            if(!r.contains(artist)){
                r.add(artist);
            }
        }
        return r;
    }

    public List<Songlist> getLists() {
        return lists;
    }

    public List<Song> getSongsOfArtist(String artist){
        ArrayList<Song> r = new ArrayList<>();
        for(Song song : dbHelper.getAllSongs()){
            if(song.getArtist().equals(artist)){
                r.add(song);
            }
        }
        return r;
    }

    public List<Song> getSongsOfList(Songlist list){
        ArrayList<Song> r = new ArrayList<>();
        if (list != null) {
            for(UUID id : list.getSongsIds()){
                if(getById(id)!=null){
                    r.add(getById(id));
                }
            }
        }
        return r;
    }

    public Song getById (UUID id){
        for(Song song:dbHelper.getAllSongs()){
            if (song.getId().equals(id)) {return song;}
        }
        if(id!=null){
            Log.d("SONGBOOK", "A research by id wasnt sucessful... id: "+id.toString());
            Log.d("SONGBOOK", "Songs in songbook: "+ songbook.getNames().toString());
            for(Song song:dbHelper.getAllSongs()){
                Log.d("SONGBOOK", "ID of "+song.getName()+" : "+song.getId().toString());
            }
        }
        else{
            Log.d("SONGBOOK", "A research by id wasnt sucessful... id null");
        }

        return new Song();
    }

    public List<Song> getSongs() {
        return dbHelper.getAllSongs();
    }

    public Object exportSongbook(String format) throws JSONException {
        Object export = null;
        switch(format){
            case EXPORT_FORMAT_JSON:
                JSONObject songbook_obj = new JSONObject();
                JSONArray songbook_array = new JSONArray();
                for(Song song:dbHelper.getAllSongs()){
                    songbook_array.put(song.toJSON());
                }
                songbook_obj.put(SONGBOOK_JSON_KEY, songbook_array);
                export = songbook_obj;
                break;

            default:
                break;
        }
        return export;
    }

    public boolean loadSongbook(File file, String option){
        String json_string="";
        if(checkJSONFile(file)){
            json_string= readJSONFile(file);
        }

        try {
            JSONObject jsonSongbook = new JSONObject(json_string);
            JSONArray jsonSongsArray = jsonSongbook.getJSONArray(SONGBOOK_JSON_KEY);
            switch(option){
                case IMPORT_OPTION_ADD:
                    for(int i =0; i<jsonSongsArray.length(); i++){
                        Song jsonSong = new Song(jsonSongsArray.getJSONObject(i));
                        addSong(jsonSong);
                    }
                    break;
                case IMPORT_OPTION_REPLACE:
                    break;
                default:
                    return false;
            }

        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    private String readJSONFile(File file)  {
        String r="";
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            br.readLine();
            while((line = br.readLine()) != null){
                r+=line;
                r+='\n';
            }
            br.close();
        }
        catch (IOException ignored){}
        return r;
    }

    private boolean checkJSONFile(File file)  {
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            return(br.readLine().equals(EXPORT_FORMAT_JSON_TOKEN));
        }
        catch (IOException ignored){}
        return false;
    }

    private void getSonglistsFromSharedPrefs(){
        try {
            JSONObject songlistsJson = new JSONObject(sharedPrefs.getString(SONGLISTS_SHARED_PREFS_KEY, ""));
            JSONArray array = songlistsJson.getJSONArray(LISTS_JSON_KEY);
            for(int i=0; i<array.length(); i++){
                lists.add(new Songlist(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Songlist getSonglistFromName(String name){
        for (Songlist existingList : songbook.getLists()) {
            if (existingList.getName().equals(name)) {
                return existingList;
            }
        }
        return null;
    }

    public void saveSonglists(){
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        for (Songlist list : lists){
            array.put(list.toJson());
        }
        try {
            obj.put(LISTS_JSON_KEY, array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sharedPrefs.edit().putString(SONGLISTS_SHARED_PREFS_KEY, obj.toString()).apply();
        for(DBHelper.OnDBChangeListener listener : listeners) {
            listener.onDBChange();
        }
    }

}

