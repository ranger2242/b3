package com.test.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.test.*;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Random;

import static com.test.Game.*;
import static com.test.Main.deck;
import static com.test.Main.outc;
import static com.test.Player.players;

@SuppressWarnings("SameParameterValue")
public class GameState extends State {
    private static final Random rn= new Random();
    static final ShapeRendererExt sr = new ShapeRendererExt();
    public static final ArrayList<Integer> roundQueue = new ArrayList<>();
    public static final ArrayList<Integer> drawQueue = new ArrayList<>();
    public static final ArrayList<String> output = new ArrayList<>();
    static final ArrayList<Icon> shopIcon= new ArrayList<>();
    public static Player dealer;
    ArrayList<Button> bList= new ArrayList<>();
    private Button hitButton;
    private Button standButton;
    private Button nextRoundButton;
    private Button splitButton;
    private Button doubleButton;
    private Button insureButton;

    private final Card movingCard = new Card(1, Card.Suits.Clubs);
    public static final Vector2 mpos=new Vector2();
    public static final Vector2 mid=new Vector2(WIDTH/2,HEIGHT/2);
    static int prevCl =0;
    int prevQ=0;
    private static float dtLag=0;
    private static float dtAce=0;
    private float flAng=0;
    private static float dtBlink=0;
    private static float dtShake=0;
    private static boolean blink=false;
    private static boolean runResults = true;
    public static boolean lag=false;
    private static boolean shakeCam=false;



    GameState(GameStateManager gsm) {
        super(gsm);
        initCamera();
        initShopPrices();
        sr.setAutoShapeType(true);
        initButtons();
        Player.initPlayers();
        startRound();
    }



    @Override
    public void dispose() {

    }
    //calc functions-----------------------------------------------------------------------------------------------------------------
    void initCamera(){
        cam.setToOrtho(false, WIDTH, HEIGHT);
        cam.position.set( WIDTH/2,HEIGHT/2,0);
    }
    private void initButtons(){
        Texture btn=new Texture("button.png");
        int     w=btn.getWidth(),
                h=btn.getHeight();

        nextRoundButton     = new Button(btn,w,h);
        hitButton           = new Button(btn,w,h);
        standButton         = new Button(btn,w,h);
        splitButton         = new Button(btn,w,h);
        doubleButton         = new Button(btn,w,h);
        insureButton         = new Button(btn,w,h);
        bList.add(nextRoundButton);
        bList.add(hitButton      );
        bList.add(standButton    );
        bList.add(splitButton    );
        bList.add(doubleButton   );
        bList.add(insureButton   );

        insureButton.setType(6);
        doubleButton.setType(5);
        splitButton.setType(4);
        nextRoundButton.setType(3);
        hitButton.setType(2);
        standButton.setType(1);
    }
    private void initShopPrices(){
        for (int i = 0; i < playerPics.getKeyFrames().length; i++) {
            Icon ic = new Icon(playerPics.getKeyFrame(i));
            int x = (int) (Math.pow(1.5, i) * 1000);
            String s1 = x + "";
            int e = (int) Math.pow(10, s1.length() - 2);
            x /= e;
            x *= e;
            if (i == 0)
                ic.setPrice(10000000);
            else
                ic.setPrice(x);

            shopIcon.add(ic);
        }
    }
    public static void startRound(){
        runResults =true;
        deck.prepare();
        placeBets();
        waitForBets();
        dealInitalRound();
        //queueNextRound();
    }
    static void dealInitalRound(){
        int n=(players.size()  * 2)+1; //two for everybody
        for (int j = 1; j < n; j++) {
            drawQueue.add(j % players.size());
        }
    }
    private static void placeBets(){
        for (int i = 1; i < players.size(); i++) {
            if(players.get(i).hasMoney()) {
                Player p = players.get(i);
                MyTextInputListener listener = new MyTextInputListener(1);
                listener.setP(i);
                Gdx.input.getTextInput(listener, i + " " + p.getName() + "'s bet : $" + formatMoney(p.getMoney()), "", "");

            }
        }
    }
    private static void waitForBets(){
        boolean trip=true;
        while (trip){
            boolean a=true;
            for(Player p:players) {
                if (!p.isDealer()) {
                    if (p.getBet() == 0)
                        a = false;
                }
            }
            if(a){
                trip=false;
            }

        }
    }
    private static void checkNaturalsAndBust(){
        if(drawQueue.isEmpty()) {
            for (Player p : players) {
                if (p.isFirstTurn()) {
                    if (p.getHand().checkNaturals()) {
                        p.setNatural1(true);
                    }
                    if (p.isSplit() && p.getHand2().checkNaturals()) {
                        p.setNatural2(true);
                    }

                    if (dealer.isNatural1()) {
                        if (p.isNatural1())
                            p.win(1, 1);
                        else
                            p.lose(1);
                    } else {
                        if (p.isNatural1()) {
                            p.win(1, 1.5);
                        }
                    }
                    if (p.isSplit()) {
                        if (dealer.isNatural1()) {
                            if (p.isNatural2())
                                p.win(2, 1);
                            else
                                p.lose(2);
                        } else {

                            if (p.isNatural2()) {
                                p.win(2, 1.5);
                            }
                        }
                    }
                    p.setFirstTurn(false);
                }

                //check bust or win

                if (!p.isDealer()) {
                    if (p.isR1Over()) {
                        p.setSecTurn(true);
                    }
                    if (p.isR2Over()) {
                        p.setSecTurn(false);
                    }
                    if (!p.isR1Over()) {
                        if (p.getHand().getTotal() == 21) {
                            if (p.getHand().hasBlackJack())
                                p.win(1, 2);
                            else
                                p.win(1, 1.5);

                        }

                        if (p.getHand().getTotal() > 21) {
                            shakeCam = true;
                            outc(p.getName() + " busted!");
                            p.lose(1);
                        }
                    }
                    if (p.isSplit() && !p.isR2Over()) {

                        if (p.getHand2().getTotal() == 21) {
                            if (p.getHand2().hasBlackJack())
                                p.win(2, 2);
                            else
                                p.win(2, 1.5);
                        }
                        if (p.getHand2().getTotal() > 21) {
                            shakeCam = true;
                            outc(p.getName() + " busted!");
                            p.lose(2);
                        }
                        if (p.getHand().getDeck().get(0).isAce() && p.getHand2().getDeck().get(0).isAce()) {
                            p.stand2();
                            p.stand1();
                        }
                    }
                }

                if (p.isRoundover()) {
                    if (roundQueue.contains(p.getOrder())) {
                        Integer i = p.getOrder();
                        roundQueue.remove(i);
                    }
                }
            }
        }
    }
    private void dealCardAnimation(int i){
        if(i!=0) {
            Rectangle   cardR=movingCard.getShape(),
                        pR=players.get(Math.abs(i)).getRect();
            Vector2 posa = new Vector2(cardR.x, cardR.y),
                    posb = new Vector2(pR.x, pR.y);

            if (posa.dst(posb) < 10) {
                endOfAnim(i);
            } else {
                float   a = (float) Math.toRadians(EMath.angle(posb, posa)),
                        rate= 15,
                        x = (float) (rate * Math.cos(a)),
                        y = (float) (rate * Math.sin(a));
                movingCard.move(new Vector2(x, y));
            }
        }else
            endOfAnim(i);
    }
    private void endOfAnim(int i){
        drawQueue.remove(0);
        if(i!=0) {//if not dealer
            if(i>0)
            players.get(i).drawCard();
            else{
                players.get(Math.abs(i)).drawCard2();
            }
        }else
            dealer.drawCard();
        movingCard.getShape().setPosition(WIDTH/2,HEIGHT/4*3);
    }
    public static void queueNextRound(){
        if (roundQueue.isEmpty()) {
            for (Player p : players) {
                if (!p.isDealer() && !p.isRoundover()) {
                    roundQueue.add(players.indexOf(p));
                }
            }
        }
    }
    private void runDealerTurns(){
        dealer.getHand().dealertotal();
        int dt= dealer.getHand().getTotal();
        if (dt<17 && drawQueue.isEmpty())
            drawQueue.add(0);
        if(dt>=17)
            dealer.setRoundover(true);
    }
    private void dealerResult(){
        int dt= dealer.getHand().getTotal();
        for(Player p:players){
            p.flipCards();
        }
        if(dt>21){ //dealer bust and all still in are winners
            shakeCam=true;
            for (Player p : players) {
                if (!p.isDealer()) {
                    if (!p.isLost())
                        p.win(1, 1.5);
                    if (p.isSplit() && !p.isLost2())
                        p.win(2, 1.5);
                }
            }
        }else {//dealer doesnt bust and checks all hands in
            for(Player p: players){
                if(!p.isDealer()) {
                    if (!p.isWon() || !p.isLost()) {
                        if (dt > p.getHand().getTotal()) {
                            p.lose(1);
                        }
                        if (dt < p.getHand().getTotal()) {
                            p.win(1, 1.5);
                        }
                        if (dt == p.getHand().getTotal()) {
                            p.win(1, 1);
                        }
                    }
                    if (p.isSplit() &&( !p.isWon2() ||!p.isLost2())) {
                        if (dt > p.getHand2().getTotal()) {
                            p.lose(2);
                        }
                        if (dt < p.getHand2().getTotal()) {
                            p.win(2, 1.5);
                        }
                        if (dt == p.getHand2().getTotal()) {
                            p.win(2, 1);
                        }
                    }

                }
            }
        }
        for(Player p: players) {
            if (dealer.getHand().getDeck().get(0).getValue() == 10 && p.isInsured()) {
                p.winIns();
            }
            if (dealer.getHand().getDeck().get(0).getValue() != 10 && p.isInsured()) {
                p.loseIns();
            }
        }
        //disp();
    }
    public static Vector2 getPosition(int i){
        int     n= players.size()-1;
        float   q= (float) Math.toRadians(((180/(n+1))*(i))+180);
        int     r=WIDTH/3,
                x= (int) (r* Math.cos(q))-(22),
                y= (int) (r* Math.sin(q))+200;
        if(players.size()>4) y-=100;
        return new Vector2(x, y);
    }
    public static int getFromRoundQueue(){
        if(roundQueue.isEmpty())
            return -1;
        else
            return roundQueue.get(0);
    }
    int getFromDrawQueue(){
        if(drawQueue.isEmpty())
            return -1;
        else
            return drawQueue.get(0);
    }


    //update functions-----------------------------------------------------------------------------------------------------------------
    @Override
    protected void handleInput() {
        updateMousePos();
        if(Gdx.input.isKeyPressed(Input.Keys.F1)){
            gsm.push(new PlayerSelectState(gsm));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            drawQueue.add(1);
        }
        //check clicks on players shit
        for(Player p: players){
            if(!p.isDealer()){
                if(p.isClicked()){//check if clicked and go to shop
                    prevCl =p.getOrder();
                    gsm.push(new ShopState(gsm));
                }
                if(p.hasAce()) {//check if aces are clicked
                    for(Card c:p.getAces()){
                        if (c.isClicked() && dtAce > 10 * ft) {
                            c.setAceHigh(!c.isAceHigh());
                            dtAce = 0;
                        }
                    }
                }
                p.getHand().total();
            }
        }
    }
    @Override
    public void update(float dt) {
        handleInput();
        checkNaturalsAndBust();
        displayCurrentTurn();
        queueNextRound();
        updateVariables(dt);

        if(!drawQueue.isEmpty())
            dealCardAnimation(getFromDrawQueue());

        if(!lag)
            updateButtons(mpos);

        if(Player.allPlayersFinished())
            runDealerTurns();

        if(dealer.isRoundover() && runResults){
            dealerResult();
            for(int i=1;i<players.size();i++){
                players.get(i).endRound();

            }
            runResults =false;
        }
        ArrayList<Integer> toRemove=new ArrayList<>();
        for(int i=0;i<players.size();i++) {
            if (!players.get(i).hasMoney()) {
                outc(players.get(i).getName() + " has no money and will be removed.");
                toRemove.add(i);
                players.remove(i);

            }

        }
        updateCamPosition();

    }
    public static void updateVariables(float dt){
        dtBlink+=dt;
        dtAce+=dt;
        if(lag){
            dtLag+=dt;
            if(dtLag>10 *ft){
                lag=false;
                dtLag=0;
            }
        }
        if(shakeCam){
            dtShake+=dt;
            if(dtShake>.3f) {
                shakeCam = false;
                dtShake=0;
            }
        }
        //blink
        if(dtBlink>10*ft){
            blink=!blink;
            dtBlink=0;
        }
    }
    public void displayCurrentTurn(){
        if(drawQueue.isEmpty() && !roundQueue.isEmpty() && prevQ!= getFromRoundQueue()){
            prevQ= getFromRoundQueue();
            outc(players.get(prevQ).getName()+"'s turn.");
        }
    }
    private static void updateCamPosition() {
        int force=10;
        Vector3 position = cam.position,
                v=new Vector3(mid.x,mid.y,0),
                shift=new Vector3((float) (force * rn.nextGaussian()),(float) (force * rn.nextGaussian()),0);
            if (shakeCam) {
                v.add(shift);
                position.lerp(v, .5f);
            } else
                position.lerp(v, .2f);
        cam.position.set(position);
        cam.update();
    }

    static void updateMousePos(){
        int x = -1, y = -1;
        if (Gdx.input.isButtonPressed(0)) {
            x = Mouse.getX();
            y = Mouse.getY();
            lag=true;
        }
        mpos.set(x,y);
    }
    private void updateButtons(Vector2 m){
        hitButton.update(m.x,m.y);
        standButton.update(m.x,m.y);
        nextRoundButton.update(m.x,m.y);
        splitButton.update(m.x,m.y);
        doubleButton.update(m.x,m.y);
        insureButton.update(m.x,m.y);
    }


    //render functions-----------------------------------------------------------------------------------------------------------------
    @Override
    public void render(SpriteBatch sb) {
        float ff=.4f;
        Gdx.gl20.glClearColor(0.316f*ff, 0.253f*ff, 0.604f*ff, 1);
        sr.setProjectionMatrix(cam.combined);
        sb.setProjectionMatrix(cam.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin();
        sr.set(ShapeRenderer.ShapeType.Line);
        srdrawCoolShit(new Vector2(mid.x,mid.y),mid.x,3);
        sr.set(ShapeRenderer.ShapeType.Filled);
        sr.setColor(.2f,.2f,.2f,.7f);
        sr.rect(new Rectangle(WIDTH/3,HEIGHT/3*2,WIDTH/3*2 ,HEIGHT/3));
        sr.set(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        sr.rect(new Rectangle(WIDTH/3,HEIGHT/3*2,WIDTH/3*2 +2,HEIGHT/3 +2));

        srdrawMovingCard();

        srdrawPlayers();
        srdrawAllCards();


        sr.end();

        sb.begin();
        sbdrawPlayers(sb);
        sbdrawPlayerInfo(sb);
        sbdrawButtons(sb);
        sbdrawStringOutput(sb);
        sbdrawAllCardText(sb);
        sb.end();
    }

    private void srdrawCoolShit(Vector2 pos, float r, int dep) {
        float mmm = (.8f * (dep + 1));
        sr.setColor(new Color(0.316f * mmm, 0.253f * mmm, 0.604f * mmm, .2f));
        sr.circle(pos.x, pos.y, r);
        flAng += .0000005;
        ArrayList<Vector2> node = new ArrayList<>();
        int n = 7;
        for (int i = 0; i < n; i++) {
            float a;
            a = (float) ((flAng * (dep * (i + 1))) + Math.toRadians(360f / n * i));

            float x = (float) (r / 2 * (Math.cos(a)));
            float y = (float) (r / 2 * (Math.sin(a)));
            Vector2 v = new Vector2(pos.x + x, pos.y + y);
            Vector2 v2 = new Vector2(pos.x - x, pos.y - y);
            node.add(v);
            //v.add(a,a);
            sr.circle(v.x, v.y, r / 9);
            sr.circle(v2.x, v2.y, r / 6);

            if (dep - 1 > 0)
                srdrawCoolShit(v, r / 2f, dep - 1);
        }
        for (Vector2 v1 : node) {
            for (Vector2 v2 : node) {
                sr.line(new Line(v1, v2));
            }
        }
    }


    private void sbdrawRules(SpriteBatch sb){
        ArrayList<String> rules=new ArrayList<>();
        rules.add("BlackJack at 21");
        rules.add("Closest to 21 Wins 3:2");
        rules.add("Bust over 21. Click Ace to change value");
        rules.add("Split on pairs matches bet");
        rules.add("Ace Split ends round, Dealer Draws to 17");
        rules.add("Double Down at 9,10,11");
      //  font.draw(sb,,)
    }

    private void sbdrawButtons(SpriteBatch sb) {
        Game.getFont().setColor(Color.BLACK);
        ArrayList<Button> buttons = new ArrayList<>();
        ArrayList<String> bnames = new ArrayList<>();

        if (!roundQueue.isEmpty() &&getFromRoundQueue() <players.size() ) {
            if (!players.get(getFromRoundQueue()).isRoundover()) {
                buttons.add(hitButton);
                buttons.add(standButton);
                bnames.add("HIT");
                bnames.add("STAND");
            }
            if (players.get(roundQueue.get(0)).canDouble()) {
                buttons.add(doubleButton);
                bnames.add("DOUBLE");
            }
            if (players.get(roundQueue.get(0)).canSplitPair()) {
                buttons.add(splitButton);
                bnames.add("SPLIT");
            }
            if (players.get(roundQueue.get(0)).canInsure()) {
                buttons.add(insureButton);
                bnames.add("INSURANCE");
            }
        }

        if (Player.allPlayersFinished()) {
            buttons.add(nextRoundButton);
            bnames.add("NEXT ROUND");
        }
        for(Button b: bList){
            if(!buttons.contains(b)){
                b.setPos(new Vector2(0,0));
            }
        }
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setPos(new Vector2(30,  50 + (i * 40)));
            buttons.get(i).render(sb);
        }
        int c = 0;
        for (String s : bnames) {
            Game.getFont().draw(sb, s, buttons.get(c).getSkin().getX() + 20, buttons.get(c).getSkin().getY() + 20);
            c++;
        }
    }
    private void sbdrawStringOutput(SpriteBatch sb){
       /* for(HoverText t:HoverText.texts){
            t.draw(sb);
        }*/
        for (int i = 0; i < output.size() && i<10; i++) {
            try {
                Color c;
                if(i==output.size()-1){
                    if(blink)
                        c=Color.WHITE;
                    else
                        c=Color.GRAY;
                }
                else
                    c=Color.GRAY;
                font.setColor(c);
                font.draw(sb, output.get(i), (2*WIDTH/3) - 30, HEIGHT - (i * 20)-30);
            } catch (IndexOutOfBoundsException|NullPointerException ignored) {
            }
        }
    }
    private void sbdrawPlayerInfo(SpriteBatch sb){
        Game.setFontSize(2);
        for(Player p : players) {
            font.setColor(Color.WHITE);
            if(p.isDealer()) {
                Game.getFont().draw(sb, dealer.getName(), mid.x - 100, mid.y + 200);
                if(dealer.isRoundover())
                font.draw(sb, "Total:" + dealer.getHand().getTotal(), mid.x - 100, mid.y+ 180);
            }else {
                int ind=players.indexOf(p);
                Boolean b=players.get(ind).isSplit();
                Vector2 pos = getPosition(players.indexOf(p));
                ArrayList<String> info=p.getInfo();
                if(b){
                    pos.y+=100;
                }
                pos.y+=140;
                for(int i=0;i<info.size();i++){
                    Game.getFont().draw(sb,info.get(i),mid.x + pos.x, mid.y + pos.y -(i*20));
                }
            }
        }
    }
    private void srdrawInfoBox(Vector2 v,Boolean b){
        int dh=150;
        if(b)
            dh+=100;
        sr.set(ShapeRenderer.ShapeType.Filled);
        sr.setColor(.2f,.2f,.2f,.7f);
        sr.rect(v.x-10,v.y,150,dh);
        sr.set(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        sr.rect(v.x-10,v.y,150,dh);


    }
    private void sbdrawAllCardText(SpriteBatch sb){
        font.setColor(Color.WHITE);
        Vector2 d=new Vector2(-(dealer.getHand().getDeck().size()*22)/2,130);
        for(Player p: players){
            if(p.isDealer()) {
                for (int j = 0; j < p.getHand().getDeck().size(); j++) {
                    if(dealer.isRoundover() &&j==0) {
                        sbdrawSingleCardText(sb, p.getHand().getDeck().get(j), d, j);
                    }else if(j>0)
                        sbdrawSingleCardText(sb, p.getHand().getDeck().get(j), d, j);
                }
            }else{
                Vector2 pos = getPosition(players.indexOf(p));
                pos.y += 10;
                for (int j = 0; j < p.getHand().getDeck().size(); j++) {
                    if(!p.getHand().getDeck().get(j).isFaceDown())
                         sbdrawSingleCardText(sb,p.getHand().getDeck().get(j),pos,j);
                }
                pos.add(0,60);
                for (int j = 0; j < p.getHand2().getDeck().size(); j++) {
                    if(!p.getHand2().getDeck().get(j).isFaceDown())
                        sbdrawSingleCardText(sb,p.getHand2().getDeck().get(j),pos,j);
                }
            }
        }
    }
    private void sbdrawSingleCardText(SpriteBatch sb, Card c, Vector2 pos, int index){
        setFontSize(0);
        font.draw(sb,c.getName2(),mid.x + pos.x + (22 * index),mid.y+ pos.y+15);
        setFontSize(2);
    }
    private void sbdrawPlayers(SpriteBatch sb) {
        for (Player player : players) {
            Vector2 v = new Vector2(player.getRect().x, player.getRect().y);
            sb.draw(playerPics.getKeyFrame(player.getPic()), v.x, v.y);
        }
    }
    private void srdrawMovingCard(){
        sr.setColor(Color.WHITE);
        sr.rect(movingCard.getShape());
    }
    private void srdrawAllCards(){
        for(Player p: players){
            if(p.isDealer()) {
                Vector2 pos=new Vector2(mid);
                pos.x-= (p.getHand().getDeck().size()*22)/2;
                pos.y+=120;
                for (int j = 0; j < dealer.getHand().getDeck().size(); j++) {
                    srdrawSingleCard(pos,j);
                }
            }else{
                int i = players.indexOf(p);
                Vector2 pos = getPosition(i);
                pos.y += 10;
                pos.add(mid);
                if (p.isSplit()) {
                    sr.set(ShapeRenderer.ShapeType.Filled);
                    sr.setColor(p.getColor());
                    sr.rect(pos.x - 50, pos.y, 10, 10);
                    sr.setColor(p.getColor2());
                    sr.rect(pos.x - 50, pos.y + 60, 10, 10);

                    sr.set(ShapeRenderer.ShapeType.Line);
                    sr.setColor(Color.WHITE);

                    sr.rect(pos.x - 50, pos.y, 10, 10);
                    sr.rect(pos.x - 50, pos.y + 60, 10, 10);


                }
                for (int j = 0; j < p.getHand().getDeck().size(); j++) {
                    p.setCardPos(new Vector2(pos.x+(22*j),pos.y),j);
                    srdrawSingleCard(pos,j);
                }
                pos.add(0,60);
                for (int j = 0; j < p.getHand2().getDeck().size(); j++) {
                    p.setCardPos2(new Vector2(pos.x+(22*j) ,pos.y),j);
                    srdrawSingleCard(pos,j);
                }
            }
       }

    }
    private void srdrawPlayers() {

        for (int i =0; i < players.size() ; i++) {
            sr.set(ShapeRenderer.ShapeType.Filled);
            if(players.get(i).isSplit())
                sr.setColor(Color.ORANGE);
            else
                sr.setColor(players.get(i).getColor());
            sr.rect(players.get(i).getRect());
            Vector2 v=getPosition(i);
            v.add(mid);
            if(!players.get(i).isDealer())
            srdrawInfoBox(v,players.get(i).isSplit());
        }
    }
    private void srdrawSingleCard(Vector2 pos, int index){
        sr.setColor(Color.WHITE);
        sr.rect( pos.x + (22 * index),  pos.y, 20, 30);
    }


}
