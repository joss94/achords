package com.joss.achords.LyricsDisplay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import com.joss.achords.Models.Chord;

/**
 * Created by Joss on 25/12/2016.
 */

public class ChordSpan extends ReplacementSpan {

    Chord mChord=new Chord();
    int space=0;

    public ChordSpan(Chord chord){
        mChord = chord;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if ( fm != null ) {
            int space = paint.getFontMetricsInt(fm);

            fm.ascent -= space;
            fm.top -= space;
        }
        return (int) paint.measureText(text, start, end);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        canvas.drawText(text, start, end, x, y, paint);
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        space = fm.ascent+fm.leading;
        paint.setColor(Color.rgb(0,0,255));
        canvas.drawText(mChord.toString(), x, y+fm.ascent+fm.leading, paint);
    }
}
