package Logic;

public class Tempo {
    private int remainingTime;
    private int timeToAddedForEachMove;

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getTimeToAddedForEachMove() {
        return timeToAddedForEachMove;
    }

    public void setTimeToAddedForEachMove(int timeToAddedForEachMove) {
        this.timeToAddedForEachMove = timeToAddedForEachMove;
    }

    public Tempo(int remainingTime, int timeToAddedForEachMove){
        this.remainingTime = remainingTime;
        this.timeToAddedForEachMove = timeToAddedForEachMove;
    }

    public Tempo(){}
}
