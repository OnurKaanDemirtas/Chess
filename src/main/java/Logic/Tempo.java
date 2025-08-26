package Logic;

public class Tempo {
    private int remainingtime;
    private final int timetoaddedforeachmove;

    public int getRemainingtime() {
        return remainingtime;
    }

    public int getTimetoaddedforeachmove() {
        return timetoaddedforeachmove;
    }

    public void decreaseRemainingtime(int elapsedtime) {
        this.remainingtime -= elapsedtime;
    }

    public void increaseRemainingtime(){
        this.remainingtime+=timetoaddedforeachmove;
    }

    public Tempo(int remainingtime, int timetoaddedforeachmove){
        this.remainingtime=remainingtime;
        this.timetoaddedforeachmove=timetoaddedforeachmove;
    }
}
