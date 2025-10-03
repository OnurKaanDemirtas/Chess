package GUI;

import ActionListeners.MainPageButtonHandler;
import Online.Account;
import com.google.cloud.firestore.Firestore;

import javax.swing.*;
import java.awt.*;

public class MainPageGUI extends JFrame {
    
    public MainPageGUI(Account player){
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
        MainPageButtonHandler mainPageButtonHandler = new MainPageButtonHandler(oneplusone, threeplustwo, fiveplusthree, tenpluszero, leaderboardButton, GameArchive, logoutButton, profileButton,player);
        oneplusone.addActionListener(mainPageButtonHandler);
        threeplustwo.addActionListener(mainPageButtonHandler);
        fiveplusthree.addActionListener(mainPageButtonHandler);
        tenpluszero.addActionListener(mainPageButtonHandler);
        leaderboardButton.addActionListener(mainPageButtonHandler);
        GameArchive.addActionListener(mainPageButtonHandler);
        logoutButton.addActionListener(mainPageButtonHandler);
        profileButton.addActionListener(mainPageButtonHandler);
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
