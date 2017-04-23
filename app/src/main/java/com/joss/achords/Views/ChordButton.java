package com.joss.achords.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.joss.achords.Models.Chord;
import com.joss.achords.R;

/**
 * Created by Joss on 02/02/2017.
 */

public class ChordButton extends View {

    Paint mTextPaint;
    Paint mButtonPaint;

    Chord chord;
    int buttonColor;
    int textColor;

    public ChordButton(Context context) {
        super(context);
        buttonColor = getResources().getColor(R.color.LightGrey);
        textColor = getResources().getColor(R.color.DarkBlue);
        createPaints();
    }

    public ChordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChordButton, 0, 0);
        int note = a.getInt(R.styleable.ChordButton_note, 0);
        int mode = a.getInt(R.styleable.ChordButton_mode, 1);
        chord = new Chord(note, mode, 0);
        buttonColor = a.getColor(R.styleable.ChordButton_color, getResources().getColor(R.color.LightGrey));
        textColor = a.getColor(R.styleable.ChordButton_textColor, getResources().getColor(R.color.DarkBlue));

        a.recycle();

        createPaints();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.drawOval(0, 0, getWidth(), getHeight(), mButtonPaint);
        canvas.drawText(chord.toString(), getWidth()/2, (int)(getHeight()/2 - (mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
    }

    private void createPaints(){
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(40.0f);

        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setColor(buttonColor);
        mButtonPaint.setFakeBoldText(true);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
        invalidate();
    }

    public void setChord(Chord chord) {
        this.chord = chord;
        invalidate();
    }

    public Chord getChord() {
        return chord;
    }
}
