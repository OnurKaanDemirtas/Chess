package Pieces;

import javax.swing.*;
import java.awt.*;

/** Counts its moves because a rook that has moved can no longer castle. */
public class Rook extends Piece{
    private int howmanytimesitmoved;

    public int getHowmanytimesitmoved() {
        return howmanytimesitmoved;
    }

    public void increasehowmanytimesitmoved(){
        howmanytimesitmoved++;
    }

    public Rook(JButton button, Color color){
        super(button,color,"R");
    }
}
