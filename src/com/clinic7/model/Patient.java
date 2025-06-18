package com.clinic7.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.clinic7.data.LinkedList;

public class Patient {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String address;
    private String phoneNumber;
    private String bloodType;
    private LinkedList<String> medicalHistory;
    private LocalDate registrationDate;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String HISTORY_DELIMITER = "||";

    public Patient(String id, String name, int age, String gender, String address, String phoneNumber, String bloodType, String medicalHistoryString, LocalDate registrationDate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.bloodType = bloodType;
        this.registrationDate = registrationDate;
        this.medicalHistory = new LinkedList<>();
        if (medicalHistoryString != null && !medicalHistoryString.trim().isEmpty()) {
            String[] histories = medicalHistoryString.split("\\|\\|");
            for (String history : histories) {
                if (!history.trim().isEmpty()) {
                    this.medicalHistory.addLast(history.trim());
                }
            }
        }
        if (this.medicalHistory.isEmpty()) {
            this.medicalHistory.addLast("Tidak ada riwayat medis awal.");
        }
    }
    

    public Patient(String id, String name, int age, String gender, String address, String phoneNumber, String bloodType) {
        this(id, name, age, gender, address, phoneNumber, bloodType, "", LocalDate.now());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public LinkedList<String> getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(LinkedList<String> medicalHistory) { this.medicalHistory = medicalHistory; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public void addMedicalRecord(String record) {
        if (this.medicalHistory.size() == 1 && "Tidak ada riwayat medis awal.".equals(this.medicalHistory.get(0))) {
            this.medicalHistory.clear();
        }
        this.medicalHistory.addLast(record);
    }
    
    public String toCSV() {
        LinkedList<String> historyItems = new LinkedList<>();
        if (this.medicalHistory.size() > 0 && !("Tidak ada riwayat medis awal.".equals(this.medicalHistory.get(0)) && this.medicalHistory.size() == 1)) {
            for (String s : medicalHistory) {
                historyItems.addLast(s.replace(",", ";"));
            }
        }
        return String.join(",",
            id, name, String.valueOf(age), gender, address, phoneNumber,
            String.join(HISTORY_DELIMITER, historyItems),
            bloodType, registrationDate.format(DATE_FORMAT)
        );
    }

    public static Patient fromCSV(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 9) throw new IllegalArgumentException("Format CSV tidak valid: " + line);
        return new Patient(
            parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4], parts[5],
            parts[7], parts[6], LocalDate.parse(parts[8], DATE_FORMAT)
        );
    }
    
    @Override
    public String toString() {
        StringBuilder historyDisplay = new StringBuilder();
        historyDisplay.append("[");
        if (this.medicalHistory != null && !this.medicalHistory.isEmpty()) {
            boolean first = true;
            for (String s : medicalHistory) {
                if (!first) {
                    historyDisplay.append(", ");
                }
                historyDisplay.append("'").append(s).append("'");
                first = false;
            }
        }
        historyDisplay.append("]");
        return "Patient{" +
                "ID='" + id + '\'' +
                ", Nama='" + name + '\'' +
                ", Usia=" + age +
                ", Jenis Kelamin='" + gender + '\'' +
                ", Kontak='" + phoneNumber + '\'' +
                ", Alamat='" + address + '\'' +
                ", Gol. Darah='" + bloodType + '\'' +
                ", Riwayat Medis=" + historyDisplay.toString() +
                ", Tanggal Registrasi=" + registrationDate.format(DATE_FORMAT) +
                '}';
    }
}
