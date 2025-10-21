package ActionListeners;

import Pieces.*;
import GUI.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PawnPromoteButtonHandler implements ActionListener {
    private final PawnPromoteGUI pawnPromoteGUI;

    public PawnPromoteButtonHandler(PawnPromoteGUI pawnPromoteGUI) {
            this.pawnPromoteGUI = pawnPromoteGUI;
    }
    @Override
    public void actionPerformed (ActionEvent e){
        if (e.getSource() == pawnPromoteGUI.getQueenButton()) {
            promotePawn("Queen");
        } else if (e.getSource() == pawnPromoteGUI.getBishopButton()) {
            promotePawn("Bishop");
        } else if (e.getSource() == pawnPromoteGUI.getKnightButton()) {
            promotePawn("Knight");
        } else if (e.getSource() == pawnPromoteGUI.getRookButton()) {
            promotePawn("Rook");
        }
        pawnPromoteGUI.dispose();
    }
    public void promotePawn (String pieceType) {
        JButton buttonForNewPiece = pawnPromoteGUI.getButtonfornewpiece();
        Color colorForNewPiece = pawnPromoteGUI.getColorfornewpiece();
        switch (pieceType) {
            case "Queen":
                Piece queen = new Queen(buttonForNewPiece, colorForNewPiece);
                pawnPromoteGUI.getPieces().add(queen);
                pawnPromoteGUI.setPromotionTypeForFirebase(queen.getPiecetype());
                if (colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white queen.png"));
                } else {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black queen.png"));
                }
                pawnPromoteGUI.setPromotionTypeForFirebase("Queen");
                break;
            case "Bishop":
                Piece bishop = new Bishop(buttonForNewPiece, colorForNewPiece);
                pawnPromoteGUI.getPieces().add(bishop);
                pawnPromoteGUI.setPromotionTypeForFirebase(bishop.getPiecetype());
                if (colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white bishop.png"));
                } else {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black bishop.png"));
                }
                pawnPromoteGUI.setPromotionTypeForFirebase("Bishop");
                break;
            case "Knight":
                Piece knight = new Knight(buttonForNewPiece, colorForNewPiece);
                pawnPromoteGUI.getPieces().add(knight);
                pawnPromoteGUI.setPromotionTypeForFirebase(knight.getPiecetype());
                if (colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white knight.png"));
                } else {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black knight.png"));
                }
                pawnPromoteGUI.setPromotionTypeForFirebase("Knight");
                break;
            case "Rook":
                Rook rook = new Rook(buttonForNewPiece, colorForNewPiece);
                pawnPromoteGUI.getPieces().add(rook);
                pawnPromoteGUI.setPromotionTypeForFirebase(rook.getPiecetype());
                rook.increasehowmanytimesitmoved();
                if (colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white rook.png"));
                } else {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black rook.png"));
                }
                pawnPromoteGUI.setPromotionTypeForFirebase("Rook");
                break;
        }
    }
}