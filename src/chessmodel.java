import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class chessmodel {
    private ArrayList<Piece> blackpieces;
    private ArrayList<Piece> whitepieces;
    private ArrayList<Piece> takedblackpieces=new ArrayList<>();
    private ArrayList<Piece> takedwhitepieces=new ArrayList<>();
    private ArrayList<JButton> board;
    private Dimension boarddimension;

    public ArrayList<Piece> getBlackpieces() {
        return blackpieces;
    }

    public ArrayList<Piece> getWhitepieces() {
        return whitepieces;
    }

    public ArrayList<JButton> getBoard() {
        return board;
    }

    public Dimension getBoarddimension() {
        return boarddimension;
    }

    public chessmodel(ArrayList<Piece> blackpieces, ArrayList<Piece> whitepieces, ArrayList<JButton> board, Dimension boarddimension){
        this.blackpieces=blackpieces;
        this.whitepieces=whitepieces;
        this.board=board;
        this.boarddimension=boarddimension;
    }
    public void movePiece(Piece piece,JButton button){
        piece.setButton(button);
        if(piece.getColor().equals(Color.WHITE)){
             Piece takedpiece=getPiece(button.getLocation(),blackpieces);
             if(takedpiece!=null){
                 blackpieces.remove(takedpiece);
                 takedblackpieces.add(takedpiece);
             }
        }else{
            Piece takedpiece=getPiece(button.getLocation(),whitepieces);
            if(takedpiece!=null){
                whitepieces.remove(takedpiece);
                takedwhitepieces.add(takedpiece);
            }
        }
    }
    public Piece getPiece(Point point,ArrayList<Piece> oppositepiece){
        for(Piece piece:oppositepiece){
            if(piece.getLocation().equals(point)){
                return piece;
            }
        }
        return null;
    }
}
