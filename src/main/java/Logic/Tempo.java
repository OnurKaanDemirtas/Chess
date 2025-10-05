package Logic;

public class Tempo {
    private int remainingtime;
    private int timetoaddedforeachmove;

    public int getRemainingtime() {
        return remainingtime;
    }

    public void setRemainingtime(int remainingtime) {
        this.remainingtime = remainingtime;
    }

    public int getTimetoaddedforeachmove() {
        return timetoaddedforeachmove;
    }

    public void setTimetoaddedforeachmove(int timetoaddedforeachmove) {
        this.timetoaddedforeachmove = timetoaddedforeachmove;
    }

    public Tempo(int remainingtime, int timetoaddedforeachmove){
        this.remainingtime=remainingtime;
        this.timetoaddedforeachmove=timetoaddedforeachmove;
    }

    public Tempo(){}
}
