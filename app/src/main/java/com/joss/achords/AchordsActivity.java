package com.joss.achords;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.joss.achords.Export.ExportEmailDialogFragment;
import com.joss.achords.Fragments.UserDialogFragment;
import com.joss.achords.Import.FileDialogFragment;
import com.joss.achords.Models.Songbook;
import com.joss.achords.Models.User;
import com.joss.achords.Settings.MySettingsActivity;
import com.joss.achords.SongbookHome.SongbookActivity;
import com.joss.utils.AbstractDialog.OnDialogFragmentInteractionListener;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class AchordsActivity extends AppCompatActivity implements
        OnDialogFragmentInteractionListener{

    public static final int SETTINGS_MENU_ITEM = R.id.settings;
    public static final int EXPORT_MENU_ITEM = R.id.exportData;
    public static final int IMPORT_MENU_ITEM = R.id.importData;
    public static final int DELETE_MENU_ITEM = R.id.delete;
    public static final String SHARED_PREFS = "shared_prefs";

    public static final String EXTRA_SONG_ID="com.joss.achords.extra_song_id";
    private static final int FILE_REQUEST_CODE = 1;
    private static final int USER_REQUEST_CODE = 2;
    private static final int EMAIL_REQUEST_CODE = 3;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ROOT);

    private String mOption="";
    public static SharedPreferences sharedPreferences;

    public static Songbook SONGBOOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeFonts();

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SONGBOOK = Songbook.get(getApplicationContext());

        if(getUser() == null){
            askNewUser();
        }
    }

    private void initializeFonts() {
        AchordsTypefaces.SONG_TITLE_FONT_LIGHT.setTypeface(Typeface.createFromAsset(getAssets(), getResources().getString(R.string.song_title_font_light)));
        AchordsTypefaces.SONG_TITLE_FONT.setTypeface(Typeface.createFromAsset(getAssets(), getResources().getString(R.string.song_title_font)));
    }

    protected void askNewUser(){
        UserDialogFragment fr = new UserDialogFragment();
        fr.setOnFragmentInteractionListener(this);
        fr.setRequestCode(USER_REQUEST_CODE);
        fr.show(getSupportFragmentManager(), "USER");
    }

    @Override
    public void setSupportActionBar(@NonNull Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        toolbar.setPadding(0,result,0,0);
        toolbar.showOverflowMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent menuIntent;
        switch(item.getItemId()){
            case IMPORT_MENU_ITEM:
                selectImportType();
                selectFile();
                break;
            case EXPORT_MENU_ITEM:
                selectEmail();
                break;
            case SETTINGS_MENU_ITEM:
                menuIntent = new Intent(getApplication(), MySettingsActivity.class);
                startActivity(menuIntent);
                break;
        }
        return false;
    }



    private void selectFile(){
        FileDialogFragment fileDialog = FileDialogFragment.newInstance();
        fileDialog.setOnFragmentInteractionListener(this);
        fileDialog.setRequestCode(FILE_REQUEST_CODE);
        fileDialog.show(this.getSupportFragmentManager(), "FILE");
    }

    private void selectImportType(){
        mOption= Songbook.IMPORT_OPTION_ADD;
    }

    private boolean importSongbook(File mFile){
        return Songbook.get(this).loadSongbook(mFile, mOption);
    }

    private void selectEmail(){
        ExportEmailDialogFragment fr = ExportEmailDialogFragment.newInstance();
        fr.setOnFragmentInteractionListener(this);
        fr.setRequestCode(EMAIL_REQUEST_CODE);
        fr.show(getSupportFragmentManager(), "EXPORT_EMAIL");
    }

    public boolean exportSongbook(String email){

        //<editor-fold desc="EXPORT SONGBOOK TO BYTE ARRAY">
        byte[] bytes = new byte[]{};
        try {
            bytes = Songbook.get(getApplicationContext()).exportSongbook(Songbook.EXPORT_FORMAT_JSON).toString().getBytes();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //</editor-fold>

        //<editor-fold desc="WRITE SONGBOOK INTO FILE">
        File export_file = new File(getCacheDir(), "export_file");
        try {
            FileOutputStream os = new FileOutputStream(export_file);
            os.write(Songbook.EXPORT_FORMAT_JSON_TOKEN.getBytes());
            os.write(new byte[]{'\n'});
            os.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //</editor-fold>

        //<editor-fold desc="SEND FILE BY EMAIL">
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("vnd.android.cursor.dir/email");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.export_mail_subject));
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.export_mail_text));
        i.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getApplicationContext(), "com.joss.achords.fileprovider", export_file));
        startActivityForResult(Intent.createChooser(i, getString(R.string.send_email)), 0);
        //</editor-fold>

        return true;
    }

    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Object... args) {
        switch (requestCode) {
            case FILE_REQUEST_CODE:
                //<editor-fold desc="HANDLE FILE CHOSEN">
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    String path = (String) args[0];
                    if (importSongbook(new File(path))) {
                        Intent i = new Intent(getApplicationContext(), SongbookActivity.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), R.string.importation_success, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.invalid_format, Toast.LENGTH_LONG).show();
                    }
                }
                //</editor-fold>
                break;

            case USER_REQUEST_CODE:
                //<editor-fold desc="HANDLE USER CREATION">
                if (resultCode == RESULT_OK && args[0] != null) {
                    String userName = (String) args[0];
                    User user = new User();
                    user.setName(userName);
                    user.save(sharedPreferences);
                }
                //</editor-fold>
                break;

            case EMAIL_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    exportSongbook((String) args[0]);
                }
                break;
        }
    }

    public static User getUser() {
        return User.getUser(sharedPreferences);
    }

    public static String formatDate(Date date){
        return sdf.format(date);
    }

    public static Date parseDate(String date) throws ParseException {
        return sdf.parse(date);
    }
}
