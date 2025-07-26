package Logic;
import GUI.BoardGUI;
import GUI.Move;
import GUI.PawnPromoteGUI;
import Pieces.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class chessmodel {
    private final ArrayList<Piece> blackpieces;
    private final  ArrayList<Piece> whitepieces;
    private final JButton [][] boardbuttons;
    private final BoardGUI boardGUI;

    public chessmodel(ArrayList<Piece> blackpieces, ArrayList<Piece> whitepieces, ArrayList<JButton> boardbuttons,BoardGUI boardGUI){
        this.blackpieces=blackpieces;
        this.whitepieces=whitepieces;
        this.boardbuttons=new JButton[8][8];
        for(int row=0;row<8;row++){
            for(int column=0;column<8;column++){
                this.boardbuttons[row][column]=boardbuttons.get(row*8+column);
            }
        }
        this.boardGUI=boardGUI;
        findplayableplaces(whitepieces);
        for (Piece piece : whitepieces) {
            if(piece instanceof King king) {
                arrangetheplayableplaces(whitepieces, blackpieces, king);
            }
        }
    }
    public Index findIndex(JButton wantedbutton){
        for(int row=0;row<8;row++){
            for(int column=0;column<8;column++){
                if(boardbuttons[row][column].equals(wantedbutton)){
                    return new Index(row,column);
                }
            }
        }
        return null;
    }
    private boolean isitintheBoard(Index index){
        return index.getRow() >= 0 && index.getColumn() >= 0 && index.getRow() <= 7 && index.getColumn() <= 7;
    }
    private boolean isthereafriendpiece(Index index, ArrayList<Piece> samecolorpieces, Piece selectedpiece){
        for(Piece piece:samecolorpieces){
            if(boardbuttons[index.getRow()][index.getColumn()].equals(piece.getButton())){
                if(!piece.getSupportingPieces().contains(selectedpiece)){
                    piece.getSupportingPieces().add(selectedpiece);
                }
                return true;
            }
        }
        return false;
    }
    private boolean isthereafriendpieceinthisroad(Index index, ArrayList<Piece> samecolorpieces){
        for(Piece piece:samecolorpieces){
            if(boardbuttons[index.getRow()][index.getColumn()].equals(piece.getButton())){
                return true;
            }
        }
        return false;
    }
    private Piece isthereaenemypiece(Index index, ArrayList<Piece> enemypieces){
        for(Piece piece:enemypieces){
            if(boardbuttons[index.getRow()][index.getColumn()].equals(piece.getButton())){
                if (piece instanceof King king) {
                    king.getPiecesthatcheck().add(piece);
                    king.getPiecesthatcheck().remove(king);
                }
                return piece;
            }
        }
        return null;
    }
    private Piece isthereaenemypieceforPawnandKing(Index index, ArrayList<Piece> enemypieces){
        for(Piece piece:enemypieces){
            if(boardbuttons[index.getRow()][index.getColumn()].equals(piece.getButton())){
                return piece;
            }
        }
        return null;
    }
    private boolean canKingmovethatindex( ArrayList<Piece> enemypieces, Index indexthatwantingtomove){
        Piece enemypiece=isthereaenemypiece(indexthatwantingtomove, enemypieces);
        if(enemypiece!=null){
            return enemypiece.getSupportingPieces().isEmpty();
        }
        for(Piece piece: enemypieces){
            if(piece instanceof King){
                Index indexofking = findIndex(piece.getButton());
                if(Math.abs(indexthatwantingtomove.getRow() - indexofking.getRow()) <= 1 &&
                        Math.abs(indexthatwantingtomove.getColumn() - indexofking.getColumn()) <= 1){
                    return false;
                }
            }else if(piece instanceof Pawn) {
                if (((Pawn) piece).getAttackingplaces().contains(boardbuttons[indexthatwantingtomove.getRow()][indexthatwantingtomove.getColumn()])) {
                    return false;
                }
            }else{
                if(piece.getPlaybleplaces().contains(boardbuttons[indexthatwantingtomove.getRow()][indexthatwantingtomove.getColumn()])){
                    return false;
                }
            }
        }
        return true;
    }

    public void findplayableplaces(ArrayList<Piece> pieces) {
        ArrayList<Piece> enemypieces;
        if (pieces.equals(whitepieces)) {
            enemypieces = blackpieces;
        } else {
            enemypieces = whitepieces;
        }
        for (Piece enemy : enemypieces) {
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
                    Index[] indexesformoves =
                            {new Index(2, 1), new Index(2, -1), new Index(-2, 1), new Index(-2, -1),
                                    new Index(1, 2), new Index(1, -2), new Index(-1, 2), new Index(-1, -2)
                            };
                    Index indexofPiece = findIndex(piece.getButton());
                    for (Index index : indexesformoves) {
                        Index temporaryIndex = new Index(indexofPiece.getRow() + index.getRow(), indexofPiece.getColumn() + index.getColumn());
                        if (isitintheBoard(temporaryIndex)) {
                            if (!isthereafriendpiece(temporaryIndex, pieces, piece)) {
                                knight.getPlaybleplaces().add(boardbuttons[temporaryIndex.getRow()][temporaryIndex.getColumn()]);
                            }
                        }
                    }
                }
                case Bishop bishop -> calculateplaybleplacesforBishop(bishop, pieces, enemypieces);
                case Rook rook -> calculateplaybleplacesforRook(rook, pieces, enemypieces);
                case Queen queen -> {
                    calculateplaybleplacesforRook(queen, pieces, enemypieces);
                    calculateplaybleplacesforBishop(queen, pieces, enemypieces);
                }
                case Pawn pawn -> {
                    pawn.getAttackingplaces().clear();
                    pawn.getEnpassantplaces().clear();
                    calculateplayableplacesforPawn(pawn, pieces, enemypieces);
                }
                case King king -> {
                    king.getCastableplaces().clear();
                    king.getCastablerooks().clear();
                    calculateplaybleplacesforKing(king, pieces, enemypieces);
                }
                default -> {
                }
            }
        }
    }
    public void arrangetheplayableplaces(ArrayList<Piece> pieces,ArrayList<Piece> enemypieces,King king) {
        for(Piece piece:pieces){
            JButton button = piece.getButton();
            for(int i=piece.getPlaybleplaces().size()-1;i>=0;i--){
                JButton playblebutton = piece.getPlaybleplaces().get(i);
                Piece enemypiece = isthereaenemypiece(findIndex(playblebutton), enemypieces);
                if(enemypiece != null) {
                    enemypieces.remove(enemypiece);
                }
                piece.setButton(playblebutton);
                king.getPiecesthatcheck().clear();
                findplayableplaces(enemypieces);
                if(!king.getPiecesthatcheck().isEmpty()) {
                    piece.getPlaybleplaces().remove(playblebutton);
                }
                piece.setButton(button);
                if(enemypiece != null) {
                    enemypieces.add(enemypiece);
                }
            }
        }
    }
    public int isitcheckmate(ArrayList<Piece> pieces){
        King king = null;
        for(Piece piece : pieces) {
            if(piece instanceof King){
                king= (King) piece;
            }
        }
        if (king!=null) {
            boolean isthereaplayableplace = true;
            for (Piece piece : pieces) {
                if (!piece.getPlaybleplaces().isEmpty()) {
                    isthereaplayableplace=false;
                }
            }
            if(isthereaplayableplace&&!king.getPiecesthatcheck().isEmpty()){
                return 0; // it is checkmate
            } else if (isthereaplayableplace && king.getPiecesthatcheck().isEmpty()) {
                return 1; // it is stalemate
            }
        }
        return 2;// it is not checkmate or stalemate
    }
    private void calculateplaybleplacesforBishop(Piece piece, ArrayList<Piece> pieces, ArrayList<Piece> enemypieces){
        Index indexofPiece=findIndex(piece.getButton());
        Index[] indexesforcalculate =
                {new Index(-1,-1),new Index(-1,1),new Index(1,-1),new Index(1,1)};
        Index[] indexesforcompare=
                {indexofPiece,new Index(indexofPiece.getRow(),7-indexofPiece.getColumn()),new Index(7-indexofPiece.getRow(),indexofPiece.getColumn()),new Index(7-indexofPiece.getRow(),7-indexofPiece.getColumn())};
        for(int a=0;a<4;a++){
            int mindistance=findsmallerone(indexesforcompare[a]);
            if(mindistance>0){
                for(int i=1;i<=mindistance;i++) {
                    Index temporaryindex=new Index(indexofPiece.getRow()+indexesforcalculate[a].getRow()*i, indexofPiece.getColumn()+indexesforcalculate[a].getColumn()*i);
                    if (isthereafriendpiece(temporaryindex, pieces,piece)) {
                        break;
                    }
                    Piece enemypiece=isthereaenemypiece(temporaryindex,enemypieces);
                    if (enemypiece!=null) {
                        if(enemypiece instanceof King){
                            ((King) enemypiece).getPiecesthatcheck().add(piece);
                        }else{
                            piece.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                            break;
                        }
                    }else {
                        piece.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }
                }
            }
        }
    }
    private void calculateplaybleplacesforRook(Piece piece, ArrayList<Piece> pieces, ArrayList<Piece> enemypieces){
        Index indexofPiece=findIndex(piece.getButton());
        for(int i=1;i<=indexofPiece.getColumn();i++){//going up
            Index temporaryindex=new Index(indexofPiece.getRow(),indexofPiece.getColumn()-i);
            if (isthereafriendpiece(temporaryindex, pieces,piece)) {
                break;
            }
            Piece enemypiece=isthereaenemypiece(temporaryindex,enemypieces);
            if (enemypiece!=null) {
                if(enemypiece instanceof King){
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                }else{
                    piece.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    break;
                }
            }else {
                piece.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
        for(int i=1;i<=indexofPiece.getRow();i++){//going left
            Index temporaryindex=new Index(indexofPiece.getRow()-i,indexofPiece.getColumn());
            if (isthereafriendpiece(temporaryindex, pieces,piece)) {
                break;
            }
            Piece enemypiece=isthereaenemypiece(temporaryindex,enemypieces);
            if (enemypiece!=null) {
                if(enemypiece instanceof King){
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                }else{
                    piece.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    break;
                }
            }else {
                piece.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
        for(int i=1;i<=7-indexofPiece.getColumn();i++){ // going right
            Index temporaryindex = new Index(indexofPiece.getRow(), indexofPiece.getColumn() + i);
            if (isthereafriendpiece(temporaryindex, pieces, piece)) {
                break;
            }
            Piece enemypiece = isthereaenemypiece(temporaryindex, enemypieces);
            if (enemypiece != null) {
                if (enemypiece instanceof King) {
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                } else {
                    piece.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    break;
                }
            } else {
                piece.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
        for(int i=1;i<=7-indexofPiece.getRow();i++){ // going down
            Index temporaryindex = new Index(indexofPiece.getRow() + i, indexofPiece.getColumn());
            if (isthereafriendpiece(temporaryindex, pieces, piece)) {
                break;
            }
            Piece enemypiece = isthereaenemypiece(temporaryindex, enemypieces);
            if (enemypiece != null) {
                if (enemypiece instanceof King) {
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                } else {
                    piece.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    break;
                }
            } else {
                piece.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
    }
    private void calculateplayableplacesforPawn(Pawn pawn, ArrayList<Piece> pieces, ArrayList<Piece> enemypieces){
        Index indexofpiece=findIndex(pawn.getButton());
        if(pawn.getColor().equals(Color.WHITE)){
            if(pawn.getHowmanytimesitmoved()==0){
                for(int i=1;i<=2;i++){
                    Index temporaryindex=new Index(indexofpiece.getRow()-i,indexofpiece.getColumn());
                    Piece piece= isthereaenemypieceforPawnandKing(temporaryindex,enemypieces);
                    if (isthereafriendpieceinthisroad(temporaryindex, pieces)) {
                        break;
                    }else if(piece!=null){
                        break;
                    }else{
                        pawn.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }
                }
            }else{
                Index temporaryindex=new Index(indexofpiece.getRow()-1,indexofpiece.getColumn());
                Piece piece= isthereaenemypieceforPawnandKing(temporaryindex,enemypieces);
                if(!isthereafriendpieceinthisroad(temporaryindex,pieces)&&piece==null){
                    pawn.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
            }
            Index[] temporaryindexes={new Index(indexofpiece.getRow()-1,indexofpiece.getColumn()+1),new Index(indexofpiece.getRow()-1,indexofpiece.getColumn()-1)};
            for (Index temporaryindex : temporaryindexes) {
                if (isitintheBoard(temporaryindex) && !isthereafriendpiece(temporaryindex, pieces, pawn)) {
                    Index indexforenpassant = new Index(temporaryindex.getRow()+1, temporaryindex.getColumn());
                    Piece pawncandidate = isthereaenemypieceforPawnandKing(indexforenpassant, enemypieces);
                    Pawn temporarypawn = null;
                    if(pawncandidate instanceof Pawn){
                        temporarypawn = (Pawn) pawncandidate;
                    }
                    pawn.getAttackingplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    Piece piece = isthereaenemypieceforPawnandKing(temporaryindex, enemypieces);
                    if (piece != null && !(piece instanceof King)) {
                        pawn.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }else if(temporarypawn!=null&&temporarypawn.getHowmanytimesitmoved()==1&& indexforenpassant.getRow() == 3){
                        pawn.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                        pawn.getEnpassantplaces().add(boardbuttons[indexforenpassant.getRow()][indexforenpassant.getColumn()]);
                    }
                }
            }
        }else {
            if(pawn.getHowmanytimesitmoved()==0){
                for(int i=1;i<=2;i++){
                    Index temporaryindex=new Index(indexofpiece.getRow()+i,indexofpiece.getColumn());
                    Piece piece= isthereaenemypieceforPawnandKing(temporaryindex,enemypieces);
                    if (isthereafriendpieceinthisroad(temporaryindex, pieces)) {
                        break;
                    }else if(piece!=null){
                        break;
                    }else{
                        pawn.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }
                }
            }else{
                Index temporaryindex=new Index(indexofpiece.getRow()+1,indexofpiece.getColumn());
                Piece piece= isthereaenemypieceforPawnandKing(temporaryindex,enemypieces);
                if(!isthereafriendpieceinthisroad(temporaryindex,pieces)&&piece==null){
                    pawn.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
            }
            Index[] temporaryindexes={new Index(indexofpiece.getRow()+1,indexofpiece.getColumn()+1),new Index(indexofpiece.getRow()+1,indexofpiece.getColumn()-1)};
            for (Index temporaryindex : temporaryindexes) {
                if (isitintheBoard(temporaryindex) && !isthereafriendpiece(temporaryindex, pieces, pawn)) {
                    Index indexforenpassant = new Index(temporaryindex.getRow()-1, temporaryindex.getColumn());
                    Piece pawncandidate = isthereaenemypieceforPawnandKing(indexforenpassant, enemypieces);
                    Pawn temporarypawn = null;
                    if(pawncandidate instanceof Pawn){
                        temporarypawn = (Pawn) pawncandidate;
                    }
                    pawn.getAttackingplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    Piece piece = isthereaenemypiece(temporaryindex, enemypieces);
                    if (piece != null && !(piece instanceof King)) {
                        pawn.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }else if(temporarypawn!=null&&temporarypawn.getHowmanytimesitmoved()==1&& indexforenpassant.getRow() == 4){
                        pawn.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                        pawn.getEnpassantplaces().add(boardbuttons[indexforenpassant.getRow()][indexforenpassant.getColumn()]);
                    }
                }
            }
        }
    }
    public void calculateplaybleplacesforKing(King king, ArrayList<Piece> pieces, ArrayList<Piece> enemypieces){
        Index indexofking = findIndex(king.getButton());
        Index[] indexesforcalculate = {
                new Index(-1,-1), new Index(-1,0), new Index(-1,1),
                new Index(0,-1),  new Index(0,1),
                new Index(1,-1),  new Index(1,0), new Index(1,1)
        };
        for(Index index : indexesforcalculate){
            Index temporaryindex = new Index(indexofking.getRow() + index.getRow(), indexofking.getColumn() + index.getColumn());
            if(isitintheBoard(temporaryindex) && !isthereafriendpiece(temporaryindex, pieces, king)) {
                if(canKingmovethatindex(enemypieces, temporaryindex)) {
                    king.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
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
                        Index indexofrook = findIndex(rook.getButton());
                        int distancebetweenkingandrook = Math.abs(indexofking.getColumn() - indexofrook.getColumn());
                        if(canplayerdocastling(distancebetweenkingandrook, indexofking, indexofrook, pieces, enemypieces, king)){
                            int direction = (indexofrook.getColumn() - indexofking.getColumn()) > 0 ? 1 : -1;
                            int castlingColumn = indexofking.getColumn() + direction * 2;
                            king.getPlaybleplaces().add(boardbuttons[indexofking.getRow()][castlingColumn]);
                            king.getCastableplaces().add(boardbuttons[indexofking.getRow()][castlingColumn]);
                            king.getCastablerooks().add(rook);
                        }
                    }
                }
            }
        }
    }
    private boolean canplayerdocastling(int distancebetweenkingandrook, Index indexofking, Index indexofrook, ArrayList<Piece> pieces, ArrayList<Piece> enemypieces, King king){
        int direction = (indexofrook.getColumn() - indexofking.getColumn()) > 0 ? 1 : -1;
        for(int a = 1; a < distancebetweenkingandrook; a++){
            int col = indexofking.getColumn() + direction * a;
            Index temporaryindex = new Index(indexofking.getRow(), col);
            if(isthereafriendpiece(temporaryindex, pieces, king)){
                return false;
            }
            Piece enemypiece = isthereaenemypiece(temporaryindex, enemypieces);
            if(enemypiece != null) {
                return false;
            }
            for(Piece enemy : enemypieces){
                if(enemy instanceof King){
                    Index indexofenemyking = findIndex(enemy.getButton());
                    if(Math.abs(temporaryindex.getRow() - indexofenemyking.getRow()) <= 1 &&
                            Math.abs(temporaryindex.getColumn() - indexofenemyking.getColumn()) <= 1){
                        return false;
                    }
                }else{
                    if(enemy.getPlaybleplaces().contains(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()])){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public void move(Piece selectedpiece, JButton clickedbutton){
        Index indexofclickedbutton=findIndex(clickedbutton);
        Index indexofselectedpiece=findIndex(selectedpiece.getButton());
        String piecetakingnotation="";
        String playedsquarenotation=
                String.valueOf((char) ('a' + indexofclickedbutton.getColumn())) +
                        (8 - indexofclickedbutton.getRow());
        String checknotation="";
        String rooknotation=null;
        Piece enemypiece;
        King WhiteKing = null;
        for(Piece piece:whitepieces){
            if(piece instanceof King){
                WhiteKing = (King) piece;
            }else if(piece instanceof Pawn pawn){
                if(pawn.getHowmanytimesitmoved()==1){
                    pawn.increasehowmanytimesitmoved();
                }
            }
        }
        King BlackKing = null;
        for(Piece piece:blackpieces){
            if(piece instanceof King){
                BlackKing = (King) piece;
            } else if (piece instanceof Pawn pawn) {
                if (pawn.getHowmanytimesitmoved() == 1) {
                    pawn.increasehowmanytimesitmoved();
                }
            }
        }
        if(selectedpiece.getColor().equals(Color.WHITE)){
            enemypiece=isthereaenemypiece(indexofclickedbutton,blackpieces);
        }else{
            enemypiece=isthereaenemypiece(indexofclickedbutton,whitepieces);
        }
        if(enemypiece!=null){
            if(enemypiece.getColor().equals(Color.BLACK)){
                String count;
                if(enemypiece instanceof Queen){
                    count=boardGUI.getTakedblackqueencount().getText();
                    String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakedblackqueencount().setText(newcount);
                } else if (enemypiece instanceof Rook) {
                    count = boardGUI.getTakedblackrookcount().getText();
                    String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakedblackrookcount().setText(newcount);
                } else if (enemypiece instanceof Bishop) {
                    count = boardGUI.getTakedblackbishopcount().getText();
                    String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakedblackbishopcount().setText(newcount);
                } else if (enemypiece instanceof Knight) {
                    count = boardGUI.getTakedblackknightcount().getText();
                    String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakedblackknightcount().setText(newcount);
                } else if (enemypiece instanceof Pawn) {
                    count = boardGUI.getTakedblackpawncount().getText();
                    String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakedblackpawncount().setText(newcount);
                }
                blackpieces.remove(enemypiece);
            }else{
                String count;
                if(enemypiece instanceof Queen){
                    count=boardGUI.getTakedwhitequeencount().getText();
                    String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakedwhitequeencount().setText(newcount);
                } else if (enemypiece instanceof Rook) {
                    count = boardGUI.getTakedwhiterookcount().getText();
                    String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakedwhiterookcount().setText(newcount);
                } else if (enemypiece instanceof Bishop) {
                    count = boardGUI.getTakedwhitebishopcount().getText();
                    String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakedwhitebishopcount().setText(newcount);
                } else if (enemypiece instanceof Knight) {
                    count = boardGUI.getTakedwhiteknightcount().getText();
                    String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakedwhiteknightcount().setText(newcount);
                } else if (enemypiece instanceof Pawn) {
                    count = boardGUI.getTakedwhitepawncount().getText();
                    String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                    boardGUI.getTakedwhitepawncount().setText(newcount);
                }
                whitepieces.remove(enemypiece);
            }
            if(selectedpiece instanceof Pawn pawn){
                piecetakingnotation = String.valueOf((char) ('a' + indexofselectedpiece.getColumn())) ;
            }
            piecetakingnotation+="x";
            enemypiece.getButton().setIcon(null);
            enemypiece.setButton(null);
        }
        switch (selectedpiece) {
            case Pawn pawn -> {
                Index indexofenpassant = findIndex(clickedbutton);
                if(selectedpiece.getColor().equals(Color.WHITE)){
                    indexofenpassant = new Index(indexofenpassant.getRow() + 1, indexofenpassant.getColumn());
                }else{
                    indexofenpassant = new Index(indexofenpassant.getRow() - 1, indexofenpassant.getColumn());
                }
                if (pawn.getEnpassantplaces().contains(boardbuttons[indexofenpassant.getRow()][indexofenpassant.getColumn()])) {
                    if(selectedpiece.getColor().equals(Color.WHITE)){
                        Piece takedpawn = isthereaenemypieceforPawnandKing(indexofenpassant, blackpieces);
                        takedpawn.getButton().setIcon(null);
                        takedpawn.setButton(null);
                        String count = boardGUI.getTakedblackpawncount().getText();
                        String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                        boardGUI.getTakedblackpawncount().setText(newcount);
                        blackpieces.remove(takedpawn);
                    }else{
                        Piece takedpawn = isthereaenemypieceforPawnandKing(indexofenpassant, whitepieces);
                        takedpawn.getButton().setIcon(null);
                        takedpawn.setButton(null);
                        String count = boardGUI.getTakedwhitepawncount().getText();
                        String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                        boardGUI.getTakedwhitepawncount().setText(newcount);
                        whitepieces.remove(takedpawn);
                    }
                }
                if (indexofclickedbutton.getRow() == 0 || indexofclickedbutton.getRow() == 7) {
                    if (selectedpiece.getColor().equals(Color.WHITE)) {
                        new PawnPromoteGUI(clickedbutton, selectedpiece.getColor(), whitepieces,boardGUI);
                    } else {
                        new PawnPromoteGUI(clickedbutton, selectedpiece.getColor(), blackpieces,boardGUI);
                    }
                    if (selectedpiece.getColor().equals(Color.WHITE)) {
                        whitepieces.remove(selectedpiece);
                        blackpieces.remove(selectedpiece);
                        String count = boardGUI.getTakedwhitepawncount().getText();
                        String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                        boardGUI.getTakedwhitepawncount().setText(newcount);
                    } else {
                        String count = boardGUI.getTakedblackpawncount().getText();
                        String newcount ="x"+ (Integer.parseInt(count.substring(1)) + 1);
                        boardGUI.getTakedblackpawncount().setText(newcount);
                    }
                }
                pawn.increasehowmanytimesitmoved();
            }
            case King king -> {
                king.increasehowmanytimesitmoved();
                Index indexofking = findIndex(selectedpiece.getButton());
                for (int i = 0; i < king.getCastableplaces().size(); i++) {
                    if (king.getCastableplaces().get(i).equals(clickedbutton)) {
                        Rook rook = king.getCastablerooks().get(i);
                        Index indexofrook = findIndex(rook.getButton());
                        JButton newrookbutton;
                        if (indexofrook.getColumn() < indexofking.getColumn()) {
                            newrookbutton = boardbuttons[indexofking.getRow()][indexofking.getColumn() - 1];
                        } else {
                            newrookbutton = boardbuttons[indexofking.getRow()][indexofking.getColumn() + 1];
                        }
                        newrookbutton.setIcon(rook.getButton().getIcon());
                        rook.getButton().setIcon(null);
                        rook.setButton(newrookbutton);
                        rook.increasehowmanytimesitmoved();
                        if(Math.abs(indexofrook.getColumn() - indexofking.getColumn()) == 4) {
                            rooknotation = "O-O-O";
                        } else {
                            rooknotation = "O-O";
                        }
                        break;
                    }
                }
            }
            case Rook rook -> rook.increasehowmanytimesitmoved();
            default -> {
            }
        }
        clickedbutton.setIcon(selectedpiece.getButton().getIcon());
        selectedpiece.getButton().setIcon(null);
        selectedpiece.getButton().setEnabled(false);
        selectedpiece.setButton(clickedbutton);
        clickedbutton.setEnabled(true);
        for(JButton button:selectedpiece.getPlaybleplaces()){
            button.setEnabled(false);
        }
        for(Piece piece:whitepieces){
            piece.getButton().setEnabled(false);
        }
        for (Piece piece:blackpieces){
            piece.getButton().setEnabled(false);
        }
        if(selectedpiece.getColor().equals(Color.WHITE)){
            findplayableplaces(blackpieces);
            arrangetheplayableplaces(blackpieces,whitepieces,BlackKing);
            if(BlackKing!=null&&!BlackKing.getPiecesthatcheck().isEmpty()){
                checknotation="+";
            }
            if(isitcheckmate(blackpieces)==0) {
                JOptionPane.showMessageDialog(null, "White wins!");
                checknotation="#";
            }else if(isitcheckmate(blackpieces)==1) {
                JOptionPane.showMessageDialog(null, "Stalemate!");
            }
            Move move;
            if(rooknotation!=null) {
                move = new Move(boardGUI, rooknotation);
            }else {
                move = new Move(boardGUI, selectedpiece.getPiecetype()+piecetakingnotation+playedsquarenotation+checknotation);
            }
            boardGUI.getWhitemovesListModel().addElement(move);
        } else {
            findplayableplaces(whitepieces);
            arrangetheplayableplaces(whitepieces,blackpieces,WhiteKing);
            if(WhiteKing!=null&&!WhiteKing.getPiecesthatcheck().isEmpty()){
                checknotation="+";
            }
            if(isitcheckmate(whitepieces)==0) {
                JOptionPane.showMessageDialog(null, "Black wins!");
                checknotation="#";
            } else if (isitcheckmate(whitepieces)==1) {
                JOptionPane.showMessageDialog(null, "Stalemate!");
            }
            Move move;
            if(rooknotation!=null) {
                move = new Move(boardGUI, rooknotation);
            }else {
                move = new Move(boardGUI, selectedpiece.getPiecetype()+piecetakingnotation+playedsquarenotation+checknotation);
            }
            boardGUI.getBlackmovesListModel().addElement(move);
        }
        boardGUI.getFrame().validate();
    }

    private int findsmallerone(Index indexforcompare){
        return Math.min(indexforcompare.getRow(), indexforcompare.getColumn());
    }
}
