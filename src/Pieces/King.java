package Pieces;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class King extends Piece{
    private ArrayList<Piece> piecesthatcheck;

    public ArrayList<Piece> getPiecesthatcheck() {
        return piecesthatcheck;
    }

    public King(JButton button, Color color){
        super(button,color);
        this.piecesthatcheck=new ArrayList<>();
    }

    public void refreshpiecesthatcheck(){
        piecesthatcheck.subList(0, piecesthatcheck.size()).clear();
    }
}
