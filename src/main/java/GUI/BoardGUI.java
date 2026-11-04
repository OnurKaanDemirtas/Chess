package GUI;

import PieceIcons.IconLoader;
import ActionListeners.BoardButtonHandler;
import ActionListeners.SelectionListener;
import Online.FireBaseConnection;
import Online.Game;
import Pieces.*;
import Logic.*;
import Online.GameResult;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.ListenerRegistration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * The game window: the board in the middle, captured pieces on the left,
 * clocks and the move list on the right. Clicking a move in the list shows
 * the board as it was after that move (see SelectionListener).
 */
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
    private final boolean replay;
    private Runnable onClose;
    private ListenerRegistration opponentMovesListener;

    public boolean isReplay() {
        return replay;
    }
    public ChessModel getChessModel() {
        return chessModel;
    }
    /** Code to run after the board window is closed (e.g. show the main page again). */
    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

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
    public JPanel getMovesPanel() {
        return movesPanel;
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
        this(colorOfThePlayer, game, false);
    }

    /**
     * @param colorOfThePlayer "W" or "B": the side this client plays (the board is drawn from that side)
     * @param replay true when an archived game is only being watched: no clocks, no clicks, no Firestore
     */
    public BoardGUI(String colorOfThePlayer, Game game, boolean replay){
        this.game=game;
        this.replay=replay;
        this.colorOfThePlayer = colorOfThePlayer;
        this.frame=new JFrame(replay ? "Replay - vs "+game.getOpponent().getPlayerName() : "Chess Game - vs "+game.getOpponent().getPlayerName());
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
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        frame.add(boardPanel);
        frame.add(movesPanel);
        frame.add(infoPanel);
        frame.setVisible(true);
        if(!replay){
            listenForOpponentMoves();
        }
    }

    /**
     * Every client writes every move of the game (its own and the opponent's) into its own
     * "moves" collection. So when the opponent's collection has more moves than we have
     * played so far, the newest one is the opponent's move and we play it on our board.
     */
    private void listenForOpponentMoves(){
        opponentMovesListener=FireBaseConnection.getDatabase()
                .collection("accounts")
                .document(game.getOpponent().getAccountId())
                .collection("gameArchive")
                .document(game.getOpponent().getId()+" ")
                .collection("moves")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        System.err.println("Lost connection to the opponent's moves: "+error.getMessage());
                        return;
                    }
                    // Firestore calls this on its own thread; Swing must only be touched on the EDT.
                    SwingUtilities.invokeLater(() -> {
                        if (querySnapshot != null && !querySnapshot.isEmpty()&&querySnapshot.getDocuments().size()>game.getNumberOfMovesMade()) {
                            DocumentSnapshot documentSnapshot=querySnapshot.getDocuments().getLast();
                            chessModel.move(documentSnapshot);
                        }
                    });
                });
    }

    /** Called when the window is closed. Leaving a running game counts as resigning. */
    public void close(){
        if(!replay&&!game.isGameFinished()){
            int answer=JOptionPane.showConfirmDialog(frame,
                    "The game is still running. Leaving now counts as resigning.\nDo you want to resign?",
                    "Resign?", JOptionPane.YES_NO_OPTION);
            if(answer!=JOptionPane.YES_OPTION){
                return;
            }
            GameResult.finishGame(this, GameResult.LOSS, "You resigned.");
        }
        stopClocks();
        if(opponentMovesListener!=null){
            opponentMovesListener.remove();
        }
        frame.dispose();
        if(onClose!=null){
            onClose.run();
        }
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
        info.add(new JLabel(IconLoader.get(Color.WHITE, "queen")));
        info.add(takenWhiteQueenCount =new JLabel("x0"));
        info.add(new JLabel(IconLoader.get(Color.WHITE, "rook")));
        info.add(takenWhiteRookCount=new JLabel("x0"));
        info.add(new JLabel(IconLoader.get(Color.WHITE, "bishop")));
        info.add(takenWhiteBishopCount =new JLabel("x0"));
        info.add(new JLabel(IconLoader.get(Color.WHITE, "knight")));
        info.add(takenWhiteKnightCount =new JLabel("x0"));
        info.add(new JLabel(IconLoader.get(Color.WHITE, "pawn")));
        info.add(takenWhitePawnCount =new JLabel("x0"));
        info.add(new JLabel(IconLoader.get(Color.BLACK, "queen")));
        info.add(takenBlackQueenCount =new JLabel("x0"));
        info.add(new JLabel(IconLoader.get(Color.BLACK, "rook")));
        info.add(takenBlackRookCount =new JLabel("x0"));
        info.add(new JLabel(IconLoader.get(Color.BLACK, "bishop")));
        info.add(takenBlackBishopCount =new JLabel("x0"));
        info.add(new JLabel(IconLoader.get(Color.BLACK, "knight")));
        info.add(takenBlackKnightCount =new JLabel("x0"));
        info.add(new JLabel(IconLoader.get(Color.BLACK, "pawn")));
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
    /** Creates the two clocks. White's clock starts right away (except in replays). */
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
        this.whitetimer=new Timer(1000,e->tick(whitetempo, whiteTimerLabel, "W"));
        this.blacktimer=new Timer(1000,e->tick(blacktempo, blackTimerLabel, "B"));
        whiteTimerLabel.setText(formatTime(whitetempo.getRemainingTime()));
        blackTimerLabel.setText(formatTime(blacktempo.getRemainingTime()));
        if(!replay){
            whitetimer.start();
        }
    }

    /** One second passed on the clock of the given side. */
    private void tick(Tempo tempo, JLabel label, String side){
        tempo.setRemainingTime(tempo.getRemainingTime()-1);
        label.setText(formatTime(tempo.getRemainingTime()));
        if(tempo.getRemainingTime()<=0){
            ArrayList<Piece> losingPieces=side.equals("W") ? whitePieces : blackPieces;
            for(Piece piece: losingPieces){
                if(piece instanceof King){
                    piece.getButton().setBackground(Color.red);
                }
            }
            String message=(side.equals("W") ? "Black" : "White")+" wins on time!";
            GameResult.finishGame(this, side.equals(colorOfThePlayer) ? GameResult.LOSS : GameResult.WIN, message);
        }
    }

    /**
     * Called after a move: adds the increment to the side that just moved
     * and hands the clock over to the other side.
     */
    public void switchClock(boolean whiteJustMoved){
        if(replay||whitetempo==null){
            return;
        }
        Tempo tempo=whiteJustMoved ? whitetempo : blacktempo;
        tempo.setRemainingTime(tempo.getRemainingTime()+tempo.getTimeToAddedForEachMove());
        (whiteJustMoved ? whiteTimerLabel : blackTimerLabel).setText(formatTime(tempo.getRemainingTime()));
        (whiteJustMoved ? whitetimer : blacktimer).stop();
        if(!game.isGameFinished()){
            (whiteJustMoved ? blacktimer : whitetimer).start();
        }
    }

    public void stopClocks(){
        if(whitetimer!=null){
            whitetimer.stop();
        }
        if(blacktimer!=null){
            blacktimer.stop();
        }
    }

    /** 125 -> "02:05" */
    public static String formatTime(int seconds){
        seconds=Math.max(seconds,0);
        return String.format("%02d:%02d", seconds/60, seconds%60);
    }

    /**
     * Creates the 64 squares and puts the pieces on their starting squares.
     * The board is drawn from the player's side (black sees it flipped), but
     * buttonList is always stored from white's point of view: index = row*8+column,
     * where row 0 is the 8th rank. ChessModel relies on that order.
     */
    private void addButtonsToBoard(String color){
        this.buttonList=new ArrayList<>();
        boolean isWhitePlayer=color.equals("W");
        for(int i=0;i<64;i++){
            int row=isWhitePlayer ? i/8 : 7-i/8;
            int column=isWhitePlayer ? i%8 : 7-i%8;
            JButton button=new JButton();
            button.setBackground((row+column)%2==0 ? Color.WHITE : Color.LIGHT_GRAY);
            makeSquare(button);
            Piece piece=createStartingPiece(row,column,button);
            if(piece!=null){
                (piece.getColor().equals(Color.WHITE) ? whitePieces : blackPieces).add(piece);
                button.setIcon(IconLoader.forPiece(piece));
                // only the player's own pieces are clickable
                button.setEnabled(piece.getColor().equals(isWhitePlayer ? Color.WHITE : Color.BLACK));
            }else{
                button.setEnabled(false);
            }
            button.setDisabledIcon(button.getIcon());
            boardPanel.add(button);
            if(isWhitePlayer){
                buttonList.add(button);
            }else{
                buttonList.addFirst(button);
            }
        }
    }
    /**
     * Makes a button look like a board square. Without this the macOS look-and-feel
     * ignores the background color and every square looks the same.
     */
    public static void makeSquare(JButton button){
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
    }
    /** Returns the piece that starts on the given square, or null for an empty square. */
    private Piece createStartingPiece(int row, int column, JButton button){
        Color color=row<=1 ? Color.BLACK : Color.WHITE;
        if(row==1||row==6){
            return new Pawn(button,color);
        }
        if(row!=0&&row!=7){
            return null;
        }
        return switch(column){
            case 0,7 -> new Rook(button,color);
            case 1,6 -> new Knight(button,color);
            case 2,5 -> new Bishop(button,color);
            case 3 -> new Queen(button,color);
            default -> new King(button,color);
        };
    }
    public void addActionListeners(){
        this.boardButtonHandler = new BoardButtonHandler(chessModel, this);
        for(JButton button:buttonList){
            button.addActionListener(boardButtonHandler);
        }
    }
}