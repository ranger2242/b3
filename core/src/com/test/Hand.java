package com.test;

import java.io.Serializable;
import java.util.ArrayList;


public class Hand implements Serializable{
    ArrayList<Card> deck = new ArrayList<>();
    ArrayList<Integer> aceInds= new ArrayList<>();
     private int tot= 0;
     boolean hasAce=false;

     public void reset(){
         hasAce=false;
         tot=0;
         deck.clear();
         aceInds.clear();
     }
    public String drawCard(boolean down) {
        Card c = Main.deck.deck.get(0);
        if(c.isAce()){
            aceInds.add(deck.size());
            hasAce=true;
            c.setAceHigh(false);
        }
        if(down)
            c.setFaceDown(true);
        deck.add(c);
        Main.deck.deck.remove(c);
        return c.getName();
    }

    public void printHand() {
        for (Card c : deck) {
            Main.out(c.printCard());
        }
    }
    public void allUp(){
         for(Card c: deck){
             if(c.isFaceDown()){
                 c.setFaceDown(false);
             }
         }
    }
    public int getTotal(){
        total();
        return tot;
    }
    public boolean hasBlackJack(){
        if((deck.get(0).isAce()||deck.get(1).isAce()) &&
                (deck.get(0).getValue()==10 ||deck.get(1).getValue()==10) && deck.size()==2){
            return true;
        }else
            return false;
    }
     public void total() {
        int total2 = 0;
        for (Card c : deck) {
            if(!c.isFaceDown()) {
                if (c.value == 1) {
                    if (c.isAceHigh()) {
                        total2 += 11;
                    } else
                        total2 += 1;
                } else {
                    if (c.value > 10)
                        total2 += 10;
                    else
                        total2 += c.value;
                }
            }
        }
        tot= total2;
    }
    public void dealertotal() {
        int total2 = 0;
        for (Card c : deck) {
            if (c.value == 1) {
                if (total2 + 11<=21)
                    total2 += 11;
                else
                    total2 += 1;
            }
             else {
                if (c.value > 10)
                    total2 += 10;
                else
                    total2 += c.value;
            }
        }
        tot= total2;
    }
    public boolean checkNaturals(){
        boolean a=false;
        boolean b=false;
        for(Card c :deck){
            if(c.value==1)a=true;
            if(c.value>=10)b=true;
        }
        return a&&b;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }
}
