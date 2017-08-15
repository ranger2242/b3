package com.test;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

import static com.test.Game.HEIGHT;
import static com.test.Game.WIDTH;
import static com.test.states.GameState.mpos;


public class Card implements Serializable {
    int value=0;
    String name="";
    Suits s =Suits.Heart;
    private Rectangle shape=new Rectangle(WIDTH/2,HEIGHT/4*3,15,30);
    private boolean aceHigh=false;

    public boolean isFaceDown() {
        return faceDown;
    }

    public void setFaceDown(boolean faceDown) {
        this.faceDown = faceDown;
    }

    private boolean faceDown=false;
    Vector2 pos = new Vector2(0,0);



    public boolean isAceHigh() {
        return aceHigh;
    }

    public void setAceHigh(boolean aceHigh) {
        this.aceHigh = aceHigh;
    }


    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public Rectangle getShape() {
        return shape;
    }

    public void setShape(Rectangle s) {
        shape=s;
    }

    public enum Suits {Heart, Spades, Diamonds, Clubs};
    public int getValue(){
        if(value>10)return 10;
        else return value;
    }

    public Card(int value, Suits s) {
        this.value = value;
        this.s = s;
        aceHigh=false;
    }
    public void move(Vector2 v) {
        shape.setPosition(shape.x + v.x, shape.y + v.y);
    }
    public String getName2() {
        switch (value) {
            case 1: {
                name = "A";
                break;
            }
            case 11: {
                name = "J";
                break;
            }
            case 12: {
                name = "Q";
                break;
            }
            case 13: {
                name = "K";
                break;
            }
            default: {
                name = "" + value;
                break;
            }
        }

        switch (s) {

            case Heart:
                name += "+";

                break;
            case Spades:
                name += "-";

                break;
            case Diamonds:
                name += "*";

                break;
            case Clubs:
                name += "/";

                break;
        }
        return name;
    }
    public String getName() {

        switch (value) {
            case 1: {
                name = "Ace";
                break;
            }
            case 11: {
                name = "Jack";
                break;
            }
            case 12: {
                name = "Queen";
                break;
            }
            case 13: {
                name = "King";
                break;
            }
            default: {
                name = "" + value;
                break;
            }

        }
        name += " " + s.name();
        return name;
    }
    public String printCard() {
        return getName();

    }
    public boolean isAce(){
        if(value==1){
            return true;
        }else return false;
    }
    public boolean isClicked(){
       return new Rectangle(pos.x,pos.y,15,30).contains(mpos);
    }
    public void setAceValue(){

    }

}
