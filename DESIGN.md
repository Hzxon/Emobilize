# Emobilize Design System

Design system aplikasi Emobilize: **Departures**. Metafornya papan keberangkatan stasiun. Tugas adalah keberangkatan, kategori adalah jalur (line), kalender adalah peta rute, dan detail tugas adalah tiket. Tipografi memakai Inter Tight dan IBM Plex Mono. Semua sudut persegi.

Desain sumber: artifact "Emobilize Departures" (artboard Departures, Route map, Ticket, Last service).

Sumber di kode:

- `app/src/main/java/ayodong/emobilize/core/ui/theme/Shadcn.kt`
- `app/src/main/java/ayodong/emobilize/core/ui/theme/Palette.kt`
- `app/src/main/java/ayodong/emobilize/core/ui/theme/Type.kt`
- `app/src/main/java/ayodong/emobilize/core/ui/theme/Metrics.kt`
- `app/src/main/java/ayodong/emobilize/core/ui/theme/Theme.kt`

Ada dua palet, Departures Light dan Departures Dark. Nama di kode tetap `NeutralLight` dan `NeutralDark`. Pilihan tema ada di Settings dengan opsi System, Light, dan Dark; ikon gear di header membuka Settings.

| Tema | Kode | Kapan dipakai |
| --- | --- | --- |
| Departures Light | `NeutralLight` | Default. Dinding stasiun abu-abu, panel putih, tinta hitam, sinyal kuning di atas hitam. |
| Departures Dark | `NeutralDark` | Stasiun malam. Latar hampir hitam, aksi utama kuning. |

---

## Warna

Token tetap berbentuk shadcn (`ShadcnColors`), tapi nilainya hex Departures.

### Departures Light

| Token | Hex | Pemakaian |
| --- | --- | --- |
| background | `#EEF0F2` | Latar halaman |
| foreground | `#111214` | Teks utama, garis kontrol |
| card | `#FFFFFF` | Panel, tombol outline |
| popover | `#FFFFFF` | Sheet |
| primary | `#111214` | Filter aktif, tombol tengah, tab aktif |
| primary-foreground | `#FFD400` | Teks dan ikon di atas primary |
| secondary | `#FFFFFF` | Tombol sekunder |
| muted | `#E3E6EA` | Stasiun kosong, bidang tenang |
| muted-foreground | `#4A4D55` | Label, tab nonaktif |
| accent | `#FFD400` | Sorotan sinyal |
| destructive | `#C4161C` | Hapus, error |
| destructive-foreground | `#FFFFFF` | Teks di atas destructive |
| border, input | `#111214` | Garis kartu, field, kontrol |
| ring | `#FFD400` | Fokus |
| chart-1 | `#D6001C` | Status progress |
| chart-2 | `#007A4D` | Status done, prioritas rendah |
| chart-3 | `#5C2D91` | Aksi update (palet) |
| chart-4 | `#B35900` | Aksi edit |
| chart-5 | `#0057B8` | Cadangan |

### Departures Dark

| Token | Hex | Pemakaian |
| --- | --- | --- |
| background | `#111214` | Latar halaman |
| foreground | `#FFFFFF` | Teks utama |
| card, popover | `#1B1D21` | Panel, sheet |
| primary | `#FFD400` | Aksi utama |
| primary-foreground | `#111214` | Teks di atas primary |
| secondary, muted | `#22242A` | Bidang tenang |
| muted-foreground | `#A9ACB3` | Label |
| destructive | `#FF7A5C` | Hapus, error |
| destructive-foreground | `#111214` | Teks di atas destructive |
| border, input | `#5C5F66` | Garis |
| ring | `#FFD400` | Fokus |
| chart-1 … chart-5 | `#FF5A6A`, `#3DDC84`, `#B18CFF`, `#FFB020`, `#5AA2FF` | Sama dengan Light |

### Papan dan sinyal

Papan keberangkatan dan tiket tetap gelap/putih di kedua tema, seperti layar stasiun sungguhan. Token ada di `AppPalette`.

| Token | Hex | Pemakaian |
| --- | --- | --- |
| board | `#111214` (Dark: `#000000`) | Latar papan, scrim tiket |
| boardRule | `#2A2C31` | Garis antar baris papan |
| boardText | `#FFFFFF` | Nama tugas di papan |
| boardMuted | `#A9ACB3` | Header kolom, baris kedua |
| boardGone | `#8A8D93` | DEPARTED, jalur tanpa kategori |
| boardBoarding | `#3DDC84` | BOARDING, swipe selesai |
| boardBoardingRow | `#1A2A20` | Latar baris yang sedang berjalan |
| boardDelayed | `#FF7A5C` | DELAYED, swipe hapus |
| signal | `#FFD400` | Jam, strip Next, banner pilihan, YOU ARE HERE |
| signalInk | `#111214` | Teks di atas signal |

### Lapisan

| Token | Light | Dark | Pemakaian |
| --- | --- | --- | --- |
| scrim | `#000000` 40% | `#000000` 62% | Overlay menu radial dan sheet |
| nav | background 96% | background 97% | Cadangan; tidak ada bar navigasi |
| shadow | transparan | transparan | Departures tidak memakai bayangan |
| text-subtle | muted-foreground 62% | muted-foreground 62% | Teks redup |

Teks di atas warna isi dipilih dengan luminance. Di atas 0.72 pakai `ink` (gelap). Di bawahnya pakai `on-dark` (`#FFFFFF`).

---

## Warna jalur (kategori)

Kategori menyimpan ARGB jalur langsung, salah satu dari `Category.lineColors` (12 warna). Warna tampilan diambil lewat `lineColor(argb)` di `Palette.kt`; nilai lama dari versi sebelumnya dipetakan lewat `Category.legacyLineColors`. Selalu pakai `palette.categoryColor(category)` atau `lineColor(...)`, jangan `Color(category.color)`.

Jumlah kategori tidak dibatasi. Dua kategori boleh berbagi warna; yang membedakan adalah kode jalur (`Category.code`, 1–2 huruf, unik) yang digambar di dalam lingkaran stasiun dan di legenda.

| Warna jalur | Nama | Default |
| --- | --- | --- |
| `#D6001C` | merah | Study (S) |
| `#0057B8` | biru | Work (W) |
| `#007A4D` | hijau | Health (H) |
| `#5C2D91` | ungu | |
| `#B35900` | oranye tua | |
| `#E6007E` | pink | |
| `#00858F` | teal | |
| `#7A4B1E` | cokelat | |
| `#1B2A6B` | navy | |
| `#6E9A00` | lime | |
| `#A0007A` | magenta | |
| `#5A5A5A` | abu | |

Teks di badge jalur selalu putih. Tugas tanpa kategori memakai jalur abu `boardGone` dengan huruf "X".

## Warna produk

| Peran | Token |
| --- | --- |
| Status todo, ON TIME, ANY TIME | signal |
| Status progress, BOARDING | boardBoarding |
| Status done, DEPARTED | boardGone, dicoret |
| Terlambat, DELAYED | boardDelayed |
| Prioritas low | chart-2 |
| Prioritas medium | `#EAB308` |
| Prioritas high | `#FF6B6B` |
| Aksi add | `#007A4D` |
| Aksi update | `#0057B8` |
| Aksi edit | chart-4 |
| Aksi delete | destructive |

Keadaan terpilih (mode Edit, Update, Delete): baris diberi garis 2 dp warna aksi dan isi warna aksi 14–28%. Baris lain redup 60%.

---

## Tipografi

| Keluarga | Kode | File | Pemakaian |
| --- | --- | --- | --- |
| Inter Tight | `AppFont`, `appStyle(...)` | `res/font/inter_tight.ttf` (variable, sumbu `wght`) | Semua teks |
| IBM Plex Mono | `MonoFont`, `monoStyle(...)` | `ibm_plex_mono_medium.ttf`, `ibm_plex_mono_semibold.ttf` | Jam, kode tiket, status papan, fakta tiket |

Kedua font berlisensi SIL OFL, diambil dari repo `google/fonts`. `monoStyle` memakai angka tabular (`tnum`).

| Peran | Ukuran | Berat | Catatan |
| --- | --- | --- | --- |
| Jam papan (header Departures) | 26 sp mono | SemiBold | letter-spacing −0.5 sp, `tnum`, di pita 56 dp dengan nama layar dan tanggal 11 sp UPPERCASE |
| Judul layar (Route map) | 24 sp | ExtraBold | letter-spacing −0.5 sp, satu baris 44 dp dengan kicker 11 sp di bawahnya |
| Judul tiket | 30 sp | ExtraBold | letter-spacing −1 sp |
| Judul hari | 18 sp | ExtraBold | |
| Nama tugas | 15–16 sp | SemiBold–Bold | |
| Label kontrol, tab, tombol | 12–15 sp | Bold–ExtraBold | UPPERCASE, letter-spacing 1 sp |
| Kicker minggu (WEEK 44 · OCT 2026) | 11 sp | Bold | UPPERCASE, letter-spacing 1 sp |
| Header kolom papan, label fakta | 10–11 sp | Bold | UPPERCASE, letter-spacing 1.5 sp |
| Jam papan | 15 sp mono | SemiBold | |
| Status papan | 11 sp mono | SemiBold | letter-spacing 0.5 sp |

Skala `ShadcnType` (xs sampai 4xl) tetap ada untuk form dan Settings.

---

## Radius

Semua token `ShadcnRadius` bernilai 0 dp. Sudut persegi di kartu, tombol, input, sheet, dan chip. Bentuk bulat hanya untuk badge jalur dan titik stasiun. Stempel tiket persegi, diputar −6°.

---

## Jarak

| Token | Nilai |
| --- | --- |
| px | 1 dp |
| 1 | 4 dp |
| 2 | 8 dp |
| 3 | 12 dp |
| 4 | 16 dp |
| 5 | 20 dp |
| 6 | 24 dp |
| 8 | 32 dp |
| 10 | 40 dp |

Jarak layar yang sudah dipakai (`AppMetrics`):

| Token | Nilai | Pemakaian |
| --- | --- | --- |
| headerHorizontal | 20 dp | Header, jam, judul |
| pageHorizontal | 20 dp | Papan, filter, peta rute |
| cardMinHeight | 68 dp | Kartu form |
| actionHeight | 44 dp | Tombol aksi |
| navHeight | 80 dp | Ruang kosong di bawah konten untuk tombol mengambang |
| fabSize | 80 dp | Area sentuh tombol mengambang (persegi terlihat 64 dp) |
| fabCenterFromBottom | 40 dp | Pusat tombol mengambang |
| nodeSize | 48 dp | Item menu radial |

Garis kontrol 2 dp `foreground`. Tombol utama tinggi 52–56 dp. Target sentuh minimal 44 dp.

---

## Komponen

### Header

- Tidak ada baris header bersama. Tiap tab menggambar header sendiri:
  - Tasks (papan jam): satu pita selebar layar, tinggi 56 dp (`boardHeaderHeight`), isi `board`. Jam mono 26 sp warna `signal`, lalu "DEPARTURES" 11 sp ExtraBold `boardText` di atas tanggal "MON 26 OCT" 11 sp `boardMuted`. Tombol Reminders dan Settings di tepi kanan pita, lebar 44 dp setinggi pita, tanpa kotak dan tanpa garis pemisah, ikon 24 dp `boardText`.
  - Calendar: satu baris 44 dp, "Route map" 24 sp di atas minggu 11 sp, lalu tombol TODAY bila hari lain dipilih. Tombol header persegi 44 dp, isi `card`, garis 2 dp `foreground`.
- `MainShell` mengirim tombolnya lewat parameter `headerActions`: varian papan ke Tasks, varian kotak ke Calendar.

### Tombol mengambang (`RadialMenu`, `FabFace`)

- Tidak ada bar navigasi bawah. Satu tombol persegi mengambang di tengah bawah.
- Persegi 64 dp, isi `signal`, garis 2 dp `signalInk`. Area sentuh 80 dp. Saat dial terbuka, persegi berputar 45° menjadi berlian 54 dp dengan tanda tutup tegak.
- Ikon menunjuk layar tujuan: kalender di Departures, daftar di Route map. Saat kipas terbuka: ikon tutup.
- Ketuk: pindah antara Departures dan Route map.
- Tahan atau seret: buka menu radial (Add, Update, Edit, Delete). Lepas di sektor untuk memilih.
- TalkBack: aksi klik "Go to Route map" / "Go to Departures", aksi tahan "Open actions".

### Menu radial: Signal dial (`RadialMenu`)

- Selalu gelap seperti papan, di kedua tema. Warna aksi khusus dial: Add `#1FA35C`, Edit `#E08A00`, Update `#3B82F6`, Delete `#FF5A4E`.
- Irisan menyatu tanpa celah (`dialSlices`): batasnya di titik tengah antar aksi, irisan terluar 28° di luar aksinya. Radius dalam 54 dp, luar 138 dp.
- Tiap irisan: isi `#1B1D21`, tepi luar 8 dp warna aksi, ikon warna aksi, label mono 11 sp putih. Sambungan antar irisan garis gelap 2 dp. Tidak ada garis tepi putih.
- Cincin tick di luar dial: tiap 6°, lebih panjang dan terang tiap 30°.
- Jarum `signal` dari hub ke irisan yang disorot, diam menunjuk ke atas, bergerak dengan spring.
- Papan tujuan di atas dial: "SLIDE TO SELECT", lalu "→ ADD" dengan isi `signal`.
- Irisan disorot: isi warna aksi, memanjang 14 dp keluar, tepi `signal`; irisan lain redup 60%.
- Pilihan memakai irisan di bawah jari, bukan sudut terdekat. Di Calendar (tanpa Update) Edit pindah ke atas agar tiga irisan seimbang.

### Filter dan toggle tersegmen

- Garis luar 2 dp `foreground`, pemisah 2 dp
- Aktif: isi `primary`, teks `primary-foreground`
- Diam: isi `card`, teks `foreground`

### Papan keberangkatan (Tasks)

- Kolom TIME 52 dp, DESTINATION fleksibel, LN 30 dp, STATUS 78 dp, jarak 10 dp
- Baris min 48 dp, garis bawah 1 dp `boardRule`
- Badge jalur bulat 26 dp dengan huruf awal kategori
- Strip bawah `signal`: "Next: tugas · jalur · waktu tunggu", atau "N of M departed"
- Semua tugas di filter selesai: panel kuning "The last service has left."

### Peta rute (Calendar)

- Minggu: tujuh stasiun pada garis 8 dp. Hari terpilih bulat 52 dp `signal`, hari lain 28 dp, hari kosong isi `muted`
- Bulan: sel persegi 44 dp; terpilih isi `primary`, hari ini garis 2 dp
- Agenda: kolom jam mono 48 dp, rel 8 dp warna jalur, titik stasiun 20 dp
- Penanda YOU ARE HERE di antara stop, hanya untuk hari ini
- Legenda jalur untuk kategori yang ada di hari itu

### Tiket (detail tugas)

- Scrim `board` 96%, tautan BOARD dan kode `TKT 0006 · 26 OCT`
- Kertas putih dengan tinta `#111214` di kedua tema
- Pita atas warna jalur, FROM/TO, judul, deskripsi, enam fakta mono
- Perforasi putus-putus, barcode dekoratif, stempel status: ON TIME, DELAYED, BOARDING, ARRIVED
- Tombol EDIT THIS TICKET (`signal`) dan CLOSE (outline)

### Banner pilihan

- Isi `signal`, garis 2 dp `signalInk`, kotak 12 dp warna aksi, tombol CANCEL

### Input dan sheet

- Sudut persegi, garis 1 dp `input` (hitam di Light)
- Garis tidak valid: `destructive`
- Isi sheet `popover`
