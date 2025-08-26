package ActionListeners;

import GUI.CreateNewAccountGUI;
import Online.Account;
import Online.DatabaseSize;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CreateNewAccountButtonHandler implements ActionListener {
    private CreateNewAccountGUI createNewAccountGUI;

    public CreateNewAccountButtonHandler(CreateNewAccountGUI createNewAccountGUI){
        this.createNewAccountGUI=createNewAccountGUI;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==createNewAccountGUI.getCreateAccountButton()){
            String username=createNewAccountGUI.getNewUsernameTextField().getText();
            String password=createNewAccountGUI.getNewPasswordTextField().getText();
            if(!username.isEmpty()&&!password.isEmpty()){
                try {
                    Firestore database=createNewAccountGUI.getDatabase();
                    ApiFuture<QuerySnapshot> future=database.collection("accounts").whereEqualTo("userName",username).get();
                    List<QueryDocumentSnapshot> documents=future.get().getDocuments();
                    if(!documents.isEmpty()){
                        JOptionPane.showMessageDialog(createNewAccountGUI,"Username is already taken. Please choose another one.");
                        return;
                    }
                    DocumentReference documentReference=database.collection("Databasesize").document("size");
                    DatabaseSize databaseSize=documentReference.get().get().toObject(DatabaseSize.class);
                    assert databaseSize != null;
                    int size=databaseSize.getSize();
                    Account newAccount=new Account(username,password,size+1);
                    database.collection("accounts").document(newAccount.toString()).set(newAccount).get();
                    documentReference.update("size",size+1);
                    createNewAccountGUI.dispose();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(createNewAccountGUI,"Please enter a valid username and password.");
            }
        }
    }
}
