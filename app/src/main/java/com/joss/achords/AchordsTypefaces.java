package com.joss.achords;

import android.graphics.Typeface;

/*
 * Created by joss on 23/02/17.
 */

public enum AchordsTypefaces {
    SONG_TITLE_FONT_LIGHT(null),
    SONG_TITLE_FONT(null);

    public Typeface typeface;

    AchordsTypefaces(Typeface t){
        this.typeface = t;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }
}
