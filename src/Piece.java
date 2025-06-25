import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public abstract class Piece implements Move{
    private JButton button;
    private Color color;

    public JButton getButton() {
        return button;
    }

    public void setButton(JButton button) {
        this.button = button;
    }

    public Point getLocation(){
        return button.getLocation();
    }

    public Color getColor() {
        return color;
    }

    public Piece(JButton button,Color color){
        this.button=button;
        this.color=color;
    }
    public boolean isthereafriendpiece(ArrayList<Piece> friendpieces, Point point){
        for(Piece piece:friendpieces){
            if(piece.getLocation().equals(point)){
                return false;
            }
        }
        return true;
    }
}
