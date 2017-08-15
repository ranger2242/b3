package com.test;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

/**
 * Created by Chris Cavazos on 1/23/2017.
 */
public class Triangle {
    float[] points= new float[6];
    public Triangle(){
        this(new float[]{0,0,0,0,0,0});
    }
    public Triangle(float[] p){
        points=p;
    }
    public float[] getPoints(){
        return points;
    }

    public boolean overlaps(Rectangle r){
        ArrayList<Line> triLines =Line.asLines(this);
        ArrayList<Line> rectLines=Line.asLines(r);
        boolean ov=false;
        for(Line l1: triLines){
            for(Line l2:rectLines){
                ov=ov|| Line.intersectsLine(l1,l2);
            }
        }
        return ov;
    }

}
