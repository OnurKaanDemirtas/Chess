package Online;

import GUI.BoardGUI;
import Pieces.Piece;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;

import javax.swing.*;
import java.util.Map;

/**
 * Ends a game: stops the clocks, locks the board, saves the result to Firestore
 * and updates the player's Elo rating.
 *
 * Both clients detect checkmate, stalemate and time-outs on their own, so each client
 * only writes to ITS OWN account and archive. Both sides use the ratings stored in
 * the Game when the match was made, so the two rating changes always mirror each other.
 */
public class GameResult {
    public static final String WIN = "WIN";
    public static final String LOSS = "LOSS";
    public static final String DRAW = "DRAW";

    /** How much a single game can move the rating. */
    private static final int K_FACTOR = 32;

    /**
     * @param result  WIN, LOSS or DRAW from the point of view of this client's player
     * @param message text shown to the player, e.g. "White wins by checkmate!"
     */
    public static void finishGame(BoardGUI boardGUI, String result, String message) {
        Game game = boardGUI.getGame();
        if (game.isGameFinished()) {
            return; // already finished (e.g. time ran out right after mate)
        }
        game.setGameFinished(true);
        game.setResult(result);
        boardGUI.stopClocks();
        for (Piece piece : boardGUI.getWhitePieces()) {
            piece.getButton().setEnabled(false);
        }
        for (Piece piece : boardGUI.getBlackPieces()) {
            piece.getButton().setEnabled(false);
        }
        if (boardGUI.isReplay()) {
            return;
        }

        int playerRating = game.getPlayer().getRating();
        int ratingChange = calculateRatingChange(playerRating, game.getOpponent().getRating(), result);
        game.setRatingChange(ratingChange);

        DocumentReference account = FireBaseConnection.getDatabase()
                .collection("accounts").document(game.getPlayer().getAccountId());
        FireBaseConnection.logIfFails(account.collection("gameArchive").document(game.getPlayer().getId() + " ")
                .update(Map.of("gameFinished", true, "result", result, "ratingChange", ratingChange)), "saving game result");
        FireBaseConnection.logIfFails(account.update("rating", FieldValue.increment(ratingChange)), "updating rating");

        String sign = ratingChange >= 0 ? "+" : "";
        JOptionPane.showMessageDialog(boardGUI.getFrame(),
                message + "\n\nRating: " + playerRating + " -> " + (playerRating + ratingChange) + " (" + sign + ratingChange + ")",
                "Game over", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Standard Elo formula. */
    public static int calculateRatingChange(int playerRating, int opponentRating, String result) {
        double expectedScore = 1.0 / (1.0 + Math.pow(10, (opponentRating - playerRating) / 400.0));
        double score = switch (result) {
            case WIN -> 1.0;
            case DRAW -> 0.5;
            default -> 0.0;
        };
        return (int) Math.round(K_FACTOR * (score - expectedScore));
    }

    private GameResult() {
    }
}
