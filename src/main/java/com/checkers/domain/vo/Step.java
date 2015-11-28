package com.checkers.domain.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Isaiev on 24.09.2015.
 */
public class Step implements Serializable{

    public static final long serialVersionUID = 43L;

    private Check check;
    private ArrayList<Position> positionAfterMove;
    private long usedTime;

    public Step(Check check, ArrayList<Position> positionAfteMove) {
        this.check = check;
        this.positionAfterMove = positionAfteMove;
    }
    
    public Step(){};

    public Check getCheck() {
        return check;
    }

    public void setPositionAfterMove(ArrayList<Position> positionAfterMove) {
        this.positionAfterMove = positionAfterMove;
    }

    public void setCheck(Check check) {
        this.check = check;
    }

    public ArrayList<Position> getPositionAfterMove() {
        return positionAfterMove;
    }

    public void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }

    public long getUsedTime() {
        return usedTime;
    }

    @Override
    public String toString() {
        return "Step{" +
                "check=" + check +
                ", positionAfterMove=" + positionAfterMove +
                '}';
    }
}
