package ActionListeners;

import GUI.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.HashMap;

public class SelectionListener implements ListSelectionListener {
    private final BoardGUI boardGUI;
    private final HashMap<Move, JFrame> moveFrames = new HashMap<>();
    private JPanel currentBoardPanel;
    private JPanel currentInfoPanel;
    private Move oldWhiteMove;
    private Move oldBlackMove;

    public SelectionListener(BoardGUI boardGUI) {
        this.boardGUI = boardGUI;
        this.currentBoardPanel =boardGUI.getBoardPanel();
        this.currentInfoPanel = boardGUI.getInfoPanel();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!boardGUI.getBlackmovesList().isSelectionEmpty()) {
            Move selectedMove = boardGUI.getBlackmovesListModel().getElementAt(boardGUI.getBlackmovesList().getSelectedIndex());
            if(oldBlackMove != null && !oldBlackMove.equals(selectedMove)) {
                boardGUI.getFrame().remove(oldBlackMove.getBoardPanel());
                boardGUI.getFrame().remove(oldBlackMove.getInfoPanel());
                if(boardGUI.getWhitemovesListModel().getSize()<=boardGUI.getBlackmovesListModel().getSize()&&boardGUI.getBlackmovesList().getSelectedIndex()==boardGUI.getBlackmovesListModel().getSize()-1){
                    boardGUI.getFrame().add(currentBoardPanel);
                    boardGUI.getFrame().add(currentInfoPanel);
                }else{
                    boardGUI.getFrame().add(selectedMove.getBoardPanel());
                    boardGUI.getFrame().add(selectedMove.getInfoPanel());
                }
            }else if(oldBlackMove==null){
                boardGUI.getFrame().remove(currentBoardPanel);
                boardGUI.getFrame().remove(currentInfoPanel);
                if(boardGUI.getWhitemovesListModel().getSize()<=boardGUI.getBlackmovesListModel().getSize()&&boardGUI.getBlackmovesList().getSelectedIndex()==boardGUI.getBlackmovesListModel().getSize()-1){
                    boardGUI.getFrame().add(currentBoardPanel);
                    boardGUI.getFrame().add(currentInfoPanel);
                }else{
                    boardGUI.getFrame().add(selectedMove.getBoardPanel());
                    boardGUI.getFrame().add(selectedMove.getInfoPanel());
                }
            }
            oldBlackMove = selectedMove;
            boardGUI.getFrame().setVisible(false);
            boardGUI.getFrame().setVisible(true);
            boardGUI.getBlackmovesList().clearSelection();
        } else if(!boardGUI.getWhitemovesList().isSelectionEmpty()) {
            Move selectedMove = boardGUI.getWhitemovesListModel().getElementAt(boardGUI.getWhitemovesList().getSelectedIndex());
            if(oldWhiteMove != null && !oldWhiteMove.equals(selectedMove)) {
                boardGUI.getFrame().remove(oldWhiteMove.getBoardPanel());
                boardGUI.getFrame().remove(oldWhiteMove.getInfoPanel());
                if(boardGUI.getWhitemovesListModel().getSize()>boardGUI.getBlackmovesListModel().getSize()&&boardGUI.getWhitemovesList().getSelectedIndex()==boardGUI.getWhitemovesListModel().getSize()-1){
                    boardGUI.getFrame().add(currentBoardPanel);
                    boardGUI.getFrame().add(currentInfoPanel);
                }else{
                    boardGUI.getFrame().add(selectedMove.getBoardPanel());
                    boardGUI.getFrame().add(selectedMove.getInfoPanel());
                }
            }else if(oldWhiteMove==null){
                boardGUI.getFrame().remove(currentBoardPanel);
                boardGUI.getFrame().remove(currentInfoPanel);
                if(boardGUI.getWhitemovesListModel().getSize()<=boardGUI.getBlackmovesListModel().getSize()&&boardGUI.getWhitemovesList().getSelectedIndex()==boardGUI.getWhitemovesListModel().getSize()-1){
                    boardGUI.getFrame().add(currentBoardPanel);
                    boardGUI.getFrame().add(currentInfoPanel);
                }else{
                    boardGUI.getFrame().add(selectedMove.getBoardPanel());
                    boardGUI.getFrame().add(selectedMove.getInfoPanel());
                }
            }
            oldWhiteMove = selectedMove;
            boardGUI.getFrame().setVisible(false);
            boardGUI.getFrame().setVisible(true);
            boardGUI.getWhitemovesList().clearSelection();
        }
    }
}
