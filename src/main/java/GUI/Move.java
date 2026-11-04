package GUI;

import PieceIcons.IconLoader;
import Pieces.Piece;

import javax.swing.*;
import java.awt.*;

/**
 * One entry in the move list. Besides its name ("Nf3", "O-O", "exd5+" ...) it keeps
 * a frozen copy of the board and the captured-pieces panel right after the move,
 * so the player can click an old move and see that position.
 */
public class Move {
    private final JPanel InfoPanel;
    private final JPanel BoardPanel;
    private String nameofthemove;
    private final int moveCount;

    public JPanel getBoardPanel() {
        return BoardPanel;
    }

    public JPanel getInfoPanel() {
        return InfoPanel;
    }

    public Move(BoardGUI boardGUI,String nameofthemove) {
        String coloroftheplayer= boardGUI.getColorOfThePlayer();
        // white's and black's move share the same number: 1. e4  1. e5  2. Nf3 ...
        int movesPlayed=boardGUI.getWhiteMovesListModel().getSize()+boardGUI.getBlackMovesListModel().getSize();
        this.moveCount=movesPlayed/2+1;
        this.InfoPanel=new JPanel();
        this.InfoPanel.setBounds(0,0,100,800);
        this.InfoPanel.setLayout(new GridLayout(10,2));
        this.InfoPanel.setBackground(Color.cyan);
        this.InfoPanel.add(new JLabel(IconLoader.get(Color.WHITE, "queen")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenWhiteQueenCount().getText()));
        this.InfoPanel.add(new JLabel(IconLoader.get(Color.WHITE, "rook")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenWhiteRookCount().getText()));
        this.InfoPanel.add(new JLabel(IconLoader.get(Color.WHITE, "bishop")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenWhiteBishopCount().getText()));
        this.InfoPanel.add(new JLabel(IconLoader.get(Color.WHITE, "knight")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenWhiteKnightCount().getText()));
        this.InfoPanel.add(new JLabel(IconLoader.get(Color.WHITE, "pawn")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenWhitePawnCount().getText()));
        this.InfoPanel.add(new JLabel(IconLoader.get(Color.BLACK, "queen")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenBlackQueenCount().getText()));
        this.InfoPanel.add(new JLabel(IconLoader.get(Color.BLACK, "rook")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenBlackRookCount().getText()));
        this.InfoPanel.add(new JLabel(IconLoader.get(Color.BLACK, "bishop")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenBlackBishopCount().getText()));
        this.InfoPanel.add(new JLabel(IconLoader.get(Color.BLACK, "knight")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenBlackKnightCount().getText()));
        this.InfoPanel.add(new JLabel(IconLoader.get(Color.BLACK, "pawn")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenBlackPawnCount().getText()));
        this.BoardPanel = new JPanel();
        this.BoardPanel.setBounds(100,60,640,640);
        this.BoardPanel.setLayout(new GridLayout(8,8));
        // Copy the current board into a frozen, read-only panel.
        // The button list is stored from white's point of view, so black walks it backwards.
        int size = boardGUI.getButtonList().size();
        for (int i = 0; i < size; i++) {
            JButton button = coloroftheplayer.equals("W")
                    ? boardGUI.getButtonList().get(i)
                    : boardGUI.getButtonList().get(size - 1 - i);
            JButton copyButton = new JButton();
            Piece piece = findPieceOn(button, boardGUI.getWhitePieces());
            if (piece == null) {
                piece = findPieceOn(button, boardGUI.getBlackPieces());
            }
            if (piece != null) {
                copyButton.setIcon(IconLoader.forPiece(piece));
                copyButton.setDisabledIcon(copyButton.getIcon());
            }
            copyButton.setBackground(button.getBackground());
            BoardGUI.makeSquare(copyButton);
            copyButton.setEnabled(false);
            this.BoardPanel.add(copyButton);
        }
        this.nameofthemove = nameofthemove;
    }

    private static Piece findPieceOn(JButton button, java.util.List<Piece> pieces) {
        for (Piece piece : pieces) {
            if (button.equals(piece.getButton())) {
                return piece;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return moveCount+". "+nameofthemove;
    }
}
