// File: clinic7/storage/DoctorFileHandler.java
package com.clinic7.storage;

import com.clinic7.model.Doctor;
import com.clinic7.data.LinkedList;
import com.clinic7.util.IDGenerator;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class DoctorFileHandler {
    public static void saveAllDoctors(LinkedList<Doctor> doctorlList, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            doctorlList.forEach(doctor -> {
                try {
                    writer.write(doctor.toCSV());
                    writer.newLine();
                } catch (IOException e) {
                    System.err.println("Error writing Doctor to file: " + doctor.getId() + " - " + e.getMessage());
                }
            });
            System.out.println("Doctors saved successfully to: " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save doctor: " + e.getMessage());
        }
    }

    public static LinkedList<Doctor> loadAllDoctors(String filename){
        LinkedList<Doctor> loaded = new LinkedList<>();
        Path path = Paths.get(filename);
        int maxIdCounter = 0;

        if (!Files.exists(path)) {
            System.out.println("File not found: " + filename + ". Returning empty list.");
            return loaded;
        }

        try (Scanner scanner = new Scanner(new FileReader(filename))) {
            int lineNumber = 0;
            String currentDatePrefix = "DOC-" + IDGenerator.getCurrentDateString() + "-";

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;
                if (!line.isEmpty()) {
                    try {
                        Doctor d = Doctor.fromCSV(line);
                        loaded.addLast(d);

                        if (d.getId().startsWith(currentDatePrefix) && d.getId().length() == currentDatePrefix.length() + 4) {
                            try {
                                int currentIdCounter = Integer.parseInt(d.getId().substring(currentDatePrefix.length()));
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
            System.out.println("Doctors loaded successfully from: " + filename);
            IDGenerator.setDoctorCounter(maxIdCounter);
            System.out.println("IDGenerator Doctor Counter set to: " + maxIdCounter);

        } catch (Exception e) {
            System.err.println("Failed to load doctors from " + filename + ": " + e.getMessage());
        }
        return loaded;
    }

    public static void appendDoctor(Doctor doctor, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(doctor.toCSV());
            writer.newLine();
            System.out.println("Appended doctor: " + doctor.getId());
        } catch (IOException e) {
            System.err.println("Failed to append doctor: " + e.getMessage());
        }
    }
}