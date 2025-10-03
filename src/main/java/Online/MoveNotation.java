package Online;

import Logic.Index;

public class MoveNotation {
    private Index from;
    private Index to;

    public Index getTo() {
        return to;
    }

    public void setTo(Index to) {
        this.to = to;
    }

    public Index getFrom() {
        return from;
    }

    public void setFrom(Index from) {
        this.from = from;
    }

    public MoveNotation(Index from,Index to){
        this.from=from;
        this.to=to;
    }
}
