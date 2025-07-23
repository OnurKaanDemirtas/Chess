package Logic;
import GUI.PawnPromoteGUI;
import Pieces.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class chessmodel {
    private ArrayList<Piece> blackpieces;
    private ArrayList<Piece> whitepieces;
    private ArrayList<Piece> takedblackpieces;
    private ArrayList<Piece> takedwhitepieces;
    private JButton [][] boardbuttons;
    private ArrayList<Piece> attackingpiecestotheWhiteking;
    private ArrayList<Piece> attackingpiecestotheBlackking;

    public chessmodel(ArrayList<Piece> blackpieces,ArrayList<Piece> whitepieces,ArrayList<JButton> boardbuttons){
        this.blackpieces=blackpieces;
        this.whitepieces=whitepieces;
        this.boardbuttons=new JButton[8][8];
        for(int row=0;row<8;row++){
            for(int column=0;column<8;column++){
                this.boardbuttons[row][column]=boardbuttons.get(row*8+column);
            }
        }
        this.takedwhitepieces=new ArrayList<>();
        this.takedblackpieces=new ArrayList<>();
        this.attackingpiecestotheWhiteking=new ArrayList<>();
        this.attackingpiecestotheBlackking=new ArrayList<>();
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
    private boolean isthereafriendpieceforPawn(Index index, ArrayList<Piece> samecolorpieces){
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
                if (piece instanceof King) {
                    if (piece.getColor().equals(Color.WHITE)) {
                        attackingpiecestotheWhiteking.add(piece);
                    } else {
                        attackingpiecestotheBlackking.add(piece);
                    }
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
    private boolean canKingmovethatindex(King king, ArrayList<Piece> enemypieces, Index indexthatwantingtomove){
        Piece enemypiece=isthereaenemypiece(indexthatwantingtomove, enemypieces);
        if(enemypiece!=null){
            if(!enemypiece.getSupportingPieces().isEmpty()){
                return false;
            }
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

    public void findplayableplaces(ArrayList<Piece> pieces){
        ArrayList<Piece> enemypieces;
        if(pieces.equals(whitepieces)){
            enemypieces=blackpieces;
        }else{
            enemypieces=whitepieces;
        }
        for(Piece enemy:enemypieces){
           if(enemy instanceof King ){
               ((King)enemy).getPiecesthatcheck().clear();
           }
        }
        for(Piece piece:pieces){
            piece.getPlaybleplaces().clear();
            if(piece instanceof Knight){
                Index[] indexesformoves=
                        {new Index(2,1),new Index(2,-1),new Index(-2,1),new Index(-2,-1),
                                new Index(1,2),new Index(1,-2),new Index(-1,2),new Index(-1,-2)
                        };
                Index indexofPiece=findIndex(piece.getButton());
                for(Index index:indexesformoves){
                    Index temporaryIndex=new Index(indexofPiece.getRow()+ index.getRow(),indexofPiece.getColumn()+ index.getColumn());
                    if(isitintheBoard(temporaryIndex)){
                        if(!isthereafriendpiece(temporaryIndex,pieces,piece)){
                            piece.getPlaybleplaces().add(boardbuttons[temporaryIndex.getRow()][temporaryIndex.getColumn()]);
                        }
                    }
                }
            }else if(piece instanceof Bishop){
                calculateplaybleplacesforBishop(piece,pieces,enemypieces);
            }else if (piece instanceof Rook){
                calculateplaybleplacesforRook(piece,pieces,enemypieces);
            }else if(piece instanceof Queen){
                calculateplaybleplacesforRook(piece,pieces,enemypieces);
                calculateplaybleplacesforBishop(piece,pieces,enemypieces);
            }else if(piece instanceof Pawn){
                ((Pawn)piece).getAttackingplaces().clear();
                calculateplayableplacesforPawn((Pawn)piece,pieces,enemypieces);
            }else if(piece instanceof King){
                ((King)piece).getCastableplaces().clear();
                ((King)piece).getCastablerooks().clear();
                calculateplaybleplacesforKing((King) piece, pieces, enemypieces);
            }
        }
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
                    if (isthereafriendpieceforPawn(temporaryindex, pieces)) {
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
                if(!isthereafriendpieceforPawn(temporaryindex,pieces)&&piece==null){
                    pawn.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
            }
            Index[] temporaryindexes={new Index(indexofpiece.getRow()-1,indexofpiece.getColumn()+1),new Index(indexofpiece.getRow()-1,indexofpiece.getColumn()-1)};
            for(int i=0;i<temporaryindexes.length;i++){
                if(isitintheBoard(temporaryindexes[i])&&!isthereafriendpiece(temporaryindexes[i],pieces,pawn)){
                    pawn.getAttackingplaces().add(boardbuttons[temporaryindexes[i].getRow()][temporaryindexes[i].getColumn()]);
                    Piece piece= isthereaenemypieceforPawnandKing(temporaryindexes[i],enemypieces);
                    if(piece!=null&&!(piece instanceof King)){
                        pawn.getPlaybleplaces().add(boardbuttons[temporaryindexes[i].getRow()][temporaryindexes[i].getColumn()]);
                    }
                }
            }
        }else {
            if(pawn.getHowmanytimesitmoved()==0){
                for(int i=1;i<=2;i++){
                    Index temporaryindex=new Index(indexofpiece.getRow()+i,indexofpiece.getColumn());
                    Piece piece= isthereaenemypieceforPawnandKing(temporaryindex,enemypieces);
                    if (isthereafriendpieceforPawn(temporaryindex, pieces)) {
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
                if(!isthereafriendpieceforPawn(temporaryindex,pieces)&&piece==null){
                    pawn.getPlaybleplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
            }
            Index[] temporaryindexes={new Index(indexofpiece.getRow()+1,indexofpiece.getColumn()+1),new Index(indexofpiece.getRow()+1,indexofpiece.getColumn()-1)};
            for(int i=0;i<temporaryindexes.length;i++){
                if(isitintheBoard(temporaryindexes[i])&&!isthereafriendpiece(temporaryindexes[i],pieces,pawn)){
                    pawn.getAttackingplaces().add(boardbuttons[temporaryindexes[i].getRow()][temporaryindexes[i].getColumn()]);
                    Piece piece=isthereaenemypiece(temporaryindexes[i],enemypieces);
                    if(piece!=null&&!(piece instanceof King)){
                        pawn.getPlaybleplaces().add(boardbuttons[temporaryindexes[i].getRow()][temporaryindexes[i].getColumn()]);
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
                if(canKingmovethatindex(king, enemypieces, temporaryindex)) {
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
        Piece enemypiece;
        if(selectedpiece.getColor().equals(Color.WHITE)){
            enemypiece=isthereaenemypiece(indexofclickedbutton,blackpieces);
        }else{
            enemypiece=isthereaenemypiece(indexofclickedbutton,whitepieces);
        }
        if(enemypiece!=null){
            if(enemypiece.getColor().equals(Color.BLACK)){
                takedblackpieces.add(enemypiece);
                blackpieces.remove(enemypiece);
            }else{
                takedwhitepieces.add(enemypiece);
                whitepieces.remove(enemypiece);
            }
            enemypiece.getButton().setIcon(null);
            enemypiece.setButton(null);
        }
        if(selectedpiece instanceof Pawn){
            ((Pawn) selectedpiece).increasehowmanytimesitmoved();
            if(indexofclickedbutton.getRow()==0|| indexofclickedbutton.getRow()==7) {
                if(selectedpiece.getColor().equals(Color.WHITE)) {
                    new PawnPromoteGUI(clickedbutton, selectedpiece.getColor(), whitepieces);
                }else{
                    new PawnPromoteGUI(clickedbutton, selectedpiece.getColor(), blackpieces);
                }
                if(selectedpiece.getColor().equals(Color.WHITE)) {
                    whitepieces.remove(selectedpiece);
                    takedwhitepieces.add(selectedpiece);
                }else{
                    blackpieces.remove(selectedpiece);
                    takedblackpieces.add(selectedpiece);
                }
            }
        }else if(selectedpiece instanceof King king){
            king.increasehowmanytimesitmoved();
            Index indexofking=findIndex(selectedpiece.getButton());
            for(int i=0;i<king.getCastableplaces().size();i++){
                if(king.getCastableplaces().get(i).equals(clickedbutton)){
                    Rook rook=king.getCastablerooks().get(i);
                    Index indexofrook=findIndex(rook.getButton());
                    if(indexofrook.getColumn() < indexofking.getColumn()) {
                        JButton newrookbutton = boardbuttons[indexofking.getRow()][indexofking.getColumn() - 1];
                        newrookbutton.setIcon(rook.getButton().getIcon());
                        rook.getButton().setIcon(null);
                        rook.setButton(newrookbutton);
                    }else{
                        JButton newrookbutton = boardbuttons[indexofking.getRow()][indexofking.getColumn() + 1];
                        newrookbutton.setIcon(rook.getButton().getIcon());
                        rook.getButton().setIcon(null);
                        rook.setButton(newrookbutton);
                    }
                    rook.increasehowmanytimesitmoved();
                }
            }
        }else if(selectedpiece instanceof Rook){
            ((Rook)selectedpiece).increasehowmanytimesitmoved();
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
    }

    private int findsmallerone(Index indexforcompare){
        return Math.min(indexforcompare.getRow(), indexforcompare.getColumn());
    }
}
