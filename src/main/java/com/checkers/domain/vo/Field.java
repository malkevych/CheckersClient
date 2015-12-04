package com.checkers.domain.vo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Isaiev on 24.09.2015.
 */
public class Field implements Serializable {
    
    public static final long serialVersionUID = 42L;

    private Set<Check> allChecks;

    private double value;

    public double getValue() {
        return value;
    }

    public Field() {
        allChecks= new HashSet<Check>();
        value = evaluate();
    }

    public Set<Check> getAllChecks() {
        return allChecks;
    }

    public void setAllChecks(Set<Check> allChecks) {
        this.allChecks = allChecks;
    }


    @Override
    public String toString() {
        String[][] myIntArray = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                myIntArray[i][j] = "-";
            }
        }
        for (Check check:getAllChecks()) {
            if (check.getColor() == 0) {
                myIntArray[check.getPosition().getX()][check.getPosition().getY()] = "O";
            } else {
                myIntArray[check.getPosition().getX()][check.getPosition().getY()] = "X";
            }
        }

        String str = "\n";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                str +=  myIntArray[j][7-i];
            }
            str += "\n";
        }
        str += "\n";

        return str;
    }

    public double evaluate() {
        double rez = 0;
        int white = 7;
        int king_white = 11;
        int black = 8;
        int king_black = 12;
        Set<Check> allChecks = this.getAllChecks();
        for (Check check : allChecks) {
            if (check.getColor() == 0) {//white
                rez += white;
                if (check.isQueen())
                    rez += king_white - white;
            } else {
                rez -= black;
                if (check.isQueen())
                    rez -= king_black + black;
            }
        }
        return rez;
    }
}