package com.joss.achords.Import;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.joss.achords.R;
import com.joss.utils.AbstractDialog.AbstractDialogFragment;
import com.joss.utils.SelectAdapter.OnItemClickListener;
import com.joss.utils.SelectAdapter.SelectAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileDialogFragment extends AbstractDialogFragment implements OnItemClickListener {

    private RecyclerView mRecyclerView;
    private File mPath;
    private List<File> mFileList;
    private String mChosenFilePath = "";
    private SelectAdapter<File> adapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_file_dialog;
    }

    public static FileDialogFragment newInstance() {
        return new FileDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileList = new ArrayList<>();

        mPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
        try {

            if (mPath.mkdirs()) {
                if(mPath.exists()){
                    mFileList = new ArrayList<>(Arrays.asList(mPath.listFiles()));
                }
                else{
                    Toast.makeText(getContext(), R.string.data_access_error, Toast.LENGTH_LONG).show();
                }
            }
        }catch(Exception ignored){
        }
    }

    @Override
    public void findViews(View v){
        mRecyclerView= (RecyclerView) v.findViewById(R.id.file_chooser_listview);
    }

    @Override
    public void setViews(){
        setTitle(getContext().getResources().getString(R.string.import_dialog_title));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FileAdapter(mFileList);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setSelectionUnique(true);
    }


    @Override
    public boolean callback(){
        if(adapter.getSelected().isEmpty()){
            Toast.makeText(getContext(), R.string.no_selected_file_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return(processChoice(adapter.getSelected().get(0)));
    }

    @Override
    public void onItemClick(int position) {
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
            listener.onFragmentInteraction(getRequestCode(), AppCompatActivity.RESULT_OK, mChosenFilePath);
            return true;
        }
    }
}
