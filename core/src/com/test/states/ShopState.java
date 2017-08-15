package com.test.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.test.Icon;
import com.test.Player;
import org.lwjgl.input.Mouse;

import static com.test.Game.*;
import static com.test.Player.players;
import static com.test.states.GameState.*;

/**
 * Created by Chris Cavazos on 8/10/2017.
 */
public class ShopState extends State {
    int icHover=0;
    public ShopState(GameStateManager gsm) {
        super(gsm);
        initCamera();
    }
    void initCamera(){
        cam.setToOrtho(false, WIDTH, HEIGHT);
        cam.position.set( WIDTH/2,HEIGHT/2,0);
    }

    boolean disp =false;
    @Override
    protected void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.TAB)){
            gsm.pop();
        }
        disp=false;
        for(Icon ic : shopIcon){
            if(ic.getRect().contains(new Vector2(Mouse.getX(),Mouse.getY()))){
                disp=true;
                icHover=shopIcon.indexOf(ic);
            }
            if(ic.getRect().contains(mpos)){
                int ind=shopIcon.indexOf(ic);
                if(players.get(prevCl).getPicsOwned().contains((Integer)ind)) {
                    players.get(prevCl).setPic(ind);
                }else{
                    if(players.get(prevCl).getMoney()>=ic.getPrice()){
                        players.get(prevCl).setPic(ind);
                        players.get(prevCl).addPics(ind);
                        players.get(prevCl).addMoney(-ic.getPrice());
                    }
                }
            }
        }
    }

    @Override
    public void update(float dt) {
        updateMousePos();
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Player p=players.get(prevCl);
        Vector2 pos =new Vector2(30,HEIGHT-20);
        sb.begin();
        //draw player info
        font.setColor(Color.WHITE);
        font.draw(sb, p.getName(), pos.x, pos.y );
        font.draw(sb, "Bet:" + formatMoney(p.getBet()),  pos.x,  pos.y -20);
        font.draw(sb, "Money:" + formatMoney(p.getMoney()),  pos.x, pos.y -40);
        font.draw(sb, "Total:" + p.getHand().getTotal(), pos.x,  pos.y -60);
        sb.draw(playerPics.getKeyFrame(p.getPic()),pos.x,pos.y-80-48);
        //draw player stats
        pos.set(pos.x,pos.y-80-58);
        font.draw(sb,"Wins:"+ p.getWins(), pos.x, pos.y-20 );
        font.draw(sb,"Losses:"+ p.getLosses(), pos.x, pos.y-40 );
        font.draw(sb,"Total Money Lost:"+ formatMoney(p.getTotalLosses()), pos.x, pos.y -60);
        font.draw(sb,"Total Winnings:"+formatMoney( p.getTotalWinnings()), pos.x, pos.y-80 );
        font.draw(sb,"Previous Bets:", pos.x, pos.y );
        for(Integer in:p.getPrevBets()){
            int z=p.getPrevBets().indexOf(in);
            font.draw(sb,z+": $"+ in, pos.x, pos.y );
        }
        int co=0;
        for(int i=0;i<3;i++) {
            for (int j = 0; j < 9; j++) {
                if(co<shopIcon.size()) {
                    Icon q = shopIcon.get(co);
                    pos.set(WIDTH - 80 - (j * 80), HEIGHT - 100 - (i * 100));
                    shopIcon.get(co).setPos(new Vector2(pos));
                    sb.draw(q.getPic(), pos.x, pos.y);
                }
                co++;
            }
        }
        if(disp){
            font.draw(sb,""+formatMoney(shopIcon.get(icHover).getPrice()),30,30);
        }

        sb.end();
        sr.begin();
        sr.set(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.GREEN);
        int c=0;
        for(int i=0;i<3;i++) {
            for (int j = 0; j < 9; j++) {
                if(c<shopIcon.size()) {
                    pos.set(WIDTH - 80 - (j * 80), HEIGHT - 100 - (i * 100));
                    if(players.get(prevCl).getPicsOwned().contains(c)){
                        sr.rect(new Rectangle(pos.x,pos.y,50,50));
                    }
                }
                c++;
            }
        }
        sr.end();
    }

    @Override
    public void dispose() {

    }
}
