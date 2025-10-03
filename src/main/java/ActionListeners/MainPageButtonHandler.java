package ActionListeners;

import GUI.BoardGUI;
import Logic.Tempo;
import Online.Account;
import Online.FireBaseConnection;
import Online.Game;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class MainPageButtonHandler implements ActionListener {
    private final JButton oneplusone;
    private final JButton threeplustwo;
    private final JButton fiveplusthree;
    private final JButton tenpluszero;
    private JButton leaderboardButton;
    private JButton GameArchive;
    private JButton logoutButton;
    private JButton profileButton;
    private Account player;

    public MainPageButtonHandler(JButton oneplusone, JButton threeplustwo, JButton fiveplusthree, JButton tenpluszero, JButton leaderboardButton, JButton GameArchive, JButton logoutButton, JButton profileButton,Account player){
        this.oneplusone=oneplusone;
        this.threeplustwo=threeplustwo;
        this.fiveplusthree=fiveplusthree;
        this.tenpluszero=tenpluszero;
        this.leaderboardButton=leaderboardButton;
        this.GameArchive=GameArchive;
        this.logoutButton=logoutButton;
        this.profileButton=profileButton;
        this.player=player;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==oneplusone){
            Tempo tempoforwhite =new Tempo(60,1);
            Tempo tempoforblack=new Tempo(60,1);
        } else if (e.getSource()==threeplustwo) {
            Tempo tempoforwhite=new Tempo(180,2);
            Tempo tempoforblack=new Tempo(180,2);
        } else if (e.getSource()==fiveplusthree) {
            Tempo tempoforwhite=new Tempo(300,3);
            Tempo tempoforblack=new Tempo(300,3);
        } else if (e.getSource()==tenpluszero) {
            Tempo tempoforwhite=new Tempo(600,0);
            Tempo tempoforblack=new Tempo(600,0);
        } else if(e.getSource()==logoutButton){
            System.exit(0);
        }
    }
    private Account findOppenent(Account player) throws ExecutionException, InterruptedException {
        Account opponent =null;
        AtomicInteger second= new AtomicInteger();
        Timer timer=new Timer(1000,e->{
            second.addAndGet(1);});
        while(opponent==null&& second.get()<120){
            ApiFuture<QuerySnapshot> future = FireBaseConnection.getDatabase().collection("accounts").whereEqualTo("lookingformatchmaking",true).whereNotEqualTo("userName",player.getUserName()).get();
            List<QueryDocumentSnapshot> documents =future.get().getDocuments();
            if(!documents.isEmpty()){
                Random random=new Random();
                opponent=documents.get(random.nextInt(0,documents.size())).toObject(Account.class);
                opponent.setLookingformatchmaking(false);
                player.setLookingformatchmaking(false);
                FireBaseConnection.getDatabase().collection("accounts").document(opponent.toString()).update("lookingformatchmaking",false);
                FireBaseConnection.getDatabase().collection("accounts").document(player.toString()).update("lookingformatchmaking",false);
                int index_for_color_of_opponent= random.nextInt(0,2);
                if(index_for_color_of_opponent==0){
                    initiateGames("W","B",player,opponent);
                }else{
                    initiateGames("B","W",player,opponent);
                }
            }
        }
        if(opponent==null){
            JOptionPane.showMessageDialog(null,"");
        }
        return opponent;
    }
    private void initiateGames(String opponent_color,String player_color,Account player,Account opponent){
        Game players_game=new Game(opponent,player_color);
        player.getGameHistory().add(players_game);
        Game opponents_game=new Game(player,opponent_color);
        opponent.getGameHistory().add(opponents_game);
        FireBaseConnection.getDatabase().collection("accounts").document(opponent.toString()).update("gameHistory",opponent.getGameHistory());
        FireBaseConnection.getDatabase().collection("accounts").document(player.toString()).update("gameHistory",player.getGameHistory());
    }
}
