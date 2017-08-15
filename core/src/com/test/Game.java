package com.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.test.states.GameStateManager;
import com.test.states.MainMenuState;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Game extends ApplicationAdapter {
    private static final BitmapFont[] fonts = new BitmapFont[6];
    public static float ft = .01666666f;
    public static int WIDTH, HEIGHT;
    static GameStateManager gsm;
    public static BitmapFont font;
    static DecimalFormat nFormat =(DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
    public static  Animation<TextureRegion> playerPics;
    SpriteBatch batch;


    public static String formatMoney(int n){
        return nFormat.format(n);
    }


    @Override
    public void create() {
        nFormat.setGroupingUsed(true);
        nFormat.setGroupingSize(3);
        gsm = new GameStateManager();
        font = new BitmapFont();
        fonts[0]=createFont(8);
        fonts[1]=createFont(10);
        fonts[2]=createFont(12);
        fonts[3]=createFont(14);
        fonts[4]=createFont(16);
        fonts[5]=createFont(20);
        loadImages();


        Gdx.graphics.setWindowedMode(1200, 700);
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        batch = new SpriteBatch();
        gsm.push(new MainMenuState(gsm));
    }
    public static void setFontSize(int x){
        font=fonts[x];

    }
    public static BitmapFont getFont(){

        return font;
    }
    private static BitmapFont createFont(int x){
        BitmapFont temp=new BitmapFont();

        try {
            FreeTypeFontGenerator generator= new FreeTypeFontGenerator(Gdx.files.internal("fonts\\prstart.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter= new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = x;
            temp = generator.generateFont(parameter);
            //console("Font Generated");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return temp;
    }
    void loadImages(){
        playerPics= loadSpriteSheet("players.png",9,3);
    }

    private Animation loadSpriteSheet(String s, int x, int y) {
        Texture sheet = new Texture(Gdx.files.internal(s));
        int  spw=sheet.getWidth() / x,
                sph=sheet.getHeight() / y,
                n=x*y,
                c=0;
        TextureRegion[][] tmp = TextureRegion.split(sheet,spw,sph);
        TextureRegion[] frames = new TextureRegion[n];

        for(int i=0;i<n;i++){
            frames[i]=tmp[c][i%x];
            if(i%x==x-1) {
                c++;
            }
        }
        return new Animation<>(1, frames);
    }


    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(batch);
    }

    @Override
    public void dispose() {
        gsm.clear();
    }
}
