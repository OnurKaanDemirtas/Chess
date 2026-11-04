package Pieces;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Base class of all pieces. A piece knows the button (square) it stands on, its color,
 * the squares it can move to ("playble places") and the friendly pieces protecting it.
 * piecetype is the letter used in move notation ("K", "Q", "R", "B", "N", "" for pawns).
 */
public abstract class Piece{
    private JButton button;
    private final Color color;
    private final ArrayList<JButton> playbleplaces;
    private final ArrayList<Piece> supportingPieces = new ArrayList<>();
    private String piecetype;

    public JButton getButton() {
        return button;
    }

    public void setButton(JButton button) {
        this.button = button;
    }

    public ArrayList<Piece> getSupportingPieces() {
        return supportingPieces;
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<JButton> getPlaybleplaces() {
        return playbleplaces;
    }

    public String getPiecetype() {
        return piecetype;
    }

    public Piece(JButton button, Color color, String piecetype) {
        this.button=button;
        this.color=color;
        this.playbleplaces =new ArrayList<>();
        this.piecetype = piecetype;
    }
}
