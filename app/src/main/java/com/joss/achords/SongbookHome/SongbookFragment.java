package com.joss.achords.SongbookHome;

/*
 * Created by joss on 28/02/17.
 */

import android.support.v4.app.Fragment;

public abstract class SongbookFragment extends Fragment {

    abstract void filter(String s);
    abstract void deleteSelected();
    abstract void refresh();
}
