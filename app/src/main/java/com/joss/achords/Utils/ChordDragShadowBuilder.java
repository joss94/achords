package com.joss.achords.Utils;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.joss.achords.R;
import com.joss.achords.Views.ChordButton;

/**
 * Created by Joss on 03/02/2017.
 */

public class ChordDragShadowBuilder extends View.DragShadowBuilder {

    private static int SHADOW_SCALE_PERCENTS = 80;
    private View shadow;
    private int width, height, padding;

    public ChordDragShadowBuilder(View view) {
        super(view);
        shadow = LayoutInflater.from(view.getContext()).inflate(R.layout.chord_shadow, null, false);
        //shadow.layout(0,0,view.getWidth()*SHADOW_SCALE_PERCENTS/100,view.getHeight()*SHADOW_SCALE_PERCENTS/100);
        ((ChordButton)shadow.findViewById(R.id.button)).setChord(((ChordButton)view).getChord());
        Log.d("SHADOW", "Setting shadow with chord: "+((ChordButton)view).getChord());
        Log.d("SHADOW", "Shadow has dimensions : height "+shadow.getMeasuredHeight());
    }

    @Override
    public void onProvideShadowMetrics (Point size, Point touch) {
        width = getView().getWidth();
        padding = (int)(getView().getHeight()*0.75);
        height = padding+getView().getHeight();

        Log.d("SHADOW", "W/H : "+width+"/"+height);

        size.set(width, height);
        touch.set(width / 2, height);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.layout(0,0,width, height-padding);
        shadow.draw(canvas);
        //shadow.draw(canvas);
    }

}
