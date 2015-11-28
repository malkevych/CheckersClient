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

    public Field() {
        allChecks= new HashSet<Check>();
    }

    public Set<Check> getAllChecks() {
        return allChecks;
    }

    public void setAllChecks(Set<Check> allChecks) {
        this.allChecks = allChecks;
    }
}