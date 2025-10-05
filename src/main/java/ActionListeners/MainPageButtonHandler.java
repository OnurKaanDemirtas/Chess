package ActionListeners;

import Logic.Tempo;
import Online.Account;
import Online.FireBaseConnection;
import Online.Game;
import Online.MatchMaking;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.ExecutionException;

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
    private volatile boolean stopmatchmaking = false;

    public MainPageButtonHandler(JButton oneplusone, JButton threeplustwo, JButton fiveplusthree, JButton tenpluszero, JButton leaderboardButton, JButton GameArchive, JButton logoutButton, JButton profileButton, Account player) {
        this.oneplusone = oneplusone;
        this.threeplustwo = threeplustwo;
        this.fiveplusthree = fiveplusthree;
        this.tenpluszero = tenpluszero;
        this.leaderboardButton = leaderboardButton;
        this.GameArchive = GameArchive;
        this.logoutButton = logoutButton;
        this.profileButton = profileButton;
        this.player = player;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == oneplusone) {
            Tempo tempoforwhite = new Tempo(60, 1);
            Tempo tempoforblack = new Tempo(60, 1);
            MatchMaking matchmaking = new MatchMaking(player.getRating(), player.getUserName());
            FireBaseConnection.getDatabase().collection("1+1 Matchmaking").document(matchmaking.toString()).set(matchmaking);
            MatchMaking("1+1 Matchmaking",tempoforwhite,tempoforblack);
        } else if (e.getSource() == threeplustwo) {
            Tempo tempoforwhite = new Tempo(180, 2);
            Tempo tempoforblack = new Tempo(180, 2);
            MatchMaking matchmaking = new MatchMaking(player.getRating(), player.getUserName());
            FireBaseConnection.getDatabase().collection("3+2 Matchmaking").document(matchmaking.toString()).set(matchmaking);
            MatchMaking("3+2 Matchmaking",tempoforwhite,tempoforblack);
        } else if (e.getSource() == fiveplusthree) {
            Tempo tempoforwhite = new Tempo(300, 3);
            Tempo tempoforblack = new Tempo(300, 3);
            MatchMaking matchmaking = new MatchMaking(player.getRating(), player.getUserName());
            FireBaseConnection.getDatabase().collection("5+3 Matchmaking").document(matchmaking.toString()).set(matchmaking);
            MatchMaking("5+3 Matchmaking",tempoforwhite,tempoforblack);
        } else if (e.getSource() == tenpluszero) {
            Tempo tempoforwhite = new Tempo(600, 0);
            Tempo tempoforblack = new Tempo(600, 0);
            MatchMaking matchmaking = new MatchMaking(player.getRating(), player.getUserName());
            FireBaseConnection.getDatabase().collection("10+0 Matchmaking").document(matchmaking.toString()).set(matchmaking);
            MatchMaking("10+0 Matchmaking",tempoforwhite,tempoforblack);
        } else if (e.getSource() == logoutButton) {
            System.exit(0);
        }
    }

    private void MatchMaking(String matchmakingtype,Tempo whitetempo, Tempo blacktempo) {
        ApiFuture<DocumentSnapshot> future = FireBaseConnection.getDatabase().collection("Matchmaker").document(matchmakingtype+" - matchmaker").get();
        try {
            DocumentSnapshot document = future.get();
            if (!document.exists()) {
                MatchMaking player1 = new MatchMaking(player.getRating(), player.getUserName());
                FireBaseConnection.getDatabase().collection("Matchmaker").document(matchmakingtype+" - matchmaker").set(player1).get();
                MatchMaking(matchmakingtype,whitetempo,blacktempo);
            } else {
                MatchMaking matchmaker = document.toObject(MatchMaking.class);
                if (matchmaker!=null&&!player.getUserName().equals(matchmaker.getPlayername())) {
                    Thread.sleep(2000);
                    StartGameforPlayer();
                } else {
                    Thread.sleep(1000);
                    Matchmakingforalltempo(whitetempo, blacktempo, matchmakingtype);
                    ApiFuture<QuerySnapshot> querySnapshotApiFuture = FireBaseConnection.getDatabase().collection(matchmakingtype).get();
                    QuerySnapshot querySnapshot = querySnapshotApiFuture.get();
                    if(!querySnapshot.isEmpty()){
                        MatchMaking player1 = querySnapshot.getDocuments().getFirst().toObject(MatchMaking.class);
                        FireBaseConnection.getDatabase().collection("Matchmaker").document(matchmakingtype+" - matchmaker").set(player1);
                    }
                    FireBaseConnection.getDatabase().collection("Matchmaker").document(matchmakingtype+" - matchmaker").delete().get();
                    StartGameforPlayer();
                }
            }
        } catch (Exception ignored) {
        }
    }
    private void Matchmakingforalltempo(Tempo whitetempo, Tempo blacktempo,String MatchmakingType) throws Exception {
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = FireBaseConnection.getDatabase().collection(MatchmakingType).get();
        QuerySnapshot querySnapshot = querySnapshotApiFuture.get();
        for (int i = querySnapshot.size() - 1; i >= 0; i = i - 2) {
            if (i==0){
                MatchMaking player1 = querySnapshot.getDocuments().get(i).toObject(MatchMaking.class);
                FireBaseConnection.getDatabase().collection(MatchmakingType).document(player1.toString()).delete().get();
                break;
            } else if (i==-1) {
                break;
            }
            MatchMaking player1 = querySnapshot.getDocuments().get(i).toObject(MatchMaking.class);
            MatchMaking player2 = querySnapshot.getDocuments().get(i - 1).toObject(MatchMaking.class);
            FireBaseConnection.getDatabase().collection(MatchmakingType).document(player1.toString()).delete().get();
            FireBaseConnection.getDatabase().collection(MatchmakingType).document(player2.toString()).delete().get();
            Random random = new Random();
            int index = random.nextInt(0, 2);
            if (index == 0) {
                setGamesinDatabase(player1, player2, whitetempo, blacktempo);
            } else {
                setGamesinDatabase(player2, player1, whitetempo, blacktempo);
            }
        }
    }
    private void setGamesinDatabase(MatchMaking player1, MatchMaking player2, Tempo whitetempo, Tempo blacktempo) throws ExecutionException, InterruptedException {
        Game gameforplayer1 = new Game(player1, player2, "W", whitetempo, blacktempo);
        Game gameforplayer2 = new Game(player2, player1, "B", whitetempo, blacktempo);
        QuerySnapshot snapshotforplayer1=FireBaseConnection.getDatabase().collection("accounts").whereEqualTo("userName",player1.getPlayername()).get().get();
        Account accountforplayer1=snapshotforplayer1.getDocuments().getFirst().toObject(Account.class);
        accountforplayer1.setGamesize(accountforplayer1.getGamesize()+1);
        FireBaseConnection.getDatabase().collection("accounts").document(accountforplayer1.toString()).update("gamesize",accountforplayer1.getGamesize());
        QuerySnapshot snapshotforplayer2=FireBaseConnection.getDatabase().collection("accounts").whereEqualTo("userName",player2.getPlayername()).get().get();
        Account accountforplayer2=snapshotforplayer2.getDocuments().getFirst().toObject(Account.class);
        accountforplayer2.setGamesize(accountforplayer2.getGamesize()+1);
        FireBaseConnection.getDatabase().collection("accounts").document(accountforplayer2.toString()).update("gamesize",accountforplayer2.getGamesize());
        FireBaseConnection.getDatabase().collection("accounts").document(accountforplayer1.toString()).collection("gameArchive").document(accountforplayer1.getGamesize()+" ").set(gameforplayer1);
        FireBaseConnection.getDatabase().collection("accounts").document(accountforplayer2.toString()).collection("gameArchive").document(accountforplayer2.getGamesize()+" ").set(gameforplayer2);
    }
    private void StartGameforPlayer() throws ExecutionException, InterruptedException {
        player=FireBaseConnection.getDatabase().collection("accounts").whereEqualTo("userName",player.getUserName()).get().get().getDocuments().getFirst().toObject(Account.class);
        ApiFuture<DocumentSnapshot> apiFuture=FireBaseConnection.getDatabase().collection("accounts").document(player.toString()).collection("gameArchive").document(player.getGamesize()+" ").get();
        DocumentSnapshot documentSnapshot=apiFuture.get();
        if(documentSnapshot.exists()){
            Game gameforplayer=documentSnapshot.toObject(Game.class);
            assert gameforplayer != null;
            gameforplayer.displayGame();
        }
    }
}
