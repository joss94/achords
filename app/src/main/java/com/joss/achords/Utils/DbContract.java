package com.joss.achords.Utils;

/**
 * Created by Joss on 21/12/2016.
 */

public final class DbContract {
    private DbContract(){}
    public static class DbSongs {
        public static final String TABLE_NAME = "Songs";
        public static final String COLUMN_NAME_UUID="UUID";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_ARTIST = "Artist";
        public static final String COLUMN_NAME_RELEASE_YEAR = "Released_year";
        public static final String COLUMN_NAME_EDITOR = "Editor";
        public static final String COLUMN_NAME_LAST_EDITION_DATE  ="Last_Edition_Date";
        public static final String COLUMN_NAME_LYRICS = "lyrics";
    }

}
