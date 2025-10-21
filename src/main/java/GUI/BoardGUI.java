package GUI;

import ActionListeners.BoardButtonHandler;
import ActionListeners.SelectionListener;
import Online.FireBaseConnection;
import Online.Game;
import Pieces.*;
import Logic.*;
import com.google.cloud.firestore.DocumentSnapshot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BoardGUI {
    private JFrame frame;
    private JPanel boardPanel;
    private JPanel infoPanel;
    private JPanel movesPanel;
    private ArrayList<JButton> buttonList;
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;
    private ChessModel chessModel;
    private JLabel whiteTimerLabel;
    private JLabel blackTimerLabel;
    private JLabel takenWhiteQueenCount;
    private JLabel takenWhiteKnightCount;
    private JLabel takenWhiteBishopCount;
    private JLabel takenWhiteRookCount;
    private JLabel takenWhitePawnCount;
    private JLabel takenBlackQueenCount;
    private JLabel takenBlackKnightCount;
    private JLabel takenBlackBishopCount;
    private JLabel takenBlackRookCount;
    private JLabel takenBlackPawnCount;
    private DefaultListModel<Move> whiteMovesListModel;
    private DefaultListModel<Move> blackMovesListModel;
    private JList<Move> whiteMovesList;
    private JList<Move> blackMovesList;
    private JSplitPane movesSplitPane;
    private Timer whitetimer;
    private Timer blacktimer;
    private Tempo whitetempo;
    private Tempo blacktempo;
    private String colorOfThePlayer;
    private Game game;
    private BoardButtonHandler boardButtonHandler;

    public BoardButtonHandler getBoardButtonHandler() {
        return boardButtonHandler;
    }
    public String getColorOfThePlayer() {
        return colorOfThePlayer;
    }
    public Game getGame() {
        return game;
    }
    public JLabel getBlackTimerLabel() {
        return blackTimerLabel;
    }
    public JLabel getWhiteTimerLabel() {
        return whiteTimerLabel;
    }
    public Tempo getBlacktempo() {
        return blacktempo;
    }
    public Tempo getWhitetempo() {
        return whitetempo;
    }
    public Timer getWhitetimer() {
        return whitetimer;
    }
    public Timer getBlacktimer() {
        return blacktimer;
    }
    public JList<Move> getWhiteMovesList() {
        return whiteMovesList;
    }
    public DefaultListModel<Move> getWhiteMovesListModel() {
        return whiteMovesListModel;
    }
    public JPanel getInfoPanel() {
        return infoPanel;
    }
    public JPanel getBoardPanel() {
        return boardPanel;
    }
    public JFrame getFrame() {
        return frame;
    }
    public ArrayList<JButton> getButtonList() {
        return buttonList;
    }
    public JLabel getTakenWhiteQueenCount() {
        return takenWhiteQueenCount;
    }
    public JLabel getTakenWhiteKnightCount() {
        return takenWhiteKnightCount;
    }
    public JLabel getTakenWhiteBishopCount() {
        return takenWhiteBishopCount;
    }
    public JLabel getTakenWhiteRookCount() {
        return takenWhiteRookCount;
    }
    public JLabel getTakenWhitePawnCount() {
        return takenWhitePawnCount;
    }
    public JLabel getTakenBlackQueenCount() {
        return takenBlackQueenCount;
    }
    public JLabel getTakenBlackKnightCount() {
        return takenBlackKnightCount;
    }
    public JLabel getTakenBlackBishopCount() {
        return takenBlackBishopCount;
    }
    public JLabel getTakenBlackRookCount() {
        return takenBlackRookCount;
    }
    public JLabel getTakenBlackPawnCount() {
        return takenBlackPawnCount;
    }
    public ArrayList<Piece> getWhitePieces() {
        return whitePieces;
    }
    public ArrayList<Piece> getBlackPieces() {
        return blackPieces;
    }
    public DefaultListModel<Move> getBlackMovesListModel() {
        return blackMovesListModel;
    }
    public JList<Move> getBlackMovesList() {
        return blackMovesList;
    }

    public BoardGUI(String colorOfThePlayer, Game game){
        this.game=game;
        this.colorOfThePlayer = colorOfThePlayer;
        this.frame=new JFrame("Chess Game");
        this.whitePieces =new ArrayList<>();
        this.blackPieces =new ArrayList<>();
        this.boardPanel=initiateBoardPanel();
        this.blackMovesListModel = new DefaultListModel<>();
        this.blackMovesList = new JList<>(blackMovesListModel);
        JScrollPane blackMovesScrollPane = new JScrollPane(blackMovesList);
        this.whiteMovesListModel = new DefaultListModel<>();
        this.whiteMovesList = new JList<>(whiteMovesListModel);
        JScrollPane whiteMovesScrollPane = new JScrollPane(whiteMovesList);
        this.movesSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, whiteMovesScrollPane, blackMovesScrollPane);
        addButtonsToBoard(colorOfThePlayer);
        chessModel =new ChessModel(blackPieces, whitePieces,buttonList,this);
        addActionListeners();
        this.movesPanel = initiateMovesPanel();
        this.infoPanel =initiateInfoPanel();
        SelectionListener selectionListener = new SelectionListener(this);
        this.whiteMovesList.addListSelectionListener(selectionListener);
        this.blackMovesList.addListSelectionListener(selectionListener);
        frame.setSize(1000,815);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(boardPanel);
        frame.add(movesPanel);
        frame.add(infoPanel);
        frame.setVisible(true);
        FireBaseConnection.getDatabase()
                .collection("accounts")
                .document(game.getOpponent().getAccountId())
                .collection("gameArchive")
                .document(game.getOpponent().getId()+" ")
                .collection("moves")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (querySnapshot != null && !querySnapshot.isEmpty()&&querySnapshot.getDocuments().size()>game.getNumberOfMovesMade()) {
                        DocumentSnapshot documentSnapshot=querySnapshot.getDocuments().getLast();
                        chessModel.move(documentSnapshot);
                    }
                });

    }
    public BoardGUI(){}
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
        info.add(takenWhiteQueenCount =new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white rook.png")));
        info.add(takenWhiteRookCount=new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white bishop.png")));
        info.add(takenWhiteBishopCount =new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white knight.png")));
        info.add(takenWhiteKnightCount =new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/White/white pawn.png")));
        info.add(takenWhitePawnCount =new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black queen.png")));
        info.add(takenBlackQueenCount =new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black rook.png")));
        info.add(takenBlackRookCount =new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black bishop.png")));
        info.add(takenBlackBishopCount =new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black knight.png")));
        info.add(takenBlackKnightCount =new JLabel("x0"));
        info.add(new JLabel(new ImageIcon("src/main/java/PieceIcons/Black/black pawn.png")));
        info.add(takenBlackPawnCount =new JLabel("x0"));
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
    public void selectTimer(Tempo whitetempo, Tempo blacktempo){
        this.whitetempo=whitetempo;
        this.blacktempo=blacktempo;
        this.whiteTimerLabel =new JLabel();
        whiteTimerLabel.setBounds(50,650,210,100);
        whiteTimerLabel.setFont(new Font("Consolas",Font.BOLD,48));
        this.movesPanel.add(whiteTimerLabel);
        this.blackTimerLabel =new JLabel();
        blackTimerLabel.setBounds(50,100,210,100);
        blackTimerLabel.setFont(new Font("Consolas",Font.BOLD,48));
        this.movesPanel.add(blackTimerLabel);
        this.whitetimer=new Timer(1000,e->{
            whitetempo.setRemainingTime(whitetempo.getRemainingTime()-1);
            int minute=whitetempo.getRemainingTime()/60;
            int second=whitetempo.getRemainingTime()-minute*60;
            String m=minute+"";
            String s=second+"";
            if(minute<10){
                m=0+""+minute;
            }
            if(second<10){
                s=0+""+second;
            }
            whiteTimerLabel.setText(m+":"+s);
            if(whitetempo.getRemainingTime()==0){
                for(Piece piece: whitePieces){
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
            blacktempo.setRemainingTime(blacktempo.getRemainingTime()-1);
            int minute=blacktempo.getRemainingTime()/60;
            int second=blacktempo.getRemainingTime()-minute*60;
            String m=minute+"";
            String s=second+"";
            if(minute<10){
                m=0+""+minute;
            }
            if(second<10){
                s=0+""+second;
            }
            blackTimerLabel.setText(m+":"+s);
            if(blacktempo.getRemainingTime()==0){
                for(Piece piece: blackPieces){
                    piece.getButton().setEnabled(false);
                    if(piece instanceof King){
                        piece.getButton().setBackground(Color.red);
                    }
                }
                JOptionPane.showMessageDialog(null,"WhiteWins");
                blacktimer.stop();
            }
        });
        int minute=blacktempo.getRemainingTime()/60;
        String m=minute+"";
        if(minute<10){
            m=0+""+minute;
        }
        blackTimerLabel.setText(m+":"+'0'+'0');
        whiteTimerLabel.setText(m+":"+'0'+'0');
        whitetimer.start();
    }
    private void addButtonsToBoard(String color){
        this.buttonList=new ArrayList<>();
        if(color.equals("W")){
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
                        whitePieces.add(knight);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white knight.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if((row==7&&column==2)||(row==7&&column==5)){
                        Piece bishop=new Bishop(button,Color.WHITE);
                        whitePieces.add(bishop);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white bishop.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if((row==7&&column==0)||(row==7&&column==7)){
                        Piece rook=new Rook(button,Color.WHITE);
                        whitePieces.add(rook);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white rook.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if(row==7&&column==3){
                        Piece queen=new Queen(button,Color.WHITE);
                        whitePieces.add(queen);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white queen.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if(column==4&&row==7){
                        Piece king=new King(button,Color.WHITE);
                        whitePieces.add(king);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white king.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if((row==0&&column==1)||(row==0&&column==6)){
                        Piece knight =new Knight(button,Color.BLACK);
                        blackPieces.add(knight);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black knight.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if((row==0&&column==2)||(row==0&&column==5)){
                        Piece bishop=new Bishop(button,Color.BLACK);
                        blackPieces.add(bishop);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black bishop.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if((row==0&&column==0)||(row==0&&column==7)){
                        Piece rook=new Rook(button,Color.BLACK);
                        blackPieces.add(rook);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black rook.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if(row==0&&column==3){
                        Piece queen=new Queen(button,Color.BLACK);
                        blackPieces.add(queen);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black queen.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if(column==4&&row==0){
                        Piece king=new King(button,Color.BLACK);
                        blackPieces.add(king);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black king.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    } else if (row==1) {
                        Piece pawn=new Pawn(button,Color.BLACK);
                        blackPieces.add(pawn);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black pawn.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    } else if (row==6) {
                        Piece pawn=new Pawn(button,Color.WHITE);
                        whitePieces.add(pawn);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white pawn.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    } else {
                        button.setEnabled(false);
                    }
                    button.setDisabledIcon(button.getIcon());
                    boardPanel.add(button);
                    buttonList.add(button);
                }
            }
        }else if(color.equals("B")){
            for(int row=7;row>=0;row--) {
                for (int column = 7; column >= 0; column--) {
                    JButton button = new JButton();
                    button.setBounds((7 - column) * 80, (7 - row) * 80, 80, 80);
                    if ((row + column) % 2 == 0) {
                        button.setBackground(Color.WHITE);
                    } else {
                        button.setBackground(Color.LIGHT_GRAY);
                    }
                    if((row==7&&column==1)||(row==7&&column==6)){
                        Piece knight =new Knight(button,Color.WHITE);
                        whitePieces.add(knight);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white knight.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if((row==7&&column==2)||(row==7&&column==5)){
                        Piece bishop=new Bishop(button,Color.WHITE);
                        whitePieces.add(bishop);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white bishop.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if((row==7&&column==0)||(row==7&&column==7)){
                        Piece rook=new Rook(button,Color.WHITE);
                        whitePieces.add(rook);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white rook.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if(row==7&&column==3){
                        Piece queen=new Queen(button,Color.WHITE);
                        whitePieces.add(queen);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white queen.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if(column==4&&row==7){
                        Piece king=new King(button,Color.WHITE);
                        whitePieces.add(king);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white king.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if((row==0&&column==1)||(row==0&&column==6)){
                        Piece knight =new Knight(button,Color.BLACK);
                        blackPieces.add(knight);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black knight.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if((row==0&&column==2)||(row==0&&column==5)){
                        Piece bishop=new Bishop(button,Color.BLACK);
                        blackPieces.add(bishop);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black bishop.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if((row==0&&column==0)||(row==0&&column==7)){
                        Piece rook=new Rook(button,Color.BLACK);
                        blackPieces.add(rook);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black rook.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if(row==0&&column==3){
                        Piece queen=new Queen(button,Color.BLACK);
                        blackPieces.add(queen);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black queen.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    }else if(column==4&&row==0){
                        Piece king=new King(button,Color.BLACK);
                        blackPieces.add(king);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black king.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    } else if (row==1) {
                        Piece pawn=new Pawn(button,Color.BLACK);
                        blackPieces.add(pawn);
                        button.setEnabled(true);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/Black/black pawn.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    } else if (row==6) {
                        Piece pawn=new Pawn(button,Color.WHITE);
                        whitePieces.add(pawn);
                        button.setEnabled(false);
                        ImageIcon imageIcon=new ImageIcon("src/main/java/PieceIcons/White/white pawn.png");
                        button.setIcon(imageIcon);
                        button.setText("");
                    } else {
                        button.setEnabled(false);
                    }
                    button.setDisabledIcon(button.getIcon());
                    boardPanel.add(button);
                    buttonList.addFirst(button);
                }
            }
        }
    }
    public void addActionListeners(){
        this.boardButtonHandler = new BoardButtonHandler(chessModel, this);
        for(JButton button:buttonList){
            button.addActionListener(boardButtonHandler);
        }
    }
}