package ActionListeners;

import Logic.*;
import PieceIcons.CircleIcon;
import Pieces.*;
import GUI.BoardGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BoardButtonHandler implements ActionListener {
    private final ChessModel model;
    private ArrayList<Piece> whitepieces;
    private ArrayList<Piece> blackpieces;
    private int selectedpieceindex;
    private Color whoseturnisit;

    public BoardButtonHandler(ChessModel model, BoardGUI boardGUI) {
        this.model = model;
        this.whitepieces = boardGUI.getWhitepieces();
        this.blackpieces = boardGUI.getBlackpieces();
        whoseturnisit = Color.WHITE;
        selectedpieceindex = -1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        Icon circleIcon = new CircleIcon(20, Color.GREEN);
        if(whoseturnisit.equals(Color.WHITE)){
            int index= getClickedPiecesIndexforWhitePieces(button);
            if(index!=-1){
                if(selectedpieceindex==-1){
                    selectedpieceindex=index;
                    for(JButton b: whitepieces.get(selectedpieceindex).getPlaybleplaces()){
                        b.setEnabled(true);
                        if(!isthereaenemypiece(b, blackpieces)) {
                            b.setIcon(circleIcon);
                        }
                    }
                }else{
                    for(JButton b: whitepieces.get(selectedpieceindex).getPlaybleplaces()){
                        b.setEnabled(false);
                        if(!isthereaenemypiece(b, blackpieces)) {
                            b.setIcon(null);
                        }
                    }
                    if(selectedpieceindex==index){
                        selectedpieceindex=-1;
                    }else{
                        selectedpieceindex=index;
                        for (JButton b:whitepieces.get(selectedpieceindex).getPlaybleplaces()) {
                            b.setEnabled(true);
                            if (!isthereaenemypiece(b, blackpieces)) {
                                b.setIcon(circleIcon);
                            }
                        }
                    }
                }
            }else{
                for(JButton b: whitepieces.get(selectedpieceindex).getPlaybleplaces()){
                    b.setEnabled(false);
                    if(!isthereaenemypiece(b, blackpieces)){
                        b.setIcon(null);
                    }
                }
                model.move(whitepieces.get(selectedpieceindex),button);
                for(Piece piece:blackpieces){
                    piece.getButton().setEnabled(true);
                }
                for(Piece piece:whitepieces){
                    piece.getButton().setEnabled(false);
                }
                selectedpieceindex=-1;
                whoseturnisit = Color.BLACK;
            }
        }else{
            int index= getClickedPiecesIndexforBlackPieces(button);
            if(index!=-1){
                if(selectedpieceindex==-1){
                    selectedpieceindex=index;
                    for(JButton b: blackpieces.get(selectedpieceindex).getPlaybleplaces()){
                        b.setEnabled(true);
                        if (!isthereaenemypiece(b, whitepieces)) {
                            b.setIcon(circleIcon);
                        }
                    }
                }else{
                    for(JButton b: blackpieces.get(selectedpieceindex).getPlaybleplaces()){
                        b.setEnabled(false);
                        if (!isthereaenemypiece(b, whitepieces)) {
                            b.setIcon(null);
                        }
                    }
                    if(selectedpieceindex==index){
                        selectedpieceindex=-1;
                    }else{
                        selectedpieceindex=index;
                        for (JButton b:blackpieces.get(selectedpieceindex).getPlaybleplaces()) {
                            b.setEnabled(true);
                            if(!isthereaenemypiece(b, whitepieces)) {
                                b.setIcon(circleIcon);
                            }
                        }
                    }
                }
            }else{
                for(JButton b: blackpieces.get(selectedpieceindex).getPlaybleplaces()){
                    b.setEnabled(false);
                    if(!isthereaenemypiece(b, whitepieces)){
                        b.setIcon(null);
                    }
                }
                model.move(blackpieces.get(selectedpieceindex),button);
                for(Piece piece:whitepieces){
                    piece.getButton().setEnabled(true);
                }
                for(Piece piece:blackpieces){
                    piece.getButton().setEnabled(false);
                }
                selectedpieceindex=-1;
                whoseturnisit = Color.WHITE;
            }
        }
    }

    public int getClickedPiecesIndexforWhitePieces(Object object){
        for(int i=0;i<whitepieces.size();i++){
            if(object.equals(whitepieces.get(i).getButton())){
                return i;
            }
        }
        return -1;
    }
    public int getClickedPiecesIndexforBlackPieces(Object object){
        for(int i=0;i<blackpieces.size();i++){
            if(object.equals(blackpieces.get(i).getButton())){
                return i;
            }
        }
        return -1;
    }
    public boolean isClickedtoPiece(JButton button){
        for(Piece piece:whitepieces){
            if(piece.getButton().equals(button)){
                return true;
            }
        }
        for(Piece piece:blackpieces){
            if(piece.getButton().equals(button)){
                return true;
            }
        }
        return false;
    }
    private boolean isthereaenemypiece(JButton button,ArrayList<Piece> piece){
        for (Piece p : piece) {
            if (p.getButton().equals(button)) {
                return true;
            }
        }
        return false;
    }
    public void ResetBoard(){
        JOptionPane.showMessageDialog(null,"Board Reseted");
    }
}
