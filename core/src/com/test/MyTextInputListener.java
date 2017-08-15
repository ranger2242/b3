package com.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.test.states.MultiplayerState;

import static com.test.Game.formatMoney;
import static com.test.Player.players;


public class MyTextInputListener implements Input.TextInputListener {
    String input = "";
    private int type = 0;
    private int p = 0;

    public MyTextInputListener(int i) {
        type=i;
    }

    public void setP(int p) {
        this.p = p;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void input(String text) {
        input = text;
        int x = 0;
        if (type != 3) {
            if (input.equals("")) {
                canceled();
            }
            x = Main.getIntInput(input, this);
            if (x == 0) {
                canceled();
            }
        }
        switch (type) {
            case 1: {//place bets
                if (x < 0) x *= -1;
                if (x > players.get(p).getMoney()) {
                    canceled();
                } else {
                    players.get(p).setBet(x);
                    players.get(p).placeBet();
                }
                break;
            }
            case 2://get insurance
            {
                if (x < 0) x *= -1;
                players.get(p).setInsurance(x);
                break;
            }
            case 3://getIP
            {
                if (MultiplayerState.validIP(input)) {
                    MultiplayerState.setIP(input);
                }
            }
        }
    }

    @Override
    public void canceled() {
        switch (type) {
            case 1: {//place bets
                Gdx.input.getTextInput(this, players.get(p).getName() + "'s bet : $" + formatMoney(players.get(p).getMoney()), "", "");
                break;
            }
            case 2: {
                Gdx.input.getTextInput(this, "BUY  INSURANCE: $" + formatMoney((players.get(p).getMoney() - players.get(p).getBet())), "", "");
                break;
            }
        }
    }
}