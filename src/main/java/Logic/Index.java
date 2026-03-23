package Logic;


public class Index {
    private int row;
    private int column;

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Index(int row, int column){
        this.row=row;
        this.column=column;
    }
    public Index(){}
}
