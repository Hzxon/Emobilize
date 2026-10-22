# Emobilize — Mobile Task & Schedule Management
## Comprehensive Project Report & Architecture Analysis

> **Sumber Analisis**: Laporan ini disusun berdasarkan analisis arsitektur basis kode dan graf pengetahuan terverifikasi **Graphify** (`graphify-out/graph.json` dan `graphify-out/GRAPH_REPORT.md`).  
> **Status Basis Kode**: 1041+ node, 2400+ relasi edge, 45 komunitas arsitektur, 0 siklus impor (*zero circular dependencies*).

---

## a. Brief Description of the App

**Emobilize** adalah aplikasi manajemen tugas (*task management*) dan penjadwalan produktivitas harian (*calendar & schedule blocking*) modern untuk platform Android. Aplikasi ini dibangun secara murni menggunakan **Kotlin** dan **Jetpack Compose** dengan menerapkan prinsip **Clean Layered Architecture** (Presentation, Domain, dan Data/DI).

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION / UI LAYER                         │
│  EmobilizeApp (NavHost / AppRoute) • RadialDock • RadialMenu           │
│  TasksScreen • CalendarScreen • SettingsScreen • TaskDetailScreen      │
│  ExpandableReminderCard (Push Notif) • AddTaskCard • TaskFormSheet     │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ consumes via StateFlow / State
┌───────────────────────────────────▼────────────────────────────────────┐
│                             DOMAIN LAYER                               │
│  Models: Task, Calendar, TimeBlock, Category, Priority, ReminderOption │
│  UseCases: TaskUseCases.kt (Add, Update, Delete, SetStatus)            │
│            ScheduleUseCases.kt (PlaceBlock, DeleteBlock, etc.)         │
│            Category & Settings UseCases                                │
└───────────────────────────────────▲────────────────────────────────────┘
                                    │ implemented by
┌───────────────────────────────────┴────────────────────────────────────┐
│                           DATA & DI LAYER                              │
│  AppContainer (Composition Root)                                       │
│  InMemoryTaskRepository • InMemoryScheduleRepository • SampleData     │
└────────────────────────────────────────────────────────────────────────┘
```

### Fitur & Karakteristik Utama:
1. **Interactive Radial Menu & Dock (`RadialMenu`, `RadialDock`)**:
   - Sistem navigasi melingkar (*radial action fan*) berbasis gestur dengan fisika pegas (*spring physics animation*).
   - Memungkinkan akses cepat 4 aksi: *Add* (-87°), *Update* (0° / 30°), *Edit* (58° / -30°), dan *Delete* (87°).
2. **Task Management & Detail Screen (`TasksScreen`, `TaskDetailScreen`)**:
   - Pengelompokan status tugas 3 tingkat: **To-do** (lingkaran abu-abu), **In Progress** (lingkaran oranye/biru chart-1), dan **Done** (lingkaran hijau chart-2).
   - Filter cepat: *All*, *Today*, *Tomorrow*, *Later*.
   - Tingkat prioritas (*High*, *Medium*, *Low*) dan penetapan rentang jadwal (*DateRange*).
   - Layar detail interaktif dengan *segmented control* status, ringkasan deadline, dan pemilihan pengingat.
3. **Calendar & Schedule Blocking (`CalendarScreen`, `DayAgenda`)**:
   - Kalender dinamis mingguan/bulanan dengan pemblokiran waktu kerja (*time blocking*).
   - Tampilan agenda harian yang sinkron dengan deadline tugas dan acara harian.
4. **Rich Push Notification & Reminder System (`ExpandableReminderCard`)**:
   - Komponen kartu notifikasi interaktif yang dapat diperluas (*expand/collapse*).
   - Memuat logo monogram resmi "E", nama aplikasi, nama tugas, dan indikator batas waktu.
   - **Skema Warna Status Waktu**:
     - 🟡 **Kuning / Amber** (`palette.prioMedium`): Pengingat aktif beberapa menit/jam sebelum deadline (`"Due in 30 mins • 04:00 PM"`).
     - 🔴 **Merah** (`palette.prioHigh`): Batas waktu tercapai atau terlewat (`"Deadline passed • 02:00 PM"`).
   - Outline kartu bersih dan netral (`palette.scheme.border`), bebas dari outline warna kuning/ungu yang mengganggu.
   - Aksi instan langsung di kartu: *Mark as done* dan *Reschedule*.
5. **Standardized Design System (Shadcn + Montserrat)**:
   - Token palet `NeutralLight` dan `NeutralDark` dari registry `@shadcn/theme-neutral`.
   - Tipografi terstandarisasi menggunakan font **Montserrat** melalui fungsi `appStyle()`.
   - Kurva bentuk terukur mengikuti rasio `ShadcnRadius` (sm: 4dp, md: 6dp, lg: 8dp, xl: 12dp).

---

## b. Full Names of All Group Members

Kelompok pengembang terdiri dari **4 orang anggota**:

| No | Nama Lengkap | Identitas / Akun Git | Peran Utama dalam Tim |
|:--:|---|---|---|
| 1 | **Louis Alexander Andreas** | `LouisAlexanderAndreas` (`louwisa18@gmail.com`) | **Lead Developer / UI & Navigation Architect** |
| 2 | **Stevent Richardo** | `sterep05` (`steventrichardo020318@gmail.com`) | **Domain & Settings Engineer** |
| 3 | **Nathan Lee** | `Hzxon` (`nathanlee912007@gmail.com`) | **Repository & Environment Specialist / DevOps** |
| 4 | **Christian Pratama** *(Anggota ke-4)* | `[Kontributor ke-4]` | **QA & Design System / Component Specialist** |

---

## c. Explanation of How Tasks Were Divided and the Group's Working System

### 1. Metode Pembagian Tugas (*Separation of Concerns*)
Berdasarkan pemetaan komunitas Graphify, basis kode dibagi menjadi domain tanggung jawab terisolasi untuk menghindari konflik penggabungan kode (*merge conflicts*):

- **Arsitektur Presentasi & Navigasi (Louis Alexander Andreas)**:
  - Mengelola shell navigasi tingkat atas (`EmobilizeApp`, `AppRoute`, `NavHost`), menu radial gestural (`RadialDock`, `RadialMenu`), serta orkestrator state tampilan (`TasksScreen`, `TasksViewModel`).
- **Logika Bisnis & Pengaturan Sistem (Stevent Richardo)**:
  - Mengelola model domain, use case transaksi tugas dan jadwal, serta fitur kustomisasi sistem (`SettingsScreen`, `AppSettings`, `Category`).
- **Manajemen Repositori & Infrastruktur Build (Nathan Lee)**:
  - Mengelola repository GitHub (`Hzxon/Emobilize`), alur percabangan Git, konfigurasi Gradle Android SDK 37, dependensi linting/CI, serta implementasi penyimpanan data in-memory (`InMemoryTaskRepository`, `InMemoryScheduleRepository`).
- **Komponen UI, Desain Sistem & QA (Christian Pratama / Anggota 4)**:
  - Mengelola aset branding (vektor logo monogram "E"), komponen kartu pengingat notifikasi (`ExpandableReminderCard`), verifikasi token tema, dan penyusunan katalog pratinjau (`Previews.kt`).

### 2. Sistem Kerja Kelompok (*Working System*)
- **Branching Strategy**: Menggunakan model *feature branch* terpusat pada cabang `tasks-radial-menu` yang terhubung ke repositori GitHub upstream.
- **Single Source of Truth (SSOT)**:
  - Seluruh mutasi state ditangani oleh `TasksViewModel` dan disuntikkan secara terpusat melalui `AppContainer`.
  - Tidak ada Composable anak yang menyimpan state data lokal tak tersinkronisasi.
- **Audit Kode & Knowledge Graph (Graphify)**:
  - Pemantauan berkala menggunakan Graphify untuk menjaga kohesi modul, memastikan tidak ada siklus impor (*zero import cycles*), dan memantau konektivitas *God Nodes*.
- **Quality Assurance & Verification**:
  - Setiap perubahan divalidasi melalui kompilasi Kotlin tanpa error (`.\gradlew compileDebugKotlin` / `assembleDebug`).
  - Pratinjau multi-tema (Light/Dark, Collapsed/Expanded) diuji visual melalui Android Studio Compose Preview (`@Preview`).

---

## d. Specific Task/Contribution of Each Individual Member

### 1. Louis Alexander Andreas (Lead Developer / UI & Navigation Architect)
- **Central Navigation Architecture**: Merancang dan menerapkan `NavHost` berbasis Jetpack Compose Navigation (`AppRoute.kt`), mengintegrasikan rute layar utama (`Tasks`, `Calendar`) dan overlay dialog (`Settings`, `AddTask`, `EditTask`).
- **Core Task Management (`TasksScreen.kt` & `TasksViewModel.kt`)**: Membangun antarmuka daftar tugas dengan filter waktu (*Today, Tomorrow, Later*), pengelompokan status (*To-do, Progress, Done*), dan manajemen seleksi tugas.
- **Physics-based Radial Menu (`RadialMenu.kt`, `RadialDock.kt`)**: Mengimplementasikan menu aksi radial melingkar dengan animasi pegas dinamis (*spring physics*), transisi rotasi ikon, dan deteksi gestur sentuh.
- **Task Detail & Form Interfaces (`TaskDetailScreen.kt`, `AddTaskCard.kt`)**: Merancang form pembuatan tugas cepat dan layar detail tugas terstruktur dengan kontrol segmentasi status dan deadline countdown.

### 2. Stevent Richardo (Domain & Settings Engineer)
- **Settings Screen & Category Editor (`SettingsScreen.kt`)**: Membangun antarmuka pengaturan preferensi aplikasi (opsi tema System/Light/Dark, awal hari kalender) serta editor palet warna dan nama kategori (*Study, Work, Health, Personal, University*).
- **Domain Use Cases (`TaskUseCases.kt`, `ScheduleUseCases.kt`)**: Membangun lapisan use case untuk operasi data terisolasi (`GetTasksUseCase`, `AddTaskUseCase`, `UpdateTaskUseCase`, `DeleteTaskUseCase`, `SetTaskStatusUseCase`, `PlaceTimeBlockUseCase`, `DeleteTimeBlockUseCase`).
- **Schedule Calculation & Validation**: Mengembangkan algoritma kalkulasi rank deadline (`deadlineRank()`), logika validasi rentang waktu (`rangeIsValid()`), dan deteksi tugas terlewat (`isOverdue()`).

### 3. Nathan Lee (Repository & Environment Specialist / DevOps)
- **Git Repository & Branch Management**: Menyiapkan dan merapikan repositori GitHub (`Hzxon/Emobilize`), membersihkan redundansi konfigurasi `.gitignore`, dan menjaga kebersihan riwayat commit tim.
- **Build Environment & Gradle Engineering**: Mengonfigurasi modul Gradle Android (`compileSdk 37`, `minSdk 28`, Java 17), memastikan kompatibilitas plugin Kotlin, dan mengonfigurasi skrip automasi pengujian build (`ci.yml`).
- **In-Memory Data Repositories (`InMemoryTaskRepository.kt`, `InMemoryScheduleRepository.kt`)**: Mengimplementasikan abstraksi repositori thread-safe berbasis in-memory untuk menyimpan data tugas dan jadwal agenda, lengkap dengan data *bootstrap* (`SampleData.kt`).

### 4. Christian Pratama / Anggota ke-4 (QA & Design System / Component Specialist)
- **Interactive Notification Reminder Card (`ExpandableReminderCard.kt`)**: Mengembangkan komponen kartu pengingat notifikasi interaktif yang dapat di-expand/collapse dengan informasi deadline, countdown, tombol aksi instan (*Mark as done*, *Reschedule*), dan diferensiasi status warna (kuning untuk pengingat aktif, merah untuk deadline terlewat).
- **Brand Identity & Vector Assets**: Merancang aset vektor monogram logo "E" modern (`ic_emobilize_logo.xml`) dan mengintegrasikannya ke ikon launcher aplikasi (`ic_launcher_foreground.xml`) serta layar splash (`SplashScreen.kt`).
- **Design System Audit & Visual QA (`Previews.kt`, `DESIGN.md`)**: Memastikan kepatuhan token desain Shadcn (warna netral, Montserrat typography via `appStyle()`, dan `ShadcnRadius`), serta menyusun kumpulan `@Preview` multi-konfigurasi untuk validasi UI instan di Android Studio.

---

## e. Graphify Knowledge Graph Insights

Berdasarkan ekstraksi AST dan topologi graf:

1. **Top God Nodes (Abstraksi Paling Terhubung)**:
   - `TasksViewModel` (60 relasi edge) — Pusat orkestrasi data & state UI.
   - `appStyle()` (57 relasi edge) — Fondasi tipografi konsisten Montserrat.
   - `TimeBlock` (27 relasi edge) — Entitas inti jadwal kalender.
   - `AppContainer` (25 relasi edge) — Pusat injeksi dependensi manual.
   - `FabAction` (25 relasi edge) — Enum pemicu aksi menu radial.
2. **Kesehatan Arsitektur (Architectural Health)**:
   - **Import Cycles**: 0 (Bebas dari dependensi melingkar).
   - **Separation Level**: Sangat tinggi, modul UI tidak pernah mengakses repositori data secara langsung, melainkan melalui use cases dan ViewModel.
3. **Komunitas Utama**:
   - `App navigation shell`, `Task domain model`, `Design system tokens`, `AddTaskCard.kt`, `Radial menu drawing`, `Bottom dock`, `SettingsScreen.kt`, `In-memory task store`.

---
*Dokumen ini diperbarui secara otomatis menggunakan analisis Graphify pada 22 Oktober 2026.*
