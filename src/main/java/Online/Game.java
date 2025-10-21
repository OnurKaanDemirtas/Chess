package Online;

import GUI.BoardGUI;
import Logic.Tempo;

public class Game {
    private MatchMaking opponent;
    private MatchMaking player;
    private String color;
    private boolean gameFinished;
    private Tempo tempoForPlayer;
    private Tempo tempoForOpponent;
    private String gameId;
    private int numberOfMovesMade;

    public int getNumberOfMovesMade() {
        return numberOfMovesMade;
    }

    public void setNumberOfMovesMade(int numberOfMovesMade) {
        this.numberOfMovesMade = numberOfMovesMade;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

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

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public Tempo getTempoForPlayer() {
        return tempoForPlayer;
    }

    public void setTempoForPlayer(Tempo tempoForPlayer) {
        this.tempoForPlayer = tempoForPlayer;
    }

    public Tempo getTempoForOpponent() {
        return tempoForOpponent;
    }

    public void setTempoForOpponent(Tempo tempoForOpponent) {
        this.tempoForOpponent = tempoForOpponent;
    }

    public Game(MatchMaking player, MatchMaking opponent, String color, Tempo tempoForPlayer, Tempo tempoForOpponent) {
        this.color=color;
        this.opponent = opponent;
        this.gameFinished=false;
        this.tempoForPlayer = tempoForPlayer;
        this.tempoForOpponent = tempoForOpponent;
        this.player=player;
        this.gameId=player.getId()+" ";
        this.numberOfMovesMade=0;
    }

    public Game(){}

    public void displayGame(Game game){
        BoardGUI boardGUI=new BoardGUI(color,game);
        boardGUI.selectTimer(tempoForPlayer, tempoForOpponent);
    }

    public String toString(){
        if(numberOfMovesMade<10) {
            return "00" + numberOfMovesMade;
        }else if(numberOfMovesMade<100){
            return "0" + numberOfMovesMade;
        }else{
            return "" + numberOfMovesMade;
        }
    }
}
