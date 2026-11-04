package GUI;

import Logic.Tempo;
import Online.Account;
import Online.FireBaseConnection;
import Online.Game;
import Online.MoveNotation;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Lists every game the player has played (accounts/{id}/gameArchive).
 * Double-clicking a game opens it in replay mode.
 */
public class GameArchiveGUI extends JDialog {
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final List<DocumentSnapshot> gameDocuments = new ArrayList<>();

    public GameArchiveGUI(JFrame parent, Account player) {
        super(parent, "Game Archive - " + player.getUserName(), false);
        setSize(700, 450);
        setLayout(new BorderLayout(10, 10));
        tableModel = new DefaultTableModel(new String[]{"#", "Date", "Opponent", "Color", "Time", "Result", "Rating", "Moves"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int[] columnWidths = {35, 130, 150, 60, 50, 90, 60, 55};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    openSelectedGame();
                }
            }
        });
        JLabel hint = new JLabel("Double-click a game to replay it.", SwingConstants.CENTER);
        hint.setOpaque(true);
        hint.setBackground(Color.CYAN);
        hint.setFont(hint.getFont().deriveFont(Font.BOLD, 14f));
        hint.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(hint, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setLocationRelativeTo(parent);
        setVisible(true);
        loadGames(player);
    }

    private void loadGames(Account player) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<List<QueryDocumentSnapshot>, Void>() {
            @Override
            protected List<QueryDocumentSnapshot> doInBackground() throws Exception {
                return FireBaseConnection.getDatabase().collection("accounts").document(player.toString())
                        .collection("gameArchive").get().get().getDocuments();
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    List<QueryDocumentSnapshot> documents = new ArrayList<>(get());
                    // document ids are "1 ", "2 ", ... -> newest game first
                    documents.sort(Comparator.comparingInt((QueryDocumentSnapshot d) -> parseGameNumber(d.getId())).reversed());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    for (QueryDocumentSnapshot document : documents) {
                        Game game;
                        try {
                            game = document.toObject(Game.class);
                        } catch (RuntimeException e) {
                            System.err.println("Skipping archived game " + document.getId() + ": " + e.getMessage());
                            continue;
                        }
                        gameDocuments.add(document);
                        String rating = game.getResult() == null ? "" : (game.getRatingChange() >= 0 ? "+" : "") + game.getRatingChange();
                        tableModel.addRow(new Object[]{
                                parseGameNumber(document.getId()),
                                game.getCreatedAt() == 0 ? "-" : dateFormat.format(new Date(game.getCreatedAt())),
                                game.getOpponent().getPlayerName() + " (" + game.getOpponent().getRating() + ")",
                                "W".equals(game.getColor()) ? "White" : "Black",
                                game.tempoName(),
                                game.getResult() == null ? "Unfinished" : game.getResult(),
                                rating,
                                (game.getNumberOfMovesMade() + 1) / 2
                        });
                    }
                    if (documents.isEmpty()) {
                        JOptionPane.showMessageDialog(GameArchiveGUI.this, "You haven't played any games yet.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(GameArchiveGUI.this, "Could not load your games: " + e.getMessage());
                }
            }
        }.execute();
    }

    private static int parseGameNumber(String documentId) {
        try {
            return Integer.parseInt(documentId.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void openSelectedGame() {
        DocumentSnapshot document = gameDocuments.get(table.getSelectedRow());
        Game game = document.toObject(Game.class);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<List<MoveNotation>, Void>() {
            @Override
            protected List<MoveNotation> doInBackground() throws Exception {
                List<MoveNotation> moves = new ArrayList<>();
                // ids are 001, 002, ... so sorting by id gives the order the moves were played in
                for (QueryDocumentSnapshot move : document.getReference().collection("moves").get().get().getDocuments()) {
                    moves.add(move.toObject(MoveNotation.class));
                }
                return moves;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    openReplay(game, get());
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(GameArchiveGUI.this, "Could not load the moves: " + e.getMessage());
                }
            }
        }.execute();
    }

    /**
     * Opens a read-only board, plays all stored moves on it and adds
     * buttons to step through them. Uses the normal move list, so
     * clicking a move also works.
     */
    public static BoardGUI openReplay(Game game, List<MoveNotation> moves) {
        game.setNumberOfMovesMade(0);
        BoardGUI boardGUI = new BoardGUI(game.getColor(), game, true);
        Tempo tempo = game.getTempoForPlayer() != null ? game.getTempoForPlayer() : new Tempo(0, 0);
        boardGUI.selectTimer(new Tempo(tempo.getRemainingTime(), tempo.getTimeToAddedForEachMove()),
                new Tempo(tempo.getRemainingTime(), tempo.getTimeToAddedForEachMove()));
        for (MoveNotation move : moves) {
            if (!boardGUI.getChessModel().replayMove(move)) {
                System.err.println("Replay stopped: a stored move does not fit the board.");
                break;
            }
        }
        for (JButton button : boardGUI.getButtonList()) {
            button.setEnabled(false);
        }
        int[] ply = {boardGUI.getWhiteMovesListModel().getSize() + boardGUI.getBlackMovesListModel().getSize()};
        JButton previousButton = new JButton("<  Prev");
        JButton nextButton = new JButton("Next  >");
        previousButton.setBounds(5, 560, 122, 40);
        nextButton.setBounds(133, 560, 122, 40);
        previousButton.addActionListener(e -> {
            if (ply[0] > 1) {
                ply[0]--;
                selectPly(boardGUI, ply[0]);
            }
        });
        nextButton.addActionListener(e -> {
            if (ply[0] < boardGUI.getWhiteMovesListModel().getSize() + boardGUI.getBlackMovesListModel().getSize()) {
                ply[0]++;
                selectPly(boardGUI, ply[0]);
            }
        });
        boardGUI.getMovesPanel().add(previousButton);
        boardGUI.getMovesPanel().add(nextButton);
        boardGUI.getMovesPanel().repaint();
        return boardGUI;
    }

    /** Selects the n-th half-move (1 = white's first move) in the move list. */
    private static void selectPly(BoardGUI boardGUI, int ply) {
        int index = (ply - 1) / 2;
        JList<Move> list = (ply % 2 == 1) ? boardGUI.getWhiteMovesList() : boardGUI.getBlackMovesList();
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
    }
}
