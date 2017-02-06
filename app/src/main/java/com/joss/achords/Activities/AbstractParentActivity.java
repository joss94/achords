package com.joss.achords.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.joss.achords.Fragments.ExportEmailDialogFragment;
import com.joss.achords.Fragments.FileDialogFragment;
import com.joss.achords.Fragments.UserDialogFragment;
import com.joss.achords.Interfaces.OnFragmentInteractionListener;
import com.joss.achords.Models.Songbook;
import com.joss.achords.Models.User;
import com.joss.achords.R;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Joss on 28/01/2017.
 */

public abstract class AbstractParentActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    public static final int SETTINGS_MENU_ITEM = R.id.settings;
    public static final int EXPORT_MENU_ITEM = R.id.exportData;
    public static final int IMPORT_MENU_ITEM = R.id.importData;
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";

    private String mOption="";
    private User user;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        try {
            UUID user_id = UUID.fromString(sharedPreferences.getString(USER_ID, ""));
            String user_name = sharedPreferences.getString(USER_NAME, "");
            user = new User(user_id, user_name);
        } catch (IllegalArgumentException e) {
            createNewUser();
        }
    }

    protected void createNewUser(){
        UserDialogFragment fr = new UserDialogFragment();
        fr.setOnFragmentInteractionListener(this);
        fr.show(getSupportFragmentManager(), "USER");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
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
                menuIntent = new Intent(this, MySettingsActivity.class);
                this.startActivity(menuIntent);
                break;

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void selectFile(){
        FileDialogFragment fileDialog = FileDialogFragment.newInstance();
        fileDialog.setOnFragmentInteractionListener(this);
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
        if (export_file!=null) {
            try {
                FileOutputStream os = new FileOutputStream(export_file);
                os.write(Songbook.EXPORT_FORMAT_JSON_TOKEN.getBytes());
                os.write(new byte[]{'\n'});
                os.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //</editor-fold>

        //<editor-fold desc="SEND FILE BY EMAIL">
        FileProvider provider = new FileProvider();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("vnd.android.cursor.dir/email");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, "Export of Songbook");
        i.putExtra(Intent.EXTRA_TEXT, "Please find attach to this email the exportSongbook file of your Songbook");
        i.putExtra(Intent.EXTRA_STREAM, provider.getUriForFile(getApplicationContext(), "com.joss.essai.fileprovider", export_file));
        startActivityForResult(Intent.createChooser(i, "Send email"), 0);
        //</editor-fold>

        return true;
    }

    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Object... args){
        switch (requestCode){
            case FileDialogFragment.FILE_REQUEST_CODE:
                //<editor-fold desc="HANDLE FILE CHOSEN">
                if(resultCode == AppCompatActivity.RESULT_OK){
                    String path = (String) args[0];
                    if(importSongbook(new File(path))){
                        Intent i = new Intent(getApplicationContext(), SongbookActivity.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "Songbook imported successfully !", Toast.LENGTH_LONG).show();
                    }
                }
                //</editor-fold>
                break;

            case UserDialogFragment.USER_REQUEST_CODE:
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

            case ExportEmailDialogFragment.EMAIL_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    exportSongbook((String)args[0]);
                }
                break;
        }
    }
}
