package ActionListeners;

import GUI.CreateNewAccountGUI;
import Online.Account;
import Online.DatabaseSize;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Creates a new account. Every account gets the next number from the
 * DatabaseSize/size counter, so the documents are account0, account1, ...
 */
public class CreateNewAccountButtonHandler implements ActionListener {
    private final CreateNewAccountGUI createNewAccountGUI;

    public CreateNewAccountButtonHandler(CreateNewAccountGUI createNewAccountGUI){
        this.createNewAccountGUI=createNewAccountGUI;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==createNewAccountGUI.getCreateAccountButton()){
            String username=createNewAccountGUI.getNewUsernameTextField().getText().trim();
            String password=new String(createNewAccountGUI.getNewPasswordTextField().getPassword());
            if(username.isEmpty()||password.isEmpty()){
                JOptionPane.showMessageDialog(createNewAccountGUI,"Please enter a valid username and password.");
                return;
            }
            try {
                Firestore database=createNewAccountGUI.getDatabase();
                List<QueryDocumentSnapshot> documents=database.collection("accounts").whereEqualTo("userName",username).get().get().getDocuments();
                if(!documents.isEmpty()){
                    JOptionPane.showMessageDialog(createNewAccountGUI,"Username is already taken. Please choose another one.");
                    return;
                }
                DocumentReference counter=database.collection("DatabaseSize").document("size");
                // A transaction makes sure two people registering at the same time never get the same id.
                // The counter document is created automatically on a fresh database.
                Account newAccount=database.runTransaction(transaction -> {
                    DocumentSnapshot snapshot=transaction.get(counter).get();
                    DatabaseSize databaseSize=snapshot.exists() ? snapshot.toObject(DatabaseSize.class) : new DatabaseSize(0);
                    assert databaseSize != null;
                    int size=databaseSize.getSize();
                    Account account=new Account(username,Account.hashPassword(password),size);
                    transaction.set(database.collection("accounts").document(account.toString()),account);
                    transaction.set(counter,new DatabaseSize(size+1));
                    return account;
                }).get();
                JOptionPane.showMessageDialog(createNewAccountGUI,"Account \""+newAccount.getUserName()+"\" created. You can log in now.");
                createNewAccountGUI.dispose();
            }catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(createNewAccountGUI,"Could not create the account: "+ex.getMessage());
            }
        }
    }
}
