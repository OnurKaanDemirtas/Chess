import ActionListeners.LogInRegisterButtonHandler;
import GUI.LogInRegisterGUI;
import Online.FireBaseConnection;
import com.google.cloud.firestore.Firestore;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Firestore database = FireBaseConnection.getDatabase();
            if (database == null) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to the database.\n\n"
                                + "Put your Firebase service-account key at\n"
                                + FireBaseConnection.DEFAULT_KEY_PATH + "\n"
                                + "or set the FIREBASE_KEY_PATH environment variable.\n"
                                + "See the README for setup steps.",
                        "Chess - database not available", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            LogInRegisterGUI registerGUI = new LogInRegisterGUI(database);
            registerGUI.addActionListeners(new LogInRegisterButtonHandler(registerGUI));
        });
    }
}
