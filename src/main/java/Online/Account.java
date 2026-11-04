package Online;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * A player account, stored in Firestore as accounts/account{id}.
 * Firestore maps this class through its getters and setters, so it needs the empty constructor.
 */
public class Account {
    private String userName;
    private String password;
    private int id;
    private int Rating;
    private boolean online;
    private int gameSize;

    public int getGameSize() {
        return gameSize;
    }

    public void setGameSize(int gameSize) {
        this.gameSize = gameSize;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public int getRating() {
        return Rating;
    }

    public boolean isOnline() {
        return online;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Account(){}

    public Account(String userName,String password,int id){
        this.userName=userName;
        this.password=password;
        this.Rating=1000;
        this.id=id;
        this.online=false;
        this.gameSize=0;
    }

    /** Firestore document id of this account. */
    @Override
    public String toString() {
        return "account"+id;
    }

    /**
     * Passwords are never stored as plain text, only as their SHA-256 hash.
     * (Fine for a hobby project; a real app should use Firebase Authentication.)
     */
    public static String hashPassword(String password) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
