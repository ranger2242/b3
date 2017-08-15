package com.test;

import java.util.ArrayList;
import java.util.Random;

import static sun.misc.MessageUtils.out;

public class Deck {
    ArrayList<Card> deck = new ArrayList<>();

    public void prepare2(){
        deck.clear();
        generateCards();
        deck.get(5).value=2;
        deck.get(6).value=10;
        deck.get(3).value=5;
        deck.get(4).value=3;
        deck.get(0).value=5;
        deck.get(1).value=3;
        //shuffleDeck();
    }
    public void prepare(){
        deck.clear();
        generateCards();
        shuffleDeck();
    }
    void generateCards(){
        deck.clear();
        for(int i=0; i<4; i++){
            for(int j=1; j<=13; j++){
                Card c = new Card(j, Card.Suits.values()[i]);
                deck.add(c);
            }
        }
    }
    public void printDeck(){
        for(Card c: deck){
            out(c.printCard());
        }
    }

    public void shuffleDeck(){
        ArrayList<Card> temp = new ArrayList<>();
        Random rn = new Random();
        while(!deck.isEmpty()){
            int index= rn.nextInt(deck.size());
            temp.add(deck.get(index));
            deck.remove(index);

        }
       deck=temp;
    }


}
