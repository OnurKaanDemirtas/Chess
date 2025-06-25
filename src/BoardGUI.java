import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class BoardGUI extends JFrame{
    private JPanel board;
    private JPanel moves;
    private JPanel info;
    private ArrayList<JButton> buttonlist;

    public JPanel getBoard() {
        return board;
    }

    public JPanel getInfo() {
        return info;
    }

    public JPanel getMoves() {
        return moves;
    }

    public BoardGUI(){
        this.board=initiateBoardPanel();
        addButtonstoBoard();
        this.moves=initiateMovesPanel();
        this.info=initiateInfoPanel();
        setBounds(200,200,1000,800);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(board);
        add(moves);
        add(info);
        setVisible(true);
    }
    private JPanel initiateBoardPanel(){
        JPanel board=new JPanel();
        board.setLayout(new GridLayout(8,8));
        board.setBounds(180,60,640,640);
        board.setBackground(Color.GREEN);
        return board;
    }
    private JPanel initiateInfoPanel(){
        JPanel info=new JPanel();
        info.setBounds(0,0,180,1000);
        info.setBackground(Color.RED);
        return info;
    }
    private JPanel initiateMovesPanel(){
        JPanel moves=new JPanel();
        moves.setBounds(820,0,180,1000);
        moves.setBackground(Color.BLUE);
        return moves;
    }
    private void addButtonstoBoard(){
        this.buttonlist=new ArrayList<>();
        for(int row=0;row<8;row++){
            for(int column=0;column<8;column++){
                JButton button=new JButton();
                button.setBounds(column*80,row*80,80,80);
                if((row+column)%2==0){
                    button.setBackground(Color.WHITE);
                }else{
                    button.setBackground(Color.BLACK);
                }
                if(row==1&&column==0){
                    Piece blackhorse=new Horse(button,Color.BLACK);
                    button.setText("Horse");
                    //button.addActionListener(new BoardButtonHandler(new chessmodel(blackhorse,)));
                }
                button.setEnabled(row == 0 || row == 1 || row == 6 || row == 7);
                board.add(button);
                buttonlist.add(button);
            }
        }
    }
}
