package Online;

import Logic.Index;

public class MoveNotation {
    private Index from;
    private Index to;
    private String promotionType;

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

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

    public MoveNotation(Index from,Index to,String promotionType){
        this.from=from;
        this.to=to;
        this.promotionType=promotionType;
    }

    public MoveNotation(){};
}
