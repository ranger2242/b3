package com.test.states;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Brent on 6/26/2015.
 */
@SuppressWarnings({"ALL", "EmptyMethod"})
abstract class State {
    static OrthographicCamera cam;
    GameStateManager gsm;

    State(GameStateManager gsm){
        this.gsm = gsm;
        cam = new OrthographicCamera();
        cam.setToOrtho(true);
        Vector3 mouse = new Vector3();
    }

    protected abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    public abstract void dispose();
}
