package Online;

/** The DatabaseSize/size document: a counter used to give each new account a unique id. */
public class DatabaseSize {
    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public DatabaseSize(int size) {
        this.size =size;
    }
    public DatabaseSize() {
    }
}
