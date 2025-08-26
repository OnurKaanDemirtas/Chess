package Pieces;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Pawn extends Piece{
    private int howmanytimesitmoved;
    private ArrayList<JButton> attackingplaces;
    private ArrayList<JButton> enpassantplaces;

    public int getHowmanytimesitmoved() {
        return howmanytimesitmoved;
    }

    public ArrayList<JButton> getAttackingplaces() {
        return attackingplaces;
    }

    public ArrayList<JButton> getEnpassantplaces() {
        return enpassantplaces;
    }

    public Pawn(JButton button, Color color){
        super(button,color,"");
        howmanytimesitmoved=0;
        attackingplaces = new ArrayList<>();
        enpassantplaces = new ArrayList<>();
    }

    public void increasehowmanytimesitmoved(){
        howmanytimesitmoved++;
    }
}
