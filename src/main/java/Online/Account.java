package Online;

import java.util.ArrayList;

public class Account {
    private String userName;
    private String password;
    private ArrayList<Game> gameHistory;
    private int id;
    private int Rating;
    private boolean online;
    private boolean lookingformatchmaking;

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<Game> getGameHistory() {
        return gameHistory;
    }

    public int getRating() {
        return Rating;
    }

    public boolean isOnline() {
        return online;
    }

    public void setGameHistory(ArrayList<Game> gameHistory) {
        this.gameHistory = gameHistory;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isLookingformatchmaking() {
        return lookingformatchmaking;
    }

    public void setLookingformatchmaking(boolean lookingformatchmaking) {
        this.lookingformatchmaking = lookingformatchmaking;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Account(){};

    public Account(String userName,String password,int id){
        this.userName=userName;
        this.password=password;
        this.gameHistory=new ArrayList<>();
        this.Rating=1000;
        this.id=id;
        this.online=false;
    }

    @Override
    public String toString() {
        return "account"+id;
    }
}
