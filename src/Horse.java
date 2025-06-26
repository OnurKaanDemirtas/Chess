
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Horse extends Piece{
    public Horse(JButton button,Color color){
        super(button,color);
    }

    @Override
    public void showmoveableplaces(ArrayList<Piece> friendpieces, ArrayList<JButton> board, Dimension boarddimension) {
        System.out.println("it is working");
        for(JButton button:board){
            Point locationofbutton=button.getLocation();
            int denominator=(int)(boarddimension.getWidth()/8);
            if((Math.abs((locationofbutton.getX()-getLocation().getX()))/denominator==2&&Math.abs((locationofbutton.getY()-getLocation().getY()))/denominator==1)||(Math.abs((locationofbutton.getX()-getLocation().getX()))/denominator==1&&Math.abs((locationofbutton.getY()-getLocation().getY()))/denominator==2)||locationofbutton.equals(getLocation())) {
                if (isthereafriendpiece(friendpieces, locationofbutton)) {
                    button.setEnabled(true);
                    button.setBackground(Color.RED);
                } else {
                    button.setEnabled(false);
                }
            }

            }
    }
}
