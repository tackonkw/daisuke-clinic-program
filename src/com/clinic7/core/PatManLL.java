// File: clinic7/core/PatManLL.java
package com.clinic7.core;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.clinic7.storage.PatientFileHandler;
import com.clinic7.data.LinkedList; // Pastikan ini diimpor
import com.clinic7.model.Patient;
import com.clinic7.util.IDGenerator;

public class PatManLL {
    private LinkedList<Patient> patientList;
    String path = "resources/patients.csv";

    public PatManLL() {
        this.patientList = new LinkedList<>();
        loadFromFile();
    }

    public void registerPat(String name, int age, String gender, String address, String phoneNumber, String bloodType, String MedicalHistory){
        String newID = IDGenerator.generatePatientID();
        // PERBAIKAN: MedicalHistory sekarang String tunggal di constructor Patient
        Patient newPatient = new Patient(newID, name, age, gender, address, phoneNumber, bloodType, MedicalHistory, LocalDate.now());
        patientList.addLast(newPatient);
        PatientFileHandler.appendPatient(newPatient, path);
    }

    public void addPatient(Patient patient) {
        patientList.addLast(patient);
    }

    public Patient findPatientById(String patientId) {
        try {
            return patientList.findData(p -> p.getId().equals(patientId));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public Patient findPatientById(Predicate<Patient> predicate) {
        try {
            return patientList.findData(predicate);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public boolean deletePatient(String patientId) {
        return patientList.remove(p -> p.getId().equals(patientId)) != null;
    }

    public boolean updatePatient(String id, String name, String ageStr, String gender, String phoneNum, String address, String bloodType, String history){
        Patient p = findPatientById(id);
        if (p == null) {
            System.out.println("Patient with ID " + id + " not found. Cannot update.");
            return false;
        }

        if (name != null && !name.isEmpty()) p.setName(name);
        if (ageStr != null && !ageStr.isEmpty()) {
            try {
                int age = Integer.parseInt(ageStr);
                p.setAge(age);
            } catch (NumberFormatException e) {
                System.out.println("Invalid age input. Skipping age update.");
            }
        }
        if (gender != null && !gender.isEmpty()) p.setGender(gender);
        if (phoneNum != null && !phoneNum.isEmpty()) p.setPhoneNumber(phoneNum);
        if (address != null && !address.isEmpty()) p.setAddress(address);
        if (bloodType != null && !bloodType.isEmpty()) p.setBloodType(bloodType);
        // PERBAIKAN: Jika riwayat medis diupdate, ini akan mengganti seluruh LinkedList
        if (history != null && !history.isEmpty()) {
            // Ini akan membuat LinkedList baru, bukan menambah
            // Jika ingin menambah, Patient.addMedicalRecord() harus dipanggil
            LinkedList<String> newHistoryList = new LinkedList<>();
            // Asumsi history di sini adalah string tunggal, jadi masukkan sebagai 1 record baru
            newHistoryList.addLast(history);
            p.setMedicalHistory(newHistoryList);
        }
        return true;
    }

    public boolean patientExists(String patientId) {
        return patientList.contains(p -> p.getId().equals(patientId));
    }

    public int getTotalPatients() {
        return patientList.size();
    }

    public boolean isEmpty() {
        return patientList.isEmpty();
    }

    public void clearAllPatients() {
        patientList.clear();
    }

    public LinkedList<Patient> getAllPatients() {
        return patientList;
    }

    public Patient get(int index) {
        return patientList.get(index);
    }

    public void loadFromFile() {
        this.patientList = PatientFileHandler.loadAllPatients(path);
    }

    public void saveToFile() {
        PatientFileHandler.saveAllPatients(patientList, path);
    }
}