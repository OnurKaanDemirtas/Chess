package Online;

/**
 * A player waiting in a matchmaking queue. It is also stored inside each Game as
 * "player" and "opponent", so it keeps the rating at the time of the match,
 * the number of the game (id) and the Firestore account id.
 */
public class MatchMaking {
    private int Rating;
    private String playerName;
    private int id;
    private String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public MatchMaking(int rating, String playerName, int id, String accountId) {
        Rating = rating;
        this.playerName = playerName;
        this.id = id;
        this.accountId = accountId;
    }

    public MatchMaking() {
    }

    public String toString() {
        return Rating+"-"+ playerName;
    }
}
