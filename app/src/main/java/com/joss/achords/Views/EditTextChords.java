package com.joss.achords.Views;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.EditText;

import com.joss.achords.Interfaces.OnEditTextChordDoubleTapListener;

/**
 * Created by Joss on 23/12/2016.
 */

public class EditTextChords extends EditText {

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

    protected class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            doubleTapListener.onDoubleTap(lineNumber, getSelectionStart());
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}
