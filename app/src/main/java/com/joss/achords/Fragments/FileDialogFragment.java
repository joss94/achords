package com.joss.achords.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joss.achords.R;

import java.io.File;

public class FileDialogFragment extends AbstractDialogFragment {

    private static final String EXTRA_FILE = "Extra_file";
    public static final int FILE_REQUEST_CODE = 985;

    private ListView mListView;
    private File mPath;
    private File[] mFileList = new File[0];
    private String mChosenFilePath;
    private FileListAdapter adapter;

    public FileDialogFragment() {
        // Required empty public constructor
    }

    public static FileDialogFragment newInstance() {
        FileDialogFragment fragment = new FileDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){

        // Inflate the layout for this fragment
        View v=getActivity().getLayoutInflater().inflate(R.layout.fragment_file_dialog, null);
        mListView=(ListView)v.findViewById(R.id.file_chooser_listview);
        //mPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
        mPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
        try {
            mPath.mkdirs();
        }catch(Exception e){
            Log.d("IMPORT", "No Access to local storage...");
        }
        if(mPath.exists()){
            Log.d("IMPORT", "value o mPath: "+mPath.getAbsolutePath());
            mFileList = mPath.listFiles();
        }
        else{
            Log.d("IMPORT", "Unable to access data storage");
            Toast.makeText(getContext(), "Unable to access data storage", Toast.LENGTH_LONG).show();
            return null;
        }
        Log.d("IMPORT", "Is the file a directory: "+mPath.isDirectory());
        adapter = new FileListAdapter(getActivity().getApplicationContext(), mFileList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File mSelectedPath;
                if(position==0){
                    if(mPath.getParent()!=null){
                        mSelectedPath = new File(mPath.getParent());
                    }
                    else {
                        mSelectedPath=mPath;
                    }
                }
                else{
                    mSelectedPath = new File(mPath, mFileList[position-1].getName());
                }
                if(mSelectedPath.isDirectory()){
                    mPath = mSelectedPath;
                    listRefresh();
                }
                else{
                    mChosenFilePath=mSelectedPath.getAbsolutePath();
                    view.setSelected(true);
                }
            }
        });

        setDialogButtons(v);

        return v;
    }

    @Override
    public boolean callback(){
        super.callback();
        listener.onFragmentInteraction(FILE_REQUEST_CODE, AppCompatActivity.RESULT_OK, mChosenFilePath);
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class FileListAdapter extends ArrayAdapter<File> {

        public FileListAdapter(Context context, File[] objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView==null) {
                convertView=getActivity().getLayoutInflater().inflate(R.layout.file_item, null);
            }
            TextView filenameTextView = (TextView)convertView.findViewById(R.id.file_name);

            if(position==0){
                filenameTextView.setText("..");
            }
            else{
                final String filename = getItem(position-1).getName();
                filenameTextView.setText(filename);
            }


            return convertView;
        }
    }

    public void listRefresh(){
        FileListAdapter newAdapter = new FileListAdapter(getContext(), mPath.listFiles());
        mListView.setAdapter(newAdapter);
        Log.d("IMPORT", "Entering a new directory, new list of files size= "+mPath.listFiles().length);
        ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
        mFileList=mPath.listFiles();
    }
}
