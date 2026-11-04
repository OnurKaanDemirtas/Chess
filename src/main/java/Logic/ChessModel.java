package Logic;

import PieceIcons.IconLoader;import GUI.BoardGUI;
import GUI.Move;
import GUI.PawnPromoteGUI;
import Online.FireBaseConnection;
import Online.GameResult;
import Online.MoveNotation;
import Pieces.*;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The chess rules. The board is the 8x8 grid of buttons; a piece "is" on a square
 * when its button is that square's button.
 *
 * The flow after every move:
 *  1. findPlayablePlaces(...) calculates the pseudo-legal moves of every piece
 *     (and, as a side effect, which enemy pieces give check and which pieces protect each other).
 *  2. arrangeThePlayablePlaces(...) tries each of those moves on the board and removes the
 *     ones that would leave the own king in check (pins, moving into check, ignoring a check).
 *  3. isItCheckmate(...) checks if the side to move has any move left.
 *  4. The draw rules are checked: insufficient material, the 50-move rule and threefold repetition.
 *
 * Coordinates: Index(row, column) with row 0 = 8th rank and column 0 = a-file.
 */
public class ChessModel {
    private final ArrayList<Piece> blackPieces;
    private final  ArrayList<Piece> whitePieces;
    private final JButton [][] boardButtons;
    private final BoardGUI boardGUI;
    /** Half-moves since the last capture or pawn move (50-move rule: draw at 100). */
    private int halfmoveClock;
    /** How many times each position has appeared (threefold repetition). */
    private final Map<String, Integer> positionCounts = new HashMap<>();

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
        positionCounts.put(positionKey(whitePieces, blackPieces), 1); // the starting position counts too
    }

    public int getHalfmoveClock(){
        return halfmoveClock;
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
    /** A king may not step next to the enemy king, onto an attacked square or onto a protected piece. */
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

    /**
     * Calculates the moves of every piece in the list without caring about checks on its own king.
     * Also fills the enemy king's "pieces that check" list and each piece's "supporting pieces"
     * (friendly pieces that protect it, so the enemy king cannot capture it).
     */
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

    /**
     * Removes the moves that would leave the own king in check: every move is made temporarily
     * (capturing the enemy piece on the target square if needed), the enemy moves are
     * recalculated and the move is dropped if the king is attacked. Then the board is put back.
     */
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
    /** @return 0 = checkmate, 1 = stalemate, 2 = the game goes on */
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
    /**
     * Pawn moves: one step forward (two on its first move), diagonal captures and en passant.
     * En passant: an enemy pawn that has moved exactly once (howmanytimesitmoved==1) and stands
     * next to our pawn on the 5th rank (4th for black) has just made a double step. move() bumps
     * the counter of every pawn after the next move, so en passant is only allowed right away.
     */
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
    /**
     * King moves plus castling. Castling is allowed when neither the king nor the rook has moved,
     * the king is not in check and the square the king passes over is free and not attacked.
     */
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
        int distanceToRook = Math.abs(indexOfRook.getColumn() - indexOfKing.getColumn());
        // every square between the king and the rook must be empty
        for(int a = 1; a < distanceToRook; a++){
            Index temporaryindex = new Index(indexOfKing.getRow(), indexOfKing.getColumn() + direction * a);
            if(isThereAFriendPieceInThisRoad(temporaryindex, pieces) || isthereaenemypiece(temporaryindex, enemyPieces) != null){
                return false;
            }
        }
        // the king may not pass over or land on an attacked square
        for(int a = 1; a <= 2; a++){
            Index temporaryindex = new Index(indexOfKing.getRow(), indexOfKing.getColumn() + direction * a);
            JButton square = boardButtons[temporaryindex.getRow()][temporaryindex.getColumn()];
            for(Piece enemy : enemyPieces){
                if(enemy instanceof King){
                    Index indexOfEnemyKing = findIndex(enemy.getButton());
                    if(Math.abs(temporaryindex.getRow() - indexOfEnemyKing.getRow()) <= 1 &&
                            Math.abs(temporaryindex.getColumn() - indexOfEnemyKing.getColumn()) <= 1){
                        return false;
                    }
                }else if(enemy instanceof Pawn pawn){
                    if(pawn.getAttackingplaces().contains(square)){ // pawns attack diagonally, not where they move
                        return false;
                    }
                }else if(enemy.getPlaybleplaces().contains(square)){
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Moves a piece on the board and updates everything that depends on it: captures,
     * en passant, promotion, castling, clocks, the move list, the end of the game
     * and (in online games) saving the move to Firestore.
     *
     * @param promotionType null for a local move (the promotion dialog asks the player),
     *                      or the piece chosen by the opponent / stored in a replay
     */
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
        // 50-move rule: a capture or any pawn move (en passant and promotions included) resets the count
        if(enemypiece!=null||selectedpiece instanceof Pawn){
            halfmoveClock=0;
        }else{
            halfmoveClock++;
        }
        if(enemypiece!=null){
            if(enemypiece.getColor().equals(Color.BLACK)){
                if(enemypiece instanceof Queen){
                    increaseCount(boardGUI.getTakenBlackQueenCount());
                } else if (enemypiece instanceof Rook) {
                    increaseCount(boardGUI.getTakenBlackRookCount());
                } else if (enemypiece instanceof Bishop) {
                    increaseCount(boardGUI.getTakenBlackBishopCount());
                } else if (enemypiece instanceof Knight) {
                    increaseCount(boardGUI.getTakenBlackKnightCount());
                } else if (enemypiece instanceof Pawn) {
                    increaseCount(boardGUI.getTakenBlackPawnCount());
                }
                blackPieces.remove(enemypiece);
            }else{
                if(enemypiece instanceof Queen){
                    increaseCount(boardGUI.getTakenWhiteQueenCount());
                } else if (enemypiece instanceof Rook) {
                    increaseCount(boardGUI.getTakenWhiteRookCount());
                } else if (enemypiece instanceof Bishop) {
                    increaseCount(boardGUI.getTakenWhiteBishopCount());
                } else if (enemypiece instanceof Knight) {
                    increaseCount(boardGUI.getTakenWhiteKnightCount());
                } else if (enemypiece instanceof Pawn) {
                    increaseCount(boardGUI.getTakenWhitePawnCount());
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
                    pieceTakingNotation = (char) ('a' + indexOfSelectedPiece.getColumn()) + "x";
                    if(selectedpiece.getColor().equals(Color.WHITE)){
                        Piece takenPawn = isthereaenemypiece(indexOfEnPassant, blackPieces);
                        assert takenPawn != null;
                        takenPawn.getButton().setIcon(null);
                        takenPawn.getButton().setDisabledIcon(null);
                        takenPawn.setButton(null);
                        increaseCount(boardGUI.getTakenBlackPawnCount());
                        blackPieces.remove(takenPawn);
                    }else{
                        Piece takenPawn = isthereaenemypiece(indexOfEnPassant, whitePieces);
                        assert takenPawn != null;
                        takenPawn.getButton().setIcon(null);
                        takenPawn.getButton().setDisabledIcon(null);
                        takenPawn.setButton(null);
                        increaseCount(boardGUI.getTakenWhitePawnCount());
                        whitePieces.remove(takenPawn);
                    }
                }
                if (indexOfClickedButton.getRow() == 0 || indexOfClickedButton.getRow() == 7) {
                    if (promotionType==null && boardGUI.isReplay()) {
                        promotionType="Queen"; // very old games did not store the choice
                    }
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
                        increaseCount(boardGUI.getTakenWhitePawnCount());
                    } else {
                        blackPieces.remove(selectedpiece);
                        increaseCount(boardGUI.getTakenBlackPawnCount());
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
        boolean whiteMoved=selectedpiece.getColor().equals(Color.WHITE);
        ArrayList<Piece> movedPieces=whiteMoved ? whitePieces : blackPieces;
        ArrayList<Piece> otherPieces=whiteMoved ? blackPieces : whitePieces;
        King otherKing=whiteMoved ? BlackKing : WhiteKing;
        boardGUI.switchClock(whiteMoved);
        // Recalculate every legal move, then remove the ones that would leave the
        // side to move in check. After that we can tell check, mate and stalemate.
        findPlayablePlaces(movedPieces);
        findPlayablePlaces(otherPieces);
        arrangeThePlayablePlaces(otherPieces, movedPieces, otherKing);
        // arrangeThePlayablePlaces tries every reply on the board, so the king's list of
        // checking pieces now belongs to the last tried position. Recalculate it for the real one.
        findPlayablePlaces(movedPieces);
        if(otherKing!=null&&!otherKing.getPiecesthatcheck().isEmpty()){
            checkNotation="+";
        }
        String endResult=null;   // result for THIS client's player, null if the game goes on
        String endMessage=null;
        int state=isItCheckmate(otherPieces);
        if(state==0) {
            if(otherKing!=null){
                otherKing.getButton().setBackground(Color.red);
            }
            checkNotation="#";
            boolean playerWon=boardGUI.getColorOfThePlayer().equals(whiteMoved ? "W" : "B");
            endResult=playerWon ? GameResult.WIN : GameResult.LOSS;
            endMessage=(whiteMoved ? "White" : "Black")+" wins by checkmate!";
        }else if(state==1) {
            endResult=GameResult.DRAW;
            endMessage="Stalemate! The game is a draw.";
        }else if(isInsufficientMaterial()) {
            endResult=GameResult.DRAW;
            endMessage="Draw! Neither side has enough pieces left to checkmate.";
        }else if(halfmoveClock>=100) {
            endResult=GameResult.DRAW;
            endMessage="Draw by the 50-move rule (50 moves each without a capture or pawn move).";
        }else if(positionCounts.merge(positionKey(otherPieces, movedPieces), 1, Integer::sum)>=3) {
            endResult=GameResult.DRAW;
            endMessage="Draw by threefold repetition.";
        }
        String notation=rookNotation!=null ? rookNotation+checkNotation
                : selectedpiece.getPiecetype()+pieceTakingNotation+playedSquareNotation+promoteNotation(moveNotation, promotionType)+checkNotation;
        Move move=new Move(boardGUI, notation);
        JList<Move> movesList=whiteMoved ? boardGUI.getWhiteMovesList() : boardGUI.getBlackMovesList();
        DefaultListModel<Move> movesListModel=whiteMoved ? boardGUI.getWhiteMovesListModel() : boardGUI.getBlackMovesListModel();
        movesListModel.addElement(move);
        movesList.setSelectedIndex(movesListModel.getSize()-1);
        // the player's own pieces stay enabled so their icons don't fade; BoardButtonHandler checks whose turn it is
        if(!boardGUI.isReplay()&&!boardGUI.getGame().isGameFinished()&&endResult==null){
            for(Piece piece : boardGUI.getColorOfThePlayer().equals("W") ? whitePieces : blackPieces) {
                piece.getButton().setEnabled(true);
            }
        }
        boardGUI.getGame().setNumberOfMovesMade(boardGUI.getGame().getNumberOfMovesMade()+1);
        if(!boardGUI.isReplay()) {
            saveMove(moveNotation);
        }
        selectedpiece.getButton().setEnabled(!boardGUI.isReplay()&&endResult==null&&boardGUI.getColorOfThePlayer().equals(boardGUI.getBoardButtonHandler().getWhoseTurnIsIt()));
        boardGUI.getFrame().validate();
        if(endResult!=null) {
            GameResult.finishGame(boardGUI, endResult, endMessage);
        }
    }

    /**
     * A text description of the position for the repetition rule. Two positions are the same when
     * the same pieces stand on the same squares, the same side is to move, and the castling and
     * en passant possibilities are the same.
     *
     * @param sideToMove the pieces of the player who moves next (their moves must already be calculated)
     */
    private String positionKey(ArrayList<Piece> sideToMove, ArrayList<Piece> otherSide){
        String[] squares=new String[64];
        StringBuilder rights=new StringBuilder();
        for(ArrayList<Piece> pieces : List.of(whitePieces, blackPieces)){
            for(Piece piece : pieces){
                Index index=findIndex(piece.getButton());
                String letter=piece.getPiecetype().isEmpty() ? "P" : piece.getPiecetype();
                squares[index.getRow()*8+index.getColumn()]=piece.getColor().equals(Color.WHITE) ? letter : letter.toLowerCase();
                // castling rights: unmoved kings and rooks
                if((piece instanceof King king&&king.getHowmanytimesitmoved()==0)||(piece instanceof Rook rook&&rook.getHowmanytimesitmoved()==0)){
                    rights.append(piece.getPiecetype()).append(index.getRow()).append(index.getColumn()).append(' ');
                }
            }
        }
        // en passant: only the pawns of the side to move can use it, and only right now
        for(Piece piece : sideToMove){
            if(piece instanceof Pawn pawn){
                for(JButton square : pawn.getEnpassantplaces()){
                    Index index=findIndex(square);
                    rights.append("ep").append(index.getRow()).append(index.getColumn()).append(' ');
                }
            }
        }
        String toMove=sideToMove==whitePieces ? "w" : "b";
        return String.join(",", Arrays.stream(squares).map(x -> x==null ? "." : x).toList())+"|"+toMove+"|"+rights;
    }

    /**
     * True when neither side can possibly checkmate: king vs king, king and one minor piece
     * (bishop or knight) vs king, or king and bishop vs king and bishop with both bishops
     * on squares of the same color.
     */
    private boolean isInsufficientMaterial(){
        ArrayList<Piece> remaining=new ArrayList<>();
        for(ArrayList<Piece> pieces : List.of(whitePieces, blackPieces)){
            for(Piece piece : pieces){
                if(piece instanceof King){
                    continue;
                }
                if(!(piece instanceof Bishop)&&!(piece instanceof Knight)){
                    return false; // a pawn, rook or queen can always still mate
                }
                remaining.add(piece);
            }
        }
        if(remaining.size()<=1){
            return true;
        }
        if(remaining.size()==2&&remaining.get(0) instanceof Bishop&&remaining.get(1) instanceof Bishop
                &&!remaining.get(0).getColor().equals(remaining.get(1).getColor())){
            Index first=findIndex(remaining.get(0).getButton());
            Index second=findIndex(remaining.get(1).getButton());
            return (first.getRow()+first.getColumn())%2==(second.getRow()+second.getColumn())%2;
        }
        return false;
    }

    /** "=Q" style suffix for promotions, "" otherwise. */
    private String promoteNotation(MoveNotation moveNotation, String promotionType){
        String type=promotionType!=null ? promotionType : moveNotation.getPromotionType();
        if(type==null){
            return "";
        }
        return "="+(type.equals("Knight") ? "N" : type.substring(0,1));
    }

    /**
     * Stores the move in this player's archive (moves/001, moves/002, ...).
     * The opponent's client is listening to this collection (see BoardGUI.listenForOpponentMoves).
     */
    private void saveMove(MoveNotation moveNotation){
        DocumentReference gameDocument=FireBaseConnection.getDatabase()
                .collection("accounts").document(boardGUI.getGame().getPlayer().getAccountId())
                .collection("gameArchive").document(boardGUI.getGame().getPlayer().getId()+" ");
        FireBaseConnection.logIfFails(gameDocument.update("numberOfMovesMade", boardGUI.getGame().getNumberOfMovesMade()), "saving the move count");
        FireBaseConnection.logIfFails(gameDocument.collection("moves").document(boardGUI.getGame().toString()).set(moveNotation), "saving the move");
    }

    /** Plays a move that the opponent made on their computer. */
    public void move(DocumentSnapshot documentSnapshot){
        MoveNotation moveNotation=documentSnapshot.toObject(MoveNotation.class);
        if(moveNotation==null){
            return;
        }
        ArrayList<Piece> opponentPieces=boardGUI.getColorOfThePlayer().equals("B") ? whitePieces : blackPieces;
        if(playMove(moveNotation, opponentPieces)){
            boardGUI.getBoardButtonHandler().setWhoseTurnIsIt(boardGUI.getColorOfThePlayer());
        }else{
            System.err.println("Received a move that does not fit the board: "+documentSnapshot.getId());
        }
    }

    /** Plays a stored move in a replay; it can be a move of either color. */
    public boolean replayMove(MoveNotation moveNotation){
        return playMove(moveNotation, whitePieces)||playMove(moveNotation, blackPieces);
    }

    private boolean playMove(MoveNotation moveNotation, ArrayList<Piece> pieces){
        for(Piece piece: pieces){
            Index indexOfButton=findIndex(piece.getButton());
            if(indexOfButton!=null&&moveNotation.getFrom().getColumn()==indexOfButton.getColumn()&&moveNotation.getFrom().getRow()==indexOfButton.getRow()){
                move(piece, boardButtons[moveNotation.getTo().getRow()][moveNotation.getTo().getColumn()], moveNotation.getPromotionType());
                return true;
            }
        }
        return false;
    }
    private void promote(String promotionType, JButton buttonForNewPiece, Color colorForNewPiece, ArrayList<Piece> piecesForNewPiece){
        piecesForNewPiece.add(createPromotedPiece(promotionType, buttonForNewPiece, colorForNewPiece));
    }

    /**
     * Creates the piece a pawn is promoted to and puts its icon on the square.
     * Used both for local promotions (PawnPromoteButtonHandler) and for
     * promotions that arrive from the opponent through Firestore.
     *
     * @param promotionType "Queen", "Rook", "Bishop" or "Knight"
     */
    public static Piece createPromotedPiece(String promotionType, JButton buttonForNewPiece, Color colorForNewPiece){
        Piece newPiece = switch (promotionType) {
            case "Rook" -> {
                Rook rook = new Rook(buttonForNewPiece, colorForNewPiece);
                rook.increasehowmanytimesitmoved(); // a promoted rook can never castle
                yield rook;
            }
            case "Bishop" -> new Bishop(buttonForNewPiece, colorForNewPiece);
            case "Knight" -> new Knight(buttonForNewPiece, colorForNewPiece);
            default -> new Queen(buttonForNewPiece, colorForNewPiece);
        };
        buttonForNewPiece.setIcon(IconLoader.forPiece(newPiece));
        buttonForNewPiece.setDisabledIcon(buttonForNewPiece.getIcon());
        return newPiece;
    }

    /** "x2" -> "x3" on the captured-pieces panel. */
    private void increaseCount(JLabel countLabel){
        int count=Integer.parseInt(countLabel.getText().substring(1));
        countLabel.setText("x"+(count+1));
    }

    private int findSmallerOne(Index indexForCompare){
        return Math.min(indexForCompare.getRow(), indexForCompare.getColumn());
    }
}