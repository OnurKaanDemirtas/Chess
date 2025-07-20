package Pieces;

import javax.swing.*;
import java.awt.*;

public class Pawn extends Piece{
    private int howmanytimesitmoved;

    public int getHowmanytimesitmoved() {
        return howmanytimesitmoved;
    }

    public Pawn(JButton button, Color color){
        super(button,color);
        howmanytimesitmoved=0;
    }

    public void increasehowmanytimesitmoved(){
        howmanytimesitmoved++;
    }
}
