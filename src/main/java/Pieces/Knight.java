package Pieces;

import javax.swing.*;
import java.awt.*;

/** Moves in an L shape and can jump over pieces. Uses "N" in notation because "K" is the king. */
public class Knight extends Piece {
    public Knight(JButton button, Color color){
        super(button,color,"N");
    }
}
