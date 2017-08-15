package com.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

/**
 * Created by Chris Cavazos on 5/28/2016.
 */
public class HoverText {
    public static ArrayList<HoverText> texts = new ArrayList<>();
    private Color color;
    private final String text;
    private boolean active=false;
    private final int x;
    private final int y;
    private int px=0;
    private int py=0;
    private int ymod;
    private float dtHov;
    private float dtFlash;
    protected float alpha=1;
    private final boolean flash;
    private boolean cycle;
    private float time;
    BitmapFont font;
    GlyphLayout gl;

    public HoverText(String s, float t, Color c, float x1, float y1, boolean flash){
        font = new BitmapFont();
        gl=new GlyphLayout(font,s);
        active=true;
        time=t;
        text=s;
        color=c;
        x= Math.round(x1);
        y=Math.round(y1);
        this.flash=flash;
        if(flash){
            dtFlash=0;
            cycle=true;
        }
        texts.add(this);
    }
    public void updateDT() {
        dtHov += Gdx.graphics.getDeltaTime();
        if (flash) dtFlash += Gdx.graphics.getDeltaTime();
        //delete inactive hoverText
        boolean[] index;
        if (!texts.isEmpty()) {
            index = new boolean[texts.size()];
            texts.stream().filter(h -> !h.isActive()).forEach(h -> index[texts.indexOf(h)] = true);
            for (int i = texts.size() - 1; i >= 0; i--) {
                try {
                    if (index[i]) {
                        texts.remove(i);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
            while (HoverText.texts.size() > 10) HoverText.texts.remove(0);
            if (active) {
                //float time = 1.2f;
                if (dtHov < time) {
                    CharSequence cs = text;
                    gl.setText(font, cs);
                    alpha= (float) -(Math.pow(Math.E,5*((dtHov/time)-1f)+1));
                    if (dtFlash > .1) {
                        if (cycle) {
                            font.setColor(Color.WHITE);
                        } else font.setColor(color);
                        cycle = !cycle;
                        dtFlash = 0;
                    }
                    font.setColor(color);
                    font.getColor().a=alpha;
                    px = (int) (x - (gl.width / 2));
                    py = y + ymod;

                    ymod++;
                } else {
                    dtHov = 0;
                    ymod = 0;
                    active = false;
                }

            }
        }
    }
    public void draw(SpriteBatch sb) {
        if (active) {
            if (dtHov < 1.2) {
                font.setColor(color);
                font.draw(sb, text, px,py);
            }
        }
//        font.getColor().a=1;


    }
    public boolean isActive() {
        return active;
    }

    public float getTime() {
        return time;
    }
}
