package Pieces;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public abstract class Piece{
    private JButton button;
    private Color color;
    private ArrayList<JButton> movableplaces;

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

    public ArrayList<JButton> getMovableplaces() {
        return movableplaces;
    }

    public Piece(JButton button,Color color){
        this.button=button;
        this.color=color;
        this.movableplaces=new ArrayList<>();
    }

    //public abstract void
}
