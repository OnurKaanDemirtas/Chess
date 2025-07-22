package GUI;

import ButtonHandlers.PawnPromoteButtonHandler;
import Pieces.Piece;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PawnPromoteGUI extends JFrame {
    private JButton queenButton;
    private JButton rookButton;
    private JButton bishopButton;
    private JButton knightButton;
    private JButton buttonfornewpiece;
    private Color colorfornewpiece;
    private ArrayList<Piece> pieces;

    public PawnPromoteGUI(JButton buttonfornewpiece,Color colorfornewpiece,ArrayList<Piece> pieces) {
        this.buttonfornewpiece = buttonfornewpiece;
        this.colorfornewpiece = colorfornewpiece;
        this.pieces = pieces;
        setSize(300,300);
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
        rookButton = new JButton();
        rookButton.setIcon(rookIcon);
        bishopButton = new JButton();
        bishopButton.setIcon(bishopIcon);
        knightButton = new JButton();
        knightButton.setIcon(knightIcon);

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
