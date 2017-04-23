package com.joss.achords.SongEnvironment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.joss.achords.AchordsActivity;
import com.joss.achords.AchordsTypefaces;
import com.joss.achords.Models.Song;
import com.joss.achords.Models.Songbook;
import com.joss.achords.R;
import com.joss.achords.SongEnvironment.ChordsEdition.ChordFragment;
import com.joss.achords.SongEnvironment.SongDisplay.DisplayFragment;
import com.joss.achords.SongEnvironment.SongEdit.EditionFragment;

import java.util.UUID;

public class SongActivity extends AchordsActivity implements EditionFragment.OnSongChangedListener{
    public static final int EXTRA_CREATE_MODE =1;
    public static final int EXTRA_DISPLAY_MODE=2;
    public static final String EXTRA_MODE="mode";


    private UUID song_id;
    private int mode;
    private CustomViewPager mViewPager;
    private SparseArray<Fragment> registeredFragments;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        song_id= (UUID)getIntent().getSerializableExtra(EXTRA_SONG_ID);
        Song song = Songbook.get(this).getById(song_id);

        //<editor-fold desc="TOOLBAR">
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        ((TextView)toolbar.findViewById(R.id.toolbar_song_title)).setTypeface(AchordsTypefaces.SONG_TITLE_FONT.typeface);
        if (song.getName() !=null && !song.getName().isEmpty()) {
            ((TextView)toolbar.findViewById(R.id.toolbar_song_title)).setText(song.getName());
            ((TextView)toolbar.findViewById(R.id.toolbar_artist)).setText((song.getArtist()!=null&&!song.getArtist().isEmpty())?song.getArtist():getResources().getString(R.string.unknown));
        }
        toolbar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setToolbarPadding(toolbar);
        configOverflowMenu(toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        //</editor-fold>

        mode = (int)getIntent().getSerializableExtra(EXTRA_MODE);

        mViewPager = (CustomViewPager)findViewById(R.id.view_pager);

        FragmentManager fm = getSupportFragmentManager();
        registeredFragments= new SparseArray<>();
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                if(Math.abs(position)<1){
                    page.setRotationY(-position*20);
                    page.setScaleX((float) (1-0.5*Math.abs(position)));
                    page.setScaleY((float) (1-0.5*Math.abs(position)));
                }
            }
        });
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
        Song song = Songbook.get(this).getById(song_id);
        ((TextView)toolbar.findViewById(R.id.toolbar_song_title)).setText(song.getName());
        ((TextView)toolbar.findViewById(R.id.toolbar_artist)).setText((song.getArtist()!=null&&!song.getArtist().isEmpty())?song.getArtist():getResources().getString(R.string.unknown));
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