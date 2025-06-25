import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardButtonHandler implements ActionListener {
    private chessmodel model;
    private BoardGUI boardGUI;

    public BoardButtonHandler(chessmodel model,BoardGUI boardGUI){
        this.model=model;
        this.boardGUI=boardGUI;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Piece piece=getClickedPiece(e.getSource());
        if(piece instanceof Horse){
            Horse horse=(Horse) piece;
            if(horse.getColor().equals(Color.WHITE)){
                horse.showmoveableplaces(model.getWhitepieces(),model.getBoard(),model.getBoarddimension());
            }else{
                horse.showmoveableplaces(model.getBlackpieces(),model.getBoard(),model.getBoarddimension());
            }
        }
    }
    public Piece getClickedPiece(Object object){
        for(Piece piece: model.getWhitepieces()){
            if(object.equals(piece)){
                return piece;
            }
        }
        for(Piece piece: model.getBlackpieces()){
            if(object.equals(piece)){
                return piece;
            }
        }
        return null;
    }
    public void ResetBoard(){
        JOptionPane.showMessageDialog(null,"Board Reseted");
    }
}
