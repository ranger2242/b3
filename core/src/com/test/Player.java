package com.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import static com.test.Game.HEIGHT;
import static com.test.Game.WIDTH;
import static com.test.Game.formatMoney;
import static com.test.states.GameState.*;
import static com.test.Main.outc;


public class Player implements Serializable {
    public static ArrayList<Player> players = new ArrayList<>();

    private String name ="Player";
    private Hand hand = new Hand();
    private Hand hand2 = new Hand();
    transient private Color color = Color.BLACK;
    private int wins=0;
    private int losses=0;
    private ArrayList<Integer> prevBets=new ArrayList<>();
    private ArrayList<Integer> picsOwned=new ArrayList<>();
    private ArrayList<Boolean> prevGames=new ArrayList<>();
    private int totalWinnings=0;
    private int totalLosses=0;
    private int pic=0;
    private int order=0;
    private int money=10000;
    private int bet=0;
    private int insurance=0;
    private int winnings=0;
    private boolean drawDown=false;
    private boolean isInsured=false;
    private boolean isSplit=false;
    private boolean secTurn =false;
    private boolean r1over=false;
    private boolean r2over=false;
    private boolean gameover=false;
    private boolean isDealer=false;
    private boolean roundover=false;
    private boolean natural1 = false;
    private boolean natural2 = false;
    private boolean lost    = false;
    private boolean won=false;
    private boolean lost2    = false;
    private boolean won2=false;
    private boolean firstTurn    = true;
    private boolean isDoubled =false;
    Rectangle rect= new Rectangle(0,0,0,0);



    public Player(String name, int order) {
        this.name = name;
        this.order = order;
    }
    public Player(String name, int order,boolean isDealer) {
        this.name = name;
        this.order = order;
        this.isDealer=isDealer;
    }

    public Player(String name, boolean isDealer) {
        this.name = name;
        this.isDealer=isDealer;
    }

    public static void initPlayers(){
        players.add(new Player("DEALER", 0, true));
        players.add(new Player("chris", 1));
        players.add(new Player("schwank", 2));
        // players.add(new Player("schwank", 2));

        dealer = players.get(0);

        try {
            FileHandler.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //calculate player pos
        for (int i = 1; i < players.size(); i++) {
            Vector2 v = getPosition(i);
            Rectangle rect = new Rectangle(WIDTH / 2 + v.x - 10, HEIGHT / 2 + v.y - 70, 50, 50);
            players.get(i).setRect(rect);
            players.get(i).setPic(1);
        }
        //calculate dealer pos
        dealer.setRect(new Rectangle(WIDTH / 2, HEIGHT / 4 * 3, 20, 20));
        dealer.setPic(0);
    }

    public static void delete() {
        Main.out("Please choose a number: ");
        for(int i=0;i<players.size();i++){
            Main.out((i+1)+": "+players.get(i).name);
        }
        int in=Main.getIntInput();
        if(in>=1 && in<=players.size()){
            String name=players.get(in-1).name;
           players.remove(in-1);
            Main.out("--"+name+" Deleted--");
        }
        for(int i=0;i<players.size();i++){
            players.get(i).order=i;
        }
        Main.mainmenu.print();
    }
    public static void register() {
        Main.out("Please enter name: ");
        String in=Main.getStringInput();
        if(in.length()>20)
            in= in.substring(0,20);
        Player p=new Player(in,players.size());
        players.add(p);
        Main.out("--"+in+" Added--");
        Main.mainmenu.print();

    }
    public void printBalance(){
        outc(name+": $"+money);
    }
    public void drawCard(){
        hand.drawCard(drawDown);
        outc(name+" recieved a card");
        if(drawDown){
            drawDown=false;
        }
    }
    public void drawCard2() {
        hand2.drawCard(drawDown);
        outc(name+" recieved a card");
        if(drawDown){
            drawDown=false;
        }
    }
    public void placeBet(){
        //printBalance();
        // Main.out("Place your bet : ");
        int bet=this.bet;
        if(money>0) {
            if (bet <= money) {
               // money -= bet;
                this.bet = bet;
            }
            else{
              //  Main.out("Insufficient funds!");

                placeBet();
            }
        }else{
            gameover=true;
            gameOver();
        }
    }
    public void turn(){
        Main.out(Main.DIV);
        Menu turnMenu=new Menu(1,this);
        turnMenu.print();
    }
    public void split() {
        isSplit=true;

        hand2.deck.add(hand.deck.get(0));
        hand.deck.remove(0);
        drawQueue.add(order);
        drawQueue.add(-order);


    }
    public void stand1(){
        r1over=true;
    }
    public void stand2(){
        r2over=true;
    }
    public void win(int r, double v) {
        if (!won && !lost) {
            winnings = (int) (v * bet);
            if (r == 1) {
                won = true;
                r1over = true;
            }
        }
        if (!won2 && !lost2) {

            if (r == 2) {
                won2 = true;
                r2over = true;
            }
        }
    }
    public void lose(int r) {
        if (!won && !lost) {
            if (r == 1) {
                r1over = true;
                lost = true;
            }
        }
        if (!won2 && !lost2) {

            if (r == 2) {
                r2over = true;
                lost2 = true;
            }
        }
    }
    public void endRound(){
        if(won){
            outc(name+" won "+winnings+" ("+money+")->"+(money+=winnings));
            wins++;
            prevGames.add(true);
            totalWinnings+=winnings;
        }if(lost){
            outc(name+" lost "+bet+" ("+money+")->"+(money-=bet));
            losses++;
            prevGames.add(false);
            totalLosses+=bet;
        }
        if(isSplit()) {
            if (won2) {
                outc(name+" won "+winnings+" ("+money+")->"+(money+=winnings));
                wins++;
                prevGames.add(true);
                totalWinnings += winnings;
            }
            if (lost2) {
                outc(name+" lost "+bet+" ("+money+")->"+(money-=bet));
                losses++;
                prevGames.add(false);
                totalLosses += bet;
            }
        }
    }
    public void reset(){
        prevBets.add(bet);
        insurance=0;
        bet=0;
        hand.reset();
        hand2.reset();
        drawDown=false;
        isDoubled=false;
        lost=false;
        won=false;
        lost2=false;
        won2=false;
        roundover=false;
        natural1=false;
        natural2=false;
        r2over=false;
        r1over=false;
        isSplit=false;
        secTurn=false;
        firstTurn=true;

    }
    public void gameOver(){
        Main.out(name+" lost all their money.");
        Main.out(name+" will be removed from game.");
    }
    public void flipCards(){
        hand.allUp();
        hand2.allUp();
    }
    public void setR1over(boolean r1over) {
        this.r1over = r1over;
    }

    public void setR2over(boolean r2over) {
        this.r2over = r2over;
    }

    public static boolean allPlayersFinished(){
        boolean a=true;
        for(Player p : players){
            if(!p.isDealer)
                if(!p.isRoundover())
                    a=false;
        }
        return a;
    }
    public boolean isClicked(){return rect.contains(mpos);}
    public boolean isSplit() {
        return isSplit || !hand2.deck.isEmpty();
    }
    public boolean isSecTurn() {
        return secTurn;
    }
    public boolean isDealer() {
        return isDealer;
    }
    public boolean isRoundover() {
        boolean a=isSplit && r1over &&r2over;
        boolean b=!isSplit && r1over;
        boolean c=isDoubled;
        return a || b || c || roundover;
    }
    public boolean isGameover() {
        return gameover;
    }
    public boolean isNatural1() {
        return natural1;
    }
    public boolean isNatural2() {
        return natural2;
    }
    public boolean isInsured(){
        return isInsured;
    }
    public boolean isLost() {
        return lost;
    }
    public boolean isFirstTurn() {
        return firstTurn;
    }
    public boolean isR1Over() {
        return r1over;
    }
    public boolean isR2Over() {
        return r2over;
    }
    public boolean isLost2() {
        return lost2;
    }
    public boolean isWon() {
        return won;
    }
    public boolean isWon2() {
        return won2;
    }
    public boolean isDoubled(){
        return isDoubled;

    }
    public boolean canInsure(){
        if(dealer.getHand().deck.size()>1 && !isInsured)
        return money>bet && dealer.getHand().deck.get(1).isAce();
        else
            return false;
    }
    public boolean canDouble(){
        return money >=2*bet&&  getHand().getTotal()>=9 &&getHand().getTotal()<=11 && !isDoubled;
    }
    public boolean canSplitPair(){
        if(money >=2*bet && hand.deck.size()==2){
            if(hand.deck.get(0).value==hand.deck.get(1).value){
                return true;
            }else
                return false;
        }else return false;
    }
    public boolean hasAce(){
        return hand2.hasAce || hand.hasAce;
    }
    public boolean hasMoney(){
        return money>0;
    }


    public void setDoubled(boolean b){
        isDoubled =b;
        bet*=2;
        drawDown=true;
        if(isSplit() && isSecTurn())
            r2over=true;
        else
            r1over=true;
    }
    public void setInsured(Boolean b){
        isInsured=b;
    }
    public void setCardPos(Vector2 pos, int j){
        hand.deck.get(j).setPos(pos);
    }
    public void setRect(Rectangle rect) {
        this.rect = rect;
    }
    public void setNatural1(boolean natural) {
        this.natural1 = natural;
    }
    public void setNatural2(boolean natural) {
        this.natural2 = natural;
    }
    public void setSplit(boolean split) {
        isSplit = split;
    }
    public void setSecTurn(boolean secTurn) {
        this.secTurn = secTurn;
    }
    public void setRoundover(boolean roundover) {
        this.roundover = roundover;
    }
    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }
    public void setWins(int wins) {
        this.wins = wins;
    }
    public void setLosses(int losses) {
        this.losses = losses;
    }
    public void setPrevBets(ArrayList<Integer> prevBets) {
        this.prevBets = prevBets;
    }
    public void setPrevGames(ArrayList<Boolean> prevGames) {
        this.prevGames = prevGames;
    }
    public void setTotalWinnings(int totalWinnings) {
        this.totalWinnings = totalWinnings;
    }
    public void setTotalLosses(int totalLosses) {
        this.totalLosses = totalLosses;
    }
    public void setPic(int pic) {
        this.pic = pic;
    }
    public void setBet(int a){
        bet=a;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public void setCardPos2(Vector2 pos, int j) {
        hand2.deck.get(j).setPos(pos);

    }
    public void addPics(int ind) {
        picsOwned.add(ind);
    }
    public void addMoney(int i) {
        money+=i;
    }

    public ArrayList<String> getInfo(){
        ArrayList<String> strings=new ArrayList<>();
        strings.add(name);
        strings.add("Bet:" +formatMoney(bet));
        strings.add("Money:" +formatMoney( money));
        if(isInsured)
            strings.add("Insurance:" + formatMoney(insurance));
        if(isSplit) {
            strings.add("Total1:" + hand2.getTotal());
            strings.add("Total2:" + hand.getTotal());
        }else
            strings.add("Total:" +hand.getTotal());
        return strings;
    }
    public ArrayList<Card> getAces(){
        ArrayList<Card> list=new ArrayList<>();
        for(Integer i: hand.aceInds){
            list.add(hand.deck.get(i));
        }
        for(Integer i: hand2.aceInds){
            list.add(hand2.deck.get(i));
        }
        return list;
    }
    public ArrayList<Boolean> getPrevGames() {
        return prevGames;
    }
    public ArrayList<Integer> getPicsOwned() {
        return picsOwned;
    }
    public ArrayList<Integer> getPrevBets() {
        return prevBets;
    }
    public Rectangle getRect() {
        return rect;
    }
    public String getName() {
        return name;
    }
    public Color getColor(){
        Color  c=Color.BLACK;
        if(r1over)
            c=Color.GRAY;
        if(won)
            c=Color.GREEN;
        if(lost)
            c=Color.RED;
        if(!roundQueue.isEmpty() && roundQueue.get(0)==order && !isSecTurn())
            c=Color.BLUE;

        return c;
    }
    public Color getColor2(){
        Color  c=Color.BLACK;
        if(r2over)
            c=Color.GRAY;
        if(won2)
            c=Color.GREEN;
        if(lost2)
            c=Color.RED;
        if(isSecTurn() && !roundQueue.isEmpty() && roundQueue.get(0).equals(order))
            c=Color.BLUE;

        return c;
    }
    public Hand getHand2() {
        return hand2;
    }
    public Hand getHand() {
        return hand;
    }
    public int getPic() {
        return pic;
    }
    public int getOrder() {
        return order;
    }
    public int getMoney() {
        return money;
    }
    public int getBet() {
        return bet;
    }
    public int getWinnings() {
        return winnings;
    }
    public int getWins() {
        return wins;
    }
    public int getLosses() {
        return losses;
    }
    public int getTotalWinnings() {
        return totalWinnings;
    }
    public int getTotalLosses() {
        return totalLosses;
    }


    public void insure() {
        setInsured(true);
        MyTextInputListener listener = new MyTextInputListener(2);
        listener.setP(order);
        Gdx.input.getTextInput(listener,   "BUY  INSURANCE: $" + (getMoney()-bet), "", "");

    }

    public void setInsurance(int insurance) {
        this.insurance = insurance;
    }

    public int getInsurance() {
        return insurance;
    }

    public void winIns() {
        outc(name+" was insured "+(2*insurance)+" ("+money+")->"+(money+=(2*insurance)));
    }
    public void loseIns(){
        outc(name+" lost ins "+insurance+" ("+money+")->"+(money-=(insurance)));

    }

    public void setOrder(int order) {
        this.order = order;
    }
}
