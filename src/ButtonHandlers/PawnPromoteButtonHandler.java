package ButtonHandlers;

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
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==pawnPromoteGUI.getQueenButton()){
            promotePawn("Queen");
        } else if(e.getSource() == pawnPromoteGUI.getBishopButton()){
            promotePawn("Bishop");
        } else if(e.getSource() == pawnPromoteGUI.getKnightButton()){
            promotePawn("Knight");
        } else if(e.getSource() == pawnPromoteGUI.getRookButton()){
            promotePawn("Rook");
        }
        pawnPromoteGUI.dispose();
    }
    public void promotePawn(String pieceType) {
        JButton buttonForNewPiece = pawnPromoteGUI.getButtonfornewpiece();
        Color colorForNewPiece = pawnPromoteGUI.getColorfornewpiece();

        switch (pieceType) {
            case "Queen":
                Piece queen=new Queen(buttonForNewPiece, colorForNewPiece);
                pawnPromoteGUI.getPieces().add(queen);
                if(colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/PieceIcons/White/white queen.png"));
                }else {
                    buttonForNewPiece.setIcon(new ImageIcon("src/PieceIcons/Black/black queen.png"));
                }
                break;
            case "Bishop":
                Piece bishop=new Bishop(buttonForNewPiece, colorForNewPiece);
                pawnPromoteGUI.getPieces().add(bishop);
                if (colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/PieceIcons/White/white bishop.png"));
                }else {
                    buttonForNewPiece.setIcon(new ImageIcon("src/PieceIcons/Black/black bishop.png"));
                }
                break;
            case "Knight":
                Piece knight=new Knight(buttonForNewPiece, colorForNewPiece);
                pawnPromoteGUI.getPieces().add(knight);
                if (colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/PieceIcons/White/white knight.png"));
                }else{
                    buttonForNewPiece.setIcon(new ImageIcon("src/PieceIcons/Black/black knight.png"));
                }
                break;
            case "Rook":
                Piece rook=new Rook(buttonForNewPiece, colorForNewPiece);
                pawnPromoteGUI.getPieces().add(rook);
                if( colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/PieceIcons/White/white rook.png"));
                }else {
                    buttonForNewPiece.setIcon(new ImageIcon("src/PieceIcons/Black/black rook.png"));
                }
                break;
        }
    }
}
