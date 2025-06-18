// File: clinic7/storage/AppointmentFileHandler.java
package com.clinic7.storage;

import com.clinic7.model.Appointment;
import com.clinic7.data.LinkedList;
import com.clinic7.util.IDGenerator;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class AppointmentFileHandler {
    public static void saveAllAppointments(LinkedList<Appointment> appointmentList, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            appointmentList.forEach(appointment -> {
                try {
                    writer.write(appointment.toCSV());
                    writer.newLine();
                } catch (IOException e) {
                    System.err.println("Error writing appointment to file: " + appointment.getAppointmentID() + " - " + e.getMessage());
                }
            });
            System.out.println("Appointments saved successfully to: " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save appointments: " + e.getMessage());
        }
    }

    public static LinkedList<Appointment> loadAllAppointments(String filename){
        LinkedList<Appointment> loaded = new LinkedList<>();
        Path path = Paths.get(filename);
        int maxIdCounter = 0;

        if (!Files.exists(path)) {
            System.out.println("File not found: " + filename + ". Returning empty list.");
            return loaded;
        }

        try (Scanner scanner = new Scanner(new FileReader(filename))) {
            int lineNumber = 0;
            String currentDatePrefix = "APT-" + IDGenerator.getCurrentDateString() + "-";

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;
                if (!line.isEmpty()) {
                    try {
                        Appointment a = Appointment.fromCSV(line);
                        loaded.addLast(a);

                        if (a.getAppointmentID().startsWith(currentDatePrefix) && a.getAppointmentID().length() == currentDatePrefix.length() + 4) {
                            try {
                                int currentIdCounter = Integer.parseInt(a.getAppointmentID().substring(currentDatePrefix.length()));
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
            System.out.println("Appointments loaded successfully from: " + filename);
            IDGenerator.setAppointmentCounter(maxIdCounter);
            System.out.println("IDGenerator Appointment Counter set to: " + maxIdCounter);

        } catch (Exception e) {
            System.err.println("Failed to load appointments from " + filename + ": " + e.getMessage());
        }
        return loaded;
    }

    public static void appendAppointment(Appointment appointment, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(appointment.toCSV());
            writer.newLine();
            System.out.println("Appended appointment: " + appointment.getAppointmentID());
        } catch (IOException e) {
            System.err.println("Failed to append appointment: " + e.getMessage());
        }
    }
}