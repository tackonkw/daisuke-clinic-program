# DAISUKE CLINIC

Program `Daisuke Clinic` adalah aplikasi konsol berbasis Java yang dirancang untuk mensimulasikan sistem manajemen data dasar bagi sebuah klinik kecil. Tujuannya adalah untuk mengelola catatan pasien, log dokter, dan riwayat janji temu secara efisien. Proyek ini dibangun sebagai latihan komprehensif dalam mengimplementasikan dan memanfaatkan berbagai struktur data kustom, seperti Linked List, Stack, Queue, dan Binary Search Tree, tanpa menggunakan koleksi bawaan Java.


## 2. Folder Structure
```
DAISUKE CLINIC/                 
├── com/                           <-- Folder induk untuk semua paket 'com.clinic7.*'
│   └── clinic7/
│       ├── app/                   <-- Berisi kelas utama aplikasi
│       │   └── Main.java          
│       ├── core/                  <-- Kelas-kelas manajer inti dan logika bisnis
│       │   ├── AppointmentQueue.java
│       │   ├── DoctorManagement.java
│       │   ├── PatManBST.java
│       │   └── PatManLL.java
│       ├── data/                  <-- Implementasi struktur data kustom
│       │   ├── BinarySearchTree.java
│       │   ├── LinkedList.java
│       │   ├── Queue.java
│       │   ├── SimpleList.java
│       │   └── Stack.java
│       ├── model/                 <-- Kelas-kelas model/entitas data (POJO)
│       │   ├── Appointment.java
│       │   ├── Doctor.java
│       │   └── Patient.java
│       ├── storage/               <-- Kelas-kelas untuk persistensi data (File Handling)
│       │   ├── AppointmentFileHandler.java
│       │   ├── DoctorFileHandler.java
│       │   └── PatientFileHandler.java
│       └── util/                  <-- Kelas-kelas utilitas dan helper
│           ├── IDGenerator.java
│           └── PATComparator.java
└── resources/                     <-- Folder untuk file data CSV
    ├── appointments.csv
    ├── doctors.csv
    └── patients.csv
```

## 3. 🔍 Implementasi

Bagian ini menjelaskan bagaimana fungsionalitas kunci dalam aplikasi Daisuke Clinic diimplementasikan, dengan fokus pada penggunaan struktur data kustom dan logika program.

### 1. 🔗 Struktur Node dalam Linked List Kustom

**Deskripsi:**
Dalam implementasi `LinkedList.java` kustom, struktur node dasar direpresentasikan oleh kelas `Node<T>`. Node ini dirancang secara generik (`<T>`) agar dapat menyimpan berbagai jenis data (misalnya, objek `Patient`, `Doctor`, atau `Appointment`).

**Implementasi:**
```java
// Dari file: com/clinic7/data/LinkedList.java
private static class Node<T> {
    T data;
    Node<T> next;

    Node(T data) {
        this.data = data;
        this.next = null;
    }
}
```
Pada metode *constructor* `Node`, argumen generik `data` digunakan untuk mengisi objek yang akan disimpan oleh node. Properti `next` diinisialisasi sebagai `null`, menandakan bahwa node ini belum terhubung ke node lain. Struktur ini membentuk rantai elemen (`linked list`) untuk mempermudah *traversal* dan manajemen data.

### 2. 🔍 Pencarian Pasien Berdasarkan Nama

**Deskripsi:**
Fungsionalitas pencarian pasien berdasarkan nama tidak dilakukan secara langsung pada `PatientNode` melainkan melalui manajer pasien (`PatManLL`) yang memanfaatkan kapabilitas `LinkedList` kustom.

**Implementasi:**
```java
// Dari file: com/clinic7/core/PatManLL.java
public Patient findPatientById(java.util.function.Predicate<Patient> predicate) {
    try {
        return patientList.findData(predicate);
    } catch (NoSuchElementException e) {
        return null;
    }
}
```
Fungsi ini memanfaatkan metode `findData(Predicate<T> match)` yang fleksibel dari `LinkedList.java` kustom. `Predicate` memungkinkan pencarian berdasarkan berbagai kriteria, termasuk nama pasien (`p.getName().equalsIgnoreCase(name)`) dengan mengabaikan huruf kapital. Metode ini akan melakukan pencarian linear dalam `LinkedList` hingga menemukan node dengan nama yang sesuai. Jika tidak ditemukan, akan dikembalikan `null`.

### 3. 🧑‍⚕️ Login Dokter & Manajemen Sesi

**Deskripsi:**
Manajemen sesi login dokter ditangani oleh kelas `DoctorManagement`, yang mengelola daftar dokter yang terdaftar dan daftar dokter yang saat ini sedang login.

**Implementasi:**
```java
// Dari file: com/clinic7/core/DoctorManagement.java
private final LinkedList<Doctor> loggedInList = new LinkedList<>();

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
    Doctor removed = loggedInList.remove(byName);
    if(removed != null) {
        removed.setLoginTime(null);
        System.out.println("Doctor " + removed.getName() + " has logged out successfully.");
    }
}
```
Setelah input nama dokter, sistem mencocokkan nama dengan data dokter yang terdaftar. Jika cocok dan dokter belum *login*, objek `Doctor` yang bersangkutan akan ditambahkan ke `loggedInList` dan properti `loginTime` pada objek `Doctor` tersebut akan diperbarui. `loggedInList` ini kemudian digunakan untuk memverifikasi ketersediaan dokter dalam penjadwalan janji temu.

### 4. 🗓️ Menjadwalkan Janji Temu (Custom Priority Queue)

**Deskripsi:**
Antrean janji temu diimplementasikan bukan sebagai antrean FIFO sederhana, melainkan sebagai *priority queue* yang mengurutkan janji temu berdasarkan waktu terlama. Proses penjadwalan juga melibatkan validasi kompleks.

**Implementasi:**
```java
// Dari file: com/clinic7/core/AppointmentQueue.java
private LinkedList<Appointment> appointments;
private static final Comparator<Appointment> APPOINTMENT_TIME_COMPARATOR = 
    (a1, a2) -> a1.getTime().compareTo(a2.getTime());

public void scheduleAppointment(Appointment appointment) {
    if (appointment.getTime().isBefore(LocalDateTime.now())) { /* ...error...*/ return; }

    if (patientManager == null || doctorManager == null) { /* ...error...*/ return; }

    Patient patient = patientManager.findPatientById(appointment.getPatientID());
    Doctor doctor = doctorManager.findDoctorById(appointment.getDoctorID());

    if (patient == null) { /* ...error...*/ return; }
    if (doctor == null) { /* ...error...*/ return; }

    if (!isDoctorAvailable(doctor.getId(), appointment.getTime())) { /* ...error...*/ return; }

    if (!isPatientAvailable(patient.getId(), appointment.getTime())) { /* ...error...*/ return; }
    
    appointments.addSorted(appointment, APPOINTMENT_TIME_COMPARATOR); 
    appointmentFileHandler.appendAppointment(appointment, APPOINTMENT_FILE);
    System.out.println("Janji temu " + appointment.getAppointmentID() + " berhasil dijadwalkan.");
}
```
Metode `scheduleAppointment()` menerima objek `Appointment` dan memasukkannya ke dalam `LinkedList` internal menggunakan metode `addSorted()`. Metode `addSorted()` dari `LinkedList` kustom memastikan bahwa janji temu tetap terurut berdasarkan waktu paling awal menggunakan `APPOINTMENT_TIME_COMPARATOR`. Sebelum penambahan, serangkaian validasi (waktu tidak di masa lalu, ketersediaan dokter/pasien pada waktu tertentu) dijalankan.

### 5. 🌳 Pencarian Pasien Menggunakan BST

**Deskripsi:**
Untuk pencarian pasien yang efisien, sistem menggunakan struktur data Binary Search Tree (BST).

**Implementasi:**
```java
// Dari file: com/clinic7/data/BinarySearchTree.java
class TreeNode<T> {
    T data;
    TreeNode<T> left, right;
}

// Dari file: com/clinic7/core/PatManBST.java
private BinarySearchTree<Patient> patientTree;

public PatManBST() {
    this.patientTree = new BinarySearchTree<>((p1, p2) -> p1.getId().compareTo(p2.getId()));
}

public Patient findPatientById(String id) {
    Patient dummy = new Patient(id, null, 0, null, null, null, null, (String)null, null, true);
    return patientTree.findData(dummy);
}
```
Struktur `TreeNode<T>` menyimpan objek `Patient`. `PatManBST` menginisialisasi `BinarySearchTree` dengan sebuah `Comparator` yang secara spesifik membandingkan objek `Patient` berdasarkan `id` mereka (`p1.getId().compareTo(p2.getId())`). Ini menjadikan `patientID` sebagai kunci utama untuk pengaturan dan pencarian dalam pohon. Saat pencarian, objek `Patient` dummy dibuat hanya dengan ID untuk memicu pencarian berbasis kunci yang cepat dalam BST.

### 6. 🔁 Inorder Traversal BST

**Deskripsi:**
Untuk menampilkan daftar pasien secara sistematis dalam urutan ID yang terurut, metode *inorder traversal* digunakan pada Binary Search Tree.

**Implementasi:**
```java
// Dari file: com/clinic7/data/BinarySearchTree.java
public void inOrderTraversal(Consumer<T> action) {
    inOrderRecursive(root, action);
}

private void inOrderRecursive(TreeNode<T> current, Consumer<T> action) {
    if (current == null) return;
    inOrderRecursive(current.left, action);
    action.accept(current.data);
    inOrderRecursive(current.right, action);
}

// Dari file: com/clinic7/core/PatManBST.java
public List<Patient> getPatientsInOrder() {
    List<Patient> list = new ArrayList<>();
    patientTree.inOrderTraversal(list::add);
    return list;
}
```
Inorder traversal (Left -> Root -> Right) menjamin bahwa data pasien ditampilkan dalam urutan ID yang terurut (dari kecil ke besar), sesuai karakteristik BST. `PatManBST.getPatientsInOrder()` menggunakan metode `inOrderTraversal` ini untuk mengumpulkan semua pasien ke dalam `List`, yang kemudian ditampilkan secara rapi di `Main.java`.

### 7. 💾 Menyimpan Data ke File

**Deskripsi:**
Untuk memastikan data tidak hilang saat program dimatikan, semua data pasien, dokter, dan janji temu disimpan ke file CSV.

**Implementasi:**
```java
// Contoh dari file: com/clinic7/storage/PatientFileHandler.java
public static void saveAllPatients(LinkedList<Patient> patientList, String filename) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
        patientList.forEach(patient -> {
            try {
                writer.write(patient.toCSV());
                writer.newLine();
            } catch (IOException e) { /* ...error handling...*/ }
        });
        System.out.println("Patients saved successfully to: " + filename);
    } catch (IOException e) { /* ...error handling...*/ }
}
```
Setiap objek data (`Patient`, `Doctor`, `Appointment`) memiliki metode `toCSV()` sendiri yang mengubah objek menjadi format string yang dipisahkan koma. Kelas-kelas `FileHandler` (`PatientFileHandler`, `DoctorFileHandler`, `AppointmentFileHandler`) bertanggung jawab untuk menulis `LinkedList` dari objek-objek ini ke file `.csv` yang sesuai (`patients.csv`, `doctors.csv`, `appointments.csv`) dan juga memuatnya kembali (`fromCSV()`) saat aplikasi dimulai.


## 3. ▶️ Panduan Penggunaan Program

### 3.1 ⚙️ Membangun (Compile) Proyek
```bash
javac -d out --source-path "src;core" src/com/clinic7/app/Main.java
```
### 3.2 🚀 Menjalankan Program
```bash
java -cp out com.clinic7.app.Main
```
### 3.3 Tampilan Program
####    1. Tampilan Awal
```
Patients loaded successfully from: resources/patients.csv
IDGenerator Patient Counter set to: 3
Doctors loaded successfully from: resources/doctors.csv
IDGenerator Doctor Counter set to: 0
==========================================================
||           SELAMAT DATANG DI DAISUKE CLINIC           ||
==========================================================
 Tanggal: 18-06-2025
 Waktu:   19:18:32
==========================================================

Tekan ENTER untuk melanjutkan...
```

####    2. Main menu
```
====================================
      MENU UTAMA DAISUKE CLINIC     
====================================
  MANAJEMEN PASIEN (LINKED LIST)
  1. Tambah Pasien Baru
  2. Hapus Pasien berdasarkan ID
  3. Cari Pasien berdasarkan Nama
  4. Tampilkan Semua Pasien (Linked List)

  MANAJEMEN DOKTER
  5. Registrasi Dokter Baru
  6. Login Dokter
  7. Logout Dokter
  8. Tampilkan Dokter yang Sedang Login

  MANAJEMEN JANJI TEMU (QUEUE)
  9. Jadwalkan Janji Temu
  10. Proses Janji Temu Berikutnya
  11. Tampilkan Janji Temu Mendatang

  PENCARIAN PASIEN (BINARY SEARCH TREE)
  12. Cari Pasien berdasarkan ID (BST)
  13. Tampilkan Semua Pasien (BST Inorder)

  0. KELUAR APLIKASI
====================================
Pilih opsi Anda: 
```
####    3. Menu Tambah Pasien
```
--- TAMBAH PASIEN BARU ---
Nama Lengkap: Huda Febri P
Usia: 19
Jenis Kelamin (Pria/Wanita): pRIA
Alamat: Sragen
Nomor Telepon: 283736363729
Golongan Darah: Ab+
Riwayat Medis Awal (opsional, pisahkan dengan titik koma jika lebih dari satu, e.g., Flu;Demam): Hidung Tersumbat
Appended patient: PAT-20250618-0004

[SUKSES] Pasien 'Huda Febri P' berhasil ditambahkan dengan ID: PAT-20250618-0004
[INFO] Pasien juga telah disinkronkan ke Binary Search Tree.
Patients saved successfully to: resources/patients.csv
```
####    4. Menu Tambah Pasien
```
--- DAFTAR SEMUA PASIEN (LINKED LIST) ---
Total Pasien: 4
======================================================
  ID: PAT-20250618-0001
  Nama: Bagas Aditama
  Usia: 19
  Jenis Kelamin: Pria
  Alamat: Kartosuro
  No. Telepon: 082112341234
  Gol. Darah: B+
  Riwayat Medis: ['Demam', 'Flu']
  Tgl. Registrasi: 2025-06-18
------------------------------------------------------
  ID: PAT-20250618-0002
  Nama: Huda Febri
  Usia: 19
  Jenis Kelamin: Pria
  Alamat: Kemlayan
  No. Telepon: 087812341234
  Gol. Darah: AB+
  Riwayat Medis: ['Demam']
  Tgl. Registrasi: 2025-06-18
------------------------------------------------------
  ID: PAT-20250618-0003
  Nama: Huda Febri Pradana
  Usia: 19
  Jenis Kelamin: Pria
  Alamat: Sragen
  No. Telepon: 082625244272
  Gol. Darah: AB+
  Riwayat Medis: ['Flu']
  Tgl. Registrasi: 2025-06-18
------------------------------------------------------
  ID: PAT-20250618-0004
  Nama: Huda Febri P
  Usia: 19
  Jenis Kelamin: pRIA
  Alamat: Sragen
  No. Telepon: 283736363729
  Gol. Darah: Ab+
  Riwayat Medis: ['Hidung Tersumbat']
  Tgl. Registrasi: 2025-06-18
------------------------------------------------------
======================================================

Tekan ENTER untuk melanjutkan...

````

## 4. Peran Anggota Kelompok
* [Bagas Aditama Suryo Nugroho](https://github.com/jempolbagas) `(L01240542)` [PatManLL.java, PatManBST.java, IDGenerator.java, Linkedlist.java, Main.java, Patients.java, BinarySearchTree.java, Queue.java, PATComparator.java, PatientFileHandler.java, AppointmentFileHandler.java, Pembuatan Video Demo, Tester]
* [Hendrata Wibsar Mulyasaputra](https://github.com/Hendrata-PJ) `(L0124056)` [Doctor.java, DoctorFileHandler.java]
* [Huda Febri Pradana](https://github.com/git-HDfp) `(L0124057)` [Readme.md, DoctorFileHandler.java, DoctorManagement.java, Main.java, Queue.java, Appointment.java, Doctor.java, AppointmentFileHandler.java, Pembuatan Video Demo, Tester]
* [Dzaky Ghiffary Susilo](https://github.com/tackonkw) `(L0124097)` [Patients.java, PatManBST.java, PatManLL.java, Peng-upload-an Assignment]

## 5. Video Tubes
Video Tubes dapat diakses  [disini](https://youtu.be/R8qa9UZ2tAM).



