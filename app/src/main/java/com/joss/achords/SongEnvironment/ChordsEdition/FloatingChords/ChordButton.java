package com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.joss.achords.Models.Chord;
import com.joss.achords.R;

public class ChordButton extends View {

    Paint mTextPaint;
    Paint mButtonPaint;

    Chord chord;
    int buttonColor, textColor, textSize;

    public ChordButton(Context context) {
        super(context);
        buttonColor = context.getResources().getColor(R.color.LightGrey);
        textColor = context.getResources().getColor(R.color.DarkBlue);
        createPaints();
    }

    public ChordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChordButton, 0, 0);
        try {
            buttonColor = a.getColor(R.styleable.ChordButton_color, context.getResources().getColor(R.color.LightGrey));
            textColor = a.getColor(R.styleable.ChordButton_textColor, context.getResources().getColor(R.color.DarkBlue));
            textSize = a.getDimensionPixelSize(R.styleable.ChordButton_textSize, context.getResources().getDimensionPixelSize(R.dimen.text_normal));
            int note = a.getInt(R.styleable.ChordButton_note, -1);
            int mode = a.getInt(R.styleable.ChordButton_mode, 0);
            int attribute = a.getInt(R.styleable.ChordButton_attribute, 0);
            chord = new Chord(note, mode, 0, attribute);
        } finally {
            a.recycle();
            createPaints();
        }
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
        mTextPaint.setTextSize(textSize);

        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setColor(buttonColor);
        mButtonPaint.setFakeBoldText(true);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        createPaints();
        invalidate();
    }

    public void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
        createPaints();
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
