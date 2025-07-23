package Pieces;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Pawn extends Piece{
    private int howmanytimesitmoved;
    private ArrayList<JButton> attackingplaces;

    public int getHowmanytimesitmoved() {
        return howmanytimesitmoved;
    }

    public ArrayList<JButton> getAttackingplaces() {
        return attackingplaces;
    }

    public Pawn(JButton button, Color color){
        super(button,color);
        howmanytimesitmoved=0;
        attackingplaces = new ArrayList<>();
    }

    public void increasehowmanytimesitmoved(){
        howmanytimesitmoved++;
    }
}
