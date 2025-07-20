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

    public ArrayList<Piece> getBlackpieces() {
        return blackpieces;
    }

    public ArrayList<Piece> getWhitepieces() {
        return whitepieces;
    }

    public JButton[][] getBoardpanels(){
        return boardbuttons;
    }

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
    private boolean isthereafriendpiece(Index index,ArrayList<Piece> samecolorpieces){
        for(Piece piece:samecolorpieces){
            if(boardbuttons[index.getRow()][index.getColumn()].equals(piece.getButton())){
                return true;
            }
        }
        return false;
    }
    private Piece isthereaenemypiece(Index index,ArrayList<Piece> enemypieces){
        for(Piece piece:enemypieces){
            if(boardbuttons[index.getRow()][index.getColumn()].equals(piece.getButton())){
                return piece;
            }
        }
        return null;
    }
    public void findmovableplaces(ArrayList<Piece> pieces){
        ArrayList<Piece> enemypieces=new ArrayList<>();
        if(pieces.equals(whitepieces)){
            enemypieces=blackpieces;
        }else{
            enemypieces=whitepieces;
        }
        for(Piece piece:pieces){
            if(piece instanceof Knight){
                Index[] indexesformoves=
                        {new Index(2,1),new Index(2,-1),new Index(-2,1),new Index(-2,-1),
                                new Index(1,2),new Index(1,-2),new Index(-1,2),new Index(-1,-2)
                        };
                Index indexofPiece=findIndex(piece.getButton());
                for(Index index:indexesformoves){
                    Index temporaryIndex=new Index(indexofPiece.getRow()+ index.getRow(),indexofPiece.getColumn()+ index.getColumn());
                    if(isitintheBoard(temporaryIndex)){
                        if(!isthereafriendpiece(temporaryIndex,pieces)){
                            piece.getMovableplaces().add(boardbuttons[temporaryIndex.getRow()][temporaryIndex.getColumn()]);
                        }
                    }
                }
            }else if(piece instanceof Bishop){
                calculatemovableplacesforBishop(piece,pieces,enemypieces);
            }else if (piece instanceof Rook){
                calculatemovableplacesforRook(piece,pieces,enemypieces);
            }else if(piece instanceof Queen){
                calculatemovableplacesforRook(piece,pieces,enemypieces);
                calculatemovableplacesforBishop(piece,pieces,enemypieces);
            }else if(piece instanceof Pawn){
                calculatemovableplacesforPawn((Pawn)piece,pieces,enemypieces);
            }
        }
    }
    private void calculatemovableplacesforBishop(Piece piece, ArrayList<Piece> pieces, ArrayList<Piece> enemypieces){
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
                    if (isthereafriendpiece(temporaryindex, pieces)) {
                        break;
                    }
                    Piece enemypiece=isthereaenemypiece(temporaryindex,enemypieces);
                    if (enemypiece!=null) {
                        if(enemypiece instanceof King){
                            ((King) enemypiece).getPiecesthatcheck().add(piece);
                        }else{
                            piece.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                            break;
                        }
                    }else {
                        piece.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }
                }
            }
        }
    }
    private void calculatemovableplacesforRook(Piece piece,ArrayList<Piece> pieces,ArrayList<Piece> enemypieces){
        Index indexofPiece=findIndex(piece.getButton());
        for(int i=1;i<=indexofPiece.getColumn();i++){//going up
            Index temporaryindex=new Index(indexofPiece.getRow(),indexofPiece.getColumn()-i);
            if (isthereafriendpiece(temporaryindex, pieces)) {
                break;
            }
            Piece enemypiece=isthereaenemypiece(temporaryindex,enemypieces);
            if (enemypiece!=null) {
                if(enemypiece instanceof King){
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                }else{
                    piece.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    break;
                }
            }else {
                piece.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
        for(int i=1;i<=indexofPiece.getRow();i++){//going left
            Index temporaryindex=new Index(indexofPiece.getRow()-i,indexofPiece.getColumn());
            if (isthereafriendpiece(temporaryindex, pieces)) {
                break;
            }
            Piece enemypiece=isthereaenemypiece(temporaryindex,enemypieces);
            if (enemypiece!=null) {
                if(enemypiece instanceof King){
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                }else{
                    piece.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    break;
                }
            }else {
                piece.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
        for(int i=1;i<=7-indexofPiece.getRow();i++){//going right
            Index temporaryindex=new Index(indexofPiece.getRow()+i,indexofPiece.getColumn());
            if (isthereafriendpiece(temporaryindex, pieces)) {
                break;
            }
            Piece enemypiece=isthereaenemypiece(temporaryindex,enemypieces);
            if (enemypiece!=null) {
                if(enemypiece instanceof King){
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                }else{
                    piece.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    break;
                }
            }else {
                piece.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
        for(int i=1;i<=7-indexofPiece.getColumn();i++){//going down
            Index temporaryindex=new Index(indexofPiece.getRow(),7-indexofPiece.getColumn());
            if (isthereafriendpiece(temporaryindex, pieces)) {
                break;
            }
            Piece enemypiece=isthereaenemypiece(temporaryindex,enemypieces);
            if (enemypiece!=null) {
                if(enemypiece instanceof King){
                    ((King) enemypiece).getPiecesthatcheck().add(piece);
                }else{
                    piece.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    break;
                }
            }else {
                piece.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
            }
        }
    }
    private void calculatemovableplacesforPawn(Pawn pawn,ArrayList<Piece> pieces,ArrayList<Piece> enemypieces){
        Index indexofpiece=findIndex(pawn.getButton());
        if(pawn.getColor().equals(Color.WHITE)){
            if(pawn.getHowmanytimesitmoved()==0){
                for(int i=1;i<=2;i++){
                    Index temporaryindex=new Index(indexofpiece.getRow()-i,indexofpiece.getColumn());
                    Piece piece=isthereaenemypiece(temporaryindex,enemypieces);
                    if (isthereafriendpiece(temporaryindex, pieces)) {
                        break;
                    }else if(piece!=null){
                        break;
                    }else{
                        pawn.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }
                }
            }else{
                Index temporaryindex=new Index(indexofpiece.getRow()-1,indexofpiece.getColumn());
                Piece piece=isthereaenemypiece(temporaryindex,enemypieces);
                if(!isthereafriendpiece(temporaryindex,pieces)&&piece==null){
                    pawn.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
            }
            Index[] temporaryindexes={new Index(indexofpiece.getRow()-1,indexofpiece.getColumn()+1),new Index(indexofpiece.getRow()-1,indexofpiece.getColumn()-1)};
            for(int i=0;i<temporaryindexes.length;i++){
                if(isitintheBoard(temporaryindexes[i])&&!isthereafriendpiece(temporaryindexes[i],pieces)){
                    Piece piece=isthereaenemypiece(temporaryindexes[i],enemypieces);
                    if(piece!=null&&!(piece instanceof King)){
                        pawn.getMovableplaces().add(boardbuttons[temporaryindexes[i].getRow()][temporaryindexes[i].getColumn()]);
                    }
                }
            }
        }else {
            if(pawn.getHowmanytimesitmoved()==0){
                for(int i=1;i<=2;i++){
                    Index temporaryindex=new Index(indexofpiece.getRow()+i,indexofpiece.getColumn());
                    Piece piece=isthereaenemypiece(temporaryindex,enemypieces);
                    if (isthereafriendpiece(temporaryindex, pieces)) {
                        break;
                    }else if(piece!=null){
                        break;
                    }else{
                        pawn.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                    }
                }
            }else{
                Index temporaryindex=new Index(indexofpiece.getRow()+1,indexofpiece.getColumn());
                Piece piece=isthereaenemypiece(temporaryindex,enemypieces);
                if(!isthereafriendpiece(temporaryindex,pieces)&&piece==null){
                    pawn.getMovableplaces().add(boardbuttons[temporaryindex.getRow()][temporaryindex.getColumn()]);
                }
            }
            Index[] temporaryindexes={new Index(indexofpiece.getRow()+1,indexofpiece.getColumn()+1),new Index(indexofpiece.getRow()+1,indexofpiece.getColumn()-1)};
            for(int i=0;i<temporaryindexes.length;i++){
                if(isitintheBoard(temporaryindexes[i])&&!isthereafriendpiece(temporaryindexes[i],pieces)){
                    Piece piece=isthereaenemypiece(temporaryindexes[i],enemypieces);
                    if(piece!=null&&!(piece instanceof King)){
                        pawn.getMovableplaces().add(boardbuttons[temporaryindexes[i].getRow()][temporaryindexes[i].getColumn()]);
                    }
                }
            }
        }
    }

    private int findsmallerone(Index indexforcompare){
        return Math.min(indexforcompare.getRow(), indexforcompare.getColumn());
    }
}
