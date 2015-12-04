package com.checkers.client;

import com.checkers.domain.vo.Check;
import com.checkers.domain.vo.Field;
import com.checkers.domain.vo.Position;
import com.checkers.domain.vo.Step;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

/**
 * Created by Bohdan on 12.11.15.
 */
public class Cheek implements CheckersBot{

    final static int minPosition = 0;
    final static int maxPosition = 7;
    final static int whiteColor = 0;
    final static int blackColor = 1;


    private enum CellStatus {
        Empty,
        Enemy,
        MyCheck
    }

    @Override
    public Step calculateNextStep(Field currentField) {

        ArrayList<Event> getAllSteps = getAllSteps(currentField, whiteColor);

//        int largestSize = 0;
//        Event largestWay = null;
//        for (Event event : getAllSteps) {
//            if (event.way.size() > largestSize) {
//                largestSize = event.way.size();
//                largestWay = event;
//            }
//        }
//

//        System.out.println("Check--------" + check.getPosition().getX() + " ++++++ " + check.getPosition().getY());
//        System.out.println("Step---------" + finalStep.getPositionAfterMove().get(0).getX() + " --- " +  finalStep.getPositionAfterMove().get(0).getY());




        for (int i = 0; i < getAllSteps.size(); i++ ) {
            Event event = getAllSteps.get(i);
            double weight = getBestStepFromMySteps(event, 0, blackColor);
            event.weight = weight;
        }

        getAllSteps.sort(new Comparator<Event>(){
            @Override
            public int compare(Event s1, Event s2)
            {
                if (s1.weight > s2.weight) {return 1;
                } else if (s1.weight > s2.weight) {
                    return -1;
                }

                return 0;
            }
        });

        Event largestWay = getAllSteps.get(0);
        Check check = largestWay.startCheck;
        Step finalStep = new Step(check, largestWay.way);

        return finalStep;
    }



    private double getBestStepFromMySteps(Event event,  int deep, int color) {
        ArrayList<Event> getAllSteps = getAllSteps(event.field, color);
        if (deep == 1) {
            return 0;
        }
        for (int i = 0; i < getAllSteps.size(); i++ ) {
            Event eventIndex = getAllSteps.get(i);
            return getBestStepFromMySteps(eventIndex, deep++, getOpositeColor(color)) + eventIndex.field.evaluate(); // + evaluate
        }
        return 0;
    }


    private int getOpositeColor(int currentColor) {
        return (currentColor == whiteColor)? blackColor : whiteColor;
    }




















    private ArrayList<Event> getAllSteps(Field currentField, int myColor) {
        ArrayList<Event> allWays = new ArrayList<>();
        ArrayList<Check> onlyMyChecks = getOnlyMyChecksForField(currentField, myColor);
        for (Check myCheck : onlyMyChecks) {
            getAllFullWaysForCheck(CopyUtils.copyCheck(myCheck), new ArrayList<>(), currentField, myCheck, allWays, false, myColor);
        }
        return allWays;
    }



    private void getAllFullWaysForCheck(
            Check myCheck, ArrayList<Position> currentWay, Field field, Check startCheck,
            ArrayList<Event> allWays, boolean isBeating, int myColor) {

        ArrayList<Position> possibleSituations =  getPossibleSquareStepsForCheek(myCheck, myCheck.isQueen(), field, false);

        for (Position position : possibleSituations) {
            if (getCellStatus(position, field, myColor) == CellStatus.Empty) {
                if(currentWay.size() > 0) continue;
                ArrayList<Position> fullPath = new ArrayList<>(currentWay);
                fullPath.add(position);
                Field newField = getNewFieldAfterStep(field, myCheck, position);
                allWays.add(new Event(startCheck, fullPath, newField, isBeating));
            } else if (getCellStatus(position, field, myColor) == CellStatus.Enemy) {
                Check enemyCheck = getCheckAtPosition(position, field);
                if (!isCanBeatCheck(enemyCheck, myCheck, field)) {
                    allWays.add(new Event(startCheck, new ArrayList<>(currentWay),field, isBeating));
                } else {
                    ArrayList<Position> possibleEnemySituations =  getPossibleSquareStepsForCheek(enemyCheck, true, field, false);
                    Position newPosition = getPositionOpositeToCheck(possibleEnemySituations, myCheck);
                    Field newField = getNewFieldWithRemovedCheck(field, enemyCheck, myCheck);
                    Check newCheck = getCheckAtPosition(newPosition, newField);
                    currentWay.add(newPosition);
                    ArrayList<Position> fullPath = new ArrayList<>(currentWay);
                    allWays.add(0, new Event(startCheck, fullPath, newField, isBeating));
                    getAllFullWaysForCheck(newCheck, new ArrayList<>(currentWay), newField, startCheck, allWays, true, myColor);
                }
            }
        }
    }

    private ArrayList<Check> getOnlyMyChecksForField(Field field, int myColor) {
        Set<Check> allChecks = field.getAllChecks();
        ArrayList<Check> myChecks = new ArrayList<>();
        for (Check check : allChecks) {
            if (check.getColor() == myColor) {
                myChecks.add(check);
            }
        }
        return myChecks;
    }

    private Field getNewFieldWithRemovedCheck(Field field, Check checkEnemy, Check myCheck) {
        ArrayList<Position> possibleEnemySituations =  getPossibleSquareStepsForCheek(checkEnemy, true, field, false);
        Position newPosition = getPositionOpositeToCheck(possibleEnemySituations, myCheck);
        //
        Field newField = CopyUtils.copyField(field);
        Check myCheckFromNewField = getCheckAtPosition(myCheck.getPosition(), newField);
        myCheckFromNewField.setPosition(newPosition);
        myCheckFromNewField.setQueen(myCheck.isQueen() || isQuinePosition(newPosition));

        Check enemyCheckFromNewField = getCheckAtPosition(checkEnemy.getPosition(), newField);
        newField.getAllChecks().remove(enemyCheckFromNewField);
        return newField;
    }

    private Field getNewFieldAfterStep(Field field, Check myCheck, Position newPosition) {
        Field newField = CopyUtils.copyField(field);
        Check myCheckFromNewField = getCheckAtPosition(myCheck.getPosition(), newField);
        myCheckFromNewField.setPosition(newPosition);
        return newField;
    }

    private boolean isQuinePosition(Position position) {
        return  (position.getY() == maxPosition);
    }

    // віддає всі гіпотетичні ходи для однієї шашки (крок лише на один хід)
    private ArrayList<Position> getPossibleSquareStepsForCheek(Check check, boolean isQeen, boolean isCanForceGoBack,  Field field, boolean forBeating) {
        Position checkP = check.getPosition();
        ArrayList<Position> positions = new ArrayList<>();
        Position posLT = new Position(checkP.getX()-1, checkP.getY()+1);
        Position posRT = new Position(checkP.getX()+1, checkP.getY()+1);
        Position posLD = new Position(checkP.getX()-1, checkP.getY()-1);
        Position posRD = new Position(checkP.getX()+1, checkP.getY()-1);

        positions.add(posLT);
        positions.add(posRT);
        positions.add(posLD);
        positions.add(posRD);

        if (check.getPosition().getX() == minPosition) {
            positions.remove(posLT);
            positions.remove(posLD);
        }
        if (check.getPosition().getX() == maxPosition) {
            positions.remove(posRT);
            positions.remove(posRD);
        }
        if (check.getPosition().getY() == minPosition || !isQeen && check.getColor() == whiteColor) {
            positions.remove(posLD);
            positions.remove(posRD);
        }
        if (check.getPosition().getY() == maxPosition || !isQeen && check.getColor() == blackColor) {
            positions.remove(posLT);
            positions.remove(posRT);
        }

        ArrayList<Position> positionsRes = new ArrayList<>();
        if (forBeating) {
            for (Position position:positions) {
                if (getCheckAtPosition(position, field) == null) {
                    positionsRes.add(position);
                 }
             }
            return positionsRes;
        } else {
            return positions;
        }
    }


    private CellStatus getCellStatus(Position anPosition, Field field, int myColor) {
        Set<Check> allChecks = field.getAllChecks();
        for (Check check : allChecks) {
            Position checkPos = check.getPosition();
            if (checkPos.getX() == anPosition.getX() && checkPos.getY() == anPosition.getY()) {
                if (check.getColor() == myColor) {
                    return CellStatus.MyCheck;
                } else {
                    return CellStatus.Enemy;
                }
            }
        }
        return CellStatus.Empty;
    }

    private Check getCheckAtPosition(Position anPosition, Field field) {
        Set<Check> allChecks = field.getAllChecks();
        for (Check check : allChecks) {
            Position checkPos = check.getPosition();
            if (checkPos.getX() == anPosition.getX() && checkPos.getY() == anPosition.getY()) {
                return check;
            }
        }
        return null;
    }

    private boolean isCanBeatCheck(Check enemyCheck, Check myCheck, Field field) {
        ArrayList<Position> possibleStepsForEnemy = getPossibleSquareStepsForCheek(enemyCheck, true, field, true);
        return isHavePositionsOpositeWayForCheck(possibleStepsForEnemy, myCheck);
    }

    private boolean isHavePositionsOpositeWayForCheck(ArrayList<Position> positions, Check check) {
        for (Position pos : positions) {
            Position chekPos = check.getPosition();
            if (chekPos.getX() + 2 == pos.getX() && chekPos.getY() + 2 == pos.getY()) {
                return true;
            }
            if (chekPos.getX() + 2 == pos.getX() && chekPos.getY() - 2 == pos.getY()) {
                return true;
            }
            if (chekPos.getX() - 2 == pos.getX() && chekPos.getY() + 2 == pos.getY()) {
                return true;
            }
            if (chekPos.getX() - 2 == pos.getX() && chekPos.getY() - 2 == pos.getY()) {
                return true;
            }
        }
        return false;
    }

    private Position getPositionOpositeToCheck(ArrayList<Position> positions, Check check) {
        for (Position pos : positions) {
            Position chekPos = check.getPosition();
            if (chekPos.getX() + 2 == pos.getX() && chekPos.getY() + 2 == pos.getY()) {
                return pos;
            }
            if (chekPos.getX() + 2 == pos.getX() && chekPos.getY() - 2 == pos.getY()) {
                return pos;
            }
            if (chekPos.getX() - 2 == pos.getX() && chekPos.getY() + 2 == pos.getY()) {
                return pos;
            }
            if (chekPos.getX() - 2 == pos.getX() && chekPos.getY() - 2 == pos.getY()) {
                return pos;
            }
        }
        return null;
    }
}


