package com.joss.achords.SongEnvironment.ChordsEdition;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

/*
 * Created by Joss on 23/12/2016.
 */

public class EditTextChords extends android.support.v7.widget.AppCompatEditText {

    private GestureDetectorCompat mDetector;
    private OnEditTextChordDoubleTapListener doubleTapListener;

    private int lineNumber;

    public EditTextChords(Context context, int lineNumber) {
        super(context);
        mDetector = new GestureDetectorCompat(context, new CustomGestureListener());
        this.lineNumber = lineNumber;
    }

    public void setOnDoubleTapListener(OnEditTextChordDoubleTapListener doubleTapListener){
        this.doubleTapListener = doubleTapListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        super.onTouchEvent(e);
        mDetector.onTouchEvent(e);
        return true;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    private class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            doubleTapListener.onDoubleTap(lineNumber, getSelectionStart());
            return true;
        }
    }
}
