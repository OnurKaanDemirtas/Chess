import GUI.LogInGUI;
import Online.FireBaseConnection;

public class Main {
    public static void main(String[] args){
        FireBaseConnection fireBaseConnection=new FireBaseConnection();
        LogInGUI registerGUI=new LogInGUI(fireBaseConnection.getDatabase());
        registerGUI.addActionListeners(new ActionListeners.RegisterButtonHandler(registerGUI));
        //new BoardGUI(fireBaseConnection.getDatabase());
    }
}
