package GUI;

import Online.Account;
import Online.FireBaseConnection;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Shows the 20 highest rated players. The logged-in player's row is highlighted. */
public class LeaderboardGUI extends JDialog {
    private static final int PLAYERS_SHOWN = 20;

    public LeaderboardGUI(JFrame parent, Account player) {
        super(parent, "Leaderboard", false);
        setSize(400, 520);
        setLayout(new BorderLayout(10, 10));
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Rank", "Player", "Rating", "Games"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                boolean isMe = player.getUserName().equals(table.getValueAt(row, 1));
                if (!isSelected) {
                    cell.setBackground(isMe ? Color.CYAN : Color.WHITE);
                }
                cell.setFont(cell.getFont().deriveFont(isMe ? Font.BOLD : Font.PLAIN));
                return cell;
            }
        });
        JLabel title = new JLabel("Top " + PLAYERS_SHOWN + " players", SwingConstants.CENTER);
        title.setOpaque(true);
        title.setBackground(Color.MAGENTA);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setLocationRelativeTo(parent);
        setVisible(true);

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<List<QueryDocumentSnapshot>, Void>() {
            @Override
            protected List<QueryDocumentSnapshot> doInBackground() throws Exception {
                return FireBaseConnection.getDatabase().collection("accounts")
                        .orderBy("rating", Query.Direction.DESCENDING)
                        .limit(PLAYERS_SHOWN).get().get().getDocuments();
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    int rank = 1;
                    for (QueryDocumentSnapshot document : get()) {
                        Account account = document.toObject(Account.class);
                        tableModel.addRow(new Object[]{rank++, account.getUserName(), account.getRating(), account.getGameSize()});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(LeaderboardGUI.this, "Could not load the leaderboard: " + e.getMessage());
                }
            }
        }.execute();
    }
}
