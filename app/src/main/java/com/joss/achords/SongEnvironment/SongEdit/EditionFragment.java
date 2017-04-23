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

import com.joss.achords.AchordsActivity;
import com.joss.achords.Models.Lyrics;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;
import com.joss.achords.SongbookHome.SongbookActivity;
import com.joss.utils.AbstractDialog.OnDialogFragmentInteractionListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;


public class EditionFragment extends Fragment implements Songbook.OnSongbookChangeListener, OnDialogFragmentInteractionListener, View.OnClickListener {
    private static final int URL_REQUEST_CODE = 1;
    private static final int RELEASE_YEAR_REQUEST_CODE = 2;
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

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_edition, container, false);

        Songbook.get(getActivity()).addOnSongbookChangeListener(this);

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
                mEditionReleaseYear.setText(R.string.release_date_hint);
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

        if (mSong != null) {
            mEditedSong = mSong.copy();
        }

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
                handleLyricsModification(start, replaced.toString().toCharArray(), replacing.toString().toCharArray());
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
        ok_button.setOnClickListener(this);

        //Set URL Button
        ImageButton url_button=(ImageButton)v.findViewById(R.id.edition_url_button);
        ((GradientDrawable)url_button.getBackground()).setColor(getResources().getColor(R.color.colorPrimary));
        url_button.setOnClickListener(this);

        return v;
    }

    public void askURL(){
        URLDialogFragment d = URLDialogFragment.newInstance();
        d.setOnFragmentInteractionListener(this);
        d.setRequestCode(URL_REQUEST_CODE);
        d.show(getFragmentManager(), "URL");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() instanceof OnSongChangedListener){
            addOnSongChangedListener((OnSongChangedListener) getActivity());
        }
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
        mEditedSong.setEditor(getActivity().getSharedPreferences(AchordsActivity.SHARED_PREFS, Context.MODE_PRIVATE).getString(AchordsActivity.USER_NAME, ""));
        try {
            mEditedSong.setReleaseYear(Integer.parseInt(mEditionReleaseYear.getText().toString()));
        } catch (NumberFormatException ignored) {
        }
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
        d.setRequestCode(RELEASE_YEAR_REQUEST_CODE);
        d.setOnFragmentInteractionListener(this);
        d.show(getFragmentManager(), "DATE");
    }

    public void handleLyricsModification(int start, char[] replaced, char[]replacing) {

        //<editor-fold desc="CHECK FOR EXISTING LYRICS">
        if (creatingLyrics) {
            String newLyrics = mEditionLyrics.getText().toString();
            while(newLyrics.endsWith("\n")){
                newLyrics = newLyrics.substring(0,newLyrics.length()-1);
            }
            mEditedSong.setLyrics(new Lyrics(newLyrics));
            return;
        }
        //</editor-fold>

        int lineNumber = getLineNumber(start);
        int charPos = getCharPosInLine(start);

        for (char ignored : replaced) {
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

    @SuppressWarnings("deprecation")
    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Object... args) {
        Log.d("EDITION", "OnFragmentInteraction called");
        switch(requestCode){
            case RELEASE_YEAR_REQUEST_CODE:
                if(resultCode== AppCompatActivity.RESULT_OK){
                    int year = (int)args[0];
                    mEditionReleaseYear.setText(String.valueOf(year));
                    mEditionReleaseYear.setTextColor(getContext().getResources().getColor(android.R.color.black));
                    mEditedSong.setReleaseYear(year);
                }
                break;

            case URL_REQUEST_CODE:
                if(resultCode== AppCompatActivity.RESULT_OK){
                    new LyricsLoader().execute((String)args[0]);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edition_ok_button:
                boolean saved = saveModifications();
                ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mEditionLyrics.getWindowToken(), 0);
                if(saved){
                    for(OnSongChangedListener listener : listeners){
                        listener.onSongChanged(mEditedSong.getId());
                    }
                }
                break;

            case R.id.edition_url_button:
                askURL();
                break;
        }
    }

    public interface OnSongChangedListener{
        void onSongChanged(UUID id);
    }

    public void loadLyricsFromHtml(Document doc){
        String website = doc.baseUri().substring(0, doc.baseUri().indexOf('/', 10));
        Toast.makeText(getContext(), "Lyrics imported from "+website, Toast.LENGTH_SHORT).show();

        switch(website){
            case "http://www.azlyrics.com/":
                break;

            case "https://play.google.com":
                parseGoogleLyrics(doc);
                break;

            case "https://www.musixmatch.com":
                parseMxmLyrics(doc);
                break;

            default:
                Toast.makeText(getContext(), "Impossible to import lyrics from this URL", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void parseMxmLyrics(Document doc){
        String lyrics="";
        Elements lyricsElms = doc.select("p.mxm-lyrics__content");
        for(Element element : lyricsElms){
            String line = ((TextNode)element.childNode(0)).getWholeText();
            lyrics+=line + '\n';
        }
        mEditionLyrics.setText(lyrics.substring(0, lyrics.length()-1));

        Element titleElement = doc.select("h1.mxm-track-title__track").first();
        mEditionName.setText(((TextNode)titleElement.childNode(1)).getWholeText());

        Element artistElement = doc.select("a.mxm-track-title__artist").first();
        mEditionArtist.setText(((TextNode)artistElement.childNode(0)).getWholeText());

        /*Element dateElement = doc.select("h3.mui-cell__subtitle").first();
        String yearText = ((TextNode)dateElement.childNode(0)).getWholeText();
        yearText = yearText.substring(yearText.length()-4);
        mEditionReleaseYear.setText(yearText);
        mEditionReleaseYear.setTextColor(getContext().getResources().getColor(android.R.color.black));*/
    }

    public void parseGoogleLyrics(Document doc){
        String lyrics="";
        Element lyricsElm = doc.select("div.lyrics").get(0);
        for(Element lyricsLine : lyricsElm.select("p")){
            for(Node childNode : lyricsLine.childNodes()){
                if (childNode instanceof TextNode) {
                    lyrics += ((TextNode)childNode).getWholeText()+'\n';
                }
            }
            lyrics += '\n';
        }
        mEditionLyrics.setText(lyrics.substring(0, lyrics.length()-1));

        Element titleElement = doc.select("div.title.fade-out").first().child(0);
        mEditionName.setText(((TextNode)titleElement.childNode(0)).getWholeText());

        Element artistElement = (Element) doc.select("div.album-artist.fade-out").first().childNode(0);
        mEditionArtist.setText(((TextNode)(artistElement.childNode(0))).getWholeText());
    }

    private class LyricsLoader extends AsyncTask<String, Void, Document>{
        @Override
        protected Document doInBackground(String... arg) {
            try {
                Document doc = Jsoup.connect(arg[0]).get();
                doc.getAllElements();
                return doc;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Document doc){
            loadLyricsFromHtml(doc);
        }
    }
}
