package com.joss.achords.SongEnvironment.SongEdit;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.joss.achords.AbstractParentActivity;
import com.joss.achords.SongbookHome.SongbookActivity;
import com.joss.achords.OnDialogFragmentInteractionListener;
import com.joss.achords.Models.Lyrics;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;


public class EditionFragment extends Fragment implements Songbook.OnSongbookChangeListener, OnDialogFragmentInteractionListener {
    Song mSong;
    Song mEditedSong;
    EditText mEditionName;
    AutoCompleteTextView mEditionArtist;
    TextView mEditionReleaseYear;
    EditText mEditionLyrics;
    ScrollView mScrollView;
    UUID song_id;

    ArrayList<OnSongChangedListener> listeners= new ArrayList<>();

    boolean creatingLyrics;

    public EditionFragment() {
    }

    public static EditionFragment newInstance(UUID song_id) {
        EditionFragment fragment = new EditionFragment();
        Bundle args = new Bundle();
        args.putSerializable(SongbookActivity.EXTRA_SONG_ID, song_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditedSong=new Song();
        song_id=(UUID)getArguments().getSerializable(SongbookActivity.EXTRA_SONG_ID);
        if(song_id!=null){
            mSong = Songbook.get(getActivity()).getById(song_id);
        }
        else{
            mSong=new Song();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_edition, container, false);

        Songbook.get(getActivity()).setOnSongbookChangeListener(this);

        //<editor-fold desc="FINDING AND SETTING VIEWS">
        mScrollView = (ScrollView)v.findViewById(R.id.scroll_view);
        mEditionName = (EditText) v.findViewById(R.id.edition_name);
        mEditionArtist = (AutoCompleteTextView) v.findViewById(R.id.edition_artist);
        mEditionLyrics = (EditText) v.findViewById(R.id.edition_lyrics);
        mEditionReleaseYear = (TextView) v.findViewById(R.id.edition_release_year);
        mEditionReleaseYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });
        mEditionName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mEditionArtist.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mEditionLyrics.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        if(mSong!=null) {
            if( mSong.getName()!=null && !mSong.getName().isEmpty()){
                mEditionName.setText(mSong.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, Songbook.get(getContext()).getArtists());
            mEditionArtist.setAdapter(adapter);
            if(mSong.getArtist()!=null && !mSong.getArtist().isEmpty()){
                mEditionArtist.setText(mSong.getArtist());
            }
            if(mSong.getReleaseYear()==0){
                mEditionReleaseYear.setText("Choose a date");
                mEditionReleaseYear.setTextColor(getResources().getColor(R.color.text_hint_color));
            }
            else{
                mEditionReleaseYear.setText(String.valueOf(mSong.getReleaseYear()));
                mEditionReleaseYear.setTextColor(getResources().getColor(R.color.text_color));
            }

            mEditionLyrics.setText(mSong.printLyrics());
            creatingLyrics=mEditionLyrics.getText().toString().isEmpty();
        }
        //</editor-fold>

        mEditedSong = mSong.copy();

        //Set TextWatcher to handle lyrics changes
        mEditionLyrics.addTextChangedListener(new TextWatcher() {

            CharSequence replaced;
            CharSequence replacing;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                replaced = s.subSequence(start, start+count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                replacing = s.subSequence(start, start+count);
                handleLyricsModification(s, start, replaced.toString().toCharArray(), replacing.toString().toCharArray());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditionLyrics.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mScrollView.smoothScrollTo(0, mScrollView.findViewById(R.id.lyrics_descriptor).getTop());
                }
            }
        });

        //Set OK Button
        ImageButton ok_button=(ImageButton)v.findViewById(R.id.edition_ok_button);
        ((GradientDrawable)ok_button.getBackground()).setColor(getResources().getColor(R.color.Green));
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean saved = saveModifications();
                ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mEditionLyrics.getWindowToken(), 0);
                if(saved){
                    for(OnSongChangedListener listener : listeners){
                        listener.onSongChanged(mEditedSong.getId());
                    }
                }
            }
        });

        //Set URL Button
        ImageButton url_button=(ImageButton)v.findViewById(R.id.edition_url_button);
        ((GradientDrawable)url_button.getBackground()).setColor(getResources().getColor(R.color.DarkBlue));
        url_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askURL();
            }
        });

        return v;
    }

    public void askURL(){
        URLDialogFragment d = URLDialogFragment.newInstance();
        d.setOnFragmentInteractionListener(this);
        d.show(getFragmentManager(), "URL");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onStart(){
        super.onStart();
        mEditedSong=mSong.copy();
    }

    public boolean saveModifications() {
        mEditedSong.setName(mEditionName.getText().toString());
        mEditedSong.setArtist(mEditionArtist.getText().toString());
        mEditedSong.setEditor(getActivity().getSharedPreferences(AbstractParentActivity.SHARED_PREFS, Context.MODE_PRIVATE).getString(AbstractParentActivity.USER_NAME, ""));
        if(mSong.getLyrics().isEmpty()){
            mEditedSong.setLyrics(new Lyrics(mEditionLyrics.getText().toString()));
        }
        mEditedSong.setLastEditionDate(Calendar.getInstance().getTime());

        if(mEditedSong.getName().isEmpty()){
            Toast.makeText(getActivity(), "Please enter a name for the new Song", Toast.LENGTH_LONG).show();
            return false;
        }

        if(song_id!=null){
            Songbook.get(getActivity()).updateSong(mEditedSong);
            return true;
        }
        else {
            song_id=mEditedSong.getId();
            Songbook.get(getActivity()).addSong(mEditedSong);
            return true;

        }
    }

    public void chooseDate(){
        YearDialogFragment d=YearDialogFragment.newInstance((mEditedSong.getReleaseYear()==0)?
                Calendar.getInstance().get(Calendar.YEAR):mEditedSong.getReleaseYear());
        d.setOnFragmentInteractionListener(this);
        d.show(getFragmentManager(), "DATE");
    }

    public void handleLyricsModification(CharSequence s, int start, char[] replaced, char[]replacing) {

        //<editor-fold desc="CHECK FOR EXISTING LYRICS">
        if (creatingLyrics) {
            String newLyrics = mEditionLyrics.getText().toString();
            if(newLyrics.endsWith("\n")){
                newLyrics = newLyrics.substring(0,newLyrics.length()-1);
            }
            mEditedSong.setLyrics(new Lyrics(mEditionLyrics.getText().toString()));
            return;
        }
        //</editor-fold>

        int lineNumber = getLineNumber(start);
        int charPos = getCharPosInLine(start);

        for(char replacedChar:replaced) {
            mEditedSong.getLyrics().deleteChar(lineNumber, charPos);
        }

        for(char addedChar:replacing){
            mEditedSong.getLyrics().addChar(addedChar, lineNumber, charPos);
            start++;
            lineNumber = getLineNumber(start);
            charPos = getCharPosInLine(start);
        }
    }

    public int getLineNumber(int start){
        int lineNumber=0;
        for (char c : mEditedSong.printLyrics().substring(0, start).toCharArray()) {
            if (c == '\n') {
                lineNumber++;
            }
        }
        return lineNumber;
    }

    public int getCharPosInLine(int start){
        int charPos=0;
        for (char c : mEditedSong.printLyrics().substring(0, start).toCharArray()) {
            charPos++;
            if (c == '\n') {
                charPos=0;
            }
        }
        return charPos;
    }

    public void refresh(){
        mEditedSong=new Song();
        if(song_id!=null){
            mSong = Songbook.get(getActivity()).getById(song_id);
        }
        else{
            mSong=new Song();
        }
        mEditedSong = mSong.copy();
    }

    @Override
    public void onDBChange() {
        refresh();
    }

    public void addOnSongChangedListener(OnSongChangedListener listener){
        listeners.add(listener);
    }

    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Object... args) {
        Log.d("EDITION", "OnFragmentInteraction called");
        switch(requestCode){
            case YearDialogFragment.RELEASE_YEAR_REQUEST_CODE:
                if(resultCode== AppCompatActivity.RESULT_OK){
                    int year = (int)args[0];
                    mEditionReleaseYear.setText(String.valueOf(year));
                    mEditionReleaseYear.setTextColor(getContext().getResources().getColor(android.R.color.black));
                    mEditedSong.setReleaseYear(year);
                }
                break;

            case URLDialogFragment.URL_REQUEST_CODE:
                if(resultCode== AppCompatActivity.RESULT_OK){
                    String[] params = new String [] {(String)args[0]};
                    new LyricsLoader().execute((String)args[0]);
                }
                break;
        }
    }

    public interface OnSongChangedListener{
        void onSongChanged(UUID id);
    }

    public class LyricsLoader extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... arg) {
            URL url = null;
            Log.d("Edition", "URL to connect: "+arg[0]);
            String line;
            String r="";
            try {
                url = new URL(arg[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    r += line;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return r;
        }

        @Override
        protected void onPostExecute(String html){
            Log.d("Edition", "html from url: "+html);
            mEditionLyrics.setText(html);
        }
    }
}
