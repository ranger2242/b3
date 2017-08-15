package com.test;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Chris Cavazos on 8/10/2017.
 */
public class Icon {
    private TextureRegion pic;
    private Vector2 pos;
    int price;

    public Icon(TextureRegion keyFrame) {
        pic=keyFrame;
    }

    public TextureRegion getPic() {
        return pic;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public Rectangle getRect() {
        return new Rectangle(pos.x,pos.y,pic.getRegionWidth(),pic.getRegionHeight());
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
