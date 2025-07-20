import Pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BoardButtonHandler implements ActionListener {
    private chessmodel model;
    private BoardGUI boardGUI;
    private ArrayList<Piece> whitepieces;
    private ArrayList<Piece> blackpieces;
    private Piece selectedpiece;

    public BoardButtonHandler(chessmodel model,BoardGUI boardGUI){
        this.model=model;
        this.boardGUI=boardGUI;
        this.whitepieces=boardGUI.getWhitepieces();
        this.blackpieces=boardGUI.getBlackpieces();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Piece clickedPiece=getClickedPiece(e.getSource());
        model.findmovableplaces(whitepieces);
        model.findmovableplaces(blackpieces);
        for(JButton button:clickedPiece.getMovableplaces()){
            button.setBackground(Color.blue);
        }
        boardGUI.validate();
    }
    public Piece getClickedPiece(Object object){
        for(Piece piece: model.getWhitepieces()){
            if(object.equals(piece.getButton())){
                return piece;
            }
        }
        for(Piece piece: model.getBlackpieces()){
            if(object.equals(piece.getButton())){
                return piece;
            }
        }
        return null;
    }
    public void ResetBoard(){
        JOptionPane.showMessageDialog(null,"Board Reseted");
    }
}
