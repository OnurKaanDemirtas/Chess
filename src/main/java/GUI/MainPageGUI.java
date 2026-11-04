package GUI;

import ActionListeners.MainPageButtonHandler;
import Online.Account;

import javax.swing.*;
import java.awt.*;

/**
 * The menu shown after logging in: the four time controls on top
 * (1+1 = 1 minute + 1 second per move) and the other options below.
 */
public class MainPageGUI extends JFrame {

    public MainPageGUI(Account player){
        super("Chess - "+player.getUserName()+" ("+player.getRating()+")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 10, 10));
        JPanel selectTempoPanel = new JPanel();
        selectTempoPanel.setLayout(new GridLayout(2, 2, 10, 10));
        JPanel otherOptionsPanel = new JPanel();
        otherOptionsPanel.setLayout(new GridLayout(4, 1, 10, 10));
        JButton oneplusone = new JButton("1+1");
        oneplusone.setBackground(Color.CYAN);
        JButton threeplustwo = new JButton("3+2");
        threeplustwo.setBackground(Color.CYAN);
        JButton fiveplusthree = new JButton("5+3");
        fiveplusthree.setBackground(Color.CYAN);
        JButton tenpluszero = new JButton("10+0");
        tenpluszero.setBackground(Color.CYAN);
        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setBackground(Color.MAGENTA);
        JButton GameArchive = new JButton("Game Archive");
        GameArchive.setBackground(Color.MAGENTA);
        JButton profileButton = new JButton("Profile");
        profileButton.setBackground(Color.MAGENTA);
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.RED);
        MainPageButtonHandler mainPageButtonHandler = new MainPageButtonHandler(this,oneplusone, threeplustwo, fiveplusthree, tenpluszero, leaderboardButton, GameArchive, logoutButton, profileButton,player);
        oneplusone.addActionListener(mainPageButtonHandler);
        threeplustwo.addActionListener(mainPageButtonHandler);
        fiveplusthree.addActionListener(mainPageButtonHandler);
        tenpluszero.addActionListener(mainPageButtonHandler);
        leaderboardButton.addActionListener(mainPageButtonHandler);
        GameArchive.addActionListener(mainPageButtonHandler);
        logoutButton.addActionListener(mainPageButtonHandler);
        profileButton.addActionListener(mainPageButtonHandler);
        for(JButton button : new JButton[]{oneplusone, threeplustwo, fiveplusthree, tenpluszero, leaderboardButton, GameArchive, profileButton, logoutButton}){
            // without this the macOS look-and-feel ignores the button colors
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFont(button.getFont().deriveFont(Font.BOLD, 15f));
        }
        oneplusone.setFont(oneplusone.getFont().deriveFont(Font.BOLD, 22f));
        threeplustwo.setFont(oneplusone.getFont());
        fiveplusthree.setFont(oneplusone.getFont());
        tenpluszero.setFont(oneplusone.getFont());
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
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
