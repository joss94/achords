package com.joss.achords.SongbookHome;


import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joss.achords.AbstractParentActivity;
import com.joss.achords.AchordsTypefaces;
import com.joss.achords.Models.Songbook;
import com.joss.achords.OnDialogFragmentInteractionListener;
import com.joss.achords.R;
import com.joss.achords.SelectAdapter.OnAdapterSelectModeChangeListener;
import com.joss.achords.SongEnvironment.SongActivity;

import java.util.Locale;

public class SongbookActivity extends AbstractParentActivity implements Songbook.OnSongbookChangeListener, OnAdapterSelectModeChangeListener, ViewPager.OnPageChangeListener, ByArtistFragment.OnArtistClickedListener, View.OnClickListener {

    private static final String TAG = "Songbook Fragment";
    private static final int DELETE_SONG_CONFIRM_REQUEST_CODE = 1;
    ;
    private EditText editSearch;

    private ImageView deleteBtn;
    private TextView artistHeader;
    private ViewPager mViewPager;
    private SparseArray<Fragment> registeredFragments;
    private View tabIndicator;
    private LinearLayout tabs;
    private FragmentManager fm;
    private FragmentStatePagerAdapter adapter;
    private HorizontalScrollView tabsScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songbook);

        tabIndicator = findViewById(R.id.tab_indicator);
        tabs = (LinearLayout) findViewById(R.id.tabs);
        tabsScrollView = (HorizontalScrollView)findViewById(R.id.tabs_scroll_view);


        artistHeader = (TextView) findViewById(R.id.songbook_artist_header);

        //<editor-fold desc="TOOLBAR">
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        ((TextView)toolbar.findViewById(R.id.title)).setTypeface(AchordsTypefaces.SONG_TITLE_FONT.typeface);
        setSupportActionBar(toolbar);
        setToolbarPadding(toolbar);
        configOverflowMenu(toolbar);
        toolbar.showOverflowMenu();
        //</editor-fold>

        //<editor-fold desc="NEW SONG BUTTON">
        ImageButton newSongButton=(ImageButton) findViewById(R.id.new_song_button);
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

        //<editor-fold desc="DELETE BUTTON">
        deleteBtn =(ImageView)findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDeleteDialogFragment fr = ConfirmDeleteDialogFragment.newInstance();
                fr.setOnFragmentInteractionListener(new OnDialogFragmentInteractionListener() {
                    @Override
                    public void onFragmentInteraction(int requestCode, int resultCode, Object... args) {
                        if(requestCode==DELETE_SONG_CONFIRM_REQUEST_CODE && resultCode == RESULT_OK){
                            deleteSelected();
                        }
                    }
                });
                fr.setRequestCode(DELETE_SONG_CONFIRM_REQUEST_CODE);
                fr.show(getSupportFragmentManager(), "DELETE_CONFIRM");
            }
        });
        //</editor-fold>

        for(int i=0; i<tabs.getChildCount(); i++){
            tabs.getChildAt(i).setOnClickListener(this);
            tabs.getChildAt(i).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (v.getId() == R.id.artists_tab && right != oldRight) {
                        setTab();
                    }
                }
            });
        }

        mViewPager = (ViewPager)findViewById(R.id.view_pager);

        fm = getSupportFragmentManager();
        registeredFragments = new SparseArray<>();
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setOnPageChangeListener(this);
        adapter = new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Fragment r;
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
                        r=createByListFragment();
                        registeredFragments.put(2,r);
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

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        };
        mViewPager.setAdapter(adapter);

        Songbook.get(this).setOnSongbookChangeListener(this);

        //<editor-fold desc="SEARCH FIELD">
        editSearch = (EditText)findViewById(R.id.edit_search);
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
                    ((SongbookFragment)registeredFragments.get(mViewPager.getCurrentItem())).filter(s.toString());
                }
            }
        });
        //</editor-fold>
    }

    @Override
    public void onBackPressed() {
        if(mViewPager.getCurrentItem() == 1) {
            if (adapter.getItem(1) instanceof BySongFragment) {
                ByArtistFragment fr = createByArtistFragment();
                registeredFragments.remove(1);
                registeredFragments.put(1, fr);
                ((TextView)findViewById(R.id.artists_tab_text)).setText(getResources().getString(R.string.artists));

                hideArtistHeader(new Animation.AnimationListener(){

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        artistHeader.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            else {
                finish();
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

    protected Fragment createByListFragment(){
        return ByListFragment.newInstance();
    }

    @Override
    public void onDBChange() {
        for(int i=0; i<registeredFragments.size(); i++){
            ((SongbookFragment) registeredFragments.get(registeredFragments.keyAt(i))).refresh();
        }
    }

    public void deleteSelected(){
        ((SongbookFragment)registeredFragments.get(mViewPager.getCurrentItem())).deleteSelected();
    }

    @Override
    public void onAdapterSelectModeChange(boolean selectMode) {
        if(selectMode){
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            deleteBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        slideTab(position, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onArtistClicked(String artist) {
        BySongFragment fr = BySongFragment.newInstance(artist);
        registeredFragments.remove(1);
        registeredFragments.put(1, fr);
        ((TextView)findViewById(R.id.artists_tab_text)).setText(artist.toUpperCase());
        showArtistHeader(artist);
        adapter.notifyDataSetChanged();
    }

    public void showArtistHeader(final String artist){
        artistHeader.setVisibility(View.VISIBLE);
        artistHeader.setAlpha(0);
        int numberOfSongs = Songbook.get(getApplicationContext()).getSongsOfArtist(artist).size();
        if(numberOfSongs>1){
            artistHeader.setText(String.format(Locale.ENGLISH, getString(R.string.artist_header_label), numberOfSongs, artist));
        }
        else{
            artistHeader.setText(String.format(Locale.ENGLISH, getString(R.string.artist_header_label_one_song), numberOfSongs, artist));
        }

        mViewPager.animate().setDuration(500).translationYBy(getResources().getDimensionPixelSize(R.dimen.songbook_artist_header)).start();
        artistHeader.animate().setDuration(500).alpha(1).start();
    }

    private void hideArtistHeader(Animation.AnimationListener animationListener) {
        artistHeader = (TextView) findViewById(R.id.songbook_artist_header);

        Animation a = new TranslateAnimation(0, -mViewPager.getWidth(), 0, 0);
        a.setDuration(500);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mViewPager.setTranslationY(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation b = new TranslateAnimation(0, artistHeader.getWidth(), 0, 0);
        b.setDuration(500);
        b.setAnimationListener(animationListener);

        artistHeader.startAnimation(b);
        mViewPager.startAnimation(a);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.songs_tab:
                mViewPager.setCurrentItem(0, true);
                break;

            case R.id.artists_tab:
                mViewPager.setCurrentItem(1, true);
                break;

            case R.id.lists_tab:
                mViewPager.setCurrentItem(2, true);
                break;

        }
    }

    public void slideTab(int position, float positionOffset){
        if (positionOffset != 0) {
            int maxScroll = tabs.getChildAt(tabs.getChildCount()-1).getRight()-mViewPager.getWidth();
            tabsScrollView.scrollTo((int) (positionOffset*maxScroll*(position+1)/(mViewPager.getChildCount()-1)+maxScroll*(position)/(mViewPager.getChildCount()-1)),0);
        }

        int leftSize = tabs.getChildAt(position).getMeasuredWidth();
        float leftPos = tabs.getChildAt(position).getX()+tabsScrollView.getX();

        int rightSize;
        float rightPos;
        if (tabs.getChildAt(position+1)!= null) {
            rightSize = tabs.getChildAt(position+1).getMeasuredWidth();
            rightPos = tabs.getChildAt(position +1).getX()+tabsScrollView.getX();
        } else {
            return;
        }

        int size = (int) (positionOffset*rightSize+(1-positionOffset)*leftSize);

        tabIndicator.setScaleX(size);

        if(positionOffset>0){
            tabIndicator.setX(size/2+leftPos - positionOffset*(leftPos - rightPos) );
        }
        else{
            tabIndicator.setX(size/2+leftPos - positionOffset*(leftPos - rightPos));
        }

    }

    public void setTab(){
        final int futureSize = tabs.getChildAt(mViewPager.getCurrentItem()).getMeasuredWidth();
        tabIndicator.animate().setDuration(500).scaleX(futureSize).x(futureSize/2+tabs.getChildAt(mViewPager.getCurrentItem()).getX()).start();
    }
}
