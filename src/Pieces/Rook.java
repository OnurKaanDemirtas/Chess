package Pieces;

import javax.swing.*;
import java.awt.*;

public class Rook extends Piece{
    private int howmanytimesitmoved;

    public int getHowmanytimesitmoved() {
        return howmanytimesitmoved;
    }

    public void increasehowmanytimesitmoved(){
        howmanytimesitmoved++;
    }

    public Rook(JButton button, Color color){
        super(button,color);
    }
}
