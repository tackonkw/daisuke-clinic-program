// File: clinic7/core/DoctorManagement.java
package com.clinic7.core;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.clinic7.util.IDGenerator;
import com.clinic7.data.LinkedList;
import com.clinic7.model.Doctor;
import java.time.LocalDateTime;
import com.clinic7.storage.DoctorFileHandler;


public class DoctorManagement {
 
    String path = "resources/doctors.csv";

    private LinkedList<Doctor> registeredDoctor; 

    private final LinkedList<Doctor> loggedInList = new LinkedList<>();

    public DoctorManagement() {
        this.registeredDoctor = DoctorFileHandler.loadAllDoctors(path);
    }

    public void registerDoctor(String name, String specialty){
        String id = IDGenerator.generateDoctorID();
        Doctor doctor = new Doctor(id, name, specialty);

        boolean alreadyRegistered = registeredDoctor.contains(d -> d.getName().equalsIgnoreCase(name) && d.getSpecialty().equalsIgnoreCase(specialty));

        if (alreadyRegistered){
            System.out.println("Doctor " + name + " is Already registered");
            return;
        }
        registeredDoctor.addLast(doctor);
        DoctorFileHandler.appendDoctor(doctor, path);
        System.out.println("Doctor " + name + " has been registered with ID: " + id);
    }

    public void loginDoctor(String name) {
        Doctor doctor = registeredDoctor.findData(d -> d.getName().equalsIgnoreCase(name));

        if(doctor == null) {
            System.out.println("Doctor with name " + name + " not found!");
            return;
        }

        boolean alreadyLoggedIn = loggedInList.contains(d -> d.getName().equals(doctor.getName()));

        if(!alreadyLoggedIn){
            doctor.setLoginTime(LocalDateTime.now());
            loggedInList.addFirst(doctor);
            System.out.println("Doctor "+doctor.getName()+" Logged in");
        } else {
            System.out.println("Doctor "+doctor.getName()+" is already Logged in");
        }
    }

    public void logoutDoctor(String doctorName){
        Predicate<Doctor> byName = d -> d.getName().equals(doctorName);

        if(!loggedInList.contains(byName)) {
            System.out.println("Doctor with name " + doctorName + " is not currently logged in!");
            return;
        }
        try {
            Doctor removed = loggedInList.remove(byName);
            if(removed != null) {
                System.out.println("Doctor " + removed.getName() + " has logged out successfully.");
                removed.setLoginTime(null);
            } else {
                System.out.println("Doctor with name " + doctorName + " not found in logged in list!");
            }
        } catch(NoSuchElementException e) {
            System.out.println("Doctor with name " + doctorName + " not found!");
        }
    }

    public void getAllLoggedInDoctors() {
        if(loggedInList.isEmpty()) {
            System.out.println("No Doctor has logged in");
            return;
        }
        System.out.println("===List of Logged In Doctors===");
        loggedInList.forEach(d -> System.out.println(d));
    }

    public int size() {
        return loggedInList.size();
    }

    public void getAllregisteredDoctors() {
        if(registeredDoctor.isEmpty()) {
            System.out.println("No Doctor has registered");
            return;
        }
        System.out.println("===List of Registered Doctors===");
        registeredDoctor.forEach(d -> System.out.println(d));
    }

    public int getRegisteredDoctorSize() {
        return registeredDoctor.size();
    }

    public LinkedList<Doctor> getAllDoctors(){
        return registeredDoctor;
    }

    public LinkedList<Doctor> getLoggedInDoctors() {
        return loggedInList;
    }

    public boolean isDoctorLoggedIn(String doctorName) {
        return loggedInList.contains(d -> d.getName().equalsIgnoreCase(doctorName));
    }

    public Doctor findDoctorBySpecialty(String specialty) {
        try {
            return registeredDoctor.findData(d -> d.getSpecialty().equalsIgnoreCase(specialty));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public LinkedList<Doctor> getDoctorsBySpecialty(String specialty) {
        LinkedList<Doctor> doctors = new LinkedList<>();
        registeredDoctor.forEach(d -> {
            if (d.getSpecialty().equalsIgnoreCase(specialty)) {
                doctors.addLast(d);
            }
        });
        return doctors;
    }

    public Doctor findDoctorById(String doctorId) {
        try {
            return registeredDoctor.findData(d -> d.getId().equals(doctorId));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public void loadFromFile(){
        this.registeredDoctor = DoctorFileHandler.loadAllDoctors(path);
        // this.doctorList = this.registeredDoctor;
    }

    public void saveToFile(){
        DoctorFileHandler.saveAllDoctors(registeredDoctor, path);
    }
}
