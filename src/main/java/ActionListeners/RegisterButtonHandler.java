package ActionListeners;

import GUI.CreateNewAccountGUI;
import GUI.MainPageGUI;
import GUI.LogInGUI;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RegisterButtonHandler implements ActionListener {
    private LogInGUI registerGUI;

    public RegisterButtonHandler(LogInGUI registerGUI){
        this.registerGUI=registerGUI;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==registerGUI.getLogInButton()) {
            try {
                ApiFuture<QuerySnapshot> future = registerGUI.getDatabase().collection("accounts").whereEqualTo("userName", registerGUI.getUsernameTextField().getText()).whereEqualTo("password", registerGUI.getPasswordTextField().getText()).get();
                List<QueryDocumentSnapshot> documents =future.get().getDocuments();
                if (!documents.isEmpty()) {
                    JOptionPane.showMessageDialog(registerGUI, "Login Successful");
                    registerGUI.dispose();
                    new MainPageGUI(registerGUI.getDatabase());
                } else {
                    JOptionPane.showMessageDialog(registerGUI, "Login Failed. Please check your username and password.");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        } else if(e.getSource()==registerGUI.getCreateNewAccountButton()) {
            new CreateNewAccountGUI(registerGUI);
        }
    }
}
