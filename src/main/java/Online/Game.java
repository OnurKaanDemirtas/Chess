package Online;

import GUI.BoardGUI;
import com.google.cloud.firestore.Firestore;

public class Game {
    private BoardGUI boardGUI;
    private Firestore database;

    public Firestore getDatabase() {
        return database;
    }

    public BoardGUI getBoardGUI() {
        return boardGUI;
    }

    public void setBoardGUI(BoardGUI boardGUI) {
        this.boardGUI = boardGUI;
    }

    public void setDatabase(Firestore database) {
        this.database = database;
    }

    public Game(Firestore database){
        this.database=database;
        this.boardGUI=new BoardGUI(database);
    };

    public Game(){};
}
