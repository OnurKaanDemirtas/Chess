package GUI;

import ActionListeners.CreateNewAccountButtonHandler;
import com.google.cloud.firestore.Firestore;

import javax.swing.*;
import java.awt.*;

/** Small dialog for registering a new account. */
public class CreateNewAccountGUI extends JDialog {
    private final JTextField newUsernameTextField;
    private final JPasswordField newPasswordTextField;
    private final JButton createAccountButton;
    private final Firestore database;

    public JTextField getNewUsernameTextField() {
        return newUsernameTextField;
    }

    public JPasswordField getNewPasswordTextField() {
        return newPasswordTextField;
    }

    public JButton getCreateAccountButton() {
        return createAccountButton;
    }

    public Firestore getDatabase() {
        return database;
    }

    public CreateNewAccountGUI(LogInRegisterGUI parent) {
        super(parent, true);
        this.database = parent.getDatabase();
        setSize(320, 220);
        setLayout(null);
        setTitle("Create New Account");
        JPanel createAccountPanel = new JPanel();
        createAccountPanel.setLayout(new GridLayout(3, 2, 10, 10));
        createAccountPanel.setBounds(20, 20, 280, 140);
        this.newUsernameTextField = new JTextField();
        this.newPasswordTextField = new JPasswordField();
        this.createAccountButton = new JButton("Create Account");
        createAccountPanel.add(new JLabel("New username:"));
        createAccountPanel.add(newUsernameTextField);
        createAccountPanel.add(new JLabel("New password:"));
        createAccountPanel.add(newPasswordTextField);
        createAccountPanel.add(new JLabel());
        createAccountPanel.add(createAccountButton);
        add(createAccountPanel);
        setLocationRelativeTo(parent);
        createAccountButton.addActionListener(new CreateNewAccountButtonHandler(this));
        getRootPane().setDefaultButton(createAccountButton);
        setVisible(true);
    }
}
