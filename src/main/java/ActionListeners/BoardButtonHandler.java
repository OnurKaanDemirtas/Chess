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
    private final ArrayList<Piece> whitePieces;
    private final ArrayList<Piece> blackPieces;
    private int selectedPieceIndex;
    private String whoseTurnIsIt;

    public void setWhoseTurnIsIt(String whoseTurnIsIt) {
        this.whoseTurnIsIt = whoseTurnIsIt;
    }

    public String getWhoseTurnIsIt() {
        return whoseTurnIsIt;
    }

    public BoardButtonHandler(ChessModel model, BoardGUI boardGUI) {
        this.model = model;
        this.whitePieces = boardGUI.getWhitePieces();
        this.blackPieces = boardGUI.getBlackPieces();
        whoseTurnIsIt ="W";
        selectedPieceIndex = -1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            JButton button = (JButton) e.getSource();
            Icon circleIcon = new CircleIcon(20, Color.GREEN);
            if(whoseTurnIsIt.equals("W")){
                int index= getClickedPiecesIndexForWhitePieces(button);
                if(index!=-1){
                    if(selectedPieceIndex ==-1){
                        selectedPieceIndex =index;
                        for(JButton b: whitePieces.get(selectedPieceIndex).getPlaybleplaces()){
                            b.setEnabled(true);
                            if(!isThereEnemyPiece(b, blackPieces)) {
                                b.setIcon(circleIcon);
                            }
                        }
                    }else{
                        for(JButton b: whitePieces.get(selectedPieceIndex).getPlaybleplaces()){
                            b.setEnabled(false);
                            if(!isThereEnemyPiece(b, blackPieces)) {
                                b.setIcon(null);
                            }
                        }
                        if(selectedPieceIndex ==index){
                            selectedPieceIndex =-1;
                        }else{
                            selectedPieceIndex =index;
                            for (JButton b: whitePieces.get(selectedPieceIndex).getPlaybleplaces()) {
                                b.setEnabled(true);
                                if (!isThereEnemyPiece(b, blackPieces)) {
                                    b.setIcon(circleIcon);
                                }
                            }
                        }
                    }
                }else{
                    for(JButton b: whitePieces.get(selectedPieceIndex).getPlaybleplaces()){
                        b.setEnabled(false);
                        if(!isThereEnemyPiece(b, blackPieces)){
                            b.setIcon(null);
                        }
                    }
                    model.move(whitePieces.get(selectedPieceIndex),button,null);
                    selectedPieceIndex =-1;
                    whoseTurnIsIt = "B";
                }
            }else{
                int index= getClickedPiecesIndexForBlackPieces(button);
                if(index!=-1){
                    if(selectedPieceIndex ==-1){
                        selectedPieceIndex =index;
                        for(JButton b: blackPieces.get(selectedPieceIndex).getPlaybleplaces()){
                            b.setEnabled(true);
                            if (!isThereEnemyPiece(b, whitePieces)) {
                                b.setIcon(circleIcon);
                            }
                        }
                    }else{
                        for(JButton b: blackPieces.get(selectedPieceIndex).getPlaybleplaces()){
                            b.setEnabled(false);
                            if (!isThereEnemyPiece(b, whitePieces)) {
                                b.setIcon(null);
                            }
                        }
                        if(selectedPieceIndex ==index){
                            selectedPieceIndex =-1;
                        }else{
                            selectedPieceIndex =index;
                            for (JButton b: blackPieces.get(selectedPieceIndex).getPlaybleplaces()) {
                                b.setEnabled(true);
                                if(!isThereEnemyPiece(b, whitePieces)) {
                                    b.setIcon(circleIcon);
                                }
                            }
                        }
                    }
                }else{
                    for(JButton b: blackPieces.get(selectedPieceIndex).getPlaybleplaces()){
                        b.setEnabled(false);
                        if(!isThereEnemyPiece(b, whitePieces)){
                            b.setIcon(null);
                        }
                    }
                    model.move(blackPieces.get(selectedPieceIndex),button,null);
                    selectedPieceIndex =-1;
                    whoseTurnIsIt = "W";
                }
            }
        }catch (IndexOutOfBoundsException ignored){
        }
    }

    public int getClickedPiecesIndexForWhitePieces(Object object){
        for(int i = 0; i< whitePieces.size(); i++){
            if(object.equals(whitePieces.get(i).getButton())){
                return i;
            }
        }
        return -1;
    }
    public int getClickedPiecesIndexForBlackPieces(Object object){
        for(int i = 0; i< blackPieces.size(); i++){
            if(object.equals(blackPieces.get(i).getButton())){
                return i;
            }
        }
        return -1;
    }
    private boolean isThereEnemyPiece(JButton button, ArrayList<Piece> piece){
        for (Piece p : piece) {
            if (p.getButton().equals(button)) {
                return true;
            }
        }
        return false;
    }
}
