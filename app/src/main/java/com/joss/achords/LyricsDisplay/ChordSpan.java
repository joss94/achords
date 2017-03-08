package com.joss.achords.LyricsDisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import com.joss.achords.Models.Chord;
import com.joss.achords.R;

/**
 * Created by Joss on 25/12/2016.
 */

public class ChordSpan extends ReplacementSpan {

    Chord mChord=new Chord();
    int space=0;
    Context ctx;

    public ChordSpan(Chord chord, Context context){
        mChord = chord;
        ctx = context.getApplicationContext();
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
        paint.setColor(ctx.getResources().getColor(R.color.colorPrimary));
        canvas.drawText(mChord.toString(), x, y+fm.ascent+fm.leading, paint);
    }
}
