package Pieces;

import javax.swing.*;
import java.awt.*;

/** Moves like a rook and a bishop together (see ChessModel.findPlayablePlaces). */
public class Queen extends Piece{
    public Queen(JButton button, Color color){
        super(button,color,"Q");
    }
}
