# Halaman Emobilize

Aplikasi Android (Kotlin, Jetpack Compose). Satu activity: `MainActivity` memasang `EmobilizeApp`. Data tugas, blok kalender, kategori, dan pengaturan tinggal di memori lewat repository. Data hilang saat proses aplikasi mati. Sampel awal ada di `data/repository/SampleData.kt`.

Teks antarmuka yang tampil ke pengguna berbahasa Inggris. Nama di bawah mengikuti teks itu.

## Alur yang benar-benar terbuka

```
Kerangka aplikasi
  Tasks          (tab kiri)
       Calendar       (tab kanan)
       Settings       (ikon gear)
       Add task       (radial Add di tab Tasks)
       Edit task      (radial Edit, lalu pilih kartu, lalu Edit task)
       Calendar form  (radial Add di tab Calendar, atau edit item kalender)
       Radial menu    (tombol tengah)
```

`AuthScreen` dan `ExpandableReminderCard` ada di kode, tetapi `EmobilizeApp` tidak memasangnya. Keduanya dijelaskan di bagian akhir.

## Kerangka aplikasi

File: `ui/EmobilizeApp.kt`, composable `EmobilizeApp`.

Layar punya tiga lapis yang selalu ada:

1. Bar atas: ruang kosong di kiri, tombol gear bulat 44 dp di kanan. Konten deskripsi: `Settings`. Mengetuknya menutup radial menu dan membuka Settings.
2. Isi tab: `TasksScreen` atau `CalendarScreen`.
3. Dock bawah (`BottomDock`): tab `Tasks` (ikon daftar) dan `Calendar` (ikon tanggal). Di tengah ada celah untuk tombol aksi. Latar dock memakai `navBg`.

Tab awal adalah Tasks. Pindah tab menutup radial menu dan menghapus pilihan blok kalender. Jika pindah ke Calendar saat aksi tertunda adalah Update, aksi itu dibatalkan (Update tidak ada di Calendar).

Tema mengikuti `AppSettings.themeMode`: System memakai tema sistem, Light memakai `NeutralLight`, Dark memakai `NeutralDark`. Default pengaturan adalah Light.

Tombol back sistem, dari yang paling atas:

1. Tutup Settings.
2. Tutup kartu Add.
3. Tutup form kalender.
4. Tutup sheet edit tugas.
5. Tutup radial menu.
6. Batalkan pilihan blok dan aksi tertunda (Edit, Delete, Update).

## Tasks

File: `ui/tasks/TasksScreen.kt`. State di `TasksViewModel`.

### Isi layar

- Judul `My tasks`.
- Filter: `All`, `Today`, `Tomorrow`, `Later`. Chip aktif memakai latar `background`.
- Penghitung di kanan filter: jumlah tugas selesai di daftar yang terlihat, garis miring, jumlah tugas yang terlihat. Contoh bentuk: `2 / 8`. Angka ini dihitung dari data, bukan teks tetap.
- Banner pilihan hanya saat radial Update, Edit, atau Delete sedang menunggu kartu.
- Daftar dikelompokkan, kalau kelompok itu tidak kosong:
  - `To-do`
  - `In progress`
  - `Done` (disembunyikan bila Settings `Show completed tasks` mati)
- Kosong: `No tasks in this view.`
- Tombol bawah hanya setelah satu kartu dipilih:
  - Edit: `Edit task`
  - Delete: `Delete task`
  - Update: judul `Set status`, lalu `Progress` dan `Done`

Kartu tugas tidak bisa diketuk saat tidak ada aksi tertunda. Saat ada aksi, kartu yang tidak dipilih meredup (alpha 0.6).

### Filter

`All` menampilkan setiap tugas, termasuk yang tidak punya jadwal.

`Today`, `Tomorrow`, dan `Later`:

- Tugas tanpa rentang waktu ikut filter yang tersimpan di `filterKey`.
- Tugas dengan rentang waktu ikut bila rentang itu mencakup hari itu. `Later` berarti tanggal selesai rentang masih setelah besok.

Tugas tanpa tanggal hanya masuk `All`.

Di dalam To-do dan In progress, urutan memakai jarak deadline (`deadlineRank`). Kelompok Done tidak diurut ulang.

### Kartu tugas

- Nama, maksimal 2 baris. Jika selesai: coret dan warna teks lebih redup.
- Badge prioritas (`Low`, `Medium`, `High`) hanya jika prioritas ada.
- Garis warna 4 dp di kiri hanya jika kategori ada.
- Tanggal dan jam di kanan hanya jika ada deadline atau rentang.
- Jam lewat waktu (hanya tugas `Today` yang punya jam, dan belum Done) memakai warna prioritas tinggi. Selain itu jam memakai warna primary.
- Rentang menulis tanggal `start -> end` bila beda hari, dan jam `start -> end`.

### Aksi dari radial di tab ini

| Aksi | Yang terjadi |
| --- | --- |
| Add | Membuka `AddTaskCard`. Draft direset sebagai `Task`. |
| Update | Banner `Select a card to update status`. Setelah pilih kartu: `Progress` atau `Done`. |
| Edit | Banner `Select a card to edit`. Setelah pilih kartu, tombol `Edit task` membuka `TaskFormSheet`. |
| Delete | Banner `Select a card to delete`. Setelah pilih kartu, tombol `Delete task` menghapus tugas. |

`Cancel` di banner membatalkan aksi dan pilihan.

## Add task

File: `ui/tasks/AddTaskCard.kt`. Hanya dari tab Tasks.

Kartu menempel di bawah, di atas lapisan gelap (`modalBg`). Judul langsung fokus dan keyboard terbuka. Placeholder: `Title`.

Tiga ikon 44 dp, tanpa label tertulis di samping ikon (label aksesibilitas tetap ada):

- Category
- Priority
- Schedule

Ketuk ikon yang sama untuk menutup panel pilihan itu.

- Category: chip nama kategori. Ketuk yang sama untuk mengosongkan kategori.
- Priority: `Low`, `Medium`, `High`. Ketuk yang sama untuk mengosongkan prioritas.
- Schedule: `Deadline` atau `Time range`. Ketuk mode yang sedang aktif untuk mematikan jadwal.
  - Deadline: tanggal dan jam. Teks bantuan: `Date and time are optional`.
  - Time range: baris `FROM` dan `TO`. Jika tidak valid: `End time must be after start time`.

Tombol `Add task` ada di bawah baris ikon. Tinggi 44 dp, sudut `lg`, isi `primary`, teks bold. Tombol mati (latar `muted`) selama judul kosong.

- Judul terisi dan jadwal valid: `Add task` menyimpan, lalu menutup.
- Time range aktif tetapi tidak valid: `Add task` membuka panel Schedule dan tidak menyimpan.
- `Close`, ketuk di luar kartu, atau back menutup tanpa menyimpan.

Kategori dan prioritas awal mengikuti Settings (`Default category`, `Default priority`). Keduanya boleh kosong.

## Edit task

File: `ui/tasks/TaskFormSheet.kt`. Judul sheet: `Edit Task`. Tombol simpan: `Save Changes`.

Sheet dari bawah. Ketuk luar, tanda tutup, atau back menutup tanpa menyimpan.

Field:

- Nama, placeholder `Task name...`
- Category (chip, tidak mengosongkan saat diketuk ulang)
- Priority
- Schedule: `Deadline` atau `Time range`
  - Deadline: tanggal wajib agar bisa simpan, jam opsional (`Time is optional`)
  - Time range: `FROM` / `TO`. Pesan `End time must be after start time` jika urutan salah

`Save Changes` aktif hanya jika nama terisi, dan jadwal (bila disertakan) valid. Simpan memanggil `updateTask` pada id yang sama.

## Calendar

File: `ui/calendar/CalendarScreen.kt`.

### Header

- Panah bulan. Konten deskripsi: `Previous month`, `Next month`.
- Judul bulan, pola `MMMM yyyy`, locale US. Contoh bentuk: `October 2026`.
- Segment `Month`, `Week`, `Day`. View awal mengikuti Settings. Default aplikasi: Week.

Hari pertama minggu (Month dan Week) mengikuti Settings: Monday atau Sunday. Default: Monday.

Tanggal terpilih dan bulan yang terlihat diingat selama composable hidup (`rememberSaveable`).

### Month

Kisi tanggal bulan yang terlihat. Huruf hari satu karakter (locale US). Sel di luar bulan dikosongkan. Titik kecil menandai hari yang punya tugas atau blok. Hari ini punya cincin. Hari terpilih terisi warna primary.

Di bawah kisi, agenda hari yang dipilih.

### Week

Tujuh hari di minggu tanggal aktif. Nama hari singkat. Angka hari, titik jika ada item. Ketuk hari untuk mengganti agenda. Bulan di header tidak ikut bergeser hanya karena pindah hari.

### Day

Nama hari penuh dan angka tanggal. Panah `Previous day` dan `Next day`. Tidak ada kisi. Agenda mengisi sisa layar.

### Agenda

Daftar vertikal, bukan kisi jam. Urutan: jam mulai naik. Item tanpa jam di bawah (nilai urut 48).

Kartu blok kalender:

- Judul blok
- Baris info diawali `Regular Schedule`, lalu nama kategori
- Jam `start to end`
- Garis warna kategori di kiri

Kartu tugas yang jatuh pada hari itu (`taskOccursOn`: satu tanggal deadline, atau tanggal di dalam rentang):

- Judul tugas
- Jenis dari `EventType` (`Event`, `Task`, atau `Regular Schedule`), nama kategori bila ada, prioritas bila ada
- Label waktu: jam, rentang jam, atau tanggal

Hari tanpa item: `Nothing scheduled`.

### Pilih, edit, hapus

Update tidak dipakai di tab ini. Radial Update disembunyikan, dan sisa aksi Update dibatalkan saat masuk tab.

| Aksi | Yang terjadi |
| --- | --- |
| Add | `CalendarFormSheet` baru. Tipe awal `Event`. |
| Edit | Banner `Select a calendar item to edit`. Tugas membuka form dengan isian tugas itu. Blok membuka form `Regular Schedule` terkunci, tanggal blok, jam mulai dan selesai. |
| Delete | Banner `Select a calendar item to delete`. Setelah satu tugas atau satu blok dipilih, tombol `Delete calendar item`. Tugas dihapus lewat `confirmAction`. Blok dihapus lewat `deleteSelectedBlock`. |

Tanpa aksi Edit atau Delete, ketuk kartu tugas tidak melakukan apa-apa. Ketuk blok tetap memilih atau membatalkan pilihan blok. Kartu yang bukan pilihan meredup saat mode pilih aktif.

`Cancel` membersihkan pilihan tugas, pilihan blok, dan aksi tertunda.

## Form kalender

File: `ui/tasks/CalendarFormSheet.kt`.

Sheet bawah, pegangan abu-abu di atas, judul field placeholder `Add title`. Ketuk luar atau `Cancel` menutup tanpa menyimpan.

Saat menambah, tipe yang bisa dipilih hanya `Event` dan `Regular Schedule`. `Task` tidak ditawarkan. Saat mengedit, tipe terkunci pada jenis item itu (termasuk `Task` jika item yang dibuka memang tugas).

### Event

- `Date & Time`: tanggal, `Start`, `End` (jam boleh dikosongkan)
- `Category`

Simpan butuh judul dan tanggal. Disimpan sebagai tugas, bukan blok jam.

### Task

Hanya muncul saat mengedit tugas yang sudah bertipe Task.

- Category
- Priority: `Low`, `Medium`, `High`
- Schedule: `Deadline` (`Time is optional`) atau `Time range` (`FROM`, `TO`, pesan `End time must be after start time`)

### Regular Schedule

- `FROM` dan `TO` pada satu tanggal (editor blok mengisi tanggal yang sama untuk mulai dan selesai)
- Validasi rentang yang sama

Simpan menulis `TimeBlock`: judul, jam desimal, kategori. Jika kategori kosong, kategori Work dipakai. Id baru memakai waktu saat ini dalam milidetik. Id lama dipertahankan saat edit.

Tombol simpan: `Save` untuk item baru, `Save Changes` untuk edit. Warna tombol mengikuti aksi radial (Add atau Edit). Tombol mati sampai `canSubmitCalendar` benar.

## Settings

File: `ui/settings/SettingsScreen.kt`. Layar penuh di atas tab, bukan sheet.

Header: panah `Back` dan judul `Settings`.

Perubahan disimpan di draft lokal. `Save Changes` hanya muncul jika ada perubahan, dan hanya bisa diketuk jika setiap nama kategori valid.

Back atau panah saat ada perubahan membuka dialog:

- Judul `Discard changes?`
- Isi `Your unsaved settings changes will be lost.`
- `Discard` menutup Settings
- `Keep editing` kembali ke form

Tanpa perubahan, back langsung menutup.

### Appearance

`Theme`. Teks bantuan: `Choose how Emobilize looks`. Pilihan: `System`, `Light`, `Dark`.

### Tasks

- `Default category`. Bantuan: `Preselect a category when adding a task`. Pilihan `None` plus setiap kategori.
- `Default priority`. Bantuan: `Preselect a priority when adding a task`. Pilihan `None`, `Low`, `Medium`, `High`.
- `Show completed tasks`. Bantuan: `Keep the Done section visible on the Tasks screen`. Switch.

### Calendar

- `Default view`. Bantuan: `Choose the view shown when you open Calendar`. Urutan chip: `Week`, `Day`, `Month`.
- `Week starts on`. Bantuan: `Use the same first day in Month and Week views`. Pilihan `Monday`, `Sunday`.

### Categories

Teks: `Edit up to 3 categories and choose a color for each.` Angka 3 adalah `Category.Limit`.

Default: Study, Work, Health.

Baris tertutup: titik warna, nama, ikon edit (`Edit <nama>`). Baris terbuka: field nama (placeholder `Category name`) dan lima pilihan warna.

Warna (nilai ARGB):

| Hex | Pemakaian awal |
| --- | --- |
| `#E76E50` | Study |
| `#2A9D90` | Health |
| `#274754` | |
| `#E8C468` | |
| `#F4A462` | Work |

Nama wajib 1 sampai 24 karakter, tanpa karakter pemisah `\u001E` dan `\u001F`. Jika tidak valid: `Category names must be 1–24 characters.` dan `Save Changes` mati.

Menyimpan menulis kategori yang berubah, lalu menulis `AppSettings`, lalu memuat ulang tugas dan jadwal.

## Radial menu dan dock

File: `ui/dock/RadialMenu.kt`, `ui/dock/RadialDock.kt`.

Tombol tengah membuka kipas. Aksi:

| Aksi | Sudut dasar | Warna (dari tema) |
| --- | --- | --- |
| Add | -87° | hijau |
| Update | 0° | biru |
| Edit | sudut tampilan -30° di kedua tab | `actionEdit` |
| Delete | 87° | `danger` |

Di Calendar, Update tidak digambar. Sudut Update di Tasks digeser ke 30°, Edit ke -30°.

Animasi buka dan tutup: spring `dampingRatio = 0.75`, `Spring.StiffnessMediumLow`. Kipas membesar dari hub, tombol naik dari dock, ikon berputar 90 derajat. Ada haptic saat pilihan berubah. Ketuk luar atau back menutup menu tanpa memilih aksi.

Dock tidak mengganti halaman lain. Ia hanya memilih Tasks atau Calendar.

## Yang ada di kode tetapi tidak dipasang

### Auth

File: `ui/auth/AuthScreen.kt`, `AuthViewModel`. `MainActivity` tidak membuat ViewModel ini dan tidak menampilkan layarnya.

Dua halaman dalam satu composable:

**Login** (`AuthPage.Login`)

- Judul `Emobilize`
- Subjudul `Log in to continue`
- Field `Email`, `Password` (tersembunyi)
- Tombol `Log in`
- Baris `Need an account?` lalu `Register`

**Register**

- Subjudul `Create an account`
- Field `Name`, `Email`, `Password`, `Confirm password`
- Tombol `Create account`
- Baris `Already have an account?` lalu `Log in`
- Back kembali ke Login

Pesan gagal, dari `AuthViewModel`:

| Kondisi | Teks |
| --- | --- |
| Nama tidak valid | `Enter your name` |
| Email tidak valid | `Enter a valid email` |
| Kata sandi di bawah 6 karakter | `Use at least 6 characters` |
| Email sudah terdaftar | `That email is already registered` |
| Login salah | `Email or password is incorrect` |
| Konfirmasi tidak sama | `Passwords do not match` |

Sukses hanya mengubah `signedIn` di ViewModel. Tidak ada navigasi ke Tasks.

### Kartu pengingat

File: `ui/notifications/ExpandableReminderCard.kt`. Tidak diimpor oleh layar mana pun.

Kartu untuk satu `Task`. Tertutup: logo, label `Emobilize`, nama tugas, jam. Baris waktu saat ini adalah teks tetap `Due in 30 mins • ` ditambah `task.time`, atau `Scheduled time` jika jam kosong. Angka 30 menit tidak dihitung dari deadline.

Ketuk kartu memperluas. Chevron berputar 180 derajat. Konten deskripsi: `Expand` atau `Collapse`.

Bagian bawah yang terbuka:

- `Mark as done` memanggil `onMarkDone`, lalu menutup
- `Reschedule` memanggil `onReschedule`, lalu menutup

Tidak ada pemanggil, jadi kedua aksi itu belum terhubung ke penyimpanan.

## Model yang dipakai semua halaman

`Task` (`domain/model/Task.kt`): id, nama, kategori (boleh kosong), deadline (string kosong berarti tidak ada tanggal), jam tampilan seperti `10:30 AM` (boleh kosong), `filterKey`, prioritas (boleh kosong), `range` (tanggal dan jam mulai-selesai, format jam `09:00`), `eventType`, `eventEndTime`.

`TimeBlock` (`domain/model/Calendar.kt`): id, judul, start, end (jam desimal), kategori. Disimpan per `LocalDate`.

`AppSettings`: themeMode, defaultCalendarView, weekStart, defaultCategoryId, defaultPriority, showCompletedTasks.

Kategori tidak ditambah dari UI. Yang bisa diubah adalah nama dan warna dari tiga slot yang sudah ada.
