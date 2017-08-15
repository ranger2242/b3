package com.test;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;


/**
 * Created by Chris Cavazos on 1/21/2017.
 */
public class ShapeRendererExt extends ShapeRenderer {

    public void rect(Rectangle r) {
        rect(r.x,r.y,r.width,r.height);
    }
    public void triangle(Triangle t){
        float[] f=t.getPoints();
        triangle(f[0],f[1],f[2],f[3],f[4],f[5]);
//        triangle(f[0],fixHeight(new Vector2(f[0],f[1])),f[2],fixHeight(new Vector2(f[2],f[3])),f[4],fixHeight(new Vector2(f[4],f[5])));

    }
    public void line(Line l){
        line(l.a,l.b);
    }
}
