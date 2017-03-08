package com.joss.achords;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.joss.achords.Export.ExportEmailDialogFragment;
import com.joss.achords.Fragments.UserDialogFragment;
import com.joss.achords.Import.FileDialogFragment;
import com.joss.achords.Models.Songbook;
import com.joss.achords.Models.User;
import com.joss.achords.Settings.MySettingsActivity;
import com.joss.achords.SongbookHome.SongbookActivity;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/*
 * Created by Joss on 28/01/2017.
 */

public abstract class AbstractParentActivity extends AppCompatActivity implements OnDialogFragmentInteractionListener, PopupMenu.OnMenuItemClickListener {

    public static final int SETTINGS_MENU_ITEM = R.id.settings;
    public static final int EXPORT_MENU_ITEM = R.id.exportData;
    public static final int IMPORT_MENU_ITEM = R.id.importData;
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";

    public static final String EXTRA_SONG_ID="com.joss.achords.extra_song_id";
    private static final int FILE_REQUEST_CODE = 1;
    private static final int USER_REQUEST_CODE = 2;
    private static final int EMAIL_REQUEST_CODE = 3;

    public static Typeface SONG_TITLE_TYPEFACE;

    private String mOption="";
    private User user;
    public SharedPreferences sharedPreferences;
    protected Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeFonts();

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        try {
            UUID user_id = UUID.fromString(sharedPreferences.getString(USER_ID, ""));
            String user_name = sharedPreferences.getString(USER_NAME, "");
            user = new User(user_id, user_name);
        } catch (IllegalArgumentException e) {
            createNewUser();
        }
    }

    private void initializeFonts() {
        AchordsTypefaces.SONG_TITLE_FONT_LIGHT.setTypeface(Typeface.createFromAsset(getAssets(), getResources().getString(R.string.song_title_font_light)));
        AchordsTypefaces.SONG_TITLE_FONT.setTypeface(Typeface.createFromAsset(getAssets(), getResources().getString(R.string.song_title_font)));
    }

    protected void createNewUser(){
        UserDialogFragment fr = new UserDialogFragment();
        fr.setOnFragmentInteractionListener(this);
        fr.setRequestCode(USER_REQUEST_CODE);
        fr.show(getSupportFragmentManager(), "USER");
    }

    public void setToolbarPadding(Toolbar toolbar) {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        if(toolbar!=null){
            toolbar.setPadding(0,result,0,0);
        }
    }

    public void configOverflowMenu(Toolbar toolbar){
        final ImageView overflowBtn = (ImageView)toolbar.findViewById(R.id.overflow_btn);
        overflowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOverflowMenu(overflowBtn);
            }
        });
    }

    public void createOverflowMenu(View anchorView){
        PopupMenu popup = new PopupMenu(AbstractParentActivity.this, anchorView);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
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
        i.putExtra(Intent.EXTRA_SUBJECT, "Export of Songbook");
        i.putExtra(Intent.EXTRA_TEXT, "Please find attach to this email the exportSongbook file of your Songbook");
        i.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getApplicationContext(), "com.joss.achords.fileprovider", export_file));
        startActivityForResult(Intent.createChooser(i, "Send email"), 0);
        //</editor-fold>

        return true;
    }

    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Object... args){
        switch (requestCode){
            case FILE_REQUEST_CODE:
                //<editor-fold desc="HANDLE FILE CHOSEN">
                if(resultCode == AppCompatActivity.RESULT_OK){
                    String path = (String) args[0];
                    if(importSongbook(new File(path))){
                        Intent i = new Intent(getApplicationContext(), SongbookActivity.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "Songbook imported successfully !", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Invalid format !!", Toast.LENGTH_LONG).show();
                    }
                }
                //</editor-fold>
                break;

            case USER_REQUEST_CODE:
                //<editor-fold desc="HANDLE USER CREATION">
                if(resultCode==RESULT_OK){
                    String userName = (String)args[0];
                    this.user = new User();
                    this.user.setName(userName);
                    sharedPreferences.edit().putString(USER_ID, user.getId().toString()).apply();
                    sharedPreferences.edit().putString(USER_NAME, user.getName()).apply();
                }
                //</editor-fold>
                break;

            case EMAIL_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    exportSongbook((String)args[0]);
                }
                break;
        }
    }
}