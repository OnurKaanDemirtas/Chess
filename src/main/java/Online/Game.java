package Online;

import GUI.BoardGUI;
import Logic.Tempo;

/**
 * One game as seen by one player. Two Game documents are created per match,
 * one in each player's gameArchive. They are mirror images (player/opponent and color swapped).
 * Firestore maps this class through its getters and setters, so it needs the empty constructor.
 */
public class Game {
    private MatchMaking opponent;
    private MatchMaking player;
    private String color;
    private boolean gameFinished;
    private Tempo tempoForPlayer;
    private Tempo tempoForOpponent;
    private String gameId;
    private int numberOfMovesMade;
    private String result;      // WIN, LOSS or DRAW (see GameResult); null while the game is running
    private int ratingChange;
    private long createdAt;     // epoch millis

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getRatingChange() {
        return ratingChange;
    }

    public void setRatingChange(int ratingChange) {
        this.ratingChange = ratingChange;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

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
        this.createdAt=System.currentTimeMillis();
    }

    public Game(){}

    /** Opens the board for this game and starts the clocks. */
    public BoardGUI displayGame(){
        BoardGUI boardGUI=new BoardGUI(color,this);
        if(color.equals("W")){
            boardGUI.selectTimer(tempoForPlayer, tempoForOpponent);
        }else{
            boardGUI.selectTimer(tempoForOpponent, tempoForPlayer);
        }
        return boardGUI;
    }

    /** "1+1", "3+2", ... */
    public String tempoName(){
        if(tempoForPlayer==null){
            return "?";
        }
        return tempoForPlayer.getRemainingTime()/60+"+"+tempoForPlayer.getTimeToAddedForEachMove();
    }

    /** The moves are stored as 001, 002, ... so that Firestore keeps them in order. */
    public String toString(){
        return String.format("%03d", numberOfMovesMade);
    }
}
