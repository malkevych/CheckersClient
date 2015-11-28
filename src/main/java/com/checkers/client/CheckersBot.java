package com.checkers.client;

import com.checkers.domain.vo.Field;
import com.checkers.domain.vo.Step;

/**
 * Created by Eugene on 08.11.2015.
 */
public interface CheckersBot {
    abstract Step calculateNextStep(Field currentField);
}
