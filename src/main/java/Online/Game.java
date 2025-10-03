package Online;

import GUI.BoardGUI;

import java.util.ArrayList;

public class Game {
    private Account opponent;
    private ArrayList<MoveNotation> MoveNotationList;
    private String color;
    private int lastPositionthatDisplayed;
    private boolean gameFinished;

    public Account getOpponent() {
        return opponent;
    }

    public void setOpponent(Account opponent) {
        this.opponent = opponent;
    }

    public ArrayList<MoveNotation> getMoveNotationList() {
        return MoveNotationList;
    }

    public void setMoveNotationList(ArrayList<MoveNotation> moveNotationList) {
        this.MoveNotationList = moveNotationList;
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

    public Game(Account opponent, String color) {
        this.lastPositionthatDisplayed =0;
        this.color=color;
        this.opponent = opponent;
        this.MoveNotationList =new ArrayList<>();
        this.gameFinished=false;
        displayGame();
    }

    public Game(){}

    public void displayGame(){
        new BoardGUI(color,opponent);
    }
}
