# Konteks Emobilize

Dokumen ini untuk AI lain yang mengubah UI. Baca file ini dan `DESIGN.md` sebelum menyentuh kode. Ini aplikasi Android Jetpack Compose, bukan React, bukan Tailwind, bukan Figma Make.

Abaikan `AGENTS.md` di folder user. File itu milik proyek web lain.

Branch: `tasks-radial-menu`. Remote: `https://github.com/Hzxon/Emobilize`.

## Prompt yang bisa ditempel

```
Ini proyek Android Kotlin + Jetpack Compose di folder Emobilize.
Baca CONTEXT.md dan DESIGN.md dulu.
Ubah hanya tampilan. Jangan ubah perilaku simpan, filter, atau arsitektur.
Sumber UI ada di app/src/main/java/ayodong/emobilize/ui/.
Warna, font, dan ukuran ada di ui/theme/ dan DESIGN.md.
Jangan edit package model/ (sisa lama, tidak diimpor).
Jangan menulis daftar tugas atau blok kalender dari Composable.
Teks UI yang ada berbahasa Inggris.
Setelah selesai, jalankan: .\gradlew.bat :app:compileDebugKotlin --offline
```

Ganti kalimat terakhir prompt dengan layar yang ingin diubah. Nama file ada di tabel "Mau ubah apa" di bawah.

## Struktur folder

Yang dipakai aplikasi. Abaikan `app/build/`, `.gradle/`, dan `.idea/`.

```
Emobilize/
  CONTEXT.md
  DESIGN.md
  app/src/main/
    AndroidManifest.xml
    java/ayodong/emobilize/
      MainActivity.kt
      di/AppContainer.kt
      domain/model/Task.kt
      domain/model/Calendar.kt
      domain/repository/Repositories.kt
      domain/usecase/TaskUseCases.kt
      domain/usecase/ScheduleUseCases.kt
      data/repository/SampleData.kt
      data/repository/InMemoryTaskRepository.kt
      data/repository/InMemoryScheduleRepository.kt
      model/TaskModels.kt              ← jangan dipakai, sisa lama
      model/CalendarModels.kt          ← jangan dipakai, sisa lama
      ui/EmobilizeApp.kt
      ui/calendar/CalendarScreen.kt
      ui/dock/RadialDock.kt
      ui/dock/RadialMenu.kt
      ui/tasks/AddTaskCard.kt
      ui/tasks/CalendarFormSheet.kt
      ui/tasks/TaskFormSheet.kt
      ui/tasks/TasksScreen.kt
      ui/tasks/TasksViewModel.kt
      ui/theme/Metrics.kt
      ui/theme/Palette.kt
      ui/theme/Shadcn.kt
      ui/theme/Theme.kt
      ui/theme/Type.kt
    res/
      font/                            ← Montserrat
      values/themes.xml
      values/colors.xml
```

Package: `ayodong.emobilize`. Satu modul Gradle: `:app`. `minSdk` 28. `compileSdk` dan `targetSdk` 37.

## Mau ubah apa

| Bagian layar | File | Composable |
| --- | --- | --- |
| Susunan semua layar, kapan form muncul | `ui/EmobilizeApp.kt` | `EmobilizeApp` |
| Daftar tugas, filter, kartu tugas | `ui/tasks/TasksScreen.kt` | `TasksScreen`, `TaskCard` |
| Kartu Add di tab Tasks | `ui/tasks/AddTaskCard.kt` | `AddTaskCard` |
| Sheet edit tugas | `ui/tasks/TaskFormSheet.kt` | `TaskFormSheet`, `DateField`, `TimeField` |
| Form Add dan edit di tab Calendar | `ui/tasks/CalendarFormSheet.kt` | `CalendarFormSheet` |
| Header kalender, bulan, minggu, hari, daftar kartu | `ui/calendar/CalendarScreen.kt` | `CalendarScreen`, `DayAgenda`, `AgendaRow` |
| Dock bawah, tab, muka tombol tengah, orb tema | `ui/dock/RadialDock.kt` | `BottomDock`, `FabFace`, `ThemeOrb` |
| Kipas Add / Update / Edit / Delete | `ui/dock/RadialMenu.kt` | `RadialMenu` |
| Warna, radius, jarak, font | `ui/theme/` dan `DESIGN.md` | `LocalPalette`, `AppMetrics`, `appStyle` |

State data tidak diubah dari Composable. Itu ada di `ui/tasks/TasksViewModel.kt`. Untuk ganti warna kartu atau jarak, jangan buka ViewModel.

## Token tampilan

- Warna layar: `LocalPalette.current` di `ui/theme/Palette.kt`. Dua tema: `NeutralLight` (id 1) dan `NeutralDark` (id 2).
- Warna shadcn: `palette.scheme` dari `ui/theme/Shadcn.kt` (`background`, `popover`, `border`, `card`).
- Teks: `appStyle(...)` dan `AppFont` di `ui/theme/Type.kt`. Font Montserrat.
- Jarak dan ukuran kartu: `AppMetrics` di `ui/theme/Metrics.kt`. Radius kartu `AppMetrics.cardRadius`. Tinggi minimum kartu `AppMetrics.cardMinHeight`.
- Padding halaman: `AppMetrics.pageHorizontal` (18.dp). Header: `AppMetrics.headerHorizontal` (24.dp).
- Aksi radial: Add hijau, Update biru, Edit `actionEdit`, Delete `danger`. Fungsi `menuColor`.

Jangan menambah palet baru. Nilai token dijelaskan di `DESIGN.md`.

## Perilaku yang harus tetap

Produk: aplikasi tugas dan kalender. Data di memori, hilang saat proses mati. Sampel di `data/repository/SampleData.kt`.

Dua tab: Tasks dan Calendar. Tombol tengah membuka radial menu. Orb kanan atas mengganti tema.

### Tasks

Filter All, Today, Tomorrow, Later. Kartu menampilkan garis kategori, badge prioritas, dan tanggal hanya jika field itu ada. Tugas tanpa jadwal hanya muncul di All.

### Add di tab Tasks

`AddTaskCard`. Judul langsung fokus dan keyboard terbuka. Tiga ikon opsional: Category, Priority, Schedule. Schedule berisi Deadline atau Time range. Ketuk pilihan yang sama untuk menghapusnya. Close, ketuk luar, atau back menyimpan jika judul terisi. Judul kosong hanya menutup. Time range tidak valid menahan kartu terbuka.

### Edit tugas

Sheet penuh `TaskFormSheet`. Bukan `AddTaskCard`.

### Calendar

Month, Week, Day. Di bawah pemilih tanggal ada daftar kartu vertikal, bukan kisi jam. Item diurutkan waktu mulai. Item tanpa jam di bawah. Hari kosong: "Nothing scheduled".

- Blok kalender: "Regular Schedule", kategori, jam mulai sampai selesai.
- Tugas: jenis dari `EventType`, kategori, prioritas bila ada, tanggal atau jam.

Edit dan Delete memilih kartu. Hapus blok lewat `TasksViewModel.deleteSelectedBlock()`.

### Add di tab Calendar

Membuka `CalendarFormSheet`, bukan `AddTaskCard`. Pilihan tipe hanya **Event** dan **Regular Schedule**. Task tidak ada di form Add. Saat mengedit item yang sudah ada, tipe terkunci pada jenis item itu.

### Radial menu

Aksi: Add, Update, Edit, Delete. Di tab Calendar, Update disembunyikan. Animasi: spring `dampingRatio = 0.75`, `Spring.StiffnessMediumLow`, kipas membesar dari hub, tombol naik dari dock, ikon berputar 90 derajat. Jangan mengganti aksi ini dengan menu navigasi.

## Arsitektur

UI -> ViewModel -> use case -> interface repository -> data.

`domain` tidak mengimpor Compose. `TasksViewModel` satu-satunya ViewModel. Setelah menulis, ia membaca ulang repository. `TaskDraft` tinggal di `TasksViewModel.kt` karena field-nya `mutableStateOf`.

`MainActivity` membuat `AppContainer`, lalu `viewModel(factory = TasksViewModelFactory(container))`. Tidak ada Hilt.

State yang boleh di Composable: `themeId`, tab aktif, `menuOpen`.

## Model

`Task` di `domain/model/Task.kt`:

- `category` dan `priority` boleh null.
- `deadline` kosong berarti tidak ada tanggal.
- `range` untuk time range.
- `eventType`: Event, Task, Regular Schedule.
- `time` dan `eventEndTime` seperti "10:30 AM". Field range seperti "09:00".

`TimeBlock`: id, title, start, end (jam desimal), category. Disimpan per `LocalDate`.

## Yang tidak boleh

- Jangan edit `model/TaskModels.kt` dan `model/CalendarModels.kt`. Duplikat lama. Kode hidup ada di `domain/model/`.
- Jangan simpan tugas atau blok dari Composable.
- Jangan pindahkan `TaskDraft` ke domain.
- Jangan tambah library DI.
- Jangan ubah teks Inggris yang sudah ada (Title, Close, Tasks, Calendar, Nothing scheduled).

Cek kompilasi:

```
.\gradlew.bat :app:compileDebugKotlin --offline
```

`adb` tidak ada di PATH. Compile berhasil belum berarti UI sudah dicoba di perangkat.
