package com.joss.achords.SongbookHome;

/*
 * Created by joss on 28/02/17.
 */

public interface SongbookFragment {
    void filter(String s);
    void deleteSelected();
    void refresh();
}
