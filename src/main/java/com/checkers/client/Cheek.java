package com.checkers.client;

import com.checkers.domain.vo.Check;
import com.checkers.domain.vo.Field;
import com.checkers.domain.vo.Position;
import com.checkers.domain.vo.Step;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Bohdan on 12.11.15.
 */
public class Cheek implements CheckersBot{

    final static int minPosition = 0;
    final static int maxPosition = 7;
    final static int whiteColor = 0;

    ArrayList<Event> allWays = new ArrayList<>();


    private enum CellStatus {
        Empty,
        Enemy,
        MyCheck
    }

    @Override
    public Step calculateNextStep(Field currentField) {
        allWays = new ArrayList<>();

        ArrayList<Check> onlyMyChecks = getOnlyMyChecksForField(currentField);

        for (Check check : currentField.getAllChecks()) {
            if (check.getPosition().getX() == 3 && check.getPosition().getY() == 4) {
                System.out.println("----------");
            }
        }

        for (Check myCheck : onlyMyChecks) {
            if (myCheck.getPosition().getX() == 2 && myCheck.getPosition().getY() == 3) {
                System.out.println("----------2");
            }
            getAllFullWaysForCheck(myCheck, new ArrayList<>(), currentField, myCheck);
        }

        int largestSize = 0;
        Event largestWay = null;
        for (Event event : allWays) {
            if (event.way.size() > largestSize) {
                largestSize = event.way.size();
                largestWay = event;
            }
        }

        Check check = largestWay.startCheck;
        Step finalStep = new Step(check, largestWay.way);

        System.out.println("Check--------" + check.getPosition().getX() + " ++++++ " + check.getPosition().getY());
        System.out.println("Step---------" + finalStep.getPositionAfterMove().get(0).getX() + " --- " +  finalStep.getPositionAfterMove().get(0).getY());

        return finalStep;
    }

    private void getAllFullWaysForCheck(
            Check myCheck, ArrayList<Position> currentWay, Field field, Check startCheck) {
        ArrayList<Position> possibleSituations =  getPossibleSquareStepsForCheek(myCheck, myCheck.isQueen(), field, false);

        for (Position position : possibleSituations) {
            if (getCellStatus(position, field) == CellStatus.Empty) {
                if(currentWay.size() > 0) continue;
                ArrayList<Position> fullPath = new ArrayList<>(currentWay);
                fullPath.add(position);
                Field newField = getNewFieldAfterStep(field, myCheck, position);
                allWays.add(new Event(startCheck, fullPath, newField));
            } else if (getCellStatus(position, field) == CellStatus.Enemy) {
                Check enemyCheck = getCheckAtPosition(position, field);
                if (!isCanBeatCheck(enemyCheck, myCheck, field)) {
                    allWays.add(new Event(startCheck, new ArrayList<>(currentWay),field));
                } else {
                    ArrayList<Position> possibleEnemySituations =  getPossibleSquareStepsForCheek(enemyCheck, true, field, false);
                    Position newPosition = getPositionOpositeToCheck(possibleEnemySituations, myCheck);
                    Field newField = getNewFieldWithRemovedCheck(field, enemyCheck, myCheck);
                    myCheck.setPosition(newPosition);
                    myCheck.setQueen(myCheck.isQueen() || isQuinePosition(newPosition));
//                    currentWay.add(newPosition);
                    ArrayList<Position> fullPath = new ArrayList<>(currentWay);
                    allWays.add(new Event(startCheck, fullPath,newField));
                    getAllFullWaysForCheck(myCheck, new ArrayList<>(currentWay), newField, startCheck);
                }
            }
        }
    }

    private ArrayList<Check> getOnlyMyChecksForField(Field field) {
        Set<Check> allChecks = field.getAllChecks();
        ArrayList<Check> myChecks = new ArrayList<>();
        for (Check check : allChecks) {
            if (check.getColor() == whiteColor) {
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
    private ArrayList<Position> getPossibleSquareStepsForCheek(Check check, boolean isCanGoBack, Field field, boolean forBeating) {
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
        if (check.getPosition().getY() == minPosition || !isCanGoBack) {
            positions.remove(posLD);
            positions.remove(posRD);
        }
        if (check.getPosition().getY() == maxPosition) {
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


    private CellStatus getCellStatus(Position anPosition, Field field) {
        Set<Check> allChecks = field.getAllChecks();
        for (Check check : allChecks) {
            Position checkPos = check.getPosition();
            if (checkPos.getX() == anPosition.getX() && checkPos.getY() == anPosition.getY()) {
                if (check.getColor() == whiteColor) {
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


