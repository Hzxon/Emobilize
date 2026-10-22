# Emobilize — Mobile Task & Schedule Management

Laporan proyek ini disusun berdasarkan analisis arsitektur basis kode dan *knowledge graph* **Graphify** (`graphify-out/graph.json` dan `GRAPH_REPORT.md`). Dokumen ini mencakup deskripsi aplikasi, susunan anggota tim, pembagian kerja, serta kontribusi spesifik setiap anggota.

---

## a. Brief Description of the App

**Emobilize** adalah aplikasi manajemen tugas (*task management*) dan penjadwalan produktivitas harian (*calendar & schedule management*) berbasis Android modern yang dibangun sepenuhnya menggunakan **Jetpack Compose** dan arsitektur berlapis (**Clean Layered Architecture: UI, Domain, Data/DI**).

### Fitur & Karakteristik Utama:
1. **Interactive Radial Menu & Dock**: Navigasi unik dengan *radial action fan* berbasis gestur dan pegas (*spring animation*), memungkinkan pengguna membuat (*Add*), memperbarui (*Update*), mengubah (*Edit*), dan menghapus (*Delete*) tugas secara cepat.
2. **Task & Agenda Management**: Pengelompokan tugas berdasarkan status (*To-do*, *In Progress*, *Done*), filter waktu (*Today*, *Tomorrow*, *Later*), tingkat prioritas (*High*, *Medium*, *Low*), kategori yang dapat dikustomisasi, serta dukungan rentang waktu (*DateRange*).
3. **Calendar & Schedule Blocking**: Tampilan agenda interaktif per hari (*DayAgenda*) dan kalender mingguan untuk mengelola blok waktu produktif (*time blocking*).
4. **Rich Push Notification & Reminder System**: Komponen kartu pengingat interaktif (*ExpandableReminderCard*) yang memuat logo monogram "E", opsi pengingat (*None, At deadline, 10 minutes before, 1 hour before, 1 day before*), indikator status visual (kuning/amber untuk pengingat aktif, merah untuk deadline terlewat), serta aksi cepat langsung pada notifikasi (*Mark as done* & *Reschedule*).
5. **Design System & Theming**: Menerapkan token desain terstandar terinspirasi dari **Shadcn** (`NeutralLight` dan `NeutralDark`), tipografi kustom menggunakan keluarga huruf **Montserrat** (`appStyle()`), dan dukungan mode gelap/terang dinamis.

---

## b. Full Names of All Group Members

Kelompok pengembangan terdiri dari **4 orang anggota**:

| No | Nama Lengkap | Identitas / Akun Git | Peran Utama |
|---|---|---|---|
| 1 | **Louis Alexander Andreas** | `LouisAlexanderAndreas` (`louwisa18@gmail.com`) | Lead Developer / UI & Navigation Architect |
| 2 | **Stevent Richardo** | `sterep05` (`steventrichardo020318@gmail.com`) | Domain & Settings Engineer |
| 3 | **Nathan Lee** | `Hzxon` (`nathanlee912007@gmail.com`) | Repository & Environment Specialist / DevOps |
| 4 | **[Nama Anggota ke-4]* / Christian Pratama** | `[Kontributor ke-4]` | QA & Design System / Component Specialist |

> *\*) Catatan: Nama anggota ke-4 dapat disesuaikan dengan nama lengkap rekan kelompok Anda jika berbeda.*

---

## c. Explanation of How Tasks Were Divided and the Group's Working System

### 1. Metode Pembagian Tugas (Separation of Concerns)
Pembagian kerja disusun secara modular mengikuti arsitektur berlapis (*layered architecture*) yang terpetakan dalam graf Graphify (`di/AppContainer`, `domain/usecase`, `ui/theme`, `ui/tasks`, `ui/calendar`, `ui/notifications`):
- **Arsitektur UI & Sistem Navigasi**: Bertanggung jawab atas integrasi `NavHost`, `AppRoute`, `RadialDock`, dan koordinasi alur layar.
- **Domain Logic & Pengaturan Aplikasi**: Bertanggung jawab atas model use case, validasi aturan bisnis tugas, serta fungsionalitas kustomisasi kategori & pengaturan.
- **Struktur Repositori, Environment & CI/CD**: Bertanggung jawab atas pengelolaan repositori Git, branching strategy, `.gitignore`, integrasi build Gradle, dan pengujian.
- **Komponen UI, Notifikasi & Design System**: Bertanggung jawab atas token palet warna, tipografi Montserrat, logo monogram, komponen pengingat, dan preview `@Preview`.

### 2. Sistem Kerja Kelompok (Working System)
- **Version Control System (Git)**: Menggunakan alur kerja berbasis *feature branch* (`tasks-radial-menu`) dengan Git remote di GitHub (`Hzxon/Emobilize`).
- **Knowledge Graph & Code Audit (Graphify)**: Memanfaatkan Graphify untuk memetakan dependensi 849+ node dan 2240+ edge, mendeteksi *god nodes* (`TasksViewModel`, `appStyle()`, `AppContainer`), serta memvalidasi tidak adanya siklus dependensi (*circular imports*).
- **Single Source of Truth (SSOT)**: Seluruh state UI dan mutasi data dikelola secara reaktif melalui `TasksViewModel` dan `AppContainer` tanpa adanya state acak di level composable leaf.
- **Sprint & Review Berkelanjutan**: Setiap fitur baru melewati proses verifikasi kompilasi lokal (`gradlew assembleDebug` / `compileDebugKotlin`) sebelum digabungkan ke branch kerja.

---

## d. Specific Task/Contribution of Each Individual Member

### 1. Louis Alexander Andreas (Lead Developer / UI & Navigation Architect)
- **Arsitektur Navigasi Terpusat**: Merancang dan mengimplementasikan integrasi `NavHost`, `rememberNavController`, serta rute layar modal `AppRoute` (`AppRoute.Tasks`, `AppRoute.Calendar`, `AppRoute.Settings`, `AppRoute.AddTask`, dll.).
- **Komponen Inti Tugas (`TasksScreen` & `TasksViewModel`)**: Membangun orkestrasi layar tugas utama, *status grouping* (*To-do*, *Progress*, *Done*), serta penghubung antara UI Composable dan Use Cases.
- **Gestur & Animasi Menu Radial (`RadialMenu` & `RadialDock`)**: Merancang menu radial dengan animasi pegas (*spring physics animation*) yang dapat membuka fungsionalitas CRUD secara cepat dan responsif.
- **Formulir Tugas & Dialog (`AddTaskCard`, `TaskFormSheet`, `CalendarFormSheet`)**: Mengembangkan kartu input judul cepat dan bottom sheet dengan opsi opsional jadwal, kategori, dan prioritas.

### 2. Stevent Richardo (Domain & Settings Engineer)
- **Halaman Pengaturan & Kategori (`SettingsScreen`)**: Mengembangkan layar *Settings* yang memungkinkan pengguna mengatur preferensi tema (*System, Light, Dark*), awal hari kerja (*WeekStart*), serta pengeditan nama dan warna palet kategori.
- **Use Cases & Domain Business Logic (`TaskUseCases.kt`, `ScheduleUseCases.kt`)**: Membantu memisahkan manipulasi state langsung ke dalam fungsi *use case* (`UpdateCategoryUseCase`, `GetAppSettingsUseCase`, `UpdateAppSettingsUseCase`).
- **Validasi Jadwal & Filter Waktu**: Menerapkan logika perhitungan *rank* deadline, penanganan rentang tanggal (`DateRange`), dan validasi waktu awal/akhir (`rangeIsValid`).

### 3. Nathan Lee (Repository & Environment Specialist / DevOps)
- **Pengelolaan Branch & Repositori Git**: Menyiapkan repository GitHub (`Hzxon/Emobilize`), struktur branch kerja (`tasks-radial-menu`), konfigurasi `.gitignore`, dan eliminasi redudansi berkas build.
- **Konfigurasi Lingkungan Gradle & Build Pipeline**: Memastikan kestabilan build Gradle, manajemen dependensi Android SDK 37 (compileSdk/targetSdk), serta integrasi pipeline CI check.
- **Manajemen Repositori Data In-Memory (`InMemoryTaskRepository`, `InMemoryScheduleRepository`)**: Menjaga integritas data sampel (`SampleData`) dan konsistensi penyimpanan in-memory untuk tasks dan calendar blocks.

### 4. Anggota ke-4 / Christian Pratama (QA & Design System / Component Specialist)
- **Sistem Notifikasi Pengingat (`ExpandableReminderCard`)**: Mengembangkan kartu notifikasi pengingat yang dapat di-expand/collapse dengan logo monogram, serta diferensiasi warna status: kuning/amber untuk *reminder aktif* dan merah untuk *deadline terlewat*.
- **Branding & Desain Aset Vektor**: Membuat dan memperbarui logo monogram "E" (`ic_emobilize_logo.xml`) dan foreground launcher (`ic_launcher_foreground.xml`) agar konsisten dengan tema minimalis.
- **Design System & Testing Preview (`Previews.kt`)**: Menyusun berkas preview Compose (`@Preview`) multi-tema (Light dan Dark mode, Collapsed dan Expanded) untuk mempermudah QA visual di Android Studio tanpa perlu menjalankan emulator penuh.

---
*Dokumen ini dibuat otomatis menggunakan data arsitektur Graphify pada 22 Oktober 2026.*
