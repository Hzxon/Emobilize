# Emobilize Design System

Design system aplikasi Emobilize. Token warna diambil dari registry shadcn `@shadcn/theme-neutral`. Tipografi memakai Montserrat. Satuan layout mengikuti skala Tailwind yang dipakai shadcn.

Sumber di kode:

- `app/src/main/java/ayodong/emobilize/ui/theme/Shadcn.kt`
- `app/src/main/java/ayodong/emobilize/ui/theme/Palette.kt`
- `app/src/main/java/ayodong/emobilize/ui/theme/Type.kt`
- `app/src/main/java/ayodong/emobilize/ui/theme/Metrics.kt`
- `app/src/main/java/ayodong/emobilize/ui/theme/Theme.kt`

Ada dua palet, Neutral Light dan Neutral Dark. Pilihan tema ada di Settings dengan opsi System, Light, dan Dark; ikon gear di header membuka Settings.

| Tema | Kode | Kapan dipakai |
| --- | --- | --- |
| Neutral Light | `NeutralLight` | Default. Status bar dan navigation bar berwarna gelap di atas latar terang. |
| Neutral Dark | `NeutralDark` | Latar hampir hitam. Status bar dan navigation bar berwarna terang. |

---

## Warna

Nilai disimpan sebagai HSL, sama seperti registry shadcn. Hex di tabel adalah hasil konversi sRGB.

### Neutral Light

| Token | HSL | Hex | Pemakaian |
| --- | --- | --- | --- |
| background | `0 0% 100%` | `#FFFFFF` | Latar halaman |
| foreground | `0 0% 3.9%` | `#0A0A0A` | Teks utama |
| card | `0 0% 100%` | `#FFFFFF` | Permukaan kartu |
| card-foreground | `0 0% 3.9%` | `#0A0A0A` | Teks di atas kartu |
| popover | `0 0% 100%` | `#FFFFFF` | Sheet dan popover |
| popover-foreground | `0 0% 3.9%` | `#0A0A0A` | Teks di atas sheet |
| primary | `0 0% 9%` | `#171717` | Aksi utama, tab aktif, filter aktif |
| primary-foreground | `0 0% 98%` | `#FAFAFA` | Teks di atas primary |
| secondary | `0 0% 96.1%` | `#F5F5F5` | Tombol sekunder, tombol tutup |
| secondary-foreground | `0 0% 9%` | `#171717` | Teks di atas secondary |
| muted | `0 0% 96.1%` | `#F5F5F5` | Bidang isi yang tenang |
| muted-foreground | `0 0% 45.1%` | `#737373` | Label, placeholder, tab nonaktif |
| accent | `0 0% 96.1%` | `#F5F5F5` | Sorotan netral |
| accent-foreground | `0 0% 9%` | `#171717` | Teks di atas accent |
| destructive | `0 84.2% 60.2%` | `#EF4444` | Hapus, error, prioritas tinggi |
| destructive-foreground | `0 0% 98%` | `#FAFAFA` | Teks di atas destructive |
| border | `0 0% 89.8%` | `#E5E5E5` | Garis kartu dan sheet |
| input | `0 0% 89.8%` | `#E5E5E5` | Garis field |
| ring | `0 0% 3.9%` | `#0A0A0A` | Fokus |
| chart-1 | `12 76% 61%` | `#E76E50` | Study, status progress |
| chart-2 | `173 58% 39%` | `#2A9D90` | Health, status selesai, prioritas rendah |
| chart-3 | `197 37% 24%` | `#274754` | University, aksi update |
| chart-4 | `43 74% 66%` | `#E8C468` | Personal, prioritas sedang, aksi edit |
| chart-5 | `27 87% 67%` | `#F4A462` | Work |

### Neutral Dark

| Token | HSL | Hex | Pemakaian |
| --- | --- | --- | --- |
| background | `0 0% 3.9%` | `#0A0A0A` | Latar halaman |
| foreground | `0 0% 98%` | `#FAFAFA` | Teks utama |
| card | `0 0% 3.9%` | `#0A0A0A` | Permukaan kartu |
| card-foreground | `0 0% 98%` | `#FAFAFA` | Teks di atas kartu |
| popover | `0 0% 3.9%` | `#0A0A0A` | Sheet dan popover |
| popover-foreground | `0 0% 98%` | `#FAFAFA` | Teks di atas sheet |
| primary | `0 0% 98%` | `#FAFAFA` | Aksi utama |
| primary-foreground | `0 0% 9%` | `#171717` | Teks di atas primary |
| secondary | `0 0% 14.9%` | `#262626` | Tombol sekunder |
| secondary-foreground | `0 0% 98%` | `#FAFAFA` | Teks di atas secondary |
| muted | `0 0% 14.9%` | `#262626` | Bidang isi yang tenang |
| muted-foreground | `0 0% 63.9%` | `#A3A3A3` | Label dan teks sekunder |
| accent | `0 0% 14.9%` | `#262626` | Sorotan netral |
| accent-foreground | `0 0% 98%` | `#FAFAFA` | Teks di atas accent |
| destructive | `0 62.8% 30.6%` | `#7F1D1D` | Hapus dan error |
| destructive-foreground | `0 0% 98%` | `#FAFAFA` | Teks di atas destructive |
| border | `0 0% 14.9%` | `#262626` | Garis kartu dan sheet |
| input | `0 0% 14.9%` | `#262626` | Garis field |
| ring | `0 0% 83.1%` | `#D4D4D4` | Fokus |
| chart-1 | `220 70% 50%` | `#2662D9` | Study, status progress |
| chart-2 | `160 60% 45%` | `#2EB88A` | Health, status selesai, prioritas rendah |
| chart-3 | `30 80% 55%` | `#E88C30` | University, aksi update |
| chart-4 | `280 65% 60%` | `#AF57DB` | Personal, prioritas sedang, aksi edit |
| chart-5 | `340 75% 55%` | `#E23670` | Work |

Kartu dan latar memakai warna yang sama. Pemisahnya adalah `border` 1 dp, bukan isi kartu yang lebih terang.

### Lapisan

| Token | Light | Dark | Pemakaian |
| --- | --- | --- | --- |
| scrim | `#000000` 40% | `#000000` 62% | Overlay menu radial dan sheet |
| nav | background 96% | background 97% | Dock bawah |
| shadow | `#000000` 6% | `#000000` 35% | Bayangan kartu |
| text-subtle | muted-foreground 62% | muted-foreground 62% | Tugas selesai |

Teks di atas warna isi dipilih dengan luminance. Di atas 0.72 pakai `ink` (gelap). Di bawahnya pakai `on-dark` (`#FAFAFA`).

---

## Warna produk

Warna ini tidak berdiri sendiri. Semuanya menunjuk ke token chart atau destructive.

| Peran | Token |
| --- | --- |
| Status todo | muted-foreground |
| Status progress | chart-1 |
| Status done | chart-2 |
| Kategori Study | chart-1 |
| Kategori Work | chart-5 |
| Kategori Health | chart-2 |
| Kategori Personal | chart-4 |
| Kategori University | chart-3 |
| Prioritas low | chart-2 |
| Prioritas medium | chart-4 |
| Prioritas high | destructive |
| Aksi add | primary |
| Aksi update | chart-3 |
| Aksi edit | chart-4 |
| Aksi delete | destructive |

Keadaan terpilih:

| Keadaan | Isi | Garis |
| --- | --- | --- |
| Hapus | destructive 10–12% | destructive 45% |
| Update | chart-4 12–16% | chart-4 55% |
| Progress | chart-1 12% | chart-1 40% |

---

## Tipografi

Font: **Montserrat**.

File: `montserrat_regular`, `montserrat_medium`, `montserrat_semibold`, `montserrat_bold`.

| Token | Ukuran | Line height | Berat default | Pemakaian |
| --- | --- | --- | --- | --- |
| xs | 12 sp | 16 sp | Regular | Meta, caption |
| sm | 14 sp | 20 sp | Regular | Body sekunder, label |
| base | 16 sp | 24 sp | Regular | Body |
| lg | 18 sp | 28 sp | Regular | Judul sheet |
| xl | 20 sp | 28 sp | Regular | Judul bagian |
| 2xl | 24 sp | 32 sp | Regular | Judul layar kecil |
| 3xl | 30 sp | 36 sp | Regular | Judul besar |
| 4xl | 36 sp | 40 sp | Regular | Display |

Berat yang dipakai di komponen:

| Berat | Pemakaian |
| --- | --- |
| Regular | Judul layar, body |
| Medium | Label, judul sedang |
| SemiBold | Nama tugas, isi field |
| Bold | Tab, filter, chip, tombol |

Label bagian ditulis uppercase dengan letter-spacing sekitar `0.1 em`.

---

## Radius

Dasar shadcn: `--radius: 0.5rem` (8 dp).

| Token | Rumus | Nilai | Pemakaian |
| --- | --- | --- | --- |
| sm | radius − 4 | 4 dp | Kontrol kecil |
| md | radius − 2 | 6 dp | Chip, badge, toggle |
| lg | radius | 8 dp | Tombol, input, field tanggal |
| xl | radius + 4 | 12 dp | Kartu tugas, sudut atas sheet |

Sudut luar kartu yang sedang dipilih: `xl + 4 dp`.

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

Jarak layar yang sudah dipakai:

| Token | Nilai | Pemakaian |
| --- | --- | --- |
| header-horizontal | 24 dp | Judul dan filter |
| page-horizontal | 18 dp | Daftar tugas |
| card min height | 68 dp | Kartu tugas |
| action height | 44 dp | Tombol aksi bawah |
| nav height | 100 dp | Dock |
| fab | 88 dp | Tombol tengah |
| node | 48 dp | Item menu radial |

---

## Komponen

### Kartu tugas

- Sudut `xl` (12 dp)
- Isi `card`
- Garis 1 dp `border` saat tidak dipilih
- Garis 2 dp memakai warna aksi saat dipilih
- Bayangan mengikuti token shadow

### Sheet

- Sudut atas `xl`
- Isi `popover`
- Garis 1 dp `border`
- Scrim memakai token lapisan
- Tombol tutup: lingkaran `secondary` dengan ikon `secondary-foreground`

### Input

- Sudut `lg`
- Isi `background`
- Garis 1 dp `input`
- Garis tidak valid: `destructive`
- Kursor: `primary`
- Placeholder: `muted-foreground`

### Toggle

- Sudut `md`
- Aktif: isi dan garis `primary`, teks `primary-foreground`
- Diam: isi transparan, garis `border`, teks `muted-foreground`

### Chip

- Sudut `md`
- Aktif: isi warna peran, teks mengikuti luminance
- Diam: isi warna peran pada alpha `0x18`

### Tombol utama

- Tinggi 44 dp
- Sudut `lg`
- Isi warna aksi
- Teks mengikuti luminance warna isi

### Dock

- Latar `nav`
- Tab aktif: `foreground` plus garis `primary`
- Tab diam: `muted-foreground`
