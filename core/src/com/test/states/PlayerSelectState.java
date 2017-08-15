package com.test.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.*;

import java.util.ArrayList;

import static com.test.Game.*;
import static com.test.states.GameState.*;
import static com.test.Player.players;

/**
 * Created by Chris Cavazos on 8/14/2017.
 */
public class PlayerSelectState extends State {
    ArrayList<Player> possible =new ArrayList<>();
    ArrayList<Rectangle> pRects =new ArrayList<>();
    ArrayList<Rectangle> qRects =new ArrayList<>();
    ShapeRendererExt sr= new ShapeRendererExt();

    PlayerSelectState(GameStateManager gsm) {
        super(gsm);
        initCamera();
        possible.add(new Player("chris",false));
        possible.add(new Player("dago",false));
        possible.add(new Player("joel",false));
        possible.add(new Player("schwank",false));
        possible.add(new Player("jpcrat",false));
        possible.add(new Player("extra",false));
    }

    @Override
    protected void handleInput() {
        updateMousePos();
        if(Gdx.input.isKeyPressed(Input.Keys.TAB)){
            GameState.startRound();
            gsm.pop();
        }
        for (Rectangle r : pRects) {
            if (r.contains(mpos)) {
                boolean b=false;
                Player p = possible.get(pRects.indexOf(r));
                for(Player p1:players){
                    if(p.getName().equals(p1.getName()))
                        b=true;

                }
                if(!b) {
                    players.add(p);
                   resetPlayerList();
                }
            }
        }
        for (Rectangle r : qRects) {
            if (r.contains(mpos)) {
                players.remove(qRects.indexOf(r)+1);
                resetPlayerList();
            }
        }
    }
    void resetPlayerList(){
        for(int i=0;i<players.size();i++){
            if(i!=0) {
                players.get(i).setOrder(i);
                Vector2 v = getPosition(i);
                Rectangle rect = new Rectangle(WIDTH / 2 + v.x - 10, HEIGHT / 2 + v.y - 70, 50, 50);
                players.get(i).setRect(rect);
            }
            players.get(i).endRound();
            players.get(i).reset();
        }
    }

    @Override
    public void update(float dt) {
        if(!lag)
        handleInput();
        updateVariables(dt);

    }
    void initCamera(){
        cam.setToOrtho(false, WIDTH, HEIGHT);
        cam.position.set( WIDTH/2,HEIGHT/2,0);
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0,0,0,1);
        sb.begin();
        getFont().setColor(Color.WHITE);
        pRects.clear();
        qRects.clear();
        for(int i=0;i<possible.size();i++){
            Game.getFont().draw(sb,possible.get(i).getName(),30,HEIGHT-30-(25*i));
            pRects.add(new Rectangle(30,HEIGHT-50-(25*i),100,20));

        }
        for(int i=1;i<players.size();i++){
            Game.getFont().draw(sb,players.get(i).getName(),200,HEIGHT-30-(25*(i-1)));
            qRects.add(new Rectangle(200,HEIGHT-50-(25*(i-1)),100,20));


        }
        sb.end();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.RED);
        for(Rectangle r:qRects) {
            sr.rect(r);
        }
        for(Rectangle r:pRects) {
            sr.rect(r);
        }
        sr.end();
    }

    @Override
    public void dispose() {

    }
}
