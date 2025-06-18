// File: Main.java
package com.clinic7.app;

// Impor semua kelas dari paket 'com.clinic7.core'
import com.clinic7.core.PatManLL;
import com.clinic7.core.PatManBST;
import com.clinic7.core.DoctorManagement;
import com.clinic7.core.AppointmentQueue;
import com.clinic7.model.Doctor;
import com.clinic7.model.Patient;
import com.clinic7.model.Appointment;

// Impor kelas utilities
import com.clinic7.util.IDGenerator;

// Impor kelas data
import com.clinic7.data.LinkedList;

// Impor dari Java Standard Library
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.time.LocalDate;

public class Main {

    // ANSI Escape Codes
    public static final String RESET = "\033[0m";
    public static final String BOLD = "\033[1m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";

    private static final PatManLL patientManagerLL = new PatManLL();
    private static final PatManBST patientManagerBST = new PatManBST();
    private static final DoctorManagement doctorManager = new DoctorManagement();
    private static final AppointmentQueue appointmentQueue = new AppointmentQueue();
    private static final Scanner scanner = new Scanner(System.in);

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        displayWelcomeScreen();

        System.out.println(CYAN + "=======================================" + RESET);
        System.out.println(BOLD + "  Memuat Data Sistem Klinik..." + RESET);
        System.out.println(CYAN + "=======================================" + RESET);

        patientManagerLL.loadFromFile();
        doctorManager.loadFromFile();
        appointmentQueue.setManagers(patientManagerLL, doctorManager);

        System.out.println(BOLD + "\n[INFO]" + RESET + " Migrasi data pasien dari LinkedList ke Binary Search Tree...");
        patientManagerLL.getAllPatients().forEach(patient -> {
            patientManagerBST.addExistingPatient(patient);
        });
        System.out.println(GREEN + "[INFO]" + RESET + " Migrasi pasien ke BST selesai. Total pasien di BST: " + patientManagerBST.size());

        System.out.println(CYAN + "=======================================" + RESET);
        System.out.println(GREEN + "  Data Berhasil Dimuat. Sistem Siap." + RESET);
        System.out.println(CYAN + "=======================================" + RESET);

        int choice;
        do {
            displayMainMenu();
            System.out.print(YELLOW + "Pilih opsi Anda: " + RESET);
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
                handleMenuChoice(choice);
            } catch (InputMismatchException e) {
                System.out.println(RED + "\n[ERROR]" + RESET + " Input tidak valid. Harap masukkan angka.");
                scanner.nextLine();
                choice = -1;
            } catch (Exception e) {
                System.err.println(RED + "\n[FATAL ERROR]" + RESET + " Terjadi kesalahan tak terduga: " + e.getMessage());
                choice = -1;
            }
            pressEnterToContinue();
        } while (choice != 0);

        System.out.println(CYAN + "\n=======================================" + RESET);
        System.out.println(BOLD + "  Menyimpan Data Sebelum Keluar..." + RESET);
        System.out.println(CYAN + "=======================================" + RESET);
        patientManagerLL.saveToFile();
        doctorManager.saveToFile();
        appointmentQueue.saveToFile();
        System.out.println(GREEN + "[INFO]" + RESET + " Data berhasil disimpan.");
        System.out.println(BOLD + "  Terima Kasih. Sampai Jumpa!" + RESET);
        System.out.println(CYAN + "=======================================" + RESET);
        scanner.close();
    }

    private static void displayWelcomeScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println(GREEN + "==========================================================" + RESET);
        System.out.println(GREEN + "||" + RESET + BOLD + "           SELAMAT DATANG DI DAISUKE CLINIC           " + RESET + GREEN + "||" + RESET);
        System.out.println(GREEN + "==========================================================" + RESET);
        System.out.println(" Tanggal: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + RESET);
        System.out.println(" Waktu:   " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + RESET);
        System.out.println(GREEN + "==========================================================" + RESET);
        pressEnterToContinue();
    }

    private static void displayMainMenu() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(CYAN + "====================================" + RESET);
        System.out.println(BOLD + "      MENU UTAMA DAISUKE CLINIC     " + RESET);
        System.out.println(CYAN + "====================================" + RESET);
        System.out.println(BOLD + "  MANAJEMEN PASIEN (LINKED LIST)" + RESET);
        System.out.println("  1. Tambah Pasien Baru" + RESET);
        System.out.println("  2. Hapus Pasien berdasarkan ID" + RESET);
        System.out.println("  3. Cari Pasien berdasarkan Nama" + RESET);
        System.out.println("  4. Tampilkan Semua Pasien (Linked List)" + RESET);
        System.out.println(BOLD + "\n  MANAJEMEN DOKTER" + RESET);
        System.out.println("  5. Registrasi Dokter Baru" + RESET);
        System.out.println("  6. Login Dokter" + RESET);
        System.out.println("  7. Logout Dokter" + RESET);
        System.out.println("  8. Tampilkan Dokter yang Sedang Login" + RESET);
        System.out.println(BOLD + "\n  MANAJEMEN JANJI TEMU (QUEUE)" + RESET);
        System.out.println("  9. Jadwalkan Janji Temu" + RESET);
        System.out.println("  10. Proses Janji Temu Berikutnya" + RESET);
        System.out.println("  11. Tampilkan Janji Temu Mendatang" + RESET);
        System.out.println(BOLD + "\n  PENCARIAN PASIEN (BINARY SEARCH TREE)" + RESET);
        System.out.println("  12. Cari Pasien berdasarkan ID (BST)" + RESET);
        System.out.println("  13. Tampilkan Semua Pasien (BST Inorder)" + RESET);
        System.out.println(RED + "\n  0. KELUAR APLIKASI" + RESET);
        System.out.println(CYAN + "====================================" + RESET);
    }

    private static void handleMenuChoice(int choice) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        switch (choice) {
            case 1:
                addNewPatient();
                break;
            case 2:
                removePatientById();
                break;
            case 3:
                searchPatientByName();
                break;
            case 4:
                displayAllPatientsLL();
                break;
            case 5:
                registerNewDoctor();
                break;
            case 6:
                doctorLogin();
                break;
            case 7:
                doctorLogout();
                break;
            case 8:
                displayLoggedInDoctors();
                break;
            case 9:
                scheduleAppointment();
                break;
            case 10:
                processAppointment();
                break;
            case 11:
                displayUpcomingAppointments();
                break;
            case 12:
                searchPatientByIdBST();
                break;
            case 13:
                displayAllPatientsBST();
                break;
            case 0:
                break;
            default:
                System.out.println(RED + "\n[ERROR]" + RESET + " Pilihan tidak valid. Silakan coba lagi.");
        }
    }

    private static void addNewPatient() {
        System.out.println(CYAN + "--- TAMBAH PASIEN BARU ---" + RESET);
        System.out.print("Nama Lengkap: " + RESET);
        String name = scanner.nextLine();
        System.out.print("Usia: " + RESET);
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine());
            if (age <= 0 || age > 120) {
                System.out.println(RED + "[ERROR]" + RESET + " Usia tidak valid. Pembatalan penambahan pasien.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "[ERROR]" + RESET + " Usia tidak valid. Harap masukkan angka. Pembatalan penambahan pasien.");
            return;
        }
        System.out.print("Jenis Kelamin (Pria/Wanita): " + RESET);
        String gender = scanner.nextLine();
        System.out.print("Alamat: " + RESET);
        String address = scanner.nextLine();
        System.out.print("Nomor Telepon: " + RESET);
        String phoneNumber = scanner.nextLine();
        System.out.print("Golongan Darah: " + RESET);
        String bloodType = scanner.nextLine();
        System.out.print("Riwayat Medis Awal (opsional, pisahkan dengan titik koma jika lebih dari satu, e.g., Flu;Demam): " + RESET);
        String medicalHistoryInitial = scanner.nextLine();

        patientManagerLL.registerPat(name, age, gender, address, phoneNumber, bloodType, medicalHistoryInitial);

        Patient newPatient = null;
        try {
            newPatient = patientManagerLL.findPatientById(p ->
                    p.getName().equalsIgnoreCase(name) &&
                            p.getAge() == age &&
                            p.getGender().equalsIgnoreCase(gender) &&
                            p.getPhoneNumber().equals(phoneNumber)
            );
        } catch (NoSuchElementException e) {
            // Pasien tidak ditemukan, newPatient akan tetap null
        }

        if (newPatient != null) {
            patientManagerBST.addExistingPatient(newPatient);
            System.out.println(GREEN + "\n[SUKSES]" + RESET + " Pasien '" + name + "' berhasil ditambahkan dengan ID: " + newPatient.getId());
            System.out.println(GREEN + "[INFO]" + RESET + " Pasien juga telah disinkronkan ke Binary Search Tree.");
        } else {
            System.out.println(RED + "\n[PERINGATAN]" + RESET + " Pasien berhasil ditambahkan ke Linked List, tetapi gagal disinkronkan ke BST (mungkin karena kriteria pencarian tidak unik atau masalah ID).");
        }

        patientManagerLL.saveToFile();
    }

    private static void registerNewDoctor() {
        System.out.println(CYAN + "--- REGISTRASI DOKTER BARU ---" + RESET);
        System.out.print("Nama Dokter: " + RESET);
        String name = scanner.nextLine();
        System.out.print("Spesialisasi (e.g., Umum, Gigi, Mata): " + RESET);
        String specialty = scanner.nextLine();

        doctorManager.registerDoctor(name, specialty);
        doctorManager.saveToFile();
        System.out.println(GREEN + "\n[INFO]" + RESET + " Proses registrasi dokter selesai.");
    }

    private static void removePatientById() {
        System.out.println(CYAN + "--- HAPUS PASIEN BERDASARKAN ID ---" + RESET);
        System.out.print("Masukkan ID Pasien yang akan dihapus: " + RESET);
        String patientId = scanner.nextLine();

        boolean existsLL = patientManagerLL.patientExists(patientId);
        boolean existsBST = patientManagerBST.patientExists(patientId);

        if (!existsLL && !existsBST) {
            System.out.println(RED + "\n[INFO]" + RESET + " Pasien dengan ID '" + patientId + "' tidak ditemukan di sistem.");
            return;
        }

        boolean deletedLL = patientManagerLL.deletePatient(patientId);
        boolean deletedBST = patientManagerBST.deletePatient(patientId);

        if (deletedLL && deletedBST) {
            System.out.println(GREEN + "\n[SUKSES]" + RESET + " Pasien dengan ID '" + patientId + "' berhasil dihapus dari Linked List dan BST.");
            patientManagerLL.saveToFile();
        } else if (deletedLL) {
            System.out.println(YELLOW + "\n[PERINGATAN]" + RESET + " Pasien dengan ID '" + patientId + "' berhasil dihapus dari Linked List, tetapi tidak ditemukan di BST.");
            patientManagerLL.saveToFile();
        } else if (deletedBST) {
            System.out.println(YELLOW + "\n[PERINGATAN]" + RESET + " Pasien dengan ID '" + patientId + "' berhasil dihapus dari BST, tetapi tidak ditemukan di Linked List.");
        }
    }

    private static void searchPatientByName() {
        System.out.println(CYAN + "--- CARI PASIEN BERDASARKAN NAMA ---" + RESET);
        System.out.print("Masukkan Nama Pasien yang akan dicari: " + RESET);
        String name = scanner.nextLine();

        Patient foundPatient = null;
        try {
            foundPatient = patientManagerLL.findPatientById(p -> p.getName().equalsIgnoreCase(name));
        } catch (NoSuchElementException e) {
            // Biarkan foundPatient tetap null
        }

        if (foundPatient != null) {
            System.out.println(GREEN + "\n[SUKSES]" + RESET + " Pasien ditemukan (dari Linked List):");
            displayPatientInfo(foundPatient);
        } else {
            System.out.println(RED + "\n[INFO]" + RESET + " Pasien dengan nama '" + name + "' tidak ditemukan.");
        }
    }

    private static void displayAllPatientsLL() {
        System.out.println(CYAN + "--- DAFTAR SEMUA PASIEN (LINKED LIST) ---" + RESET);
        if (patientManagerLL.isEmpty()) {
            System.out.println(YELLOW + "Tidak ada pasien terdaftar di Linked List." + RESET);
        } else {
            System.out.println(BOLD + "Total Pasien: " + patientManagerLL.getTotalPatients() + RESET);
            System.out.println(CYAN + "======================================================" + RESET);
            patientManagerLL.getAllPatients().forEach(Main::displayPatientInfo);
            System.out.println(CYAN + "======================================================" + RESET);
        }
    }

    private static void displayPatientInfo(Patient patient) {
        System.out.println(GREEN + "  ID: " + RESET + patient.getId());
        System.out.println("  Nama: " + patient.getName());
        System.out.println("  Usia: " + patient.getAge());
        System.out.println("  Jenis Kelamin: " + patient.getGender());
        System.out.println("  Alamat: " + patient.getAddress());
        System.out.println("  No. Telepon: " + patient.getPhoneNumber());
        System.out.println("  Gol. Darah: " + patient.getBloodType());
        System.out.print("  Riwayat Medis: ");
        if (patient.getMedicalHistory().isEmpty()) {
            System.out.println("[Tidak ada riwayat]");
        } else {
            StringBuilder historyBuilder = new StringBuilder();
            patient.getMedicalHistory().forEach(record -> historyBuilder.append("'").append(record).append("', "));
            if (historyBuilder.length() > 0) {
                historyBuilder.setLength(historyBuilder.length() - 2);
            }
            System.out.println("[" + historyBuilder.toString() + "]");
        }
        System.out.println("  Tgl. Registrasi: " + patient.getRegistrationDate());
        System.out.println(CYAN + "------------------------------------------------------" + RESET);
    }

    private static void doctorLogin() {
        System.out.println(CYAN + "--- LOGIN DOKTER ---" + RESET);
        System.out.print("Masukkan Nama Dokter: " + RESET);
        String doctorName = scanner.nextLine();
        doctorManager.loginDoctor(doctorName);
    }

    private static void doctorLogout() {
        System.out.println(CYAN + "--- LOGOUT DOKTER ---" + RESET);
        System.out.print("Masukkan Nama Dokter yang akan logout: " + RESET);
        String doctorName = scanner.nextLine();
        doctorManager.logoutDoctor(doctorName);
    }

    private static void displayLoggedInDoctors() {
        System.out.println(CYAN + "--- DOKTER YANG SEDANG LOGIN ---" + RESET);
        doctorManager.getAllLoggedInDoctors();
    }

    private static void scheduleAppointment() {
        System.out.println(CYAN + "--- JADWALKAN JANJI TEMU ---" + RESET);
        System.out.print("Masukkan ID Pasien: " + RESET);
        String patientId = scanner.nextLine();
        Patient patient = patientManagerLL.findPatientById(patientId);
        if (patient == null) {
            System.out.println(RED + "[ERROR]" + RESET + " Pasien dengan ID '" + patientId + "' tidak ditemukan.");
            return;
        }

        System.out.print("Masukkan Keluhan Utama Pasien (e.g., Sakit Kepala, Demam, Mata Merah): " + RESET);
        String complaint = scanner.nextLine();

        String requiredSpecialty = getSpecialtyFromComplaint(complaint);
        System.out.println(BOLD + "\n[INFO]" + RESET + " Berdasarkan keluhan, diperlukan dokter spesialis: " + (requiredSpecialty.isEmpty() ? "Umum" : requiredSpecialty) + RESET);

        LinkedList<Doctor> suitableDoctors = doctorManager.getDoctorsBySpecialty(requiredSpecialty);
        if (suitableDoctors.isEmpty()) {
            System.out.println(RED + "[ERROR]" + RESET + " Tidak ada dokter spesialis " + (requiredSpecialty.isEmpty() ? "Umum" : requiredSpecialty) + " yang terdaftar.");
            return;
        }

        LinkedList<Doctor> loggedInSuitableDoctors = new LinkedList<>();
        System.out.println(BOLD + "\nDokter Spesialis " + (requiredSpecialty.isEmpty() ? "Umum" : requiredSpecialty) + " yang Tersedia (Login):" + RESET);
        boolean foundLoggedIn = false;
        for (Doctor doc : suitableDoctors) {
            if (doctorManager.isDoctorLoggedIn(doc.getName())) {
                System.out.println("  ID: " + doc.getId() + ", Nama: " + doc.getName() + ", Spesialisasi: " + doc.getSpecialty());
                loggedInSuitableDoctors.addLast(doc);
                foundLoggedIn = true;
            }
        }
        if (!foundLoggedIn) {
            System.out.println(YELLOW + "Tidak ada dokter spesialis " + (requiredSpecialty.isEmpty() ? "Umum" : requiredSpecialty) + " yang sedang login." + RESET);
            return;
        }

        System.out.print(WHITE + "\nMasukkan ID Dokter yang dipilih dari daftar di atas: " + RESET);
        String doctorId = scanner.nextLine();
        Doctor doctor = null;
        try {
            doctor = loggedInSuitableDoctors.findData(d -> d.getId().equals(doctorId));
        } catch (NoSuchElementException e) {
            // doctor akan tetap null
        }

        if (doctor == null) {
            System.out.println(RED + "[ERROR]" + RESET + " Dokter dengan ID '" + doctorId + "' tidak valid atau tidak sedang login sebagai spesialis " + (requiredSpecialty.isEmpty() ? "Umum" : requiredSpecialty) + ".");
            return;
        }

        System.out.print("Masukkan waktu janji temu (yyyy-MM-dd HH:mm): " + RESET);
        String timeString = scanner.nextLine();
        LocalDateTime appointmentTime;
        try {
            appointmentTime = LocalDateTime.parse(timeString, DATETIME_FORMATTER);
            if (appointmentTime.isBefore(LocalDateTime.now())) {
                System.out.println(RED + "[ERROR]" + RESET + " Waktu janji temu tidak boleh di masa lalu.");
                return;
            }
        } catch (Exception e) {
            System.out.println(RED + "[ERROR]" + RESET + " Format waktu tidak valid. Gunakan MMMM-dd HH:mm. Pembatalan janji temu.");
            return;
        }

        String appointmentID = IDGenerator.generateAppointmentID();
        Appointment newAppointment = new Appointment(appointmentID, patientId, doctor.getId(), appointmentTime);

        appointmentQueue.scheduleAppointment(newAppointment);
    }

    private static String getSpecialtyFromComplaint(String complaint) {
        String lowerComplaint = complaint.toLowerCase();
        if (lowerComplaint.contains("mata") || lowerComplaint.contains("penglihatan")) {
            return "Mata";
        } else if (lowerComplaint.contains("gigi") || lowerComplaint.contains("mulut")) {
            return "Gigi";
        } else if (lowerComplaint.contains("jantung") || lowerComplaint.contains("dada")) {
            return "Jantung";
        } else if (lowerComplaint.contains("saraf") || lowerComplaint.contains("otak") || lowerComplaint.contains("kepala") || lowerComplaint.contains("migrain")) {
            return "Saraf";
        } else if (lowerComplaint.contains("tulang") || lowerComplaint.contains("sendi") || lowerComplaint.contains("patah")) {
            return "Ortopedi";
        } else if (lowerComplaint.contains("kulit") || lowerComplaint.contains("jerawat") || lowerComplaint.contains("ruam")) {
            return "Kulit";
        } else if (lowerComplaint.contains("anak") || lowerComplaint.contains("balita") || lowerComplaint.contains("bayi")) {
            return "Anak";
        }
        return "Umum";
    }

    private static void processAppointment() {
        System.out.println(CYAN + "--- PROSES JANJI TEMU BERIKUTNYA ---" + RESET);
        Appointment processed = appointmentQueue.processNextAppointment();
        if (processed != null) {
            System.out.println(GREEN + "\n[SUKSES]" + RESET + " Janji temu " + processed.getAppointmentID() + " selesai diproses.");
        } else {
            System.out.println(YELLOW + "Tidak ada janji temu yang perlu diproses." + RESET);
        }
    }

    private static void displayUpcomingAppointments() {
        System.out.println(CYAN + "--- DAFTAR JANJI TEMU MENDATANG ---" + RESET);
        if (appointmentQueue.isEmpty()) {
            System.out.println(YELLOW + "Tidak ada janji temu mendatang." + RESET);
        } else {
            System.out.println(BOLD + "Total Janji Temu: " + appointmentQueue.size() + RESET);
            System.out.println(CYAN + "======================================================" + RESET);
            appointmentQueue.viewUpcomingAppointments(Main::displayAppointmentInfo);
            System.out.println(CYAN + "======================================================" + RESET);
        }
    }

    private static void displayAppointmentInfo(Appointment appointment) {
        System.out.println(GREEN + "  ID Janji Temu: " + RESET + appointment.getAppointmentID());
        System.out.println("  ID Pasien: " + appointment.getPatientID());
        System.out.println("  ID Dokter: " + appointment.getDoctorID());
        System.out.println("  Waktu: " + appointment.getFormattedTime());
        System.out.println(CYAN + "------------------------------------------------------" + RESET);
    }

    private static void searchPatientByIdBST() {
        System.out.println(CYAN + "--- CARI PASIEN BERDASARKAN ID (BST) ---" + RESET);
        System.out.print("Masukkan ID Pasien yang akan dicari: " + RESET);
        String patientId = scanner.nextLine();

        Patient foundPatient = patientManagerBST.findPatientById(patientId);
        if (foundPatient != null) {
            System.out.println(GREEN + "\n[SUKSES]" + RESET + " Pasien ditemukan (dari BST):");
            displayPatientInfo(foundPatient);
        } else {
            System.out.println(RED + "\n[INFO]" + RESET + " Pasien dengan ID '" + patientId + "' tidak ditemukan di BST.");
        }
    }

    private static void displayAllPatientsBST() {
        System.out.println(CYAN + "--- DAFTAR SEMUA PASIEN (BST INORDER) ---" + RESET);
        LinkedList<Patient> allPatients = patientManagerBST.getPatientsInOrder();
        if (allPatients.isEmpty()) {
            System.out.println(YELLOW + "Tidak ada pasien terdaftar di BST." + RESET);
        } else {
            System.out.println(BOLD + "Total Pasien: " + patientManagerBST.size() + RESET);
            System.out.println(CYAN + "======================================================" + RESET);
            allPatients.forEach(Main::displayPatientInfo);
            System.out.println(CYAN + "======================================================" + RESET);
        }
    }

    private static void pressEnterToContinue() {
        System.out.println(CYAN + "\nTekan ENTER untuk melanjutkan..." + RESET);
        scanner.nextLine();
    }
}
