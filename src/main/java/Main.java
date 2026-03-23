import ActionListeners.LogInRegisterButtonHandler;
import GUI.LogInRegisterGUI;
import Online.FireBaseConnection;

public class Main {
    public static void main(String[] args) {
        LogInRegisterGUI registerGUI=new LogInRegisterGUI(FireBaseConnection.getDatabase());
        registerGUI.addActionListeners(new LogInRegisterButtonHandler(registerGUI));
    }
}
