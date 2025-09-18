package GUI;

import ActionListeners.BoardButtonHandler;
import ActionListeners.SelectTempoListener;
import ActionListeners.SelectionListener;
import Pieces.*;
import Logic.*;
import com.google.cloud.firestore.Firestore;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BoardGUI {
    private final JFrame frame;
    private final JPanel boardPanel;
    private final JPanel infoPanel;
    private final JPanel movesPanel;
    private ArrayList<JButton> buttonlist;
    private final ArrayList<Piece> whitepieces;
    private final ArrayList<Piece> blackpieces;
    private final ChessModel chessmodel;
    private JLabel whitetimerlabel;
    private JLabel blacktimerlabel;
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
    private final DefaultListModel<Move> whitemovesListModel;
    private final DefaultListModel<Move> blackmovesListModel;
    private final JList<Move> whitemovesList;
    private final JList<Move> blackmovesList;
    private final JSplitPane movesSplitPane;
    private Timer whitetimer;
    private Timer blacktimer;
    private Tempo whitetempo;
    private Tempo blacktempo;
    private Firestore database;

    public Tempo getWhitetempo() {
        return whitetempo;
    }

    public Tempo getBlacktempo() {
        return blacktempo;
    }

    public void setBlacktempo(Tempo blacktempo) {
        this.blacktempo = blacktempo;
    }

    public void setWhitetempo(Tempo whitetempo) {
        this.whitetempo = whitetempo;
    }

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

    public ArrayList<JButton> getButtonlist() {
        return buttonlist;
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getBoardPanel() {
        return boardPanel;
    }

    public JPanel getInfoPanel() {
        return infoPanel;
    }

    public ArrayList<Piece> getWhitepieces() {
        return whitepieces;
    }

    public ArrayList<Piece> getBlackpieces() {
        return blackpieces;
    }

    public DefaultListModel<Move> getWhitemovesListModel() {
        return whitemovesListModel;
    }

    public DefaultListModel<Move> getBlackmovesListModel() {
        return blackmovesListModel;
    }

    public JList<Move> getWhitemovesList() {
        return whitemovesList;
    }

    public JList<Move> getBlackmovesList() {
        return blackmovesList;
    }

    public Timer getBlacktimer() {
        return blacktimer;
    }

    public Timer getWhitetimer() {
        return whitetimer;
    }

    public JLabel getWhitetimerlabel() {
        return whitetimerlabel;
    }

    public JLabel getBlacktimerlabel() {
        return blacktimerlabel;
    }

    public Firestore getDatabase() {
        return database;
    }

    public BoardGUI(Firestore database){
        this.database=database;
        this.frame=new JFrame("Chess Game");
        this.whitepieces=new ArrayList<>();
        this.blackpieces=new ArrayList<>();
        this.boardPanel=initiateBoardPanel();
        this.blackmovesListModel = new DefaultListModel<>();
        this.blackmovesList = new JList<>(blackmovesListModel);
        JScrollPane blackmovesScrollPane = new JScrollPane(blackmovesList);
        this.whitemovesListModel = new DefaultListModel<>();
        this.whitemovesList = new JList<>(whitemovesListModel);
        JScrollPane whitemovesScrollPane = new JScrollPane(whitemovesList);
        this.movesSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, whitemovesScrollPane, blackmovesScrollPane);
        addButtonstoBoard();
        chessmodel=new ChessModel(blackpieces,whitepieces,buttonlist,this);
        addActionListeners();
        this.movesPanel = initiateMovesPanel();
        this.infoPanel =initiateInfoPanel();
        SelectionListener selectionListener = new SelectionListener(this);
        this.whitemovesList.addListSelectionListener(selectionListener);
        this.blackmovesList.addListSelectionListener(selectionListener);
        frame.setSize(1000,815);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(boardPanel);
        frame.add(movesPanel);
        frame.add(infoPanel);
        frame.setVisible(true);
    }
    private JPanel initiateBoardPanel(){
        JPanel board=new JPanel();
        board.setLayout(new GridLayout(8,8));
        board.setBounds(100,60,640,640);
        return board;
    }
    private JPanel initiateInfoPanel(){
        JPanel info=new JPanel();
        info.setBounds(0,0,100,800);
        info.setLayout(new GridLayout(10,2));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white queen.png")));
        info.add(takedwhitequeencount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white rook.png")));
        info.add(takedwhiterookcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white bishop.png")));
        info.add(takedwhitebishopcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white knight.png")));
        info.add(takedwhiteknightcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white pawn.png")));
        info.add(takedwhitepawncount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black queen.png")));
        info.add(takedblackqueencount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black rook.png")));
        info.add(takedblackrookcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black bishop.png")));
        info.add(takedblackbishopcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black knight.png")));
        info.add(takedblackknightcount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black pawn.png")));
        info.add(takedblackpawncount=new JLabel("x0"));
        info.setBackground(Color.cyan);
        return info;
    }
    private JPanel initiateMovesPanel(){
        JPanel moves=new JPanel();
        moves.setBounds(740,0,260,800);
        moves.setLayout(null);
        moves.setBackground(Color.cyan);
        this.movesSplitPane.setBounds(0,250,260,300);
        this.movesSplitPane.setDividerLocation(128);
        this.movesSplitPane.setDividerSize(4);
        this.movesSplitPane.setEnabled(false);
        moves.add(movesSplitPane);
        return moves;
    }
    public void selecttimer(){
        this.whitetimerlabel=new JLabel();
        whitetimerlabel.setBounds(50,650,210,100);
        whitetimerlabel.setFont(new Font("Consolas",Font.BOLD,48));
        this.movesPanel.add(whitetimerlabel);
        this.blacktimerlabel=new JLabel();
        blacktimerlabel.setBounds(50,100,210,100);
        blacktimerlabel.setFont(new Font("Consolas",Font.BOLD,48));
        this.movesPanel.add(blacktimerlabel);
        /*JDialog selecttempo=new JDialog(getFrame(),true);
        selecttempo.setUndecorated(true);
        selecttempo.setSize(300,200);
        selecttempo.setLocationRelativeTo(getFrame());
        selecttempo.setLayout(new GridLayout(1,4));
        JButton oneplusone=new JButton("1+1");
        JButton threeplustwo=new JButton("3+2");
        JButton fiveplusthree=new JButton("5+3");
        JButton tenpluszero=new JButton("10+0");
        SelectTempoListener selectTempoListener=new SelectTempoListener(oneplusone,threeplustwo,fiveplusthree,tenpluszero,this,selecttempo);
        oneplusone.addActionListener(selectTempoListener);
        threeplustwo.addActionListener(selectTempoListener);
        fiveplusthree.addActionListener(selectTempoListener);
        tenpluszero.addActionListener(selectTempoListener);
        selecttempo.add(oneplusone);
        selecttempo.add(threeplustwo);
        selecttempo.add(fiveplusthree);
        selecttempo.add(tenpluszero);
        selecttempo.setVisible(true);*/
        this.whitetimer=new Timer(1000,e->{
            whitetempo.decreaseRemainingtime(1);
            int minute=whitetempo.getRemainingtime()/60;
            int second=whitetempo.getRemainingtime()-minute*60;
            String m=minute+"";
            String s=second+"";
            if(minute<10){
                m=0+""+minute;
            }
            if(second<10){
                s=0+""+second;
            }
            whitetimerlabel.setText(m+":"+s);
            if(whitetempo.getRemainingtime()==0){
                for(Piece piece:whitepieces){
                    piece.getButton().setEnabled(false);
                    if(piece instanceof King){
                        piece.getButton().setBackground(Color.red);
                    }
                }
                JOptionPane.showMessageDialog(null,"BlackWins");
                whitetimer.stop();
            }
        });
        this.blacktimer=new Timer(1000,e ->{
            blacktempo.decreaseRemainingtime(1);
            int minute=blacktempo.getRemainingtime()/60;
            int second=blacktempo.getRemainingtime()-minute*60;
            String m=minute+"";
            String s=second+"";
            if(minute<10){
                m=0+""+minute;
            }
            if(second<10){
                s=0+""+second;
            }
            blacktimerlabel.setText(m+":"+s);
            if(blacktempo.getRemainingtime()==0){
                for(Piece piece:blackpieces){
                    piece.getButton().setEnabled(false);
                    if(piece instanceof King){
                        piece.getButton().setBackground(Color.red);
                    }
                }
                JOptionPane.showMessageDialog(null,"WhiteWins");
                blacktimer.stop();
            }
        });
        int minute=blacktempo.getRemainingtime()/60;
        String m=minute+"";
        if(minute<10){
            m=0+""+minute;
        }
        blacktimerlabel.setText(m+":"+'0'+'0');
        whitetimerlabel.setText(m+":"+'0'+'0');
        whitetimer.start();
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
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white knight.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if((row==7&&column==2)||(row==7&&column==5)){
                    Piece bishop=new Bishop(button,Color.WHITE);
                    whitepieces.add(bishop);
                    button.setEnabled(true);
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white bishop.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if((row==7&&column==0)||(row==7&&column==7)){
                    Piece rook=new Rook(button,Color.WHITE);
                    whitepieces.add(rook);
                    button.setEnabled(true);
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white rook.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if(row==7&&column==3){
                    Piece queen=new Queen(button,Color.WHITE);
                    whitepieces.add(queen);
                    button.setEnabled(true);
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white queen.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if(column==4&&row==7){
                    Piece king=new King(button,Color.WHITE);
                    whitepieces.add(king);
                    button.setEnabled(true);
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white king.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if((row==0&&column==1)||(row==0&&column==6)){
                    Piece knight =new Knight(button,Color.BLACK);
                    blackpieces.add(knight);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black knight.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if((row==0&&column==2)||(row==0&&column==5)){
                    Piece bishop=new Bishop(button,Color.BLACK);
                    blackpieces.add(bishop);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black bishop.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if((row==0&&column==0)||(row==0&&column==7)){
                    Piece rook=new Rook(button,Color.BLACK);
                    blackpieces.add(rook);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black rook.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if(row==0&&column==3){
                    Piece queen=new Queen(button,Color.BLACK);
                    blackpieces.add(queen);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black queen.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                }else if(column==4&&row==0){
                    Piece king=new King(button,Color.BLACK);
                    blackpieces.add(king);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black king.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                } else if (row==1) {
                    Piece pawn=new Pawn(button,Color.BLACK);
                    blackpieces.add(pawn);
                    button.setEnabled(false);
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black pawn.png");
                    button.setIcon(imageIcon);
                    button.setText("");
                } else if (row==6) {
                    Piece pawn=new Pawn(button,Color.WHITE);
                    whitepieces.add(pawn);
                    button.setEnabled(true);
                    ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white pawn.png");
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
