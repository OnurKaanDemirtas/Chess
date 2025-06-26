import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BoardButtonHandler implements ActionListener {
    private chessmodel model;
    private BoardGUI boardGUI;
    private Piece clickedpiece;
    private JButton clickedpiecesbutton;

    public BoardButtonHandler(chessmodel model,BoardGUI boardGUI){
        this.model=model;
        this.boardGUI=boardGUI;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(clickedpiece!=null){
             if(e.getSource()!=clickedpiecesbutton){
                 model.movePiece(clickedpiece,((JButton)e.getSource()));
                 clickedpiecesbutton.setText("");
                 ((JButton)((JButton) e.getSource())).setText("Horse");
                 clickedpiecesbutton=null;
                 clickedpiece=null;
                 boardGUI.validate();
             }
        }else{
            this.clickedpiece=getClickedPiece(e.getSource());
            this.clickedpiecesbutton=(JButton) e.getSource();
            if(clickedpiece instanceof Horse){
                Horse horse=(Horse) clickedpiece;
                if(horse.getColor().equals(Color.WHITE)){
                    horse.showmoveableplaces(friendpiecelist(model.getWhitepieces()),model.getBoard(),model.getBoarddimension());
                    boardGUI.validate();
                }else{
                    horse.showmoveableplaces(friendpiecelist((model.getBlackpieces())),model.getBoard(),model.getBoarddimension());
                }
            }
        }
    }
    public Piece getClickedPiece(Object object){
        for(Piece piece: model.getWhitepieces()){
            if(object==piece.getButton()){
                return piece;
            }
        }
        for(Piece piece: model.getBlackpieces()){
            if(object==piece.getButton()){
                return piece;
            }
        }
        return null;
    }
    public void ResetBoard(){
        JOptionPane.showMessageDialog(null,"Board Reseted");
    }
    public ArrayList<Piece> friendpiecelist(ArrayList<Piece> pieces){
        ArrayList<Piece> friendpieces=new ArrayList<>();
        for(Piece piece:model.getWhitepieces()){
            if(!piece.equals(clickedpiece)){
                friendpieces.add(piece);
            }
        }
        return friendpieces;
    }
}
