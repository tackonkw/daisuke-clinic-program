package com.clinic7.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.Properties;

public class IDGenerator {
    private static final String STATE_FILE = "resources/id_state.properties";
    private static final Properties props = new Properties();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    static{
        loadState();
    }
    
    private static String lastDate = props.getProperty("lastDate", "");
    private static int patientCounter = Integer.parseInt(props.getProperty("patientCounter", "0"));
    private static int doctorCounter = Integer.parseInt(props.getProperty("doctorCounter", "0"));
    private static int appointmentCounter = Integer.parseInt(props.getProperty("appointmentCounter", "0"));

    public static String getCurrentDateString() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    private static void loadState(){
        try (InputStream input = new FileInputStream(STATE_FILE)){
            props.load(input);
        }
        catch (IOException e) {
            System.err.println("No existing state file. Starting fresh.");
        }
    }

    public static void saveState(){
        try (OutputStream output = new FileOutputStream(STATE_FILE)){
            props.store(output, "ID Generator State");
        }
        catch (IOException e) {
            System.err.println("Failed to save state: " + e.getMessage());
        }
    }

    public static synchronized void setPatientCounter(int counter) {
        patientCounter = counter;
    }

    public static synchronized void setDoctorCounter(int counter) {
        doctorCounter = counter;
    }

    public static synchronized void setAppointmentCounter(int counter) {
        appointmentCounter = counter;
    }

    public static synchronized String generatePatientID() {
        String currentDate = getCurrentDateString();
        if (!currentDate.equals(lastDate)) {
            patientCounter = 0;
            props.setProperty("lastDate", currentDate);
        }
        props.setProperty("patientCounter", String.valueOf(++patientCounter));
        saveState();

        return generateID("PAT", patientCounter);
    }

    public static synchronized String generateDoctorID() {
        String currentDate = getCurrentDateString();
        if (!currentDate.equals(lastDate)) {
            doctorCounter = 0;
            props.setProperty("lastDate", currentDate);
        }
        props.setProperty("doctorCounter", String.valueOf(++doctorCounter));
        saveState();

        return generateID("DOC", doctorCounter);
    }

    public static synchronized String generateAppointmentID() {
        String currentDate = getCurrentDateString();
        if (!currentDate.equals(lastDate)) {
            appointmentCounter = 0;
            props.setProperty("lastDate", currentDate);
        }
        props.setProperty("appointmentCounter", String.valueOf(++appointmentCounter));
        saveState();

        return generateID("APT", appointmentCounter);
    }

    private static String generateID(String prefix, int count) {
        String currentDate = getCurrentDateString();
        return String.format("%s-%s-%04d", prefix, currentDate, count);
    }
}
