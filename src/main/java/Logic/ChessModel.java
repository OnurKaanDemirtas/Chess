package Logic;
import GUI.BoardGUI;
import GUI.Move;
import GUI.PawnPromoteGUI;
import Online.FireBaseConnection;
import Online.MoveNotation;
import Pieces.*;
import com.google.cloud.firestore.DocumentSnapshot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ChessModel {
    private final ArrayList<Piece> blackPieces;
    private final  ArrayList<Piece> whitePieces;
    private final JButton [][] boardButtons;
    private final BoardGUI boardGUI;

    public ChessModel(ArrayList<Piece> blackPieces, ArrayList<Piece> whitePieces, ArrayList<JButton> boardButtons, BoardGUI boardGUI){
        this.blackPieces = blackPieces;
        this.whitePieces = whitePieces;
        this.boardButtons =new JButton[8][8];
        for(int row=0;row<8;row++){
            for(int column=0;column<8;column++){
                this.boardButtons[row][column]= boardButtons.get(row*8+column);
            }
        }
        this.boardGUI=boardGUI;
        findPlayablePlaces(whitePieces);
        for (Piece piece : whitePieces) {
            if(piece instanceof King king) {
                arrangeThePlayablePlaces(whitePieces, blackPieces, king);
            }
        }
    }
    public Index findIndex(JButton wantedButton){
        for(int row=0;row<8;row++){
            for(int column=0;column<8;column++){
                if(boardButtons[row][column].equals(wantedButton)){
                    return new Index(row,column);
                }
            }
        }
        return null;
    }
    private boolean isItInTheBoard(Index index){
        return index.getRow() >= 0 && index.getColumn() >= 0 && index.getRow() <= 7 && index.getColumn() <= 7;
    }
    private boolean isThereAFriendPiece(Index index, ArrayList<Piece> sameColorPieces, Piece selectedpiece){
        for(Piece piece:sameColorPieces){
            if(boardButtons[index.getRow()][index.getColumn()].equals(piece.getButton())){
                if(!piece.getSupportingPieces().contains(selectedpiece)){
                    piece.getSupportingPieces().add(selectedpiece);
                }
                return true;
            }
        }
        return false;
    }
    private boolean isThereAFriendPieceInThisRoad(Index index, ArrayList<Piece> sameColorPieces){
        for(Piece piece:sameColorPieces){
            if(boardButtons[index.getRow()][index.getColumn()].equals(piece.getButton())){
                return true;
            }
        }
        return false;
    }
    private Piece isthereaenemypiece(Index index, ArrayList<Piece> enemyPieces){
        for(Piece piece:enemyPieces){
            if(boardButtons[index.getRow()][index.getColumn()].equals(piece.getButton())){
                return piece;
            }
        }
        return null;
    }
    private boolean canKingMoveToThatIndex(ArrayList<Piece> enemyPieces, Index indexThatWantingToMoveTo){
        Piece enemypiece=isthereaenemypiece(indexThatWantingToMoveTo, enemyPieces);
        if(enemypiece!=null){
            return enemypiece.getSupportingPieces().isEmpty();
        }
        for(Piece piece: enemyPieces){
            if(piece instanceof King){
                Index indexOfKing = findIndex(piece.getButton());
                if(Math.abs(indexThatWantingToMoveTo.getRow() - indexOfKing.getRow()) <= 1 &&
                        Math.abs(indexThatWantingToMoveTo.getColumn() - indexOfKing.getColumn()) <= 1){
                    return false;
                }
            }else if(piece instanceof Pawn) {
                if (((Pawn) piece).getAttackingplaces().contains(boardButtons[indexThatWantingToMoveTo.getRow()][indexThatWantingToMoveTo.getColumn()])) {
                    return false;
                }
            }else{
                if(piece.getPlaybleplaces().contains(boardButtons[indexThatWantingToMoveTo.getRow()][indexThatWantingToMoveTo.getColumn()])){
                    return false;
                }
            }
        }
        return true;
    }

    public void findPlayablePlaces(ArrayList<Piece> pieces) {
        ArrayList<Piece> enemyPieces;
        if (pieces.equals(whitePieces)) {
            enemyPieces = blackPieces;
        } else {
            enemyPieces = whitePieces;
        }
        for (Piece enemy : enemyPieces) {
            if (enemy instanceof King) {
                ((King) enemy).getPiecesthatcheck().clear();
                break;
            }
        }
        for (Piece piece : pieces) {
            piece.getPlaybleplaces().clear();
            piece.getSupportingPieces().clear();
            switch (piece) {
                case Knight knight -> {
                    Index[] indexesForMoves =
                            {new Index(2, 1), new Index(2, -1), new Index(-2, 1), new Index(-2, -1),
                                    new Index(1, 2), new Index(1, -2), new Index(-1, 2), new Index(-1, -2)
                            };
                    Index indexOfPiece = findIndex(piece.getButton());
                    for (Index index : indexesForMoves) {
                        Index temporaryIndex = new Index(indexOfPiece.getRow() + index.getRow(), indexOfPiece.getColumn() + index.getColumn());
                        if (isItInTheBoard(temporaryIndex)) {
                            if (!isThereAFriendPiece(temporaryIndex, pieces, piece)) {
                                Piece enemy = isthereaenemypiece(temporaryIndex, enemyPieces);
                                if (enemy instanceof King king) {
                                    king.getPiecesthatcheck().add(knight);
                                }
                                knight.getPlaybleplaces().add(boardButtons[temporaryIndex.getRow()][temporaryIndex.getColumn()]);
                            }
                        }
                    }
                }
                case Bishop bishop -> calculatePlayablePlacesForBishop(bishop, pieces, enemyPieces);
                case Rook rook -> calculatePlayablePlacesForRook(rook, pieces, enemyPieces);
                case Queen queen -> {
                    calculatePlayablePlacesForRook(queen, pieces, enemyPieces);
                    calculatePlayablePlacesForBishop(queen, pieces, enemyPieces);
                }
                case Pawn pawn -> {
                    pawn.getAttackingplaces().clear();
                    pawn.getEnpassantplaces().clear();
                    calculatePlayablePlacesForPawn(pawn, pieces, enemyPieces);
                }
                case King king -> {
                    king.getCastableplaces().clear();
                    king.getCastablerooks().clear();
                    calculatePlayablePlacesForKing(king, pieces, enemyPieces);
                }
                default -> {
                }
            }
        }
    }

    public void arrangeThePlayablePlaces(ArrayList<Piece> pieces, ArrayList<Piece> enemyPieces, King king) {
        for (Piece piece : pieces) {
            JButton button = piece.getButton();
            for (int i = piece.getPlaybleplaces().size() - 1; i >= 0; i--) {
                JButton playableButton = piece.getPlaybleplaces().get(i);
                Piece enemypiece = isthereaenemypiece(findIndex(playableButton), enemyPieces);
                if (enemypiece != null) {
                    enemyPieces.remove(enemypiece);
                }
                piece.setButton(playableButton);
                king.getPiecesthatcheck().clear();
                findPlayablePlaces(enemyPieces);
                if (!king.getPiecesthatcheck().isEmpty()) {
                    piece.getPlaybleplaces().remove(playableButton);
                }
                piece.setButton(button);
                if (enemypiece != null) {
                    enemyPieces.add(enemypiece);
                }
            }
        }
    }
    public int isItCheckmate(ArrayList<Piece> pieces){
        King king = null;
        for(Piece piece : pieces) {
            if(piece instanceof King){
                king= (King) piece;
            }
        }
        if (king!=null) {
            boolean isTherePlayablePlace = true;
            for (Piece piece : pieces) {
                if (!piece.getPlaybleplaces().isEmpty()) {
                    isTherePlayablePlace =false;
                    break;
                }
            }
            if(isTherePlayablePlace &&!king.getPiecesthatcheck().isEmpty()){
                return 0; // it is checkmate
            } else if (isTherePlayablePlace && king.getPiecesthatcheck().isEmpty()) {
                return 1; // it is stalemate
            }
        }
        return 2;// it is not checkmate or stalemate
    }
    private void calculatePlayablePlacesForBishop(Piece piece, ArrayList<Piece> pieces, ArrayList<Piece> enemyPieces){
        Index indexOfPiece=findIndex(piece.getButton());
        Index[] indexesForCalculate =
                {new Index(-1,-1),new Index(-1,1),new Index(1,-1),new Index(1,1)};
        Index[] indexesForCompare=
                {indexOfPiece,new Index(indexOfPiece.getRow(),7-indexOfPiece.getColumn()),new Index(7-indexOfPiece.getRow(),indexOfPiece.getColumn()),new Index(7-indexOfPiece.getRow(),7-indexOfPiece.getColumn())};
        for(int a=0;a<4;a++){
            int minDistance= findSmallerOne(indexesForCompare[a]);
            if(minDistance>0){
                for(int i=1;i<=minDistance;i++) {
                    Index temporaryindex=new Index(indexOfPiece.getRow()+indexesForCalculate[a].getRow()*i, indexOfPiece.getColumn()+indexesForCalculate[a].getColumn()*i);
                    if (isThereAFriendPiece(temporaryindex, pieces,piece)) {
                        break;
                    }
                    Piece enemypiece=isthereaenemypiece(temporaryindex,enemyPieces);
                    if (enemypiece!=null) {
                        if(enemypiece instanceof King){
                            ((King) enemypiece).getPiecesthatcheck().add(piece);
                        }else{
                            piece.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                        }
                        break;
                    }else {
                        piece.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }
                }
            }
        }
    }
    private void calculatePlayablePlacesForRook(Piece piece, ArrayList<Piece> pieces, ArrayList<Piece> enemyPieces){
        Index indexOfPiece=findIndex(piece.getButton());
        for(int i=1;i<=indexOfPiece.getColumn();i++){//going up
            Index temporaryindex=new Index(indexOfPiece.getRow(),indexOfPiece.getColumn()-i);
            if (isThereAFriendPiece(temporaryindex, pieces,piece)) {
                break;
            }
            Piece enemypiece=isthereaenemypiece(temporaryindex,enemyPieces);
            if (enemypiece!=null) {
                if(enemypiece instanceof King){
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                }else{
                    piece.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
                break;
            }else {
                piece.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
        for(int i=1;i<=indexOfPiece.getRow();i++){//going left
            Index temporaryindex=new Index(indexOfPiece.getRow()-i,indexOfPiece.getColumn());
            if (isThereAFriendPiece(temporaryindex, pieces,piece)) {
                break;
            }
            Piece enemypiece=isthereaenemypiece(temporaryindex,enemyPieces);
            if (enemypiece!=null) {
                if(enemypiece instanceof King){
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                }else{
                    piece.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
                break;
            }else {
                piece.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
        for(int i=1;i<=7-indexOfPiece.getColumn();i++){ // going right
            Index temporaryindex = new Index(indexOfPiece.getRow(), indexOfPiece.getColumn() + i);
            if (isThereAFriendPiece(temporaryindex, pieces, piece)) {
                break;
            }
            Piece enemypiece = isthereaenemypiece(temporaryindex, enemyPieces);
            if (enemypiece != null) {
                if (enemypiece instanceof King) {
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                } else {
                    piece.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
                break;
            } else {
                piece.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
        for(int i=1;i<=7-indexOfPiece.getRow();i++){ // going down
            Index temporaryindex = new Index(indexOfPiece.getRow() + i, indexOfPiece.getColumn());
            if (isThereAFriendPiece(temporaryindex, pieces, piece)) {
                break;
            }
            Piece enemypiece = isthereaenemypiece(temporaryindex, enemyPieces);
            if (enemypiece != null) {
                if (enemypiece instanceof King) {
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                } else {
                    piece.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
                break;
            } else {
                piece.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
    }
    private void calculatePlayablePlacesForPawn(Pawn pawn, ArrayList<Piece> pieces, ArrayList<Piece> enemyPieces){
        Index indexOfPiece=findIndex(pawn.getButton());
        if(pawn.getColor().equals(Color.WHITE)){
            if(pawn.getHowmanytimesitmoved()==0){
                for(int i=1;i<=2;i++){
                    Index temporaryindex=new Index(indexOfPiece.getRow()-i,indexOfPiece.getColumn());
                    Piece piece= isthereaenemypiece(temporaryindex,enemyPieces);
                    if (isThereAFriendPieceInThisRoad(temporaryindex, pieces)) {
                        break;
                    }else if(piece!=null){
                        break;
                    }else{
                        pawn.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }
                }
            }else{
                Index temporaryindex=new Index(indexOfPiece.getRow()-1,indexOfPiece.getColumn());
                Piece piece= isthereaenemypiece(temporaryindex,enemyPieces);
                if(!isThereAFriendPieceInThisRoad(temporaryindex,pieces)&&piece==null){
                    pawn.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
            }
            Index[] temporaryIndexes={new Index(indexOfPiece.getRow()-1,indexOfPiece.getColumn()+1),new Index(indexOfPiece.getRow()-1,indexOfPiece.getColumn()-1)};
            for (Index temporaryindex : temporaryIndexes) {
                if (isItInTheBoard(temporaryindex) && !isThereAFriendPiece(temporaryindex, pieces, pawn)) {
                    Index indexForEnPassant = new Index(temporaryindex.getRow()+1, temporaryindex.getColumn());
                    Piece pawnCandidate = isthereaenemypiece(indexForEnPassant, enemyPieces);
                    Pawn temporarypawn = null;
                    if(pawnCandidate instanceof Pawn){
                        temporarypawn = (Pawn) pawnCandidate;
                    }
                    Piece kingCandidate=isthereaenemypiece(temporaryindex, enemyPieces);
                    if(kingCandidate instanceof King){
                        ((King) kingCandidate).getPiecesthatcheck().add(pawn);
                    }
                    pawn.getAttackingplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    Piece piece = isthereaenemypiece(temporaryindex, enemyPieces);
                    if (piece != null && !(piece instanceof King)) {
                        pawn.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }else if(temporarypawn!=null&&temporarypawn.getHowmanytimesitmoved()==1&& indexForEnPassant.getRow() == 3){
                        pawn.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                        pawn.getEnpassantplaces().add(boardButtons[indexForEnPassant.getRow()][indexForEnPassant.getColumn()]);
                    }
                }
            }
        }else {
            if(pawn.getHowmanytimesitmoved()==0){
                for(int i=1;i<=2;i++){
                    Index temporaryindex=new Index(indexOfPiece.getRow()+i,indexOfPiece.getColumn());
                    Piece piece= isthereaenemypiece(temporaryindex,enemyPieces);
                    if (isThereAFriendPieceInThisRoad(temporaryindex, pieces)) {
                        break;
                    }else if(piece!=null){
                        break;
                    }else{
                        pawn.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }
                }
            }else{
                Index temporaryindex=new Index(indexOfPiece.getRow()+1,indexOfPiece.getColumn());
                Piece piece= isthereaenemypiece(temporaryindex,enemyPieces);
                if(!isThereAFriendPieceInThisRoad(temporaryindex,pieces)&&piece==null){
                    pawn.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
            }
            Index[] temporaryIndexes={new Index(indexOfPiece.getRow()+1,indexOfPiece.getColumn()+1),new Index(indexOfPiece.getRow()+1,indexOfPiece.getColumn()-1)};
            for (Index temporaryindex : temporaryIndexes) {
                if (isItInTheBoard(temporaryindex) && !isThereAFriendPiece(temporaryindex, pieces, pawn)) {
                    Index indexForEnPassant = new Index(temporaryindex.getRow()-1, temporaryindex.getColumn());
                    Piece pawnCandidate = isthereaenemypiece(indexForEnPassant, enemyPieces);
                    Pawn temporarypawn = null;
                    if(pawnCandidate instanceof Pawn){
                        temporarypawn = (Pawn) pawnCandidate;
                    }
                    pawn.getAttackingplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    Piece piece = isthereaenemypiece(temporaryindex, enemyPieces);
                    if (piece != null && !(piece instanceof King)) {
                        pawn.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }else if(temporarypawn!=null&&temporarypawn.getHowmanytimesitmoved()==1&& indexForEnPassant.getRow() == 4){
                        pawn.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                        pawn.getEnpassantplaces().add(boardButtons[indexForEnPassant.getRow()][indexForEnPassant.getColumn()]);
                    }
                }
            }
        }
    }
    public void calculatePlayablePlacesForKing(King king, ArrayList<Piece> pieces, ArrayList<Piece> enemyPieces){
        Index indexOfKing = findIndex(king.getButton());
        Index[] indexesForCalculate = {
                new Index(-1,-1), new Index(-1,0), new Index(-1,1),
                new Index(0,-1),  new Index(0,1),
                new Index(1,-1),  new Index(1,0), new Index(1,1)
        };
        for(Index index : indexesForCalculate){
            Index temporaryindex = new Index(indexOfKing.getRow() + index.getRow(), indexOfKing.getColumn() + index.getColumn());
            if(isItInTheBoard(temporaryindex) && !isThereAFriendPiece(temporaryindex, pieces, king)) {
                if(canKingMoveToThatIndex(enemyPieces, temporaryindex)) {
                    king.getPlaybleplaces().add(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
            }
        }
        ArrayList<Rook> rooks = new ArrayList<>();
        for(Piece piece : pieces){
            if(piece instanceof Rook){
                rooks.add((Rook) piece);
            }
        }
        if(king.getHowmanytimesitmoved() == 0){
            if(king.getPiecesthatcheck().isEmpty()){
                for(Rook rook : rooks){
                    if(rook.getHowmanytimesitmoved() == 0){
                        Index indexOfRook = findIndex(rook.getButton());
                        if(canPlayerDoCastling(indexOfKing, indexOfRook, pieces, enemyPieces, king)){
                            int direction = (indexOfRook.getColumn() - indexOfKing.getColumn()) > 0 ? 1 : -1;
                            int castlingColumn = indexOfKing.getColumn() + direction * 2;
                            king.getPlaybleplaces().add(boardButtons[indexOfKing.getRow()][castlingColumn]);
                            king.getCastableplaces().add(boardButtons[indexOfKing.getRow()][castlingColumn]);
                            king.getCastablerooks().add(rook);
                        }
                    }
                }
            }
        }
    }
    private boolean canPlayerDoCastling(Index indexOfKing, Index indexOfRook, ArrayList<Piece> pieces, ArrayList<Piece> enemyPieces, King king){
        int direction = (indexOfRook.getColumn() - indexOfKing.getColumn()) > 0 ? 1 : -1;
        for(int a = 1; a < 2; a++){
            int col = indexOfKing.getColumn() + direction * a;
            Index temporaryindex = new Index(indexOfKing.getRow(), col);
            if(isThereAFriendPiece(temporaryindex, pieces, king)){
                return false;
            }
            Piece enemypiece = isthereaenemypiece(temporaryindex, enemyPieces);
            if(enemypiece != null) {
                return false;
            }
            for(Piece enemy : enemyPieces){
                if(enemy instanceof King){
                    Index indexOfEnemyKing = findIndex(enemy.getButton());
                    if(Math.abs(temporaryindex.getRow() - indexOfEnemyKing.getRow()) <= 1 &&
                            Math.abs(temporaryindex.getColumn() - indexOfEnemyKing.getColumn()) <= 1){
                        return false;
                    }
                }else{
                    if(enemy.getPlaybleplaces().contains(boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()])){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public void move(Piece selectedpiece, JButton clickedButton, String promotionType){
        Index from=findIndex(selectedpiece.getButton());
        Index to=findIndex(clickedButton);
        MoveNotation moveNotation = new MoveNotation(from,to, null);
        Index indexOfClickedButton=findIndex(clickedButton);
        Index indexOfSelectedPiece=findIndex(selectedpiece.getButton());
        String pieceTakingNotation="";
        String playedSquareNotation=
                String.valueOf((char) ('a' + indexOfClickedButton.getColumn())) +
                        (8 - indexOfClickedButton.getRow());
        String promoteNotation="";
        String checkNotation="";
        String rookNotation=null;
        Piece enemypiece;
        King WhiteKing = null;
        for(Piece piece: whitePieces){
            if(piece instanceof King){
                WhiteKing = (King) piece;
            }else if(piece instanceof Pawn pawn){
                if(pawn.getHowmanytimesitmoved()==1){
                    pawn.increasehowmanytimesitmoved();
                }
            }
        }
        King BlackKing = null;
        for(Piece piece: blackPieces){
            if(piece instanceof King){
                BlackKing = (King) piece;
            } else if (piece instanceof Pawn pawn) {
                if (pawn.getHowmanytimesitmoved() == 1) {
                    pawn.increasehowmanytimesitmoved();
                }
            }
        }
        if(selectedpiece.getColor().equals(Color.WHITE)){
            enemypiece=isthereaenemypiece(indexOfClickedButton, blackPieces);
        }else{
            enemypiece=isthereaenemypiece(indexOfClickedButton, whitePieces);
        }
        if(enemypiece!=null){
            if(enemypiece.getColor().equals(Color.BLACK)){
                String count;
                if(enemypiece instanceof Queen){
                    count=boardGUI.getTakenBlackQueenCount().getText();
                    String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakenBlackQueenCount().setText(newCount);
                } else if (enemypiece instanceof Rook) {
                    count = boardGUI.getTakenBlackRookCount().getText();
                    String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakenBlackRookCount().setText(newCount);
                } else if (enemypiece instanceof Bishop) {
                    count = boardGUI.getTakenBlackBishopCount().getText();
                    String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakenBlackBishopCount().setText(newCount);
                } else if (enemypiece instanceof Knight) {
                    count = boardGUI.getTakenBlackKnightCount().getText();
                    String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakenBlackKnightCount().setText(newCount);
                } else if (enemypiece instanceof Pawn) {
                    count = boardGUI.getTakenBlackPawnCount().getText();
                    String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakenBlackPawnCount().setText(newCount);
                }
                blackPieces.remove(enemypiece);
            }else{
                String count;
                if(enemypiece instanceof Queen){
                    count=boardGUI.getTakenWhiteQueenCount().getText();
                    String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakenWhiteQueenCount().setText(newCount);
                } else if (enemypiece instanceof Rook) {
                    count = boardGUI.getTakenWhiteRookCount().getText();
                    String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakenWhiteRookCount().setText(newCount);
                } else if (enemypiece instanceof Bishop) {
                    count = boardGUI.getTakenWhiteBishopCount().getText();
                    String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakenWhiteBishopCount().setText(newCount);
                } else if (enemypiece instanceof Knight) {
                    count = boardGUI.getTakenWhiteKnightCount().getText();
                    String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakenWhiteKnightCount().setText(newCount);
                } else if (enemypiece instanceof Pawn) {
                    count = boardGUI.getTakenWhitePawnCount().getText();
                    String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakenWhitePawnCount().setText(newCount);
                }
                whitePieces.remove(enemypiece);
            }
            if(selectedpiece instanceof Pawn ){
                pieceTakingNotation = String.valueOf((char) ('a' + indexOfSelectedPiece.getColumn())) ;
            }
            pieceTakingNotation+="x";
            enemypiece.getButton().setIcon(null);
            enemypiece.setButton(null);
        }
        clickedButton.setIcon(selectedpiece.getButton().getIcon());
        clickedButton.setDisabledIcon(clickedButton.getIcon());
        selectedpiece.getButton().setIcon(null);
        selectedpiece.getButton().setDisabledIcon(null);
        selectedpiece.getButton().setEnabled(false);
        selectedpiece.setButton(clickedButton);
        switch (selectedpiece) {
            case Pawn pawn -> {
                Index indexOfEnPassant = findIndex(clickedButton);
                if(selectedpiece.getColor().equals(Color.WHITE)){
                    indexOfEnPassant = new Index(indexOfEnPassant.getRow() + 1, indexOfEnPassant.getColumn());
                }else{
                    indexOfEnPassant = new Index(indexOfEnPassant.getRow() - 1, indexOfEnPassant.getColumn());
                }
                if (pawn.getEnpassantplaces().contains(boardButtons[indexOfEnPassant.getRow()][indexOfEnPassant.getColumn()])) {
                    if(selectedpiece.getColor().equals(Color.WHITE)){
                        Piece takenPawn = isthereaenemypiece(indexOfEnPassant, blackPieces);
                        assert takenPawn != null;
                        takenPawn.getButton().setIcon(null);
                        takenPawn.getButton().setDisabledIcon(null);
                        takenPawn.setButton(null);
                        String count = boardGUI.getTakenBlackPawnCount().getText();
                        String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                        boardGUI.getTakenBlackPawnCount().setText(newCount);
                        blackPieces.remove(takenPawn);
                    }else{
                        Piece takenPawn = isthereaenemypiece(indexOfEnPassant, whitePieces);
                        assert takenPawn != null;
                        takenPawn.getButton().setIcon(null);
                        takenPawn.getButton().setDisabledIcon(null);
                        takenPawn.setButton(null);
                        String count = boardGUI.getTakenWhitePawnCount().getText();
                        String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                        boardGUI.getTakenWhitePawnCount().setText(newCount);
                        whitePieces.remove(takenPawn);
                    }
                }
                if (indexOfClickedButton.getRow() == 0 || indexOfClickedButton.getRow() == 7) {
                    if (selectedpiece.getColor().equals(Color.WHITE)) {
                        if(promotionType!=null) {
                            promote(promotionType, clickedButton, selectedpiece.getColor(), whitePieces);
                        }else{
                            PawnPromoteGUI pawnPromoteGUI=new PawnPromoteGUI(boardGUI.getFrame(),clickedButton, selectedpiece.getColor(), whitePieces,boardGUI, null);
                            moveNotation.setPromotionType(pawnPromoteGUI.getPromotionTypeForFirebase());
                        }
                    } else {
                        if(promotionType!=null) {
                            promote(promotionType, clickedButton, selectedpiece.getColor(), blackPieces);
                        }else{
                            PawnPromoteGUI pawnPromoteGUI=new PawnPromoteGUI(boardGUI.getFrame(),clickedButton, selectedpiece.getColor(), blackPieces,boardGUI, null);
                            moveNotation.setPromotionType(pawnPromoteGUI.getPromotionTypeForFirebase());
                        }
                    }
                    if (selectedpiece.getColor().equals(Color.WHITE)) {
                        whitePieces.remove(selectedpiece);
                        String count = boardGUI.getTakenWhitePawnCount().getText();
                        String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                        boardGUI.getTakenWhitePawnCount().setText(newCount);
                    } else {
                        blackPieces.remove(selectedpiece);
                        String count = boardGUI.getTakenBlackPawnCount().getText();
                        String newCount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                        boardGUI.getTakenBlackPawnCount().setText(newCount);
                    }
                }
                pawn.increasehowmanytimesitmoved();
            }
            case King king -> {
                king.increasehowmanytimesitmoved();
                Index indexOfKing = findIndex(selectedpiece.getButton());
                for (int i = 0; i < king.getCastableplaces().size(); i++) {
                    if (king.getCastableplaces().get(i).equals(clickedButton)) {
                        Rook rook = king.getCastablerooks().get(i);
                        Index indexOfRook = findIndex(rook.getButton());
                        JButton newRookButton;
                        if (indexOfRook.getColumn() < indexOfKing.getColumn()) {
                            newRookButton = boardButtons[indexOfKing.getRow()][indexOfKing.getColumn() + 1];
                        } else {
                            newRookButton = boardButtons[indexOfKing.getRow()][indexOfKing.getColumn() - 1];
                        }
                        newRookButton.setIcon(rook.getButton().getIcon());
                        rook.getButton().setIcon(null);
                        rook.getButton().setDisabledIcon(null);
                        rook.getButton().setEnabled(false);
                        rook.setButton(newRookButton);
                        newRookButton.setDisabledIcon(newRookButton.getIcon());
                        rook.increasehowmanytimesitmoved();
                        rook.getButton().setEnabled(true);
                        if(Math.abs(indexOfRook.getColumn() - indexOfKing.getColumn()) == 4) {
                            rookNotation = "O-O-O";
                        } else {
                            rookNotation = "O-O";
                        }
                        break;
                    }
                }
            }
            case Rook rook -> rook.increasehowmanytimesitmoved();
            default -> {
            }
        }
        for(JButton button:selectedpiece.getPlaybleplaces()){
            button.setEnabled(false);
        }
        if(selectedpiece.getColor().equals(Color.WHITE)){
            boardGUI.getWhitetempo().setRemainingTime(boardGUI.getWhitetempo().getRemainingTime()+boardGUI.getWhitetempo().getTimeToAddedForEachMove());
            int minute=boardGUI.getWhitetempo().getRemainingTime()/60;
            int second=boardGUI.getWhitetempo().getRemainingTime()-60*minute;
            String m=minute+"";
            String s=second+"";
            if(minute<10){
                m=0+""+minute;
            }
            if(second<10){
                s=0+""+second;
            }
            boardGUI.getWhiteTimerLabel().setText(m+":"+s);
            boardGUI.getWhitetimer().stop();
            boardGUI.getBlacktimer().start();
            findPlayablePlaces(whitePieces);
            findPlayablePlaces(blackPieces);
            arrangeThePlayablePlaces(blackPieces, whitePieces,BlackKing);
            if(BlackKing!=null&&!BlackKing.getPiecesthatcheck().isEmpty()){
                checkNotation="+";
            }
            if(isItCheckmate(blackPieces)==0) {
                for(Piece piece: blackPieces){
                    piece.getButton().setEnabled(false);
                    if(piece instanceof King){
                        piece.getButton().setBackground(Color.red);
                    }
                }
                boardGUI.getBlacktimer().stop();
                JOptionPane.showMessageDialog(null, "White wins!");
                checkNotation="#";
            }else if(isItCheckmate(blackPieces)==1) {
                for(Piece piece: blackPieces){
                    piece.getButton().setEnabled(false);
                }
                boardGUI.getBlacktimer().stop();
                JOptionPane.showMessageDialog(null, "Stalemate!");
            }
            Move move;
            if(rookNotation!=null) {
                move = new Move(boardGUI, rookNotation);
            }else {
                move = new Move(boardGUI, selectedpiece.getPiecetype()+pieceTakingNotation+playedSquareNotation+promoteNotation+checkNotation);
            }
            boardGUI.getWhiteMovesListModel().addElement(move);
            boardGUI.getWhiteMovesList().setSelectedIndex(boardGUI.getWhiteMovesListModel().getSize()-1);
        } else {
            boardGUI.getBlacktempo().setRemainingTime(boardGUI.getBlacktempo().getRemainingTime()+boardGUI.getBlacktempo().getTimeToAddedForEachMove());
            int minute=boardGUI.getBlacktempo().getRemainingTime()/60;
            int second=boardGUI.getBlacktempo().getRemainingTime()-60*minute;
            String m=minute+"";
            String s=second+"";
            if(minute<10){
                m=0+""+minute;
            }
            if(second<10){
                s=0+""+second;
            }
            boardGUI.getBlackTimerLabel().setText(m+":"+s);
            boardGUI.getBlacktimer().stop();
            boardGUI.getWhitetimer().start();
            findPlayablePlaces(blackPieces);
            findPlayablePlaces(whitePieces);
            arrangeThePlayablePlaces(whitePieces, blackPieces,WhiteKing);
            if(WhiteKing!=null&&!WhiteKing.getPiecesthatcheck().isEmpty()){
                checkNotation="+";
            }
            if(isItCheckmate(whitePieces)==0) {
                for(Piece piece: whitePieces){
                    piece.getButton().setEnabled(false);
                    if(piece instanceof King){
                        piece.getButton().setBackground(Color.red);
                    }
                }
                boardGUI.getWhitetimer().stop();
                JOptionPane.showMessageDialog(null, "Black wins!");
                checkNotation="#";
            } else if (isItCheckmate(whitePieces)==1) {
                boardGUI.getWhitetimer().stop();
                JOptionPane.showMessageDialog(null, "Stalemate!");
            }
            Move move;
            if(rookNotation!=null) {
                move = new Move(boardGUI, rookNotation);
            }else {
                move = new Move(boardGUI, selectedpiece.getPiecetype()+pieceTakingNotation+playedSquareNotation+promoteNotation+checkNotation);
            }
            boardGUI.getBlackMovesListModel().addElement(move);
            boardGUI.getBlackMovesList().setSelectedIndex(boardGUI.getBlackMovesListModel().getSize()-1);
        }
        if(boardGUI.getColorOfThePlayer().equals("W")) {
            for(Piece piece : whitePieces) {
                piece.getButton().setEnabled(true);
            }
        }else{
            for(Piece piece : blackPieces) {
                piece.getButton().setEnabled(true);
            }
        }
        boardGUI.getGame().setNumberOfMovesMade(boardGUI.getGame().getNumberOfMovesMade()+1);
        FireBaseConnection.getDatabase().collection("accounts").document(boardGUI.getGame().getPlayer().getAccountId()).collection("gameArchive").document(boardGUI.getGame().getPlayer().getId()+" ").update("numberOfMovesMade", boardGUI.getGame().toString());
        try{
            FireBaseConnection.getDatabase().collection("accounts").document(boardGUI.getGame().getPlayer().getAccountId()).collection("gameArchive").document(boardGUI.getGame().getPlayer().getId()+" ").collection("moves").document(String.valueOf(boardGUI.getGame().toString())).set(moveNotation).get();
        }catch (Exception ignored){
        }
        selectedpiece.getButton().setEnabled(boardGUI.getColorOfThePlayer().equals(boardGUI.getBoardButtonHandler().getWhoseTurnIsIt()));
        boardGUI.getFrame().validate();
    }
    public void move(DocumentSnapshot documentSnapshot){
        try{
            MoveNotation moveNotation=documentSnapshot.toObject(MoveNotation.class);
            if(boardGUI.getColorOfThePlayer().equals("B")){
                for(Piece piece: whitePieces){
                    Index indexOfButton=findIndex(piece.getButton());
                    assert moveNotation != null;
                    if(moveNotation.getFrom().getColumn()==indexOfButton.getColumn()&&moveNotation.getFrom().getRow()==indexOfButton.getRow()){
                        move(piece, boardButtons[moveNotation.getTo().getRow()][moveNotation.getTo().getColumn()],moveNotation.getPromotionType());
                        boardGUI.getBoardButtonHandler().setWhoseTurnIsIt("B");
                        break;
                    }
                }
            }else{
                for(Piece piece: blackPieces){
                    Index indexOfButton=findIndex(piece.getButton());
                    assert moveNotation != null;
                    if(moveNotation.getFrom().getColumn()==indexOfButton.getColumn()&&moveNotation.getFrom().getRow()==indexOfButton.getRow()){
                        move(piece, boardButtons[moveNotation.getTo().getRow()][moveNotation.getTo().getColumn()], moveNotation.getPromotionType());
                        boardGUI.getBoardButtonHandler().setWhoseTurnIsIt("W");
                        break;
                    }
                }
            }
        }catch (Exception ignored){
        }
    }
    private void promote(String promotionType, JButton buttonForNewPiece, Color colorForNewPiece, ArrayList<Piece> piecesForNewPiece){
        switch (promotionType) {
            case "Queen":
                Piece queen = new Queen(buttonForNewPiece, colorForNewPiece);
                piecesForNewPiece.add(queen);
                if (colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white queen.png"));
                    buttonForNewPiece.setDisabledIcon(buttonForNewPiece.getIcon());
                } else {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black queen.png"));
                    buttonForNewPiece.setDisabledIcon(buttonForNewPiece.getIcon());
                }
                break;
            case "Bishop":
                Piece bishop = new Bishop(buttonForNewPiece, colorForNewPiece);
                piecesForNewPiece.add(bishop);
                if (colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white bishop.png"));
                    buttonForNewPiece.setDisabledIcon(buttonForNewPiece.getIcon());
                } else {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black bishop.png"));
                    buttonForNewPiece.setDisabledIcon(buttonForNewPiece.getIcon());
                }
                break;
            case "Knight":
                Piece knight = new Knight(buttonForNewPiece, colorForNewPiece);
                piecesForNewPiece.add(knight);
                if (colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white knight.png"));
                    buttonForNewPiece.setDisabledIcon(buttonForNewPiece.getIcon());
                } else {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black knight.png"));
                    buttonForNewPiece.setDisabledIcon(buttonForNewPiece.getIcon());
                }
                break;
            case "Rook":
                Rook rook = new Rook(buttonForNewPiece, colorForNewPiece);
                piecesForNewPiece.add(rook);
                rook.increasehowmanytimesitmoved();
                if (colorForNewPiece.equals(Color.WHITE)) {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/White/white rook.png"));
                    buttonForNewPiece.setDisabledIcon(buttonForNewPiece.getIcon());
                } else {
                    buttonForNewPiece.setIcon(new ImageIcon("src/main/java/PieceIcons/Black/black rook.png"));
                    buttonForNewPiece.setDisabledIcon(buttonForNewPiece.getIcon());
                }
                break;
        }
    }

    private int findSmallerOne(Index indexForCompare){
        return Math.min(indexForCompare.getRow(), indexForCompare.getColumn());
    }
}