package com.joss.achords.Interfaces;

import android.view.View;

/**
 * Created by joss on 08/02/17.
 */

public interface OnSongItemClickListener {
    void onSongClick(View v, int position);
    void onSongLongClick(View v, int position);
}
