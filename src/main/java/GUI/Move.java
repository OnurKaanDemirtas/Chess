package GUI;

import Pieces.Piece;

import javax.swing.*;
import java.awt.*;

public class Move {
    private JPanel InfoPanel;
    private JPanel BoardPanel;
    private String nameofthemove;
    private static double currentmoveCount = 0.5;
    private int moveCount=0;

    public JPanel getBoardPanel() {
        return BoardPanel;
    }

    public JPanel getInfoPanel() {
        return InfoPanel;
    }

    public Move(BoardGUI boardGUI,String nameofthemove) {
        currentmoveCount+= 0.5;
        this.moveCount += (int)currentmoveCount;
        this.InfoPanel=new JPanel();
        this.InfoPanel.setBounds(0,0,100,800);
        this.InfoPanel.setLayout(new GridLayout(10,2));
        this.InfoPanel.setBackground(Color.cyan);
        this.InfoPanel.add(new JLabel(new ImageIcon("src/PieceIcons/White/white queen.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakedwhitequeencount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/PieceIcons/White/white rook.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakedwhiterookcount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/PieceIcons/White/white bishop.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakedwhitebishopcount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/PieceIcons/White/white knight.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakedwhiteknightcount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/PieceIcons/White/white pawn.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakedwhitepawncount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/PieceIcons/Black/black queen.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakedblackqueencount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/PieceIcons/Black/black rook.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakedblackrookcount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/PieceIcons/Black/black bishop.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakedblackbishopcount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/PieceIcons/Black/black knight.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakedblackknightcount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/PieceIcons/Black/black pawn.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakedblackpawncount().getText()));
        this.BoardPanel = new JPanel();
        this.BoardPanel.setBounds(100,60,640,640);
        this.BoardPanel.setLayout(new GridLayout(8,8));
        for(JButton button: boardGUI.getButtonlist()) {
            JButton copyButton = new JButton();
            for(Piece piece:boardGUI.getWhitepieces()){
                if(piece.getButton().equals(button)){
                    if(piece instanceof Pieces.Queen) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/White/white queen.png"));
                    } else if(piece instanceof Pieces.Rook) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/White/white rook.png"));
                    } else if(piece instanceof Pieces.Bishop) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/White/white bishop.png"));
                    } else if(piece instanceof Pieces.Knight) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/White/white knight.png"));
                    } else if(piece instanceof Pieces.Pawn) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/White/white pawn.png"));
                    } else if (piece instanceof Pieces.King) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/White/white king.png"));
                    }
                    break;
                }
            }
            for(Piece piece:boardGUI.getBlackpieces()){
                if(piece.getButton().equals(button)){
                    if(piece instanceof Pieces.Queen) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/Black/black queen.png"));
                    } else if(piece instanceof Pieces.Rook) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/Black/black rook.png"));
                    } else if(piece instanceof Pieces.Bishop) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/Black/black bishop.png"));
                    } else if(piece instanceof Pieces.Knight) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/Black/black knight.png"));
                    } else if(piece instanceof Pieces.Pawn) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/Black/black pawn.png"));
                    }else if (piece instanceof Pieces.King) {
                        copyButton.setIcon(new ImageIcon("src/PieceIcons/Black/black king.png"));
                    }
                    break;
                }
            }
            copyButton.setBackground(button.getBackground());
            copyButton.setEnabled(false);
            this.BoardPanel.add(copyButton);
        }
        this.nameofthemove = nameofthemove;
    }
    @Override
    public String toString() {
        return moveCount+". "+nameofthemove;
    }
}
