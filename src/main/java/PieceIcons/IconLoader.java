package PieceIcons;

import Pieces.*;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads the piece images from the classpath (src/main/resources/PieceIcons).
 * Loading from the classpath instead of a hard-coded "src/main/java/..." path
 * means the icons also work when the game is started from a jar or another folder.
 * Every image is loaded once and then reused.
 */
public class IconLoader {
    private static final Map<String, ImageIcon> cache = new HashMap<>();

    /**
     * @param color     Color.WHITE or Color.BLACK
     * @param pieceName "king", "queen", "rook", "bishop", "knight" or "pawn"
     */
    public static ImageIcon get(Color color, String pieceName) {
        String path = color.equals(Color.WHITE)
                ? "/PieceIcons/White/white " + pieceName + ".png"
                : "/PieceIcons/Black/black " + pieceName + ".png";
        return cache.computeIfAbsent(path, p -> {
            URL url = IconLoader.class.getResource(p);
            if (url == null) {
                System.err.println("Icon not found on classpath: " + p);
                return new ImageIcon();
            }
            return new ImageIcon(url);
        });
    }

    /** Returns the icon that matches the type and color of the given piece. */
    public static ImageIcon forPiece(Piece piece) {
        return get(piece.getColor(), nameOf(piece));
    }

    private static String nameOf(Piece piece) {
        return switch (piece) {
            case King k -> "king";
            case Queen q -> "queen";
            case Rook r -> "rook";
            case Bishop b -> "bishop";
            case Knight n -> "knight";
            default -> "pawn";
        };
    }
}
