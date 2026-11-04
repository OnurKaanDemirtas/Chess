package Logic;

/** A clock: remaining seconds and the increment added after every move (3+2 = 180 s + 2 s). */
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
