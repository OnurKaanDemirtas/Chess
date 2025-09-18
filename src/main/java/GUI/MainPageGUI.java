package GUI;

import ActionListeners.SelectTempoListener;
import ActionListeners.SelectionListener;
import com.google.cloud.firestore.Firestore;

import javax.swing.*;
import java.awt.*;

public class MainPageGUI extends JFrame {
    private JButton oneplusone;
    private JButton threeplustwo;
    private JButton fiveplusthree;
    private JButton tenpluszero;
    private JButton leaderboardButton;
    private JButton GameArchive;
    private JButton logoutButton;
    private JButton profileButton;

    public JButton getFiveplusthree() {
        return fiveplusthree;
    }
    public JButton getGameArchive() {
        return GameArchive;
    }
    public JButton getLeaderboardButton() {
        return leaderboardButton;
    }
    public JButton getLogoutButton() {
        return logoutButton;
    }
    public JButton getOneplusone() {
        return oneplusone;
    }
    public JButton getProfileButton() {
        return profileButton;
    }
    public JButton getTenpluszero() {
        return tenpluszero;
    }
    public JButton getThreeplustwo() {
        return threeplustwo;
    }

    public MainPageGUI(Firestore database){
        setLayout(new GridLayout(2, 1, 10, 10));
        JPanel selectTempoPanel = new JPanel();
        selectTempoPanel.setLayout(new GridLayout(2, 2, 10, 10));
        JPanel otherOptionsPanel = new JPanel();
        otherOptionsPanel.setLayout(new GridLayout(4, 1, 10, 10));
        this.oneplusone = new JButton("1+1");
        this.oneplusone.setBackground(Color.CYAN);
        this.threeplustwo = new JButton("3+2");
        this.threeplustwo.setBackground(Color.CYAN);
        this.fiveplusthree = new JButton("5+3");
        this.fiveplusthree.setBackground(Color.CYAN);
        this.tenpluszero = new JButton("10+0");
        this.tenpluszero.setBackground(Color.CYAN);
        this.leaderboardButton = new JButton("Leaderboard");
        this.leaderboardButton.setBackground(Color.MAGENTA);
        this.GameArchive = new JButton("Game Archive");
        this.GameArchive.setBackground(Color.MAGENTA);
        this.profileButton = new JButton("Profile");
        this.profileButton.setBackground(Color.MAGENTA);
        this.logoutButton = new JButton("Logout");
        this.logoutButton.setBackground(Color.RED);
        SelectTempoListener selectTempoListener = new SelectTempoListener(oneplusone, threeplustwo, fiveplusthree, tenpluszero,database);
        this.oneplusone.addActionListener(selectTempoListener);
        this.threeplustwo.addActionListener(selectTempoListener);
        this.fiveplusthree.addActionListener(selectTempoListener);
        this.tenpluszero.addActionListener(selectTempoListener);
        selectTempoPanel.add(oneplusone);
        selectTempoPanel.add(threeplustwo);
        selectTempoPanel.add(fiveplusthree);
        selectTempoPanel.add(tenpluszero);
        otherOptionsPanel.add(leaderboardButton);
        otherOptionsPanel.add(GameArchive);
        otherOptionsPanel.add(profileButton);
        otherOptionsPanel.add(logoutButton);
        add(selectTempoPanel);
        add(otherOptionsPanel);
        setSize(400, 400);
        setVisible(true);
    }
}
