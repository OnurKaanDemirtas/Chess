package GUI;

import Online.Account;
import Online.FireBaseConnection;
import Online.GameResult;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import javax.swing.*;
import java.awt.*;

/** Shows the player's rating and how many games they won, lost and drew. */
public class ProfileGUI extends JDialog {
    private final JLabel ratingLabel = new JLabel("...");
    private final JLabel gamesLabel = new JLabel("...");
    private final JLabel winsLabel = new JLabel("...");
    private final JLabel lossesLabel = new JLabel("...");
    private final JLabel drawsLabel = new JLabel("...");
    private final JLabel scoreLabel = new JLabel("...");

    public ProfileGUI(JFrame parent, Account player) {
        super(parent, "Profile", false);
        setSize(360, 380);
        setLayout(new BorderLayout(10, 10));
        JLabel nameLabel = new JLabel(player.getUserName(), SwingConstants.CENTER);
        nameLabel.setOpaque(true);
        nameLabel.setBackground(Color.MAGENTA);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 22f));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
        add(nameLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        addRow(statsPanel, "Rating", ratingLabel);
        addRow(statsPanel, "Games played", gamesLabel);
        addRow(statsPanel, "Wins", winsLabel);
        addRow(statsPanel, "Losses", lossesLabel);
        addRow(statsPanel, "Draws", drawsLabel);
        addRow(statsPanel, "Score", scoreLabel);
        add(statsPanel, BorderLayout.CENTER);
        setLocationRelativeTo(parent);
        setVisible(true);
        loadStats(player);
    }

    private void addRow(JPanel panel, String name, JLabel valueLabel) {
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(nameLabel.getFont().deriveFont(15f));
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 15f));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(nameLabel);
        panel.add(valueLabel);
    }

    private void loadStats(Account player) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<int[], Void>() {
            private Account freshAccount;

            @Override
            protected int[] doInBackground() throws Exception {
                DocumentReference account = FireBaseConnection.getDatabase().collection("accounts").document(player.toString());
                freshAccount = account.get().get().toObject(Account.class);
                int wins = 0, losses = 0, draws = 0;
                for (QueryDocumentSnapshot document : account.collection("gameArchive").get().get().getDocuments()) {
                    String result = document.getString("result");
                    if (GameResult.WIN.equals(result)) {
                        wins++;
                    } else if (GameResult.LOSS.equals(result)) {
                        losses++;
                    } else if (GameResult.DRAW.equals(result)) {
                        draws++;
                    }
                }
                return new int[]{wins, losses, draws};
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    int[] stats = get();
                    Account account = freshAccount != null ? freshAccount : player;
                    int finished = stats[0] + stats[1] + stats[2];
                    ratingLabel.setText(String.valueOf(account.getRating()));
                    gamesLabel.setText(String.valueOf(account.getGameSize()));
                    winsLabel.setText(String.valueOf(stats[0]));
                    lossesLabel.setText(String.valueOf(stats[1]));
                    drawsLabel.setText(String.valueOf(stats[2]));
                    scoreLabel.setText(finished == 0 ? "-" : Math.round((stats[0] + 0.5 * stats[2]) * 100.0 / finished) + "%");
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ProfileGUI.this, "Could not load your profile: " + e.getMessage());
                }
            }
        }.execute();
    }
}
