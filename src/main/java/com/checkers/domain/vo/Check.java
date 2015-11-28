package com.checkers.domain.vo;


/**
 * Created by Isaiev on 24.09.2015.
 */
public class Check {

    private Position position;
    private int color;
    private boolean queen;

    public Check() {
    }

    public Check(Position position, int color) {
        this.position = position;
        this.color = color;
        this.queen = false;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setQueen(boolean queen) {
        this.queen = queen;
    }

    public boolean isQueen() {
        return queen;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Check)) return false;

        Check check = (Check) o;

        if (color != check.color) return false;
        if (queen != check.queen) return false;
        if (position != null ? !position.equals(check.position) : check.position != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = position != null ? position.hashCode() : 0;
        result = 31 * result + color;
        result = 31 * result + (queen ? 1 : 0);
        return result;
    }
}
