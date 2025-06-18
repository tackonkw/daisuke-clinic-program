// File: clinic7/core/PatManBST.java
package com.clinic7.core;

import java.time.LocalDate;

import com.clinic7.data.BinarySearchTree;
import com.clinic7.data.LinkedList;
import com.clinic7.model.Patient;
import com.clinic7.util.IDGenerator;

public class PatManBST {
    private BinarySearchTree<Patient> patientTree;

    public PatManBST() {
        this.patientTree = new BinarySearchTree<>((p1, p2) -> p1.getId().compareTo(p2.getId()));
    }

    public void addExistingPatient(Patient patient) {
        patientTree.insert(patient);
    }

    public void registerPatient(String name, int age, String gender, String address,
                                String phoneNumber, String bloodType, String medicalHistory) {
        String newID = IDGenerator.generatePatientID();
        Patient patient = new Patient(newID, name, age, gender, address, phoneNumber, bloodType, medicalHistory, LocalDate.now());
        patientTree.insert(patient);
    }

    public Patient findPatientById(String id) {
        Patient dummy = new Patient(id, null, 0, null, null, null, null, null, null);
        return patientTree.findData(dummy);
    }

    public boolean deletePatient(String id) {
        Patient dummy = new Patient(id, null, 0, null, null, null, null, null, null);
        if (patientTree.contains(dummy)) {
            patientTree.delete(dummy);
            return true;
        }
        return false;
    }

    public boolean updatePatient(String id, String name, String ageStr, String gender,
                                 String phoneNum, String address, String bloodType, String history){
        Patient dummy = new Patient(id, null, 0, null, null, null, null, null, null);
        Patient p = patientTree.findData(dummy);
        if (p == null) return false;

        if (name != null && !name.isEmpty()) p.setName(name);
        if (ageStr != null && !ageStr.isEmpty()) {
            try {
                p.setAge(Integer.parseInt(ageStr));
            } catch (NumberFormatException e) {
                System.out.println("Invalid age input.");
            }
        }
        if (gender != null && !gender.isEmpty()) p.setGender(gender);
        if (phoneNum != null && !phoneNum.isEmpty()) p.setPhoneNumber(phoneNum);
        if (address != null && !address.isEmpty()) p.setAddress(address);
        if (bloodType != null && !bloodType.isEmpty()) p.setBloodType(bloodType);
        if (history != null && !history.isEmpty()) {
            p.addMedicalRecord(history);
        }

        return true;
    }

    public boolean patientExists(String id) {
        Patient dummy = new Patient(id, null, 0, null, null, null, null, null, null);
        return patientTree.contains(dummy);
    }

    public void clearAllPatients() {
        patientTree.clear();
    }

    public LinkedList<Patient> getPatientsInOrder() {
        LinkedList<Patient> list = new LinkedList<>();
        patientTree.inOrderTraversal(list::addLast);
        return list;
    }

    public boolean isEmpty() {
        return patientTree.isEmpty();
    }

    public int size() {
        return patientTree.size();
    }
}
