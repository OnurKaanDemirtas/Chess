package GUI;

import com.google.cloud.firestore.Firestore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LogInRegisterGUI extends JFrame {
    private Firestore database;
    private JButton createNewAccountButton;
    private JButton logInButton;
    private JTextField usernameTextField;
    private JTextField passwordTextField;

    public Firestore getDatabase() {
        return database;
    }

    public JButton getCreateNewAccountButton() {
        return createNewAccountButton;
    }

    public JButton getLogInButton() {
        return logInButton;
    }

    public JTextField getUsernameTextField() {
        return usernameTextField;
    }

    public JTextField getPasswordTextField() {
        return passwordTextField;
    }

    public LogInRegisterGUI(Firestore database){
        this.database=database;
        setSize(400,400);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Register");
        JPanel registerPanel=new JPanel();
        registerPanel.setLayout(new GridLayout(2,2,10,5));
        registerPanel.setBounds(20,20,360,180);
        this.usernameTextField=new JTextField();
        this.passwordTextField=new JTextField();
        this.logInButton =new JButton("Log in");
        this.createNewAccountButton=new JButton("Create New Account");
        registerPanel.add(new JLabel("Username:"));
        registerPanel.add(usernameTextField);
        registerPanel.add(new JLabel("Password:"));
        registerPanel.add(passwordTextField);
        logInButton.setBounds(20,190,360,85);
        add(logInButton);
        createNewAccountButton.setBounds(20,285,360,85);
        add(createNewAccountButton);
        add(registerPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void addActionListeners(ActionListener actionListener){
        this.logInButton.addActionListener(actionListener);
        this.createNewAccountButton.addActionListener(actionListener);
    }
}
