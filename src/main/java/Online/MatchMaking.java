package Online;

public class MatchMaking {
    private int Rating;
    private String playername;

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public MatchMaking(int rating, String playername) {
        Rating = rating;
        this.playername = playername;
    }

    public MatchMaking() {
    }

    public String toString() {
        return Rating+"-"+playername;
    }
}
