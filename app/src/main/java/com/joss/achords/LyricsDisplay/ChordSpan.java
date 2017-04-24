package com.joss.achords.LyricsDisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.style.ReplacementSpan;

import com.joss.achords.Models.Chord;
import com.joss.achords.R;

public class ChordSpan extends ReplacementSpan {

    private Chord mChord=new Chord();
    private Context ctx;

    public ChordSpan(Chord chord, Context context){
        mChord = chord;
        ctx = context.getApplicationContext();
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if ( fm != null ) {
            int space = paint.getFontMetricsInt(fm);
            fm.ascent -= space;
            fm.top -= space;
        }
        return (int) paint.measureText(text, start, end);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        canvas.drawText(text, start, end, x, y, paint);
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        paint.setColor(ctx.getResources().getColor(R.color.colorPrimary));
        canvas.drawText(mChord.toString(), x, y+fm.ascent+fm.leading, paint);
    }
}
