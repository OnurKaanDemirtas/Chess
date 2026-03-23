package ActionListeners;

import GUI.CreateNewAccountGUI;
import GUI.MainPageGUI;
import GUI.LogInRegisterGUI;
import Online.Account;
import Online.FireBaseConnection;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LogInRegisterButtonHandler implements ActionListener {
    private LogInRegisterGUI LogInRegisterGUI;

    public LogInRegisterButtonHandler(LogInRegisterGUI LogInRegisterGUI){
        this.LogInRegisterGUI = LogInRegisterGUI;
    }
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource()== LogInRegisterGUI.getLogInButton()) {
            try {
                ApiFuture<QuerySnapshot> future = FireBaseConnection.getDatabase().collection("accounts").whereEqualTo("userName", LogInRegisterGUI.getUsernameTextField().getText()).whereEqualTo("password", LogInRegisterGUI.getPasswordTextField().getText()).get();
                List<QueryDocumentSnapshot> documents =future.get().getDocuments();
                if (!documents.isEmpty()) {
                    JOptionPane.showMessageDialog(LogInRegisterGUI, "Login Successful");
                    String documentId = documents.getFirst().getId();
                    Account player = documents.getFirst().toObject(Account.class);
                    player.setOnline(true);
                    FireBaseConnection.getDatabase().collection("accounts").document(documentId).update("online",true);
                    new MainPageGUI(player);
                    LogInRegisterGUI.dispose();
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            player.setOnline(false);
                            FireBaseConnection.getDatabase().collection("accounts").document(documentId).update("online",false);
                        } catch (Exception ignored) {
                        }
                    }));
                } else {
                    JOptionPane.showMessageDialog(LogInRegisterGUI, "Login Failed. Please check your username and password.");
                }
            }catch (Exception ex){
                JOptionPane.showMessageDialog(LogInRegisterGUI,"Login failed! Please try again");
            }
        } else if(event.getSource()== LogInRegisterGUI.getCreateNewAccountButton()) {
            new CreateNewAccountGUI(LogInRegisterGUI);
        }
    }
}
