package com.clinic7.util;

import java.util.Comparator;

import com.clinic7.model.Patient;

public class PATComparator implements Comparator<Patient> {
    @Override
    public int compare(Patient p1, Patient p2) {
        return p1.getId().compareTo(p2.getId()); 
    }
}