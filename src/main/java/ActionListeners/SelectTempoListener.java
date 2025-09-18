package ActionListeners;

import GUI.BoardGUI;
import Logic.Tempo;
import Online.Game;
import com.google.cloud.firestore.Firestore;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectTempoListener implements ActionListener {
    private final JButton oneplusone;
    private final JButton threeplustwo;
    private final JButton fiveplusthree;
    private final JButton tenpluszero;
    private Game game;
    private final Firestore database;

    public Game getGame() {
        return game;
    }

    public SelectTempoListener(JButton oneplusone, JButton threeplustwo, JButton fiveplusthree, JButton tenpluszero, Firestore database){
        this.oneplusone=oneplusone;
        this.threeplustwo=threeplustwo;
        this.fiveplusthree=fiveplusthree;
        this.tenpluszero=tenpluszero;
        this.game=null;
        this.database=database;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==oneplusone){
            Tempo tempoforwhite =new Tempo(60,1);
            Tempo tempoforblack=new Tempo(60,1);
            this.game=new Game(database);
            game.getBoardGUI().setBlacktempo(tempoforblack);
            game.getBoardGUI().setWhitetempo(tempoforwhite);
            game.getBoardGUI().selecttimer();
        } else if (e.getSource()==threeplustwo) {
            Tempo tempoforwhite=new Tempo(180,2);
            Tempo tempoforblack=new Tempo(180,2);
            this.game=new Game(database);
            game.getBoardGUI().setBlacktempo(tempoforblack);
            game.getBoardGUI().setWhitetempo(tempoforwhite);
            game.getBoardGUI().selecttimer();
        } else if (e.getSource()==fiveplusthree) {
            Tempo tempoforwhite=new Tempo(300,3);
            Tempo tempoforblack=new Tempo(300,3);
            this.game=new Game(database);
            game.getBoardGUI().setBlacktempo(tempoforblack);
            game.getBoardGUI().setWhitetempo(tempoforwhite);
            game.getBoardGUI().selecttimer();
        } else if (e.getSource()==tenpluszero) {
            Tempo tempoforwhite=new Tempo(600,0);
            Tempo tempoforblack=new Tempo(600,0);
            this.game=new Game(database);
            game.getBoardGUI().setBlacktempo(tempoforblack);
            game.getBoardGUI().setWhitetempo(tempoforwhite);
            game.getBoardGUI().selecttimer();
        }
    }
}
