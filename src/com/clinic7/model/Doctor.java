// File: clinic7/model/Doctor.java
package com.clinic7.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Doctor {
    private String id;
    private String name;
    private String specialty;
    private LocalDateTime loginTime;

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Doctor(String id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.loginTime = null;
    }

    public String getId(){ return id;}
    public void setId(String id){ this.id = id;}

    public String getName(){ return name;}
    public void setName(String name){ this.name = name;}

    public String getSpecialty(){ return specialty;}
    public void setSpecialty(String specialty){ this.specialty = specialty;}

    public LocalDateTime getLoginTime(){ return loginTime;}
    public void setLoginTime(LocalDateTime loginTime){this.loginTime = loginTime;}

    public boolean isPresent(){
        return loginTime != null && loginTime.toLocalDate().equals(LocalDate.now());
    }

    public String toCSV(){
        return String.join(",",
                id, name, specialty
        );
    }

    public static Doctor fromCSV(String line) {
        String[] parts = line.split (",", -1);
        if (parts.length < 3 ) throw new IllegalArgumentException("Invalid CSV format for Doctor: " + line);
        return new Doctor(
                parts[0],
                parts[1],
                parts[2]
        );
    }

    @Override
    public String toString(){
        String timeStr = (loginTime != null)
                ? loginTime.format(DATETIME_FORMAT)
                : "N/A";

        return "Doctor{"+
                "ID='"+ id + '\''+
                ", Nama='" + name + '\''+
                ", Spesialisasi='" + specialty + '\''+
                ", LoginTime='" + timeStr + '\'' +
                '}';
    }
}