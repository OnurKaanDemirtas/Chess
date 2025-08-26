import GUI.BoardGUI;
import GUI.RegisterGUI;
import Online.FireBaseConnection;

public class Main {
    public static void main(String[] args){
        FireBaseConnection fireBaseConnection=new FireBaseConnection();
        RegisterGUI registerGUI=new RegisterGUI(fireBaseConnection.getDatabase());
        registerGUI.addActionListeners(new ActionListeners.RegisterButtonHandler(registerGUI));
        new BoardGUI(fireBaseConnection.getDatabase());
    }
}
