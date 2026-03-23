package ActionListeners;

import GUI.MainPageGUI;
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
    private final JButton onePlusOne;
    private final JButton threePlusTwo;
    private final JButton fivePlusThree;
    private final JButton tenPlusZero;
    private final JButton leaderBoardButton;
    private final JButton GameArchive;
    private final JButton logOutButton;
    private final JButton profileButton;
    private Account player;
    private final MainPageGUI mainPageGUI;

    public MainPageButtonHandler(MainPageGUI mainPageGUI, JButton onePlusOne, JButton threePlusTwo, JButton fivePlusThree, JButton tenPlusZero, JButton leaderBoardButton, JButton GameArchive, JButton logOutButton, JButton profileButton, Account player) {
        this.mainPageGUI=mainPageGUI;
        this.onePlusOne = onePlusOne;
        this.threePlusTwo = threePlusTwo;
        this.fivePlusThree = fivePlusThree;
        this.tenPlusZero = tenPlusZero;
        this.leaderBoardButton = leaderBoardButton;
        this.GameArchive = GameArchive;
        this.logOutButton = logOutButton;
        this.profileButton = profileButton;
        this.player = player;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == onePlusOne) {
            Tempo tempoForWhite = new Tempo(60, 1);
            Tempo tempoForBlack = new Tempo(60, 1);
            MatchMaking matchmaking = new MatchMaking(player.getRating(), player.getUserName(), player.getGameSize()+1, player.toString());
            FireBaseConnection.getDatabase().collection("1+1 Matchmaking").document(matchmaking.toString()).set(matchmaking);
            MatchMaking("1+1 Matchmaking",tempoForWhite,tempoForBlack);
        } else if (e.getSource() == threePlusTwo) {
            Tempo tempoForWhite = new Tempo(180, 2);
            Tempo tempoForBlack = new Tempo(180, 2);
            MatchMaking matchmaking = new MatchMaking(player.getRating(), player.getUserName(), player.getGameSize()+1, player.toString());
            FireBaseConnection.getDatabase().collection("3+2 Matchmaking").document(matchmaking.toString()).set(matchmaking);
            MatchMaking("3+2 Matchmaking",tempoForWhite, tempoForBlack);
        } else if (e.getSource() == fivePlusThree) {
            Tempo tempoForWhite = new Tempo(300, 3);
            Tempo tempoForBlack = new Tempo(300, 3);
            MatchMaking matchmaking = new MatchMaking(player.getRating(), player.getUserName(), player.getGameSize()+1, player.toString());
            FireBaseConnection.getDatabase().collection("5+3 Matchmaking").document(matchmaking.toString()).set(matchmaking);
            MatchMaking("5+3 Matchmaking", tempoForWhite, tempoForBlack);
        } else if (e.getSource() == tenPlusZero) {
            Tempo tempoForWhite = new Tempo(600, 0);
            Tempo tempoForBlack = new Tempo(600, 0);
            MatchMaking matchmaking = new MatchMaking(player.getRating(), player.getUserName(), player.getGameSize()+1, player.toString());
            FireBaseConnection.getDatabase().collection("10+0 Matchmaking").document(matchmaking.toString()).set(matchmaking);
            MatchMaking("10+0 Matchmaking",tempoForWhite,tempoForBlack);
        } else if (e.getSource() == logOutButton) {
            System.exit(0);
        }
    }

    private void MatchMaking(String matchMakingType,Tempo whitetempo, Tempo blacktempo) {
        ApiFuture<DocumentSnapshot> future = FireBaseConnection.getDatabase().collection("Matchmaker").document(matchMakingType+" - matchmaker").get();
        try {
            DocumentSnapshot document = future.get();
            if (!document.exists()) {
                MatchMaking player1 = new MatchMaking(player.getRating(), player.getUserName(), player.getGameSize(),  player.toString());
                FireBaseConnection.getDatabase().collection("Matchmaker").document(matchMakingType+" - matchmaker").set(player1).get();
                MatchMaking(matchMakingType,whitetempo,blacktempo);
            } else {
                MatchMaking matchmaker = document.toObject(MatchMaking.class);
                if (matchmaker!=null&&!player.getUserName().equals(matchmaker.getPlayerName())) {
                    Thread.sleep(2000);
                    StartGameForPlayer();
                } else {
                    Thread.sleep(1000);
                    MatchmakingForAllTempo(whitetempo, blacktempo, matchMakingType);
                    ApiFuture<QuerySnapshot> querySnapshotApiFuture = FireBaseConnection.getDatabase().collection(matchMakingType).get();
                    QuerySnapshot querySnapshot = querySnapshotApiFuture.get();
                    if(!querySnapshot.isEmpty()){
                        MatchMaking player1 = querySnapshot.getDocuments().getFirst().toObject(MatchMaking.class);
                        FireBaseConnection.getDatabase().collection("Matchmaker").document(matchMakingType+" - matchmaker").set(player1);
                    }
                    FireBaseConnection.getDatabase().collection("Matchmaker").document(matchMakingType+" - matchmaker").delete().get();
                    StartGameForPlayer();
                }
            }
        } catch (Exception ignored) {
        }
    }
    private void MatchmakingForAllTempo(Tempo whitetempo, Tempo blacktempo,String MatchmakingType) throws Exception {
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = FireBaseConnection.getDatabase().collection(MatchmakingType).get();
        QuerySnapshot querySnapshot = querySnapshotApiFuture.get();
        for (int i = querySnapshot.size() - 1; i >= 0; i = i - 2) {
            if (i==0){
                MatchMaking player1 = querySnapshot.getDocuments().get(i).toObject(MatchMaking.class);
                FireBaseConnection.getDatabase().collection(MatchmakingType).document(player1.toString()).delete().get();
                break;
            }
            MatchMaking player1 = querySnapshot.getDocuments().get(i).toObject(MatchMaking.class);
            MatchMaking player2 = querySnapshot.getDocuments().get(i - 1).toObject(MatchMaking.class);
            FireBaseConnection.getDatabase().collection(MatchmakingType).document(player1.toString()).delete().get();
            FireBaseConnection.getDatabase().collection(MatchmakingType).document(player2.toString()).delete().get();
            Random random = new Random();
            int index = random.nextInt(0, 2);
            if (index == 0) {
                setGamesInDatabase(player1, player2, whitetempo, blacktempo);
            } else {
                setGamesInDatabase(player2, player1, whitetempo, blacktempo);
            }
        }
    }
    private void setGamesInDatabase(MatchMaking player1, MatchMaking player2, Tempo whitetempo, Tempo blacktempo) throws ExecutionException, InterruptedException {
        Game gameForPlayer1 = new Game(player1, player2, "W", whitetempo, blacktempo);
        Game gameForPlayer2 = new Game(player2, player1, "B", whitetempo, blacktempo);
        QuerySnapshot snapshotForPlayer1 =FireBaseConnection.getDatabase().collection("accounts").whereEqualTo("userName",player1.getPlayerName()).get().get();
        Account accountForPlayer1= snapshotForPlayer1.getDocuments().getFirst().toObject(Account.class);
        accountForPlayer1.setGameSize(accountForPlayer1.getGameSize()+1);
        FireBaseConnection.getDatabase().collection("accounts").document(accountForPlayer1.toString()).update("gameSize",accountForPlayer1.getGameSize()).get();
        QuerySnapshot snapshotForPlayer2=FireBaseConnection.getDatabase().collection("accounts").whereEqualTo("userName",player2.getPlayerName()).get().get();
        Account accountForPlayer2=snapshotForPlayer2.getDocuments().getFirst().toObject(Account.class);
        accountForPlayer2.setGameSize(accountForPlayer2.getGameSize()+1);
        FireBaseConnection.getDatabase().collection("accounts").document(accountForPlayer2.toString()).update("gameSize",accountForPlayer2.getGameSize()).get();
        FireBaseConnection.getDatabase().collection("accounts").document(accountForPlayer1.toString()).collection("gameArchive").document(accountForPlayer1.getGameSize()+" ").set(gameForPlayer1).get();
        FireBaseConnection.getDatabase().collection("accounts").document(accountForPlayer2.toString()).collection("gameArchive").document(accountForPlayer2.getGameSize()+" ").set(gameForPlayer2).get();
    }
    private void StartGameForPlayer() throws ExecutionException, InterruptedException {
        player=FireBaseConnection.getDatabase().collection("accounts").whereEqualTo("userName",player.getUserName()).get().get().getDocuments().getFirst().toObject(Account.class);
        ApiFuture<DocumentSnapshot> apiFuture=FireBaseConnection.getDatabase().collection("accounts").document(player.toString()).collection("gameArchive").document(player.getGameSize()+" ").get();
        DocumentSnapshot documentSnapshot=apiFuture.get();
        if(documentSnapshot.exists()){
            Game gameForPlayer=documentSnapshot.toObject(Game.class);
            assert gameForPlayer != null;
            gameForPlayer.displayGame(gameForPlayer);
            mainPageGUI.setVisible(false);
        }else{
            JOptionPane.showMessageDialog(mainPageGUI, "Game not found, please try again.");
        }
    }
}
