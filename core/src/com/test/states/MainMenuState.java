package com.test.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

import static com.test.Game.*;

/**
 * Created by Tom on 12/22/2015.
 */
@SuppressWarnings("DefaultFileTemplate")
public class MainMenuState extends State {
    private static final ArrayList<String> options=new ArrayList<>();
    private static int selector=0;
    private int titlePosY=0;
    private int optionsPosY =0;
    private float dtCursor = 0;
    private static GameStateManager gsm;


    public MainMenuState(GameStateManager gsm) {
        super(gsm);
        this.gsm=gsm;
        addOptionsToList();
        titlePosY=(int)((HEIGHT/3)*2);
        optionsPosY =(int)((HEIGHT/3));
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            incrementSelector();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            decrementSelector();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            if (selector == 0) {
                gsm.push(new GameState(gsm));
            }
            if (selector == 1) {
                gsm.push(new MultiplayerState(gsm));
            }
        }
    }

    private void addOptionsToList(){
        options.add("Start");
        options.add("Multiplayer");
        options.add("Exit");
    }

    public static void incrementSelector(){
        selector--;
        if(selector<0)selector=options.size()-1;
    }
    public static void decrementSelector() {
        selector++;
        if(selector>options.size()-1)selector=0;
    }
    public static void selectOption(){
        switch (selector){
            case(0):{
                gsm.push(new GameState(gsm));
                break;
            }case(1):{
                System.exit(0);
                break;
            }
        }
    }
    @Override
    public void update(float dt) {
        dtCursor+=dt;
        if(dtCursor>5*ft) {
            dtCursor = 0;
            handleInput();
        }
    }
    @Override
    public void dispose() {
    }
    /////////////////////////////////////////////////////////////////////////////////////////
    //DRAWING FUNCTIONS
    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl20.glClearColor(0,0,0,1);
        sb.begin();
        sb.end();
        drawTitle(sb);
        drawOptions(sb);
    }
    private void drawTitle(SpriteBatch sb){
        sb.begin();
        String title="-TEST AREA-";
        font.setColor(Color.WHITE);

        font.draw(sb,title, WIDTH/2,titlePosY-100);
        sb.end();
    }
    private void drawOptions(SpriteBatch sb){
       /* sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.WHITE);
        sr.rect(WIDTH/2+100,optionsPosY-(selector*20),10,10);
        sr.reset();*/
        sb.begin();
        for(int i=0;i<options.size();i++){
            if(selector==i)
                font.setColor(Color.BLUE);
            else
                font.setColor(Color.WHITE);

            font.draw(sb,options.get(i),WIDTH/2+100,optionsPosY-(i*20));
        }
        sb.end();
    }

}
