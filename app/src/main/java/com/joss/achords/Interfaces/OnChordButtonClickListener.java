package com.joss.achords.Interfaces;

import android.view.View;

import com.joss.achords.Models.Chord;

/**
 * Created by Joss on 02/02/2017.
 */

public interface OnChordButtonClickListener {
    void onChordButtonClicked(Chord chord);
    void onChordButtonLongClicked(View v, Chord chord);
}
