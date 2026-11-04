package GUI;

import PieceIcons.IconLoader;
import ActionListeners.PawnPromoteButtonHandler;
import Pieces.Piece;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/** The small dialog where the player picks the piece a pawn is promoted to. */
public class PawnPromoteGUI extends JDialog {
    private final JButton queenButton;
    private final JButton rookButton;
    private final JButton bishopButton;
    private final JButton knightButton;
    private final JButton buttonfornewpiece;
    private final Color colorfornewpiece;
    private final ArrayList<Piece> pieces;
    private String promotionTypeForFirebase;

    public String getPromotionTypeForFirebase() {
        return promotionTypeForFirebase;
    }

    public void setPromotionTypeForFirebase(String promotionTypeForFirebase) {
        this.promotionTypeForFirebase = promotionTypeForFirebase;
    }

    public PawnPromoteGUI(JFrame parent, JButton buttonfornewpiece, Color colorfornewpiece, ArrayList<Piece> pieces, BoardGUI boardGUI, String promotionTypeForFirebase) {
        super(parent,true);
        this.buttonfornewpiece = buttonfornewpiece;
        this.colorfornewpiece = colorfornewpiece;
        this.pieces = pieces;
        this.promotionTypeForFirebase = promotionTypeForFirebase;
        setSize(200,100);
        setLocationRelativeTo(boardGUI.getBoardPanel());
        setUndecorated(true);
        setLayout(new GridLayout(1, 4));
        ImageIcon queenIcon;
        ImageIcon rookIcon;
        ImageIcon bishopIcon;
        ImageIcon knightIcon;
        if(colorfornewpiece.equals(Color.WHITE)){
            queenIcon = IconLoader.get(Color.WHITE, "queen");
            rookIcon = IconLoader.get(Color.WHITE, "rook");
            bishopIcon = IconLoader.get(Color.WHITE, "bishop");
            knightIcon = IconLoader.get(Color.WHITE, "knight");
        }else{
            queenIcon = IconLoader.get(Color.BLACK, "queen");
            rookIcon = IconLoader.get(Color.BLACK, "rook");
            bishopIcon = IconLoader.get(Color.BLACK, "bishop");
            knightIcon = IconLoader.get(Color.BLACK, "knight");
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

        BoardGUI.makeSquare(queenButton);
        BoardGUI.makeSquare(rookButton);
        BoardGUI.makeSquare(bishopButton);
        BoardGUI.makeSquare(knightButton);
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
    public void addActionListeners(){
        PawnPromoteButtonHandler pawnPromoteButtonHandler = new PawnPromoteButtonHandler(this);
        queenButton.addActionListener(pawnPromoteButtonHandler);
        rookButton.addActionListener(pawnPromoteButtonHandler);
        bishopButton.addActionListener(pawnPromoteButtonHandler);
        knightButton.addActionListener(pawnPromoteButtonHandler);
    }
}
