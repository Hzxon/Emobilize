EMOBILIZE - Mobile Task & Schedule Management
IF570L Lab Mobile Application Programming - Ujian Tengah Semester


a. DESKRIPSI SINGKAT APLIKASI

Emobilize adalah aplikasi Android untuk mengelola tugas harian dan
menyusun jadwal di kalender. Aplikasi ini dibangun dengan Kotlin dan
Jetpack Compose, dan memakai arsitektur berlapis: Presentation (UI),
Domain, dan Data/DI. Tidak ada backend atau database; tugas dan blok
jadwal disimpan di memori dan diisi data contoh, sedangkan pengaturan
aplikasi disimpan di SharedPreferences.

┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION / UI LAYER                         │
│  EmobilizeApp (NavHost / AppRoute) • RadialDock • RadialMenu           │
│  SplashScreen • AuthScreen • TasksScreen • TaskDetailScreen            │
│  CalendarScreen • SettingsScreen • ReminderScreen                      │
│  ExpandableReminderCard • AddTaskCard • TaskFormSheet                  │
│  TasksViewModel • AuthViewModel                                        │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ consumes via Compose state
┌───────────────────────────────────▼────────────────────────────────────┐
│                             DOMAIN LAYER                               │
│  Models: Task, Calendar, TimeBlock, Category, Priority, ReminderOption │
│          AppSettings (termasuk notificationsEnabled)                   │
│  UseCases: Task, Schedule, Auth, Category & Settings                   │
└───────────────────────────────────▲────────────────────────────────────┘
                                    │ implemented by
┌───────────────────────────────────┴────────────────────────────────────┐
│                           DATA & DI LAYER                              │
│  AppContainer (Composition Root)                                       │
│  InMemoryTaskRepository • InMemoryScheduleRepository • SampleData      │
│  PreferencesAppSettingsRepository (SharedPreferences)                  │
└────────────────────────────────────────────────────────────────────────┘

Fitur utama:
1. Menu radial (RadialMenu, RadialDock)
   - Tombol + membuka kipas aksi melingkar dengan animasi pegas.
   - Empat aksi cepat: Add, Update, Edit, dan Delete.

2. Daftar dan detail tugas (TasksScreen, TaskDetailScreen)
   - Tugas dikelompokkan menjadi To-do, In Progress, dan Done.
   - Filter cepat: All, Today, Tomorrow, Later.
   - Prioritas High, Medium, Low, serta jadwal berupa deadline atau
     rentang waktu.
   - Layar detail dibuka dengan mengirim ID tugas lewat navigasi, dan
     menampilkan status, kategori, prioritas, jadwal, pengingat, serta
     estimasi.

3. Kalender (CalendarScreen)
   - Tampilan Month dan Week.
   - Panah bulan/tahun memindahkan grid dan agenda sekaligus.
   - Agenda harian sinkron dengan deadline tugas dan blok waktu. Event
     diedit lewat form kalender.

4. Push Notification UI (ReminderScreen, ExpandableReminderCard)
   - Ikon lonceng di header membuka layar "Push Notification UI".
   - Kartu notifikasi bisa dibuka-tutup dan berisi logo, nama tugas, dan
     label jatuh tempo. Tugas yang sudah selesai disembunyikan.
   - Kuning: pengingat masih berjalan. Merah: batas waktu terlewat.
   - Aksi di kartu: "Mark as done" mengubah status menjadi Done,
     "Reschedule" membuka editor tugas atau kalender.
   - Sakelar Push notifications ada di Settings, bagian NOTIFICATIONS.
     Saat mati, layar pengingat menampilkan pesan bahwa notifikasi
     sedang nonaktif.
   - Ini mockup UI saja: tidak ada notifikasi sistem, alarm, izin,
     atau backend.

5. Form, keyboard, dan validasi
   - Login/Register memakai keyboard email dan password, dengan cek
     format email dan field wajib.
   - Form tugas: judul dan deskripsi wajib diisi; field "Estimate
     (minutes)" memakai keyboard angka, hanya menerima bilangan bulat
     1-1440, dan boleh dikosongkan.
   - Event ditolak bila waktu selesai tidak setelah waktu mulai.
   - Pesan error tampil di bawah field dan penyimpanan diblokir sampai
     form valid.

6. Tema Material 3 kustom
   - Skema warna terang (NeutralLight) dan gelap (NeutralDark) sendiri.
   - Tipografi font Montserrat lewat fungsi appStyle().
   - Skala sudut ShadcnRadius (sm 4dp, md 6dp, lg 8dp, xl 12dp).


b. NAMA LENGKAP SELURUH ANGGOTA KELOMPOK

No  Nama Lengkap                   NIM          Peran
1   Louis Alexander Putra Andreas  00000117690  Lead Developer, UI & Navigation
2   Stevent Richardo Putra         00000119273  Domain & Settings Engineer
3   Jiro Nathan Lee                00000117431  Repository, Build & Data
4   Kurniawan                      00000114726  Design System, Components & QA


c. PEMBAGIAN TUGAS DAN SISTEM KERJA KELOMPOK

Pembagian tugas
Kode dibagi per area tanggung jawab, sehingga tiap anggota memegang
bagian aplikasi yang berbeda dan konflik saat penggabungan kode bisa
dihindari:
- Presentasi dan navigasi (Louis): NavHost dan AppRoute, menu radial,
  TasksScreen, dan TasksViewModel.
- Logika domain dan pengaturan (Stevent): model domain, use case tugas
  dan jadwal, SettingsScreen, AppSettings, dan Category.
- Repositori, build, dan data (Jiro): repositori GitHub, alur branch
  Git, konfigurasi Gradle, CI, serta penyimpanan data in-memory.
- Desain sistem, komponen, dan QA (Kurniawan): aset branding, kartu
  pengingat notifikasi, pengecekan token tema, dan Compose Preview.

Sistem kerja
- Seluruh kode ada di satu repositori GitHub (Hzxon/Emobilize).
  Pengembangan utama berjalan di branch "tasks-radial-menu".
- Single source of truth: perubahan tugas dan jadwal lewat
  TasksViewModel, akun lokal lewat AuthViewModel. Keduanya dibuat dari
  AppContainer. Layar hanya membaca state dan mengirim aksi.
- Settings memakai draf di layar dan baru disimpan ke AppSettings saat
  tombol "Save Changes" ditekan.
- Setiap perubahan dicek dengan build Kotlin tanpa error
  (gradlew assembleDebug).
- Tampilan diperiksa dalam mode terang dan gelap lewat Compose Preview
  di Android Studio dan di emulator.


d. TUGAS / KONTRIBUSI SPESIFIK TIAP ANGGOTA

1. Louis Alexander Putra Andreas (Lead Developer, UI & Navigation)
   - Merancang navigasi aplikasi dengan NavHost (AppRoute.kt): layar
     Tasks dan Calendar serta dialog Settings, AddTask, dan EditTask.
   - Membangun daftar tugas (TasksScreen.kt, TasksViewModel.kt): filter
     waktu, pengelompokan status, dan seleksi tugas.
   - Membuat menu radial (RadialMenu.kt, RadialDock.kt) dengan animasi
     pegas, rotasi ikon, dan deteksi gestur sentuh.
   - Membuat form tambah tugas cepat (AddTaskCard.kt) dan layar detail
     tugas (TaskDetailScreen.kt).
   - Membuat rute pengingat (ReminderScreen.kt) dari lonceng header.
   - Menyederhanakan kalender menjadi Month/Week dan menghubungkan panah
     bulan/tahun ke grid dan tanggal aktif.
   - Memindahkan sakelar push notification ke Settings.

2. Stevent Richardo Putra (Domain & Settings Engineer)
   - Membangun layar Settings (SettingsScreen.kt): tema System/Light/
     Dark, awal minggu kalender, kategori dan prioritas default,
     sakelar notifikasi, serta editor nama dan warna kategori.
   - Membangun use case domain (TaskUseCases.kt, ScheduleUseCases.kt):
     ambil, tambah, ubah, hapus, ubah status tugas, serta tambah dan
     hapus blok waktu.
   - Menulis logika jadwal: urutan deadline (deadlineRank), validasi
     rentang waktu (rangeIsValid), dan deteksi tugas terlewat
     (isOverdue).

3. Jiro Nathan Lee (Repository, Build & Data)
   - Menyiapkan dan merapikan repositori GitHub, branch, dan .gitignore.
   - Mengonfigurasi Gradle (compileSdk 37, minSdk 28, Java 17) dan
     build otomatis CI (ci.yml).
   - Membuat repositori data in-memory (InMemoryTaskRepository.kt,
     InMemoryScheduleRepository.kt) beserta data contoh (SampleData.kt).
   - Menambah field Estimate (minutes) dengan validasi angka, cek waktu
     selesai Event, data contoh yang lebih lengkap, serta memperbaiki
     tap yang tembus ke tab tersembunyi dan tombol back.

4. Kurniawan (Design System, Components & QA)
   - Membuat kartu pengingat notifikasi yang bisa dibuka-tutup
     (ExpandableReminderCard.kt), dengan info deadline, tombol "Mark as
     done" dan "Reschedule", serta warna kuning/merah sesuai status.
   - Merancang logo monogram "E" (ic_emobilize_logo.xml) dan
     memasangnya di ikon launcher serta layar splash.
   - Memeriksa konsistensi token desain (warna, tipografi Montserrat,
     ShadcnRadius) dan menyusun @Preview untuk mode terang/gelap dan
     kartu terbuka/tertutup.
