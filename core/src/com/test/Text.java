package com.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by range_000 on 1/5/2017.
 */
public class Text {
    public int size =1;
    public Color c=Color.GRAY;
    public Vector2 pos= new Vector2();
    public String text = "";

    public Text(String s,Vector2 v,Color c, int size){
        text=s;
        pos=v;
        this.c=c;
        this.size=size;
    }
   /* public static float strWidth(String s){
        CharSequence cs=s;
        gl.setText(Game.getFont(),cs);
        return gl.width;
    }
    public static float centerString(String s){
        return (viewX+WIDTH/2)- (strWidth(s)/2);
    }
*/
}
