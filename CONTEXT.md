# Konteks Emobilize

Dokumen ini untuk AI lain yang mengubah UI. Baca file ini dan `DESIGN.md` sebelum menyentuh kode. Ini aplikasi Android Jetpack Compose, bukan React, bukan Tailwind, bukan Figma Make.

`AGENTS.md` di root repo ini berlaku. Abaikan `AGENTS.md` di folder user; file itu milik proyek web lain.

Branch: `departures-redesign`. Remote: `https://github.com/Hzxon/Emobilize`.

## Prompt yang bisa ditempel

```
Ini proyek Android Kotlin + Jetpack Compose di folder Emobilize.
Baca AGENTS.md, CONTEXT.md, dan DESIGN.md dulu.
Tugas ini hanya boleh mengubah folder: app/src/main/java/ayodong/emobilize/feature/<nama>/
Jangan edit core/, file Gradle, atau AndroidManifest.xml. Kalau perlu, berhenti dan laporkan.
Ubah hanya tampilan. Jangan ubah perilaku simpan, filter, atau arsitektur.
Warna, font, dan ukuran ada di core/ui/theme/ dan DESIGN.md.
Jangan menulis daftar tugas atau blok kalender dari Composable.
Teks UI yang ada berbahasa Inggris.
Setelah selesai, jalankan: .\gradlew.bat :app:compileDebugKotlin :app:testDebugUnitTest --offline
```

Ganti `<nama>` dengan folder fitur (tasks, calendar, settings, reminders, auth, splash). Nama file ada di tabel "Mau ubah apa" di bawah.

## Struktur folder

Yang dipakai aplikasi. Abaikan `app/build/`, `.gradle/`, dan `.idea/`. Kode dikelompokkan per fitur, lapisan ada di dalam fitur. Aturan lengkap ada di `AGENTS.md`.

```
Emobilize/
  AGENTS.md                            <- aturan untuk agent AI (CLAUDE.md menunjuk ke sini)
  CONTEXT.md
  DESIGN.md
  app/src/main/
    AndroidManifest.xml
    java/ayodong/emobilize/
      EmobilizeApplication.kt          <- membuat AppContainer sekali
      MainActivity.kt
      core/                            <- satu pemilik; fitur tidak mengeditnya
        model/       Task.kt, Calendar.kt, AppSettings.kt
        domain/      Repositories.kt   (TaskRepository, ScheduleRepository, CategoryRepository, AppSettingsRepository)
        data/        InMemory*Repository, Preferences*Repository, FakeRepositories.kt, SampleData.kt
        di/          AppContainer.kt   (repository bersama saja)
        navigation/  AppRoute.kt, AppNavHost.kt, MainShell.kt, ShellViewModel.kt
        ui/theme/    Palette, Shadcn, Type, Metrics, Theme, EmobilizeLogo
        ui/component/FormField.kt
        ui/form/     TaskDraft.kt, FormPickers.kt (dipakai form Tasks dan Calendar)
        ui/dock/     RadialDock.kt, RadialMenu.kt, DockModels.kt
      feature/                         <- satu pemilik per folder
        tasks/       ui/ (TasksScreen, TaskDetailScreen, AddTaskCard, TaskFormSheet, TasksViewModel, TaskFormViewModel), di/
        calendar/    ui/ (CalendarScreen, CalendarFormSheet, CalendarViewModel, CalendarFormViewModel), di/
        settings/    domain/SaveSettingsUseCase.kt, ui/ (SettingsScreen, SettingsViewModel), di/
        reminders/   ui/ (ReminderScreen, ExpandableReminderCard, RemindersViewModel), di/
        auth/        domain/ (AuthRepository, use case), data/ (Preferences, Fake), ui/, di/
        splash/      ui/SplashScreen.kt
    res/
      font/                            <- Inter Tight (variable), IBM Plex Mono
      values/themes.xml
      values/colors.xml
```

Package: `ayodong.emobilize`. Satu modul Gradle: `:app`. `minSdk` 28. `compileSdk` dan `targetSdk` 37.

## Mau ubah apa

| Bagian layar | File | Composable |
| --- | --- | --- |
| Daftar rute, kapan form muncul | `core/navigation/AppNavHost.kt` | `AppNavHost` |
| Tombol header (Reminders, Settings), tab, dock, radial menu | `core/navigation/MainShell.kt` | `MainShell`, `HeaderButton` |
| Preferensi aplikasi dan kategori | `feature/settings/ui/SettingsScreen.kt` | `SettingsScreen` |
| Papan keberangkatan, filter, baris tugas | `feature/tasks/ui/TasksScreen.kt` | `TasksScreen`, `BoardRow`, `NextStrip`, `LastService` |
| Kartu Add di tab Tasks | `feature/tasks/ui/AddTaskCard.kt` | `AddTaskCard` |
| Sheet edit tugas | `feature/tasks/ui/TaskFormSheet.kt` | `TaskFormSheet`, `DateField`, `TimeField` |
| Form Add dan edit di tab Calendar | `feature/calendar/ui/CalendarFormSheet.kt` | `CalendarFormSheet` |
| Peta rute: header, garis minggu, kisi bulan, agenda | `feature/calendar/ui/CalendarScreen.kt` | `CalendarScreen`, `WeekLine`, `MonthGrid`, `DayAgenda`, `RouteRow` |
| Tiket detail tugas | `feature/tasks/ui/TaskDetailScreen.kt` | `TaskDetailScreen`, `Ticket` |
| Dock bawah, tab, muka tombol tengah | `core/ui/dock/RadialDock.kt` | `BottomDock`, `FabFace` |
| Kipas Add / Update / Edit / Delete | `core/ui/dock/RadialMenu.kt` | `RadialMenu` |
| Warna, radius, jarak, font | `core/ui/theme/` dan `DESIGN.md` | `LocalPalette`, `AppMetrics`, `appStyle`, `monoStyle`, `lineColor` |

State data tidak diubah dari Composable. Setiap fitur punya ViewModel sendiri di `feature/<x>/ui/`. Untuk ganti warna kartu atau jarak, jangan buka ViewModel.

## Token tampilan

- Warna layar: `LocalPalette.current` di `core/ui/theme/Palette.kt`. Tema Departures, dua palet: `NeutralLight` (id 1, Departures Light) dan `NeutralDark` (id 2, Departures Dark). Papan dan tiket memakai token `board*`, `signal`, `signalInk`.
- Warna shadcn: `palette.scheme` dari `core/ui/theme/Shadcn.kt` (`background`, `popover`, `border`, `card`).
- Teks: `appStyle(...)` (Inter Tight) dan `monoStyle(...)` (IBM Plex Mono, untuk jam dan status) di `core/ui/theme/Type.kt`.
- Warna kategori: `palette.categoryColor(...)` atau `lineColor(argb)`. Jangan `Color(category.color)`; ARGB tersimpan dipetakan ke warna jalur.
- Semua radius 0 dp. Jangan tambah `RoundedCornerShape` dengan angka sendiri.
- Jarak dan ukuran kartu: `AppMetrics` di `core/ui/theme/Metrics.kt`. Radius kartu `AppMetrics.cardRadius`. Tinggi minimum kartu `AppMetrics.cardMinHeight`.
- Padding halaman: `AppMetrics.pageHorizontal` (20.dp). Header: `AppMetrics.headerHorizontal` (20.dp).
- Header ada di tiap layar tab (jam di Tasks, Route map di Calendar). Tombol Reminders dan Settings datang dari `MainShell` lewat parameter `headerActions`.
- Aksi radial: Add hijau, Update biru, Edit `actionEdit`, Delete `danger`. Fungsi `menuColor`.

Jangan menambah palet baru. Nilai token dijelaskan di `DESIGN.md`.

## Perilaku yang harus tetap

Produk: aplikasi tugas dan kalender. Data di memori, hilang saat proses mati. Sampel di `core/data/SampleData.kt`.

Dua tab: Tasks (label dock DEPARTURES) dan Calendar (label dock ROUTE MAP). Tombol tengah persegi membuka radial menu. Ikon gear di header membuka Settings. Tema hanya diubah dari Settings.

### Tasks

Filter Today, Tmrw, Later, All. Papan gelap dengan kolom TIME, DESTINATION, LN, STATUS. Urutan tetap: to-do, progress, done, masing-masing menurut deadline. Status: ON TIME, ANY TIME, DELAYED (lewat jam hari ini), BOARDING (progress), DEPARTED (done). Baris kedua: prioritas, durasi, dan hari bila bukan hari ini. Tugas tanpa jadwal hanya muncul di All. Swipe kanan selesai, swipe kiri hapus, snackbar Undo. Semua tugas di filter selesai: panel "The last service has left."

### Add di tab Tasks

`AddTaskCard`. Judul langsung fokus dan keyboard terbuka. Tiga ikon opsional: Category, Priority, Schedule. Schedule berisi Deadline atau Time range. Ketuk pilihan yang sama untuk menghapusnya. Tombol Add task menyimpan jika judul terisi. Close, ketuk luar, atau back menutup tanpa menyimpan. Time range tidak valid membuat Add task membuka panel Schedule dan tidak menyimpan.

### Edit tugas

Sheet penuh `TaskFormSheet`. Bukan `AddTaskCard`.

### Calendar

Month dan Week (Day ditampilkan sebagai Week). Week berupa garis tujuh stasiun. Di bawahnya agenda sebagai rute vertikal, bukan kisi jam: jam mono, rel warna kategori, titik stasiun. Item diurutkan waktu mulai, item tanpa jam di bawah (ANY). Hari ini diberi penanda YOU ARE HERE. Hari kosong: "Nothing scheduled".

- Blok kalender: "Regular Schedule", kategori, jam mulai sampai selesai.
- Tugas: jenis dari `EventType`, kategori, prioritas bila ada, tanggal atau jam.

Edit dan Delete memilih kartu. Hapus blok lewat `ShellViewModel.deleteSelectedBlock()`.

### Add di tab Calendar

Membuka `CalendarFormSheet`, bukan `AddTaskCard`. Pilihan tipe hanya **Event** dan **Regular Schedule**. Task tidak ada di form Add. Saat mengedit item yang sudah ada, tipe terkunci pada jenis item itu.

### Radial menu

Aksi: Add, Update, Edit, Delete. Di tab Calendar, Update disembunyikan. Animasi: spring `dampingRatio = 0.75`, `Spring.StiffnessMediumLow`, kipas membesar dari hub, tombol naik dari dock, ikon berputar 90 derajat. Jangan mengganti aksi ini dengan menu navigasi.

## Arsitektur

Feature-first Clean Architecture + MVVM. Alasan dan aturan lengkap ada di `AGENTS.md`.

UI (Screen stateless) -> ViewModel (`StateFlow<XUiState>`) -> interface repository -> data.

- Repository yang dibaca banyak fitur (tugas, jadwal, kategori, pengaturan) ada di `core/domain` dan `core/data`. Semuanya mengekspos `StateFlow`, jadi tulisan dari satu layar langsung terlihat di layar lain.
- Repository milik satu fitur ada di fitur itu (contoh: `feature/auth/domain/AuthRepository.kt`).
- Use case hanya untuk aturan bisnis atau gabungan beberapa repository: `LoginUseCase`, `RegisterAccountUseCase`, `SaveSettingsUseCase`. Operasi CRUD biasa dipanggil langsung dari ViewModel.
- Tiap fitur punya modul DI sendiri di `feature/<x>/di/` (`viewModelFactory`). `AppContainer` hanya berisi repository bersama dan dibuat sekali di `EmobilizeApplication`. Tidak ada Hilt.
- `ShellViewModel` memegang tema dan mode aksi radial (Edit, Update, Delete) karena mode itu berlaku di dua tab.
- Form memakai `TaskDraft` (field `mutableStateOf`) karena teks berubah setiap ketikan. Ini pengecualian dari pola UiState.

State yang boleh di Composable: tab aktif, `menuOpen`, state animasi.

## Model

`Task` di `core/model/Task.kt`:

- `category` dan `priority` boleh null.
- `deadline` kosong berarti tidak ada tanggal.
- `range` untuk time range.
- `eventType`: Event, Task, Regular Schedule.
- `time` dan `eventEndTime` seperti "10:30 AM". Field range seperti "09:00".

`TimeBlock`: id, title, start, end (jam desimal), category. Disimpan per `LocalDate`.

## Yang tidak boleh

- Jangan edit `core/`, file Gradle, atau manifest dari tugas fitur. Minta pemilik `core/` lewat PR kecil.
- Jangan simpan tugas atau blok dari Composable.
- Jangan pindahkan `TaskDraft` ke domain.
- Jangan tambah library DI.
- Jangan ubah teks Inggris yang sudah ada (Title, Close, Departures, Route map, Nothing scheduled). Nama tab di kode tetap Tasks dan Calendar.

Cek kompilasi:

```
.\gradlew.bat :app:compileDebugKotlin :app:testDebugUnitTest --offline
```

`adb` tidak ada di PATH. Compile berhasil belum berarti UI sudah dicoba di perangkat.
