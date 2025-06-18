// File: clinic7/storage/PatientFileHandler.java
package com.clinic7.storage;

import com.clinic7.data.LinkedList;
import com.clinic7.model.Patient;
import com.clinic7.util.IDGenerator;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class PatientFileHandler {
    public static void saveAllPatients(LinkedList<Patient> patientList, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            patientList.forEach(patient -> {
                try {
                    writer.write(patient.toCSV());
                    writer.newLine();
                } catch (IOException e) {
                    System.err.println("Error writing patient to file: " + patient.getId() + " - " + e.getMessage());
                }
            });
            System.out.println("Patients saved successfully to: " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save patients: " + e.getMessage());
        }
    }

    public static LinkedList<Patient> loadAllPatients(String filename){
        LinkedList<Patient> loaded = new LinkedList<>();
        Path path = Paths.get(filename);
        int maxIdCounter = 0;

        if (!Files.exists(path)) {
            System.out.println("File not found: " + filename + ". Returning empty list.");
            return loaded;
        }

        try (Scanner scanner = new Scanner(new FileReader(filename))) {
            int lineNumber = 0;
            String currentDatePrefix = "PAT-" + IDGenerator.getCurrentDateString() + "-";

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;
                if (!line.isEmpty()) {
                    try {
                        Patient p = Patient.fromCSV(line);
                        loaded.addLast(p);

                        if (p.getId().startsWith(currentDatePrefix) && p.getId().length() == currentDatePrefix.length() + 4) {
                            try {
                                int currentIdCounter = Integer.parseInt(p.getId().substring(currentDatePrefix.length()));
                                if (currentIdCounter > maxIdCounter) {
                                    maxIdCounter = currentIdCounter;
                                }
                            } catch (NumberFormatException nfe) {
                                // Abaikan
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Skipped corrupted line " + lineNumber + " in " + filename + ": " + line + " - " + e.getMessage());
                    }
                }
            }
            System.out.println("Patients loaded successfully from: " + filename);
            IDGenerator.setPatientCounter(maxIdCounter);
            System.out.println("IDGenerator Patient Counter set to: " + maxIdCounter);

        } catch (Exception e) {
            System.err.println("Failed to load patients from " + filename + ": " + e.getMessage());
        }
        return loaded;
    }

    public static void appendPatient(Patient patient, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(patient.toCSV());
            writer.newLine();
            System.out.println("Appended patient: " + patient.getId());
        } catch (IOException e) {
            System.err.println("Failed to append patient: " + e.getMessage());
        }
    }
}