package ActionListeners;

import GUI.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;

public class SelectionListener implements ListSelectionListener {
    private final BoardGUI boardGUI;
    private final ArrayList<Move> moves;
    private Move oldMove;
    private Move currentMove;

    public SelectionListener(BoardGUI boardGUI) {
        this.boardGUI = boardGUI;
        this.moves=new ArrayList<>();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        if (boardGUI.getWhiteMovesListModel().getSize() > boardGUI.getBlackMovesListModel().getSize()) {
            this.currentMove = boardGUI.getWhiteMovesListModel().getElementAt(boardGUI.getWhiteMovesListModel().getSize() - 1);
        } else if (boardGUI.getBlackMovesListModel().getSize() >= boardGUI.getWhiteMovesListModel().getSize()) {
            this.currentMove = boardGUI.getBlackMovesListModel().getElementAt(boardGUI.getBlackMovesListModel().getSize() - 1);
        }
        Move selectedMove = null;
        if (e.getSource()==boardGUI.getWhiteMovesList()) {
            boardGUI.getBlackMovesList().removeListSelectionListener(this);
            boardGUI.getBlackMovesList().clearSelection();
            boardGUI.getBlackMovesList().addListSelectionListener(this);
            selectedMove = boardGUI.getWhiteMovesList().getSelectedValue();
        } else if (e.getSource()==boardGUI.getBlackMovesList()) {
            boardGUI.getWhiteMovesList().removeListSelectionListener(this);
            boardGUI.getWhiteMovesList().clearSelection();
            boardGUI.getWhiteMovesList().addListSelectionListener(this);
            selectedMove = boardGUI.getBlackMovesList().getSelectedValue();
        }
        if (selectedMove != null) {
            if(moves.isEmpty()&&!selectedMove.equals(currentMove)){
                boardGUI.getFrame().add(selectedMove.getBoardPanel());
                boardGUI.getFrame().add(selectedMove.getInfoPanel());
                boardGUI.getBoardPanel().setVisible(false);
                boardGUI.getInfoPanel().setVisible(false);
                moves.add(selectedMove);
                oldMove=selectedMove;
            }else if(!moves.isEmpty()){
                if(selectedMove.equals(currentMove)){
                    boardGUI.getBoardPanel().setVisible(true);
                    boardGUI.getInfoPanel().setVisible(true);
                    oldMove.getBoardPanel().setVisible(false);
                    oldMove.getInfoPanel().setVisible(false);
                    oldMove=selectedMove;
                }else if(!moves.contains(selectedMove)){
                    boardGUI.getFrame().add(selectedMove.getBoardPanel());
                    boardGUI.getFrame().add(selectedMove.getInfoPanel());
                    selectedMove.getBoardPanel().setVisible(true);
                    selectedMove.getInfoPanel().setVisible(true);
                    if(oldMove.equals(currentMove)){
                        boardGUI.getBoardPanel().setVisible(false);
                        boardGUI.getInfoPanel().setVisible(false);
                    }else{
                        oldMove.getBoardPanel().setVisible(false);
                        oldMove.getInfoPanel().setVisible(false);
                    }
                    moves.add(selectedMove);
                    oldMove=selectedMove;
                }else{
                    selectedMove.getInfoPanel().setVisible(true);
                    selectedMove.getBoardPanel().setVisible(true);
                    if(oldMove.equals(currentMove)){
                        boardGUI.getBoardPanel().setVisible(false);
                        boardGUI.getInfoPanel().setVisible(false);
                    }else{
                        oldMove.getBoardPanel().setVisible(false);
                        oldMove.getInfoPanel().setVisible(false);
                    }
                    oldMove=selectedMove;
                }
            }
        }
    }
}
