package GUI;

import ButtonHandlers.BoardButtonHandler;
import Pieces.*;
import Logic.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BoardGUI extends JFrame{
    private JPanel boardPanel;
    private JPanel movesPanel;
    private JPanel infoPanel;
    private ArrayList<JButton> buttonlist;
    private ArrayList<Piece> whitepieces;
    private ArrayList<Piece> blackpieces;
    private chessmodel chessmodel;
    private JLabel takedwhitequeencount;
    private JLabel takedwhiteknightcount;
    private JLabel takedwhitebishopcount;
    private JLabel takedwhiterookcount;
    private JLabel takedwhitepawncount;
    private JLabel takedblackqueencount;
    private JLabel takedblackknightcount;
    private JLabel takedblackbishopcount;
    private JLabel takedblackrookcount;
    private JLabel takedblackpawncount;

    public JLabel getTakedwhitequeencount() {
        return takedwhitequeencount;
    }

    public JLabel getTakedwhiteknightcount() {
        return takedwhiteknightcount;
    }

    public JLabel getTakedwhitebishopcount() {
        return takedwhitebishopcount;
    }

    public JLabel getTakedwhiterookcount() {
        return takedwhiterookcount;
    }

    public JLabel getTakedwhitepawncount() {
        return takedwhitepawncount;
    }

    public JLabel getTakedblackqueencount() {
        return takedblackqueencount;
    }

    public JLabel getTakedblackknightcount() {
        return takedblackknightcount;
    }

    public JLabel getTakedblackbishopcount() {
        return takedblackbishopcount;
    }

    public JLabel getTakedblackrookcount() {
        return takedblackrookcount;
    }

    public JLabel getTakedblackpawncount() {
        return takedblackpawncount;
    }

    public JPanel getBoardPanel() {
        return boardPanel;
    }

    public JPanel getInfoPanel() {
        return infoPanel;
    }

    public JPanel getMovesPanel() {
        return movesPanel;
    }

    public ArrayList<Piece> getWhitepieces() {
        return whitepieces;
    }

    public ArrayList<Piece> getBlackpieces() {
        return blackpieces;
    }

    public BoardGUI(){
        this.whitepieces=new ArrayList<>();
        this.blackpieces=new ArrayList<>();
        this.boardPanel=initiateBoardPanel();
        addButtonstoBoard();
        chessmodel=new chessmodel(blackpieces,whitepieces,buttonlist,this);
        addActionListeners();
        this.movesPanel=initiateMovesPanel();
        this.infoPanel =initiateInfoPanel();
        setSize(1000,800);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(boardPanel);
        add(movesPanel);
        add(infoPanel);
        setVisible(true);
    }
    private JPanel initiateBoardPanel(){
        JPanel board=new JPanel();
        board.setLayout(new GridLayout(8,8));
        board.setBounds(100,60,640,640);
        board.setBackground(Color.GREEN);
        return board;
    }
    private JPanel initiateInfoPanel(){
        JPanel info=new JPanel();
        info.setBounds(0,0,100,1000);
        info.setLayout(new GridLayout(12,2));
        info.add(new JLabel(new ImageIcon("src/PieceIcons/White/white queen.png")));
        info.add(takedwhitequeencount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/PieceIcons/White/white rook.png")));
        info.add(takedwhiterookcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/PieceIcons/White/white bishop.png")));
        info.add(takedwhitebishopcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/PieceIcons/White/white knight.png")));
        info.add(takedwhiteknightcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/PieceIcons/White/white pawn.png")));
        info.add(takedwhitepawncount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/PieceIcons/Black/black queen.png")));
        info.add(takedblackqueencount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/PieceIcons/Black/black rook.png")));
        info.add(takedblackrookcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/PieceIcons/Black/black bishop.png")));
        info.add(takedblackbishopcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/PieceIcons/Black/black knight.png")));
        info.add(takedblackknightcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/PieceIcons/Black/black pawn.png")));
        info.add(takedblackpawncount=new JLabel("x0"));
        info.setBackground(Color.cyan);
        return info;
    }
    private JPanel initiateMovesPanel(){
        JPanel moves=new JPanel();
        moves.setBounds(740,0,260,1000);
        moves.setBackground(Color.BLUE);
        return moves;
    }
    private void addButtonstoBoard(){
        this.buttonlist=new ArrayList<>();
        for(int row=0;row<8;row++){
            for(int column=0;column<8;column++){
                JButton button=new JButton();
                button.setBounds(column*80,row*80,80,80);
                if((row+column)%2==0){
                    button.setBackground(Color.WHITE);
                }else{
                    button.setBackground(Color.LIGHT_GRAY);
                }
                if((row==7&&column==1)||(row==7&&column==6)){
                    Piece knight =new Knight(button,Color.WHITE);
                    whitepieces.add(knight);
                    button.setEnabled(true);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/White/white knight.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if((row==7&&column==2)||(row==7&&column==5)){
                    Piece bishop=new Bishop(button,Color.WHITE);
                    whitepieces.add(bishop);
                    button.setEnabled(true);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/White/white bishop.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if((row==7&&column==0)||(row==7&&column==7)){
                    Piece rook=new Rook(button,Color.WHITE);
                    whitepieces.add(rook);
                    button.setEnabled(true);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/White/white rook.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if(row==7 && column==3){
                    Piece queen=new Queen(button,Color.WHITE);
                    whitepieces.add(queen);
                    button.setEnabled(true);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/White/white queen.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if(row==7 && column==4){
                    Piece king=new King(button,Color.WHITE);
                    whitepieces.add(king);
                    button.setEnabled(true);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/White/white king.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if((row==0&&column==1)||(row==0&&column==6)){
                    Piece knight =new Knight(button,Color.BLACK);
                    blackpieces.add(knight);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/Black/black knight.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if((row==0&&column==2)||(row==0&&column==5)){
                    Piece bishop=new Bishop(button,Color.BLACK);
                    blackpieces.add(bishop);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/Black/black bishop.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if((row==0&&column==0)||(row==0&&column==7)){
                    Piece rook=new Rook(button,Color.BLACK);
                    blackpieces.add(rook);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/Black/black rook.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if(row==0 && column==3){
                    Piece queen=new Queen(button,Color.BLACK);
                    blackpieces.add(queen);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/Black/black queen.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if(row==0 && column==4){
                    Piece king=new King(button,Color.BLACK);
                    blackpieces.add(king);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/Black/black king.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                } else if (row==1) {
                    Piece pawn=new Pawn(button,Color.BLACK);
                    blackpieces.add(pawn);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/Black/black pawn.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                } else if (row==6) {
                    Piece pawn=new Pawn(button,Color.WHITE);
                    whitepieces.add(pawn);
                    button.setEnabled(true);
                    ImageIcon imageIcon=new ImageIcon("src/PieceIcons/White/white pawn.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                } else {
                    button.setEnabled(false);
                }
                boardPanel.add(button);
                buttonlist.add(button);
            }
        }
    }
    public void addActionListeners(){
        BoardButtonHandler boardButtonHandler = new BoardButtonHandler(chessmodel, this);
        for(JButton button:buttonlist){
            button.addActionListener(boardButtonHandler);
        }
    }
}
