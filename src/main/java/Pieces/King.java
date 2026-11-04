package Pieces;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/** The king also remembers who is checking it and where it can castle (and with which rook). */
public class King extends Piece{
    private ArrayList<Piece> piecesthatcheck;
    private int howmanytimesitmoved;
    private ArrayList<JButton> castableplaces;
    private ArrayList<Rook> castablerooks;

    public ArrayList<Piece> getPiecesthatcheck() {
        return piecesthatcheck;
    }

    public ArrayList<JButton> getCastableplaces() {
        return castableplaces;
    }

    public int getHowmanytimesitmoved() {
        return howmanytimesitmoved;
    }

    public ArrayList<Rook> getCastablerooks() {
        return castablerooks;
    }

    public void increasehowmanytimesitmoved(){
        howmanytimesitmoved++;
    }

    public King(JButton button, Color color){
        super(button,color,"K");
        this.piecesthatcheck=new ArrayList<>();
        this.howmanytimesitmoved = 0;
        this.castableplaces = new ArrayList<>();
        this.castablerooks = new ArrayList<>();
    }
}
