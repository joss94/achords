package com.joss.achords.SongbookHome;


import android.animation.Animator;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.joss.achords.AchordsActivity;
import com.joss.achords.AchordsTypefaces;
import com.joss.achords.Database.DBHelper;
import com.joss.achords.Models.Songbook;
import com.joss.achords.Models.Songlist;
import com.joss.achords.R;
import com.joss.achords.SongEnvironment.SongActivity;
import com.joss.utils.TabsScrollView.TabScrollView;

import java.util.ArrayList;
import java.util.List;

public class SongbookActivity extends AchordsActivity implements DBHelper.OnDBChangeListener,
        ByArtistFragment.OnArtistClickedListener,
        ByListFragment.OnListClickedListener {

    private ViewPager mViewPager;
    private SparseArray<SongbookFragment> registeredFragments;
    private FragmentStatePagerAdapter adapter;
    private TabScrollView tabsScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songbook);

        tabsScrollView = (TabScrollView) findViewById(R.id.tabs_scroll_view2);

        //<editor-fold desc="TOOLBAR">
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        ((TextView)toolbar.findViewById(R.id.title)).setTypeface(AchordsTypefaces.SONG_TITLE_FONT.typeface);
        setSupportActionBar(toolbar);
        //</editor-fold>

        //<editor-fold desc="NEW SONG BUTTON">
        ImageButton newSongButton=(ImageButton) findViewById(R.id.new_song_button);
        //noinspection deprecation
        ((GradientDrawable)newSongButton.getBackground()).setColor(getResources().getColor(R.color.Red));
        newSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), SongActivity.class);
                i.putExtra(SongActivity.EXTRA_MODE, SongActivity.EXTRA_CREATE_MODE);
                startActivity(i);
            }
        });
        //</editor-fold>

        mViewPager = (ViewPager)findViewById(R.id.view_pager);

        registeredFragments = new SparseArray<>();
        mViewPager.setOffscreenPageLimit(3);

        adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                SongbookFragment r;
                switch (position){
                    case 0:
                        r=createBySongFragment();
                        registeredFragments.put(0,r);
                        break;
                    case 1:
                        if(registeredFragments.get(1) == null){
                            r=createByArtistFragment();
                            registeredFragments.put(1,r);
                        } else {
                            r = registeredFragments.get(1);
                        }

                        break;
                    case 2:
                        if(registeredFragments.get(2) == null){
                            r=createByListFragment();
                            registeredFragments.put(2,r);
                        } else {
                            r = registeredFragments.get(2);
                        }
                        break;
                    default:
                        r=createBySongFragment();
                }
                return r;
            }

            @Override
            public int getCount() {
                return 3;
            }

            //*
            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }/**/

        };

        mViewPager.setAdapter(adapter);
        tabsScrollView.setViewPager(mViewPager);

        List<String> titles = new ArrayList<>();
        titles.add(getResources().getString(R.string.songs));
        titles.add(getResources().getString(R.string.artists));
        titles.add(getResources().getString(R.string.lists));

        tabsScrollView.setTitles(titles);

        Songbook.get(this).addOnSongbookChangeListener(this);

        //<editor-fold desc="SEARCH FIELD">
        EditText editSearch = (EditText) findViewById(R.id.edit_search);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (registeredFragments.get(mViewPager.getCurrentItem()) != null) {
                    registeredFragments.get(mViewPager.getCurrentItem()).filter(s.toString());
                }
            }
        });
        //</editor-fold>
    }

    @Override
    public void onBackPressed() {
        if(mViewPager.getCurrentItem() == 1) {
            if (adapter.getItem(1) instanceof BySongFragment) {
                replaceFragment(1, createByArtistFragment(), getResources().getString(R.string.artists));
            }
            else {
                super.onBackPressed();
            }
        }
        else if (mViewPager.getCurrentItem() == 2){
            if(adapter.getItem(2) instanceof BySongFragment){
                replaceFragment(2, createByListFragment(), getResources().getString(R.string.lists));
            }
            else {
                super.onBackPressed();
            }
        }
        else{
            super.onBackPressed();
        }
    }

    protected BySongFragment createBySongFragment(){
        return BySongFragment.newInstance();
    }

    protected ByArtistFragment createByArtistFragment(){
        ByArtistFragment fr = ByArtistFragment.newInstance();
        fr.setOnArtistClickedListener(this);
        return fr;
    }

    protected ByListFragment createByListFragment(){
        ByListFragment fr = ByListFragment.newInstance();
        fr.setListClickedListener(this);
        return fr;
    }

    @Override
    public void onDBChange() {
        for(int i=0; i<registeredFragments.size(); i++){
            registeredFragments.get(registeredFragments.keyAt(i)).refresh();
        }
    }

    @Override
    public void onArtistClicked(final String artist) {
        replaceFragment(1, BySongFragment.newInstance(artist), artist.toUpperCase());
    }

    public void goToLists() {
        mViewPager.setCurrentItem(2, true);
    }

    @Override
    public void onListClicked(Songlist list) {
        replaceFragment(2, BySongFragment.newInstance(list), list.getName().toUpperCase());
    }

    public void replaceFragment(final int oldFragmentIndex, final SongbookFragment newFragment, final String newTabTitle){

        mViewPager.animate().setDuration(200).alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                registeredFragments.remove(oldFragmentIndex);
                registeredFragments.put(oldFragmentIndex, newFragment);
                adapter.notifyDataSetChanged();
                tabsScrollView.setTitle(oldFragmentIndex, newTabTitle);
                mViewPager.animate().setDuration(200).setListener(null).alpha(1).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }
}
