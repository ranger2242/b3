package com.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.test.states.GameState;
import com.test.states.MultiplayerState;

import java.io.IOException;

import static com.test.Game.ft;
import static com.test.Main.outc;
import static com.test.states.GameState.*;
import static com.test.Player.players;

public class Button {

    private Sprite skin;
    float dtDebounce=0;
    int type=0;
    public Button(Texture texture, float x, float y, float width, float height) {
        skin = new Sprite(texture); // your image
        skin.setPosition(x, y);
        skin.setSize(width, height);
    }

    public Button(Texture btn, int w, int h) {
        skin = new Sprite(btn); // your image
        skin.setSize(w, h);
    }

    public Sprite getSkin() {
        return skin;
    }

    public void setSkin(Sprite skin) {
        this.skin = skin;
    }

    public void update (float input_x, float input_y) {
        dtDebounce+= Gdx.graphics.getDeltaTime();
        if(dtDebounce>ft*7) {
            checkIfClicked(input_x, input_y);
            dtDebounce=0;
        }
    }
    public void render(SpriteBatch sb){
        skin.draw(sb); // draw the hitButton

    }

    private void checkIfClicked(float ix, float iy) {
        int ind=0;
        if(!roundQueue.isEmpty()) {

            ind = getFromRoundQueue();
        }
        if (ix > skin.getX() && ix < skin.getX() + skin.getWidth()) {
            if (iy > skin.getY() && iy < skin.getY() + skin.getHeight()) {
                // the hitButton was clicked, perform an action
                switch (type) {
                    case 1: {//stand
                        if (!roundQueue.isEmpty()) {
                            if (!roundQueue.isEmpty()) {
                                Player p = players.get(ind);
                                if (p.isSecTurn() && !p.isR2Over()) {
                                    players.get(ind).stand2();
                                    players.get(ind).setSecTurn(false);
                                    roundQueue.remove(0);
                                }
                                if (!p.isSecTurn() && !p.isR1Over()) {//first turn
                                    players.get(ind).stand1();
                                    if (p.isSplit()) {
                                        players.get(ind).setSecTurn(true);
                                    } else {
                                        p.setRoundover(true);
                                        roundQueue.remove(0);
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case 2: {//hit
                        if (!roundQueue.isEmpty()) {
                            Player p = players.get(ind);
                            if (p.isSecTurn()) {
                                drawQueue.add(-ind);
                            } else if (!p.isSecTurn()) {//first turn
                                drawQueue.add(ind);
                            }
                        }
                        break;
                    }
                    case 3: {//next round
                        for (Player p : players) {
                            p.reset();
                        }
                        try {
                            FileHandler.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        GameState.startRound();
                        break;
                    }
                    case 4: {//split
                        if (!roundQueue.isEmpty()) {
                            players.get(ind).split();
                        }
                        break;
                    }
                    case 5:{//double down
                        if(players.get(ind).canDouble()) {
                            players.get(ind).setDoubled(true);
                            if (players.get(ind).isSecTurn()) {
                                drawQueue.add(-ind);
                            } else if (!players.get(ind).isSecTurn()) {//first turn
                                drawQueue.add(ind);
                            }
                        }
                        break;
                    }
                    case 6:{//insurance
                        if(players.get(ind).canInsure())
                            players.get(ind).insure();
                        break;
                    }
                    case 7:{//host
                        outc("host button");
                        MultiplayerState.hosting=true;

                        try {
                            outc("Connect to "+ MultiplayerState.getIp());
                            MultiplayerState.connectionHandler();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                    case 8:{//join
                        MyTextInputListener listener = new MyTextInputListener(3);
                        try {
                            Gdx.input.getTextInput(listener,"Enter valid IP address",MultiplayerState.getIp(),"");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }

    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPos(Vector2 pos) {
        skin.setPosition(pos.x,pos.y);
    }
}