package com.joss.achords.SongEnvironment.ChordsEdition.FloatingChords;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

/**
 * Created by Joss on 03/02/2017.
 */

public class ChordDragShadowBuilder extends View.DragShadowBuilder {

    private float scale = 0.8f;
    private int width, height, padding;

    public ChordDragShadowBuilder(View view) {
        super(view);
    }

    @Override
    public void onProvideShadowMetrics (Point size, Point touch) {

        width = (int) (getView().getWidth()*scale);
        padding = (int)(getView().getHeight()*1.1);
        height = (int) (padding+getView().getHeight()*scale);

        size.set(width, height-padding);
        Log.d("SHADOW BUILDER", size.toString());
        touch.set(width / 2, height);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        canvas.scale(scale,scale);

        getView().draw(canvas);
    }

}
