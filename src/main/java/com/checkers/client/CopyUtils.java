package com.checkers.client;


import com.checkers.domain.vo.Check;
import com.checkers.domain.vo.Field;
import com.checkers.domain.vo.Position;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Bohdan on 28.11.15.
 */
public class CopyUtils {

    public static Field copyField(Field field) {
        Field newField = new Field();
        newField.setAllChecks(copySetOfChecks(field.getAllChecks()));
        return newField;
    }

    public static Set<Check> copySetOfChecks(Set<Check> checks) {
        Set<Check> newChecks  = new HashSet<>();
        for (Check check:checks) {
            newChecks.add(copyCheck(check));
        }
        return newChecks;
    }

    public static Check copyCheck(Check check) {
        Check newCheck = new Check(copyPosition(check.getPosition()), check.getColor());
        newCheck.setQueen(check.isQueen());
        return newCheck;
    }

    public static Position copyPosition(Position position) {
        return new Position(position.getX(), position.getY());
    }
}
