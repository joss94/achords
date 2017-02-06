package com.joss.achords.Models;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.joss.achords.Utils.DbContract;
import com.joss.achords.Utils.SongsDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

public class Songbook {
    public static final String EXPORT_FORMAT_JSON = "json";
    public static final String EXPORT_FORMAT_TEXT = "text";
    public static final String IMPORT_OPTION_ADD = "Add";
    public static final String IMPORT_OPTION_REPLACE = "Replace";

    public static final String EXPORT_FORMAT_JSON_TOKEN="IE876RTEZ87YGDSD67";

    public interface OnSongbookChangeListener{
        public void onDBChange();
    }

    private static Songbook songbook;
    private Context mContext;
    private ArrayList<Song> mSongs;
    private SQLiteDatabase db;
    private SongsDbHelper dbHelper;

    private ArrayList<OnSongbookChangeListener> listeners = new ArrayList<>();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    private Songbook(Context context)  {
        this.mContext = context;
        dbHelper = new SongsDbHelper(mContext);
        loadDB();
        readDB();
    }

    public static Songbook get(Context context) {
        if (songbook ==null){
            songbook = new Songbook(context.getApplicationContext());
        }
        return songbook;
    }

    public void setOnSongbookChangeListener(OnSongbookChangeListener listener) {
        listeners.add(listener);
    }

    public void updateSong(Song song) {
        ContentValues row = new ContentValues();
        row.put(DbContract.DbSongs.COLUMN_NAME_NAME, song.getName());
        row.put(DbContract.DbSongs.COLUMN_NAME_ARTIST, song.getArtist());
        row.put(DbContract.DbSongs.COLUMN_NAME_EDITOR, song.getEditor());
        row.put(DbContract.DbSongs.COLUMN_NAME_RELEASE_YEAR, song.getReleaseYear());
        row.put(DbContract.DbSongs.COLUMN_NAME_LAST_EDITION_DATE, sdf.format(song.getLastEditionDate()));
        try {
            row.put(DbContract.DbSongs.COLUMN_NAME_LYRICS, song.getLyrics().ToJSON().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String selection = DbContract.DbSongs.COLUMN_NAME_UUID + " LIKE ?";
        String[] args = {song.getId().toString()};
        db.update(DbContract.DbSongs.TABLE_NAME, row, selection, args);
        readDB();
        for(OnSongbookChangeListener listener : listeners) {
            listener.onDBChange();
        }
    }

    public void addSong(Song song){
        ContentValues row = new ContentValues();
        row.put(DbContract.DbSongs.COLUMN_NAME_UUID, song.getId().toString());
        row.put(DbContract.DbSongs.COLUMN_NAME_NAME, song.getName());
        row.put(DbContract.DbSongs.COLUMN_NAME_ARTIST, song.getArtist());
        row.put(DbContract.DbSongs.COLUMN_NAME_EDITOR, song.getEditor());
        row.put(DbContract.DbSongs.COLUMN_NAME_RELEASE_YEAR, song.getReleaseYear());
        row.put(DbContract.DbSongs.COLUMN_NAME_LAST_EDITION_DATE, sdf.format(song.getLastEditionDate()));
        try {
            row.put(DbContract.DbSongs.COLUMN_NAME_LYRICS, song.getLyrics().ToJSON().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        db.insert(DbContract.DbSongs.TABLE_NAME, null, row);
        readDB();
        for(OnSongbookChangeListener listener : listeners) {
            listener.onDBChange();
        }
    }

    public void deleteSong(UUID id){
        String selection = DbContract.DbSongs.COLUMN_NAME_UUID + " LIKE ?";
        String[] args = {id.toString()};
        db.delete(DbContract.DbSongs.TABLE_NAME, selection, args);
        readDB();
        for(OnSongbookChangeListener listener : listeners) {
            listener.onDBChange();
        }
    }

    public ArrayList<String> getNames (){
        ArrayList<String> names = new ArrayList<String>();
        for (Song song : mSongs){
            names.add(song.getName());
        }
        return names;
    }

    public ArrayList<String> getArtists(){
        ArrayList<String> r = new ArrayList<>();
        for(Song song : mSongs){
            String artist = song.getArtist();
            if(!r.contains(artist)){
                r.add(artist);
            }
        }
        Log.d("SONGBOOK", "List of artists: "+r.toString());
        return r;
    }

    public Song getById (UUID id){
        for(Song song:mSongs){
            if (song.getId().equals(id)) {return song;}
        }
        if(id!=null){
            Log.d("SONGBOOK", "A research by id wasnt sucessful... id: "+id.toString());
            Log.d("SONGBOOK", "Songs in songbook: "+ songbook.getNames().toString());
            for(Song song:mSongs){
                Log.d("SONGBOOK", "ID of "+song.getName()+" : "+song.getId().toString());
            }
        }
        else{
            Log.d("SONGBOOK", "A research by id wasnt sucessful... id null");
        }

        return new Song();
    }

    public ArrayList<Song> getSongs() {
        return mSongs;
    }

    public void loadDB(){
        db = dbHelper.getWritableDatabase();
    }

    public ArrayList<Song> readDB()  {
        mSongs=new ArrayList<>();
        Cursor r = db.query (DbContract.DbSongs.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DbContract.DbSongs.COLUMN_NAME_NAME);
        while(r.moveToNext()){
            Song song = new Song();
            song.setId(UUID.fromString(r.getString(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_UUID))));
            song.setName(r.getString(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_NAME)));
            song.setArtist(r.getString(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_ARTIST)));
            song.setEditor(r.getString(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_EDITOR)));
            song.setReleaseYear(r.getInt(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_RELEASE_YEAR)));
            try {
                song.setLastEditionDate(sdf.parse(r.getString(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_LAST_EDITION_DATE))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                song.setLyrics(new Lyrics(new JSONObject(r.getString(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_LYRICS)))));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSongs.add(song);
        }

        return mSongs;
    }

    public Object exportSongbook(String format) throws JSONException {
        Object export = null;
        switch(format){
            case EXPORT_FORMAT_JSON:
                JSONObject songbook_obj = new JSONObject();
                JSONArray songbook_array = new JSONArray();
                for(Song song:mSongs){
                    songbook_array.put(song.toJSON());
                }
                songbook_obj.put("songbook", songbook_array);
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
            JSONArray jsonSongsArray = jsonSongbook.getJSONArray("songbook");
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
            Toast.makeText(mContext, "Invalid format !!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public String readJSONFile(File file)  {
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
        catch (IOException e){

        }
        return r;
    }

    public boolean checkJSONFile(File file)  {
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            return(br.readLine().equals(EXPORT_FORMAT_JSON_TOKEN));
        }
        catch (IOException e){
        }
        return false;
    }

}

