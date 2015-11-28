package com.checkers.domain.vo;

/**
 * Created by Isaiev on 24.09.2015.
 */
public class Position {

    private int y;
    private int x;

    public Position() {
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;

        Position position = (Position) o;

        if (x != position.x) return false;
        if (y != position.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = y;
        result = 31 * result + x;
        return result;
    }
}
