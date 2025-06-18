package com.clinic7.core;

import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.function.Consumer; // Tambahkan import ini untuk viewUpcomingAppointments

import com.clinic7.data.LinkedList;
import com.clinic7.model.Appointment;
import com.clinic7.model.Doctor;
import com.clinic7.model.Patient;
import com.clinic7.storage.AppointmentFileHandler;

public class AppointmentQueue{
    private LinkedList<Appointment> appointments;
    private static final Comparator<Appointment> APPOINTMENT_TIME_COMPARATOR =
            (a1, a2) -> a1.getTime().compareTo(a2.getTime());

    private static final String APPOINTMENT_FILE = "resources/appointments.csv";
    // private static AppointmentFileHandler appointmentFileHandler = new AppointmentFileHandler();

    private PatManLL patientManager;
    private DoctorManagement doctorManager;
    private Scanner scanner;

    public AppointmentQueue() {
        appointments = new LinkedList<>();
        this.scanner = new Scanner(System.in);
    }

    public void setManagers(PatManLL patientManager, DoctorManagement doctorManager) {
        this.patientManager = patientManager;
        this.doctorManager = doctorManager;
        loadFromFile();
    }


    public void scheduleAppointment(Appointment appointment) {
        if (appointment.getTime().isBefore(LocalDateTime.now())) {
            System.out.println("Error: Waktu janji temu tidak boleh di masa lalu.");
            return;
        }

        if (patientManager == null || doctorManager == null) {
            System.out.println("Error: Manager pasien/dokter belum diinisialisasi. Tidak dapat menjadwalkan janji temu.");
            return;
        }

        Patient patient = patientManager.findPatientById(appointment.getPatientID());
        Doctor doctor = null;
        try { // Perbaikan: Ambil dokter dari DoctorManagement
            doctor = doctorManager.getAllDoctors().findData(d -> d.getId().equals(appointment.getDoctorID()));
        } catch (NoSuchElementException e) {
            // doctor akan tetap null
        }

        if (patient == null) {
            System.out.println("Error: Pasien dengan ID " + appointment.getPatientID() + " tidak ditemukan.");
            return;
        }
        if (doctor == null) {
            System.out.println("Error: Dokter dengan ID " + appointment.getDoctorID() + " tidak ditemukan.");
            return;
        }

        if (!isDoctorAvailable(doctor.getId(), appointment.getTime())) {
            System.out.println("Error: Dokter " + doctor.getName() + " tidak tersedia pada waktu " + appointment.getFormattedTime() + ".");
            return;
        }

        if (!isPatientAvailable(patient.getId(), appointment.getTime())) {
            System.out.println("Error: Pasien " + patient.getName() + " sudah memiliki janji temu pada waktu " + appointment.getFormattedTime() + ".");
            return;
        }

        appointments.addSorted(appointment, APPOINTMENT_TIME_COMPARATOR);
        AppointmentFileHandler.appendAppointment(appointment, APPOINTMENT_FILE);
        System.out.println("Janji temu " + appointment.getAppointmentID() + " berhasil dijadwalkan.");
    }

    private boolean isDoctorAvailable(String doctorId, LocalDateTime requestedTime) {
        for (Appointment existingAppt : appointments) {
            if (existingAppt.getDoctorID().equals(doctorId)) {
                if (existingAppt.getTime().equals(requestedTime)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isPatientAvailable(String patientId, LocalDateTime requestedTime) {
        for (Appointment existingAppt : appointments) {
            if (existingAppt.getPatientID().equals(patientId)) {
                if (existingAppt.getTime().equals(requestedTime)) {
                    return false;
                }
            }
        }
        return true;
    }

    public LinkedList<Doctor> getAvailableDoctorsForSpecialty(String specialty) {
        if (doctorManager == null) {
            System.out.println("Error: Doctor manager belum diinisialisasi.");
            return new LinkedList<>();
        }
        LinkedList<Doctor> availableDoctors = new LinkedList<>();
        doctorManager.getDoctorsBySpecialty(specialty).forEach(doctor -> {
            if (doctorManager.isDoctorLoggedIn(doctor.getName())) {
                availableDoctors.addLast(doctor);
            }
        });
        return availableDoctors;
    }


    public Appointment processNextAppointment () {
        if(appointments.isEmpty()) {
            System.out.println("Tidak ada janji temu untuk diproses.");
            return null;
        }

        Appointment processed = appointments.removeFirst();

        System.out.println("\n--- Memproses Janji Temu ---");
        System.out.println("Pasien ID: " + processed.getPatientID());
        System.out.println("Dokter ID: " + processed.getDoctorID());
        System.out.println("Waktu: " + processed.getFormattedTime());

        System.out.print("Masukkan Keluhan Pasien: ");
        String complaint = scanner.nextLine();
        System.out.print("Masukkan Hasil Diagnosis Akhir: ");
        String diagnosis = scanner.nextLine();

        if (patientManager != null) {
            Patient patient = patientManager.findPatientById(processed.getPatientID());
            if (patient != null) {
                patient.addMedicalRecord(diagnosis);
                patientManager.saveToFile();
                System.out.println("Riwayat medis pasien " + patient.getName() + " berhasil diperbarui.");
            } else {
                System.out.println("Peringatan: Pasien " + processed.getPatientID() + " tidak ditemukan. Riwayat medis tidak dapat diperbarui.");
            }
        }

        saveToFile();
        System.out.println("Janji temu " + processed.getAppointmentID() + " selesai diproses.");
        return processed;
    }

    public boolean isEmpty() {
        return appointments.isEmpty();
    }
    public int size() {
        return appointments.size();
    }

    // Perbaikan: Sekarang menerima Consumer untuk format tampilan dari Main.java
    public void viewUpcomingAppointments(Consumer<Appointment> displayAction) {
        if(appointments.isEmpty()) {
            System.out.println("Tidak ada janji temu mendatang.");
            return;
        }
        System.out.println("Janji Temu Mendatang (Terurut Waktu):");
        System.out.println("======================================================");
        appointments.forEach(displayAction); // Memanggil displayAction dari Main.java
        System.out.println("======================================================");
    }

    public void clear() {
        appointments.clear();
    }

    public void loadFromFile() {
        LinkedList<Appointment> loadedAppointments = AppointmentFileHandler.loadAllAppointments(APPOINTMENT_FILE);
        this.appointments.clear();
        loadedAppointments.forEach(appt -> this.appointments.addSorted(appt, APPOINTMENT_TIME_COMPARATOR));
    }

    public void saveToFile() {
        AppointmentFileHandler.saveAllAppointments(appointments, APPOINTMENT_FILE);
    }
}