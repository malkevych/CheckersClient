package com.checkers.client;

import com.checkers.domain.vo.Check;
import com.checkers.domain.vo.Field;
import com.checkers.domain.vo.Position;

import java.util.ArrayList;

/**
 * Created by Bohdan on 28.11.15.
 */
public class Event {

    public Check startCheck;
    public ArrayList<Position> way;
    public Field field;
    public boolean isBeating;
    public double weight;

    public Event(Check startCheck, ArrayList<Position> way, Field field, boolean isBeating) {
        this.startCheck = startCheck;
        this.way = way;
        this.field = field;
        this.isBeating = isBeating;
    }




}
