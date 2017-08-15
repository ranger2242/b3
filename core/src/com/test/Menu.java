package com.test;

import java.util.ArrayList;

/**
 * Created by Chris Cavazos on 7/22/2017.
 */
public class Menu {
    ArrayList<String> options = new ArrayList<>();
    String title="Title";
    int menuID = 0;
    Player p=null;

    public Menu(int menuID, Player player) {
        this.menuID = menuID;
        p=player;
        init();
    }
    public Menu(int menuID) {
        this.menuID = menuID;
        init();
    }
    void init(){
        options.clear();
        switch (menuID){
            case 0:{
                title="Main Menu";
                options.add("Start");
                options.add("Register Players");
                options.add("Delete Players");
                options.add("Check Players");
                options.add("Exit");
                break;
            }
            case 1:{
                title=p.getName()+ "'s Turn";
                options.add("Stand");
                options.add("Hit");
              //  options.add("Double Down");
              //1
                //  options.add("Insurance");
                break;
            }
        }
    }

    public void print() {
        Main.out("--- " + title + " ---");
        for (int i = 1; i <= 8 && i < options.size() + 1; i++) {
            Main.out(i + ": " + options.get(i - 1));
        }
        if (menuID == 1) {
            getTurnInput();
        } else {
            getInput();
        }
    }
    public void getTurnInput() {/*
       int input= Main.getIntInput();
        if(input <9 && input >0 && input<options.size()+1){
            switch (menuID){
                case 1:{//player turn
                    switch (input){
                        case 1:{
                            Main.players.get(Main.players.indexOf(p)).setRoundover(true);
                            break;
                        }
                        case 2:{
                            Main.players.get(Main.players.indexOf(p)).drawCard();
                            break;
                        }
                        case 3:{
                            break;
                        }
                        case 4:{
                            break;
                        }
                    }

                }
            }
        }*/
    }
        public void getInput(){
        int input= Main.getIntInput();
        if(input <9 && input >0 && input<options.size()){
            switch (menuID){
                case 0:{//main menu
                    switch (input){
                        case 1:{
                           // Main.gameLoop();
                            break;
                        }
                        case 2:{
                            Player.register();
                            break;
                        }
                        case 3:{
                            Player.delete();
                            break;
                        }
                        case 4:{
                            Main.printPlayersMoney();
                            break;
                        }
                        case 5:{
                            System.exit(0);
                            break;
                        }
                    }
                }
            }
        }
    }
}
