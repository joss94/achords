package com.joss.achords.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.joss.achords.AchordsActivity;
import com.joss.achords.Models.Lyrics;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBHelper {

    private static DBHelper helper;
    private static SQLiteDatabase db;
    private OnDBChangeListener listener;

    private List<Song> allSongs;

    public static DBHelper getInstance(Context context) {
        helper = helper == null ? new DBHelper(context) : helper;
        return helper;
    }

    private DBHelper(Context context) {
        db = (new DBOpenHelper(context)).getWritableDatabase();
    }

    public void updateSong(Song song){
        ContentValues row = new ContentValues();
        row.put(DbContract.DbSongs.COLUMN_NAME_NAME, song.getName());
        row.put(DbContract.DbSongs.COLUMN_NAME_ARTIST, song.getArtist());
        if (song.getEditor() != null) {
            row.put(DbContract.DbSongs.COLUMN_NAME_EDITOR, song.getEditor().toJSON().toString());
        }
        row.put(DbContract.DbSongs.COLUMN_NAME_RELEASE_YEAR, song.getReleaseYear());
        row.put(DbContract.DbSongs.COLUMN_NAME_LAST_EDITION_DATE, AchordsActivity.formatDate(song.getLastEditionDate()));
        row.put(DbContract.DbSongs.COLUMN_NAME_CAPO, song.getCapo());
        try {
            row.put(DbContract.DbSongs.COLUMN_NAME_LYRICS, song.getLyrics().ToJSON().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String selection = DbContract.DbSongs.COLUMN_NAME_UUID + " LIKE ?";
        String[] args = {song.getId().toString()};
        db.update(DbContract.DbSongs.TABLE_NAME, row, selection, args);
        onDbChange();
    }


    public void addSong(Song song){
        ContentValues row = new ContentValues();
        row.put(DbContract.DbSongs.COLUMN_NAME_UUID, song.getId().toString());
        row.put(DbContract.DbSongs.COLUMN_NAME_NAME, song.getName());
        row.put(DbContract.DbSongs.COLUMN_NAME_ARTIST, song.getArtist());
        row.put(DbContract.DbSongs.COLUMN_NAME_EDITOR, song.getEditor().toJSON().toString());
        row.put(DbContract.DbSongs.COLUMN_NAME_RELEASE_YEAR, song.getReleaseYear());
        row.put(DbContract.DbSongs.COLUMN_NAME_LAST_EDITION_DATE, AchordsActivity.formatDate(song.getLastEditionDate()));
        row.put(DbContract.DbSongs.COLUMN_NAME_CAPO, song.getCapo());
        try {
            row.put(DbContract.DbSongs.COLUMN_NAME_LYRICS, song.getLyrics().ToJSON().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        db.insert(DbContract.DbSongs.TABLE_NAME, null, row);
        onDbChange();
    }


    public void deleteSong(UUID id){
        String selection = DbContract.DbSongs.COLUMN_NAME_UUID + " LIKE ?";
        String[] args = {id.toString()};
        db.delete(DbContract.DbSongs.TABLE_NAME, selection, args);
        onDbChange();
    }

    private List<Song> getAll()  {
        List<Song> songs=new ArrayList<>();
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
            song.setEditor(User.parseJson(r.getString(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_EDITOR))));
            song.setReleaseYear(r.getInt(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_RELEASE_YEAR)));
            song.setCapo(r.getInt(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_CAPO)));
            try {
                song.setLastEditionDate(AchordsActivity.parseDate(r.getString(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_LAST_EDITION_DATE))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                song.setLyrics(new Lyrics(new JSONObject(r.getString(r.getColumnIndex(DbContract.DbSongs.COLUMN_NAME_LYRICS)))));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            songs.add(song);
        }
        r.close();
        return songs;
    }


    public List<Song> getAllSongs()  {
         if(allSongs == null) {
             allSongs = getAll();
         }
         return allSongs;
    }

    private void onDbChange(){
        allSongs = getAll();
        if(listener != null){
            listener.onDBChange();
        }
    }

    public void setListener(OnDBChangeListener listener) {
        this.listener = listener;
    }

    public interface OnDBChangeListener{
        void onDBChange();
    }
}
