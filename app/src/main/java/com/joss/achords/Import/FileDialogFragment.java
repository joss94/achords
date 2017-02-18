package com.joss.achords.Import;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.joss.achords.AbstractDialogFragment;
import com.joss.achords.OnItemClickListener;
import com.joss.achords.R;
import com.joss.achords.SelectAdapter.SelectAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FileDialogFragment extends AbstractDialogFragment implements OnItemClickListener {

    private static final String EXTRA_FILE = "Extra_file";
    public static final int FILE_REQUEST_CODE = 985;

    private RecyclerView mRecyclerView;
    private File mPath;
    private ArrayList<File> mFileList;
    private String mChosenFilePath = "";
    private SelectAdapter<File> adapter;

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
        mFileList = new ArrayList<>();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){

        // Inflate the layout for this fragment
        View v=getActivity().getLayoutInflater().inflate(R.layout.fragment_file_dialog, null);
        mRecyclerView= (RecyclerView) v.findViewById(R.id.file_chooser_listview);

        mPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
        try {
            mPath.mkdirs();
            if(mPath.exists()){
                mFileList = new ArrayList<>(Arrays.asList(mPath.listFiles()));
            }
            else{
                Toast.makeText(getContext(), "Unable to access data storage", Toast.LENGTH_LONG).show();
                return null;
            }
        }catch(Exception ignored){
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FileAdapter(mFileList);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setSelectionUnique(true);

        setDialogButtons(v);

        return v;
    }

    @Override
    public boolean callback(){
        super.callback();
        if(adapter.getSelected().isEmpty()){
            Toast.makeText(getContext(), "Please select a file", Toast.LENGTH_SHORT).show();
            return false;
        }
        return(processChoice(adapter.getSelected().get(0)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(int position) {
        File mSelectedPath;
        if(position==0){
            if(mPath.getParent()!=null){
                mSelectedPath = new File(mPath.getParent());
            }
            else {
                mSelectedPath=mPath;
            }
            adapter.resetSelected();
        }
        else{
            mSelectedPath = new File(mPath, adapter.getItems().get(position-1).getName());
        }
        if(mSelectedPath.isDirectory()){
            mPath = mSelectedPath;
            listRefresh();
        }
        else{
            mChosenFilePath=mSelectedPath.getAbsolutePath();
        }
    }

    public void listRefresh(){
        mFileList = new ArrayList<>(Arrays.asList(mPath.listFiles()));
        adapter = new FileAdapter(mFileList);
        adapter.setSelectionUnique(true);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    public boolean processChoice(File selectedFile){
        if(selectedFile.isDirectory()){
            mPath = selectedFile;
            listRefresh();
            return false;
        }
        else {
            mChosenFilePath=selectedFile.getAbsolutePath();
            listener.onFragmentInteraction(FILE_REQUEST_CODE, AppCompatActivity.RESULT_OK, mChosenFilePath);
            return true;
        }
    }
}
