package ActionListeners;

import GUI.CreateNewAccountGUI;
import GUI.RegisterGUI;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RegisterButtonHandler implements ActionListener {
    private RegisterGUI registerGUI;

    public RegisterButtonHandler(RegisterGUI registerGUI){
        this.registerGUI=registerGUI;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==registerGUI.getRegisterButton()) {
            try {
                ApiFuture<QuerySnapshot> future = registerGUI.getDatabase().collection("accounts").whereEqualTo("userName", registerGUI.getUsernameTextField().getText()).whereEqualTo("password", registerGUI.getPasswordTextField().getText()).get();
                List<QueryDocumentSnapshot> documents =future.get().getDocuments();
                if (!documents.isEmpty()) {
                    JOptionPane.showMessageDialog(registerGUI, "Login Successful");
                    registerGUI.dispose();
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
