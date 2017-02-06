package com.joss.achords.Activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;

import com.joss.achords.Fragments.ChordFragment;
import com.joss.achords.Fragments.DisplayFragment;
import com.joss.achords.Fragments.EditionFragment;
import com.joss.achords.Fragments.SongbookFragment;
import com.joss.achords.R;
import com.joss.achords.Utils.CustomViewPager;

import java.util.UUID;

public class SongActivity extends AbstractParentActivity implements EditionFragment.OnSongChangedListener{
    public static final int EXTRA_CREATE_MODE =1;
    public static final int EXTRA_DISPLAY_MODE=2;
    public static final String EXTRA_MODE="mode";


    private UUID song_id;
    private int mode;
    private CustomViewPager mViewPager;
    private SparseArray<Fragment> registeredFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View v=getLayoutInflater().inflate(R.layout.activity_song,null);
        setContentView(v);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        song_id= (UUID)getIntent().getSerializableExtra(SongbookFragment.EXTRA_SONG_ID);
        mode = (int)getIntent().getSerializableExtra(EXTRA_MODE);

        mViewPager = (CustomViewPager)v.findViewById(R.id.view_pager);

        FragmentManager fm = getSupportFragmentManager();
        registeredFragments= new SparseArray<>();
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Fragment r;
                switch (position){
                    case 0:
                        r=createChordsFragment();
                        registeredFragments.put(0,r);
                        break;
                    case 1:
                        r=createDisplayFragment();
                        registeredFragments.put(1,r);
                        break;
                    case 2:
                        r=createEditionFragment();
                        registeredFragments.put(2,r);
                        break;
                    default:
                        r=null;
                }
                return r;
            }

            @Override
            public int getCount() {
                    return 3;
            }
        });

        if(mode== EXTRA_CREATE_MODE){
            mViewPager.setCurrentItem(2);
            mViewPager.setPagingEnabled(false);
        }
        if(mode==EXTRA_DISPLAY_MODE){mViewPager.setCurrentItem(1);}
    }


    protected Fragment createEditionFragment(){
        EditionFragment f = EditionFragment.newInstance(song_id);
        f.addOnSongChangedListener(this);
        return f;
    }
    protected Fragment createDisplayFragment(){return DisplayFragment.newInstance(song_id);}
    protected Fragment createChordsFragment(){return ChordFragment.newInstance(song_id);}


    @Override
    public void onSongChanged(UUID id) {
        mode = EXTRA_DISPLAY_MODE;
        song_id=id;
        if(registeredFragments.get(1)!=null){
            ((DisplayFragment)registeredFragments.get(1)).changeID(id);
        }
        if(registeredFragments.get(0)!=null){
            ((ChordFragment)registeredFragments.get(0)).changeID(id);
        }
        mViewPager.setPagingEnabled(true);
        mViewPager.setCurrentItem(1);
    }
}