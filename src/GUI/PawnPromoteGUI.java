package GUI;

import ActionListeners.PawnPromoteButtonHandler;
import Pieces.Piece;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PawnPromoteGUI extends JDialog {
    private final JButton queenButton;
    private final JButton rookButton;
    private final JButton bishopButton;
    private final JButton knightButton;
    private final JButton buttonfornewpiece;
    private final Color colorfornewpiece;
    private final ArrayList<Piece> pieces;
    private String promotedpiece;

    public String getPromotedpiece() {
        return promotedpiece;
    }

    public void setPromotedpiece(String promotedpiece) {
        this.promotedpiece = promotedpiece;
    }

    public PawnPromoteGUI(JFrame parent, JButton buttonfornewpiece, Color colorfornewpiece, ArrayList<Piece> pieces, BoardGUI boardGUI) {
        super(parent,true);
        this.buttonfornewpiece = buttonfornewpiece;
        this.colorfornewpiece = colorfornewpiece;
        this.pieces = pieces;
        setSize(200,100);
        setLocationRelativeTo(boardGUI.getBoardPanel());
        setUndecorated(true);
        setLayout(new GridLayout(1, 4));
        ImageIcon queenIcon;
        ImageIcon rookIcon;
        ImageIcon bishopIcon;
        ImageIcon knightIcon;
        if(colorfornewpiece.equals(Color.WHITE)){
            queenIcon = new ImageIcon("src/PieceIcons/White/white queen.png");
            rookIcon = new ImageIcon("src/PieceIcons/White/white rook.png");
            bishopIcon = new ImageIcon("src/PieceIcons/White/white bishop.png");
            knightIcon = new ImageIcon("src/PieceIcons/White/white knight.png");
        }else{
            queenIcon = new ImageIcon("src/PieceIcons/Black/black queen.png");
            rookIcon = new ImageIcon("src/PieceIcons/Black/black rook.png");
            bishopIcon = new ImageIcon("src/PieceIcons/Black/black bishop.png");
            knightIcon = new ImageIcon("src/PieceIcons/Black/black knight.png");
        }

        queenButton = new JButton();
        queenButton.setIcon(queenIcon);
        queenButton.setBackground(Color.lightGray);
        rookButton = new JButton();
        rookButton.setIcon(rookIcon);
        rookButton.setBackground(Color.WHITE);
        bishopButton = new JButton();
        bishopButton.setIcon(bishopIcon);
        bishopButton.setBackground(Color.lightGray);
        knightButton = new JButton();
        knightButton.setIcon(knightIcon);
        knightButton.setBackground(Color.white);

        add(queenButton);
        add(rookButton);
        add(bishopButton);
        add(knightButton);
        addActionListeners();
        setVisible(true);
    }

    public JButton getQueenButton() {
        return queenButton;
    }

    public JButton getRookButton() {
        return rookButton;
    }

    public JButton getBishopButton() {
        return bishopButton;
    }

    public JButton getKnightButton() {
        return knightButton;
    }

    public Color getColorfornewpiece() {
        return colorfornewpiece;
    }

    public JButton getButtonfornewpiece() {
        return buttonfornewpiece;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }
    public void addActionListeners() {
        PawnPromoteButtonHandler pawnPromoteButtonHandler = new PawnPromoteButtonHandler(this);
        queenButton.addActionListener(pawnPromoteButtonHandler);
        rookButton.addActionListener(pawnPromoteButtonHandler);
        bishopButton.addActionListener(pawnPromoteButtonHandler);
        knightButton.addActionListener(pawnPromoteButtonHandler);
    }
}
