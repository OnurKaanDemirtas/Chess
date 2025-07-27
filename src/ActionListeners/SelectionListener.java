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
        if (boardGUI.getWhitemovesListModel().getSize() > boardGUI.getBlackmovesListModel().getSize()) {
            this.currentMove = boardGUI.getWhitemovesListModel().getElementAt(boardGUI.getWhitemovesListModel().getSize() - 1);
        } else if (boardGUI.getBlackmovesListModel().getSize() >= boardGUI.getWhitemovesListModel().getSize()) {
            this.currentMove = boardGUI.getBlackmovesListModel().getElementAt(boardGUI.getBlackmovesListModel().getSize() - 1);
        }
        Move selectedMove = null;
        if (e.getSource()==boardGUI.getWhitemovesList()) {
            boardGUI.getBlackmovesList().removeListSelectionListener(this);
            boardGUI.getBlackmovesList().clearSelection();
            boardGUI.getBlackmovesList().addListSelectionListener(this);
            selectedMove = boardGUI.getWhitemovesList().getSelectedValue();
        } else if (e.getSource()==boardGUI.getBlackmovesList()) {
            boardGUI.getWhitemovesList().removeListSelectionListener(this);
            boardGUI.getWhitemovesList().clearSelection();
            boardGUI.getWhitemovesList().addListSelectionListener(this);
            selectedMove = boardGUI.getBlackmovesList().getSelectedValue();
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
