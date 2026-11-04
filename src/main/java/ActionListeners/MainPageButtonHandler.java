package ActionListeners;

import GUI.*;
import Logic.Tempo;
import Online.Account;
import Online.FireBaseConnection;
import Online.Game;
import Online.MatchMaking;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Handles the main page buttons: the four time controls start matchmaking,
 * the other buttons open the leaderboard, game archive and profile windows.
 *
 * How matchmaking works:
 * 1. The player is put in the queue collection of the chosen time control ("3+2 Matchmaking").
 * 2. Every waiting client regularly tries to become the "matchmaker" by creating the lock
 *    document Matchmaker/"3+2 Matchmaking - matchmaker". Only one client can create it.
 * 3. The matchmaker pairs the queued players two by two (closest ratings together), creates a
 *    Game in both players' archives, removes them from the queue and deletes the lock.
 * 4. Each client waits until a game appears in its own archive and then opens the board.
 */
public class MainPageButtonHandler implements ActionListener {
    /** If a client crashes while holding the matchmaker lock, others may take it over after this time. */
    private static final long LOCK_TIMEOUT_MILLIS = 15_000;
    private static final long POLL_INTERVAL_MILLIS = 1_500;

    private final JButton onePlusOne;
    private final JButton threePlusTwo;
    private final JButton fivePlusThree;
    private final JButton tenPlusZero;
    private final JButton leaderBoardButton;
    private final JButton GameArchive;
    private final JButton logOutButton;
    private final JButton profileButton;
    private Account player;
    private final MainPageGUI mainPageGUI;

    public MainPageButtonHandler(MainPageGUI mainPageGUI, JButton onePlusOne, JButton threePlusTwo, JButton fivePlusThree, JButton tenPlusZero, JButton leaderBoardButton, JButton GameArchive, JButton logOutButton, JButton profileButton, Account player) {
        this.mainPageGUI=mainPageGUI;
        this.onePlusOne = onePlusOne;
        this.threePlusTwo = threePlusTwo;
        this.fivePlusThree = fivePlusThree;
        this.tenPlusZero = tenPlusZero;
        this.leaderBoardButton = leaderBoardButton;
        this.GameArchive = GameArchive;
        this.logOutButton = logOutButton;
        this.profileButton = profileButton;
        this.player = player;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == onePlusOne) {
            searchForGame("1+1 Matchmaking", 60, 1);
        } else if (e.getSource() == threePlusTwo) {
            searchForGame("3+2 Matchmaking", 180, 2);
        } else if (e.getSource() == fivePlusThree) {
            searchForGame("5+3 Matchmaking", 300, 3);
        } else if (e.getSource() == tenPlusZero) {
            searchForGame("10+0 Matchmaking", 600, 0);
        } else if (e.getSource() == leaderBoardButton) {
            new LeaderboardGUI(mainPageGUI, player);
        } else if (e.getSource() == GameArchive) {
            new GameArchiveGUI(mainPageGUI, player);
        } else if (e.getSource() == profileButton) {
            new ProfileGUI(mainPageGUI, player);
        } else if (e.getSource() == logOutButton) {
            logOut();
        }
    }

    /**
     * Puts the player in the queue and waits for an opponent in the background,
     * so the window stays responsive. A small dialog lets the player cancel.
     */
    private void searchForGame(String matchMakingType, int seconds, int increment) {
        setTempoButtonsEnabled(false);
        JDialog searchingDialog = new JDialog(mainPageGUI, "Matchmaking", false);
        JLabel label = new JLabel("Searching for a " + matchMakingType.replace(" Matchmaking", "") + " game...", SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.RED);
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);
        searchingDialog.setLayout(new BorderLayout(10, 10));
        searchingDialog.add(label, BorderLayout.CENTER);
        searchingDialog.add(cancelButton, BorderLayout.SOUTH);
        searchingDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        searchingDialog.setSize(300, 120);
        searchingDialog.setLocationRelativeTo(mainPageGUI);

        MatchMaking[] queueEntry = new MatchMaking[1];
        SwingWorker<Game, Void> worker = new SwingWorker<>() {
            @Override
            protected Game doInBackground() throws Exception {
                Firestore database = FireBaseConnection.getDatabase();
                player = reloadPlayer();
                int gameNumber = player.getGameSize() + 1;
                queueEntry[0] = new MatchMaking(player.getRating(), player.getUserName(), gameNumber, player.toString());
                database.collection(matchMakingType).document(queueEntry[0].toString()).set(queueEntry[0]).get();
                while (!isCancelled()) {
                    Game game = findGame(gameNumber);
                    if (game != null) {
                        return game;
                    }
                    tryToPairPlayers(matchMakingType, new Tempo(seconds, increment));
                    Thread.sleep(POLL_INTERVAL_MILLIS);
                }
                return null;
            }

            @Override
            protected void done() {
                searchingDialog.dispose();
                setTempoButtonsEnabled(true);
                if (isCancelled()) {
                    return;
                }
                try {
                    startGame(get());
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(mainPageGUI, "Matchmaking failed: " + e.getMessage());
                }
            }
        };
        cancelButton.addActionListener(e -> {
            worker.cancel(true);
            leaveQueue(matchMakingType, queueEntry[0]);
        });
        searchingDialog.setVisible(true);
        worker.execute();
    }

    /**
     * Removes the player from the queue. If the matchmaker paired us just before
     * the cancel, the game already exists, so we start it anyway.
     */
    private void leaveQueue(String matchMakingType, MatchMaking queueEntry) {
        if (queueEntry == null) {
            return;
        }
        new Thread(() -> {
            try {
                FireBaseConnection.getDatabase().collection(matchMakingType).document(queueEntry.toString()).delete().get();
                Game game = findGame(queueEntry.getId());
                if (game != null) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(mainPageGUI, "An opponent was found just before you cancelled.");
                        startGame(game);
                    });
                }
            } catch (Exception e) {
                System.err.println("Could not leave the queue: " + e.getMessage());
            }
        }).start();
    }

    /** Becomes the matchmaker if nobody else is, pairs everyone in the queue, then releases the lock. */
    private void tryToPairPlayers(String matchMakingType, Tempo tempo) throws Exception {
        DocumentReference lock = FireBaseConnection.getDatabase().collection("Matchmaker").document(matchMakingType + " - matchmaker");
        try {
            // create() fails if the document already exists, so only one client gets the lock
            lock.create(Map.of("playerName", player.getUserName(), "lockedAt", System.currentTimeMillis())).get();
        } catch (ExecutionException alreadyLocked) {
            DocumentSnapshot lockDocument = lock.get().get();
            Long lockedAt = lockDocument.getLong("lockedAt");
            if (lockDocument.exists() && (lockedAt == null || System.currentTimeMillis() - lockedAt > LOCK_TIMEOUT_MILLIS)) {
                lock.delete().get(); // stale lock left by a crashed client
            }
            return;
        }
        try {
            MatchmakingForAllTempo(tempo, tempo, matchMakingType);
        } finally {
            lock.delete().get();
        }
    }

    /** Pairs the queued players two by two. Players are sorted by rating so similar players meet. */
    private void MatchmakingForAllTempo(Tempo whitetempo, Tempo blacktempo, String MatchmakingType) throws Exception {
        List<MatchMaking> queue = new ArrayList<>();
        for (QueryDocumentSnapshot document : FireBaseConnection.getDatabase().collection(MatchmakingType).get().get().getDocuments()) {
            queue.add(document.toObject(MatchMaking.class));
        }
        queue.sort(Comparator.comparingInt(MatchMaking::getRating));
        Random random = new Random();
        // if the number of players is odd, the last one simply stays in the queue
        for (int i = 0; i + 1 < queue.size(); i += 2) {
            MatchMaking player1 = queue.get(i);
            MatchMaking player2 = queue.get(i + 1);
            FireBaseConnection.getDatabase().collection(MatchmakingType).document(player1.toString()).delete().get();
            FireBaseConnection.getDatabase().collection(MatchmakingType).document(player2.toString()).delete().get();
            if (random.nextBoolean()) {
                setGamesInDatabase(player1, player2, whitetempo, blacktempo);
            } else {
                setGamesInDatabase(player2, player1, whitetempo, blacktempo);
            }
        }
    }

    /** Creates the game in both players' archives. player1 plays white. */
    private void setGamesInDatabase(MatchMaking player1, MatchMaking player2, Tempo whitetempo, Tempo blacktempo) throws ExecutionException, InterruptedException {
        Game gameForPlayer1 = new Game(player1, player2, "W", whitetempo, blacktempo);
        Game gameForPlayer2 = new Game(player2, player1, "B", blacktempo, whitetempo);
        Account accountForPlayer1 = increaseGameSize(player1);
        Account accountForPlayer2 = increaseGameSize(player2);
        FireBaseConnection.getDatabase().collection("accounts").document(accountForPlayer1.toString()).collection("gameArchive").document(accountForPlayer1.getGameSize()+" ").set(gameForPlayer1).get();
        FireBaseConnection.getDatabase().collection("accounts").document(accountForPlayer2.toString()).collection("gameArchive").document(accountForPlayer2.getGameSize()+" ").set(gameForPlayer2).get();
    }

    private Account increaseGameSize(MatchMaking queuedPlayer) throws ExecutionException, InterruptedException {
        Account account = FireBaseConnection.getDatabase().collection("accounts").document(queuedPlayer.getAccountId()).get().get().toObject(Account.class);
        assert account != null;
        account.setGameSize(account.getGameSize()+1);
        FireBaseConnection.getDatabase().collection("accounts").document(account.toString()).update("gameSize", account.getGameSize()).get();
        return account;
    }

    /** @return the player's n-th game if the matchmaker has created it, otherwise null */
    private Game findGame(int gameNumber) throws ExecutionException, InterruptedException {
        DocumentSnapshot documentSnapshot = FireBaseConnection.getDatabase().collection("accounts").document(player.toString())
                .collection("gameArchive").document(gameNumber+" ").get().get();
        return documentSnapshot.exists() ? documentSnapshot.toObject(Game.class) : null;
    }

    private Account reloadPlayer() throws ExecutionException, InterruptedException {
        Account account = FireBaseConnection.getDatabase().collection("accounts").document(player.toString()).get().get().toObject(Account.class);
        return account != null ? account : player;
    }

    private void startGame(Game game) {
        BoardGUI boardGUI = game.displayGame();
        boardGUI.setOnClose(() -> mainPageGUI.setVisible(true));
        mainPageGUI.setVisible(false);
    }

    private void setTempoButtonsEnabled(boolean enabled) {
        onePlusOne.setEnabled(enabled);
        threePlusTwo.setEnabled(enabled);
        fivePlusThree.setEnabled(enabled);
        tenPlusZero.setEnabled(enabled);
    }

    /** Marks the player offline and goes back to the login window. */
    private void logOut() {
        FireBaseConnection.logIfFails(FireBaseConnection.getDatabase().collection("accounts").document(player.toString()).update("online", false), "logging out");
        mainPageGUI.dispose();
        LogInRegisterGUI logInRegisterGUI = new LogInRegisterGUI(FireBaseConnection.getDatabase());
        logInRegisterGUI.addActionListeners(new LogInRegisterButtonHandler(logInRegisterGUI));
    }
}
