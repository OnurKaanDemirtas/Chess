package GUI;

import Pieces.Piece;

import javax.swing.*;
import java.awt.*;

public class Move {
    private final JPanel InfoPanel;
    private final JPanel BoardPanel;
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
        String coloroftheplayer= boardGUI.getColorOfThePlayer();
        currentmoveCount+= 0.5;
        this.moveCount += (int)currentmoveCount;
        this.InfoPanel=new JPanel();
        this.InfoPanel.setBounds(0,0,100,800);
        this.InfoPanel.setLayout(new GridLayout(10,2));
        this.InfoPanel.setBackground(Color.cyan);
        this.InfoPanel.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white queen.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenWhiteQueenCount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white rook.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenWhiteRookCount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white bishop.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenWhiteBishopCount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white knight.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenWhiteKnightCount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white pawn.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenWhitePawnCount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black queen.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenBlackQueenCount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black rook.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenBlackRookCount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black bishop.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenBlackBishopCount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black knight.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenBlackKnightCount().getText()));
        this.InfoPanel.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black pawn.png")));
        this.InfoPanel.add(new JLabel(boardGUI.getTakenBlackPawnCount().getText()));
        this.BoardPanel = new JPanel();
        this.BoardPanel.setBounds(100,60,640,640);
        this.BoardPanel.setLayout(new GridLayout(8,8));
        if(coloroftheplayer.equals("W")){
            for(JButton button: boardGUI.getButtonList()) {
                JButton copyButton = new JButton();
                for(Piece piece:boardGUI.getWhitePieces()){
                    if(piece.getButton().equals(button)){
                        if(piece instanceof Pieces.Queen) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white queen.png"));
                        } else if(piece instanceof Pieces.Rook) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white rook.png"));
                        } else if(piece instanceof Pieces.Bishop) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white bishop.png"));
                        } else if(piece instanceof Pieces.Knight) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white knight.png"));
                        } else if(piece instanceof Pieces.Pawn) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white pawn.png"));
                        } else if (piece instanceof Pieces.King) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white king.png"));
                        }
                        break;
                    }
                }
                for(Piece piece:boardGUI.getBlackPieces()){
                    if(piece.getButton().equals(button)){
                        if(piece instanceof Pieces.Queen) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black queen.png"));
                        } else if(piece instanceof Pieces.Rook) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black rook.png"));
                        } else if(piece instanceof Pieces.Bishop) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black bishop.png"));
                        } else if(piece instanceof Pieces.Knight) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black knight.png"));
                        } else if(piece instanceof Pieces.Pawn) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black pawn.png"));
                        }else if (piece instanceof Pieces.King) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black king.png"));
                        }
                        break;
                    }
                }
                copyButton.setBackground(button.getBackground());
                copyButton.setEnabled(false);
                this.BoardPanel.add(copyButton);
            }
            this.nameofthemove = nameofthemove;
        }else if(coloroftheplayer.equals("B")){
            for(int i = boardGUI.getButtonList().size()-1; i>=0; i--) {
                JButton button=boardGUI.getButtonList().get(i);
                JButton copyButton = new JButton();
                for(Piece piece:boardGUI.getWhitePieces()){
                    if(piece.getButton().equals(button)){
                        if(piece instanceof Pieces.Queen) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white queen.png"));
                        } else if(piece instanceof Pieces.Rook) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white rook.png"));
                        } else if(piece instanceof Pieces.Bishop) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white bishop.png"));
                        } else if(piece instanceof Pieces.Knight) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white knight.png"));
                        } else if(piece instanceof Pieces.Pawn) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white pawn.png"));
                        } else if (piece instanceof Pieces.King) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white king.png"));
                        }
                        break;
                    }
                }
                for(Piece piece:boardGUI.getBlackPieces()){
                    if(piece.getButton().equals(button)){
                        if(piece instanceof Pieces.Queen) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black queen.png"));
                        } else if(piece instanceof Pieces.Rook) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black rook.png"));
                        } else if(piece instanceof Pieces.Bishop) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black bishop.png"));
                        } else if(piece instanceof Pieces.Knight) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black knight.png"));
                        } else if(piece instanceof Pieces.Pawn) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black pawn.png"));
                        }else if (piece instanceof Pieces.King) {
                            copyButton.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black king.png"));
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
    }
    @Override
    public String toString() {
        return moveCount+". "+nameofthemove;
    }
}
