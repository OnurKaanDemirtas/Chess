package GUI;

import ActionListeners.CreateNewAccountButtonHandler;
import com.google.cloud.firestore.Firestore;

import javax.swing.*;
import java.awt.*;

public class CreateNewAccountGUI extends JDialog {
    private final JTextField newUsernameTextField;
    private final JTextField newPasswordTextField;
    private final JButton createAccountButton;
    private final Firestore database;

    public JTextField getNewUsernameTextField() {
        return newUsernameTextField;
    }

    public JTextField getNewPasswordTextField() {
        return newPasswordTextField;
    }

    public JButton getCreateAccountButton() {
        return createAccountButton;
    }

    public Firestore getDatabase() {
        return database;
    }

    public CreateNewAccountGUI(RegisterGUI parent) {
        super(parent, true);
        this.database = parent.getDatabase();
        setSize(300, 200);
        setLayout(null);
        setTitle("Create New Account");
        JPanel createAccountPanel = new JPanel();
        createAccountPanel.setLayout(new GridLayout(3, 1, 10, 10));
        createAccountPanel.setBounds(20, 20, 260, 140);
        this.newUsernameTextField = new JTextField("Enter New Username");
        this.newPasswordTextField = new JTextField("Enter New Password");
        this.createAccountButton = new JButton("Create Account");
        createAccountPanel.add(newUsernameTextField);
        createAccountPanel.add(newPasswordTextField);
        createAccountPanel.add(createAccountButton);
        add(createAccountPanel);
        setLocationRelativeTo(parent);
        createAccountButton.addActionListener(new CreateNewAccountButtonHandler(this));
        setVisible(true);
    }
}
