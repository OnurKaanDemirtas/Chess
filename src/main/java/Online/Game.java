package Online;

import GUI.BoardGUI;
import Logic.Tempo;

import java.util.ArrayList;

public class Game {
    private MatchMaking opponent;
    private MatchMaking player;
    private String color;
    private int lastPositionthatDisplayed;
    private boolean gameFinished;
    private Tempo tempoforplayer;
    private Tempo tempoforopponent;

    public MatchMaking getPlayer() {
        return player;
    }

    public void setPlayer(MatchMaking player) {
        this.player = player;
    }

    public MatchMaking getOpponent() {
        return opponent;
    }

    public void setOpponent(MatchMaking opponent) {
        this.opponent = opponent;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getLastPositionthatDisplayed() {
        return lastPositionthatDisplayed;
    }

    public void setLastPositionthatDisplayed(int lastPositionthatDisplayed) {
        this.lastPositionthatDisplayed = lastPositionthatDisplayed;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public Tempo getTempoforplayer() {
        return tempoforplayer;
    }

    public void setTempoforplayer(Tempo tempoforplayer) {
        this.tempoforplayer = tempoforplayer;
    }

    public Tempo getTempoforopponent() {
        return tempoforopponent;
    }

    public void setTempoforopponent(Tempo tempoforopponent) {
        this.tempoforopponent = tempoforopponent;
    }

    public Game(MatchMaking player,MatchMaking opponent, String color, Tempo tempoforplayer, Tempo tempoforopponent) {
        this.lastPositionthatDisplayed =0;
        this.color=color;
        this.opponent = opponent;
        this.gameFinished=false;
        this.tempoforplayer=tempoforplayer;
        this.tempoforopponent=tempoforopponent;
        this.player=player;
    }

    public Game(){}

    public void displayGame(){
        BoardGUI boardGUI=new BoardGUI(color,opponent);
        boardGUI.selecttimer(tempoforplayer,tempoforopponent);
    }
}
