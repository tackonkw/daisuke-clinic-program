// File: clinic7/model/Appointment.java
package  com.clinic7.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private String appointmentID;
    private String patientID;
    private String doctorID;
    private LocalDateTime time;

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Appointment (String appointmentID, String patientID, String doctorID, LocalDateTime time ) {
        this.appointmentID = appointmentID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.time = time;
    }

    public String getAppointmentID() {
        return appointmentID;
    }
    public String getPatientID() {
        return patientID;
    }
    public String getDoctorID() {
        return doctorID;
    }
    public LocalDateTime getTime() {
        return time;
    }
    public String getFormattedTime() {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String toCSV(){
        return String.join(",",
                appointmentID,
                patientID,
                doctorID,
                time.format(DATETIME_FORMAT)
        );
    }

    public static Appointment fromCSV(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 4) throw new IllegalArgumentException("Invalid CSV format for Appointment: " + line);
        return new Appointment(
                parts[0],
                parts[1],
                parts[2],
                LocalDateTime.parse(parts[3], DATETIME_FORMAT)
        );
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentID='" + appointmentID + '\'' +
                ", patientID='" + patientID + '\'' +
                ", doctorID='" + doctorID + '\'' +
                ", time=" + getFormattedTime() +
                '}';
    }
}