package ActionListeners;

import GUI.BoardGUI;
import Logic.Tempo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectTempoListener implements ActionListener {
    private final JButton oneplusone;
    private final JButton threeplustwo;
    private final JButton fiveplusthree;
    private final JButton tenpluszero;
    private final BoardGUI boardGUI;
    private final JDialog dialog;

    public SelectTempoListener(JButton oneplusone, JButton threeplustwo, JButton fiveplusthree, JButton tenpluszero, BoardGUI boardGUI,JDialog dialog){
        this.oneplusone=oneplusone;
        this.threeplustwo=threeplustwo;
        this.fiveplusthree=fiveplusthree;
        this.tenpluszero=tenpluszero;
        this.boardGUI=boardGUI;
        this.dialog=dialog;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==oneplusone){
            Tempo tempoforwhite =new Tempo(60,1);
            Tempo tempoforblack=new Tempo(60,1);
            boardGUI.setBlacktempo(tempoforblack);
            boardGUI.setWhitetempo(tempoforwhite);
            dialog.dispose();
        } else if (e.getSource()==threeplustwo) {
            Tempo tempoforwhite=new Tempo(180,2);
            Tempo tempoforblack=new Tempo(180,2);
            boardGUI.setBlacktempo(tempoforblack);
            boardGUI.setWhitetempo(tempoforwhite);
            dialog.dispose();
        } else if (e.getSource()==fiveplusthree) {
            Tempo tempoforwhite=new Tempo(300,3);
            Tempo tempoforblack=new Tempo(300,3);
            boardGUI.setBlacktempo(tempoforblack);
            boardGUI.setWhitetempo(tempoforwhite);
            dialog.dispose();
        } else if (e.getSource()==tenpluszero) {
            Tempo tempoforwhite=new Tempo(600,0);
            Tempo tempoforblack=new Tempo(600,0);
            boardGUI.setBlacktempo(tempoforblack);
            boardGUI.setWhitetempo(tempoforwhite);
            dialog.dispose();
        }
    }
}
