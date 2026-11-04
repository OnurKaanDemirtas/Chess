package ActionListeners;

import GUI.PawnPromoteGUI;
import Logic.ChessModel;
import Pieces.Piece;

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
    /** Puts the chosen piece on the promotion square and remembers the choice so it can be sent to the opponent. */
    public void promotePawn (String pieceType) {
        Piece newPiece = ChessModel.createPromotedPiece(pieceType, pawnPromoteGUI.getButtonfornewpiece(), pawnPromoteGUI.getColorfornewpiece());
        pawnPromoteGUI.getPieces().add(newPiece);
        pawnPromoteGUI.setPromotionTypeForFirebase(pieceType);
    }
}
