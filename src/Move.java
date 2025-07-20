import Pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public interface Move {
     public void showmoveableplaces(ArrayList<Piece> friendteam, ArrayList<JButton> board, Dimension boarddimension);
}
