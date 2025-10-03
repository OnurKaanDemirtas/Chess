import ActionListeners.LogInRegisterButtonHandler;
import GUI.BoardGUI;
import GUI.LogInRegisterGUI;
import Online.FireBaseConnection;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        LogInRegisterGUI registerGUI=new LogInRegisterGUI(FireBaseConnection.getDatabase());
        registerGUI.addActionListeners(new LogInRegisterButtonHandler(registerGUI));
        new BoardGUI();
    }
}
