package com.test;

import java.util.Scanner;

import static com.test.states.GameState.output;
import static com.test.Player.players;

public class Main {
    public static Deck deck = new Deck();
    public static Scanner sc = new Scanner(System.in);
    public static String DIV="-------------------------------------";
    public static Menu mainmenu=new Menu(0);



    public static void out(String s) {
        System.out.println(s);}
    public static void outc(String s) {
        output.add(s);
        if(output.size()>10)
            output.remove(0);
    }
    public static void main(String[] args) {
        int index=0,x=4,y=6,n=x*y;
        int c=0;
        for(int i=0;i<n;i++){
            out("@ "+c+" "+i%x);
            if(i%x==x-1)
                c++;
            //walkFrames[i]=tmp[i%(n/y)][i%x];
        }
  }
    public static String getStringInput(){
        String s= sc.next();
        while(s.equals("")){
            s=sc.next();
        }


        return s;
    }
    public static int getIntInput(){
        return Integer.parseInt(getStringInput());
    }
    public static void printPlayersMoney(){
        for(Player p:players){
            p.printBalance();
        }
        mainmenu.print();
    }

    public static int getIntInput(String input, MyTextInputListener listener) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            try {
                String s = input.substring(0, input.length() - 1);
                char c = input.charAt(input.length() - 1);
                c = Character.toUpperCase(c);
                switch (c) {
                    case 'K': {
                        return 1000 * getIntInput(s, listener);
                    }
                    case 'M': {
                        return 1000000 * getIntInput(s, listener);
                    }
                    default:{
                        listener.canceled();
                    }
                }
            } catch (NumberFormatException e1) {
                listener.canceled();
            }
        }
        return 0;
    }
}
