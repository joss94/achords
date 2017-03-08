package com.joss.achords;

/*
 * Created by joss on 01/03/17.
 */

public final class AnimationsCurves {

    public static float decelerate(float x){
        return (float) (x + 0.3*(1 - (x-0.5)*(x-0.5)/0.25));
    }

    public static float accelerate(float x){
        return (float) (x - 0.3*(1 - (x-0.5)*(x-0.5)/0.25));
    }
}
