package com.joss.achords.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "songbook_database";

    public DBOpenHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ DbContract.DbSongs.TABLE_NAME + " (" +
                    DbContract.DbSongs.COLUMN_NAME_UUID+" TEXT," +
                    DbContract.DbSongs.COLUMN_NAME_NAME+" TEXT," +
                    DbContract.DbSongs.COLUMN_NAME_ARTIST+" TEXT," +
                    DbContract.DbSongs.COLUMN_NAME_RELEASE_YEAR+" INTEGER," +
                    DbContract.DbSongs.COLUMN_NAME_EDITOR+" TEXT," +
                    DbContract.DbSongs.COLUMN_NAME_LAST_EDITION_DATE+" TEXT," +
                    DbContract.DbSongs.COLUMN_NAME_LYRICS+" TEXT," +
                    DbContract.DbSongs.COLUMN_NAME_CAPO+" INTEGER," +
                    "PRIMARY KEY (" + DbContract.DbSongs.COLUMN_NAME_UUID + ")" +
                    ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
