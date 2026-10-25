# Aturan agent AI untuk Emobilize

Berlaku untuk setiap agent (Claude Code, Codex, Gemini, Cursor) yang mengubah repo ini. Baca juga `CONTEXT.md` (peta file) dan `DESIGN.md` (token tampilan).

Tujuan aturan ini: beberapa orang bisa menjalankan agent bersamaan tanpa merge conflict. Conflict datang dari file hotspot yang disentuh semua fitur, dan dari agent yang mengedit di luar tugasnya. Jadi kode dikelompokkan per fitur, hotspot dipegang satu pemilik, dan setiap agent dibatasi ke satu folder.

## Batas edit

1. Kerjakan hanya folder `feature/<x>/` yang disebut di tugas. Path lengkap: `app/src/main/java/ayodong/emobilize/feature/<x>/`. Tes fitur itu boleh ditambah di `app/src/test/java/ayodong/emobilize/feature/<x>/`.
2. Jangan edit file ini dari tugas fitur. Kalau tugas butuh perubahan di sini, berhenti dan laporkan apa yang dibutuhkan:
   - `core/` (model, repository bersama, navigasi, tema, DI bersama)
   - `MainActivity.kt`, `EmobilizeApplication.kt`
   - `build.gradle.kts`, `app/build.gradle.kts`, `gradle/libs.versions.toml`, `settings.gradle.kts`
   - `AndroidManifest.xml`, `res/values/strings.xml`, `res/values/themes.xml`
3. Jangan format ulang, ganti nama, atau "rapikan" kode di luar tugas. Jangan naikkan versi dependency.
4. Jangan impor dari fitur lain (`feature/a` tidak boleh mengimpor `feature/b`). Kalau dua fitur butuh kode yang sama, kode itu pindah ke `core/` lewat pemilik `core/`.

## Struktur di dalam fitur

```
feature/<x>/
  domain/   interface repository milik fitur ini, use case (hanya jika ada aturan bisnis atau gabungan repository)
  data/     implementasi repository, plus Fake<X>Repository untuk preview dan tes
  ui/       <X>Screen.kt (stateless), <X>ViewModel.kt, <X>UiState
  di/       <X>Module.kt: viewModelFactory { initializer { ... } }
```

- Arah dependensi: `ui -> domain <- data`. `domain` tidak mengimpor Android atau Compose.
- Data yang dibaca beberapa fitur (tugas, jadwal, kategori, pengaturan) ada di `core/domain` dan `core/data`, bukan di fitur.
- Domain opsional. Jangan buat use case yang hanya meneruskan satu panggilan repository. Jangan buat DTO atau model terpisah kalau bentuknya sama.

## Pola MVVM

- Satu ViewModel mengekspos satu `StateFlow<XUiState>` (data class immutable). Event masuk sebagai fungsi biasa (`onX()`, `selectX()`).
- Screen stateless: menerima state dan lambda. Screen tidak memegang ViewModel dan tidak memanggil repository.
- ViewModel dibuat di `core/navigation/AppNavHost.kt` atau `MainShell.kt` lewat `feature/<x>/di/<X>Module`.
- Pengecualian: form memakai `TaskDraft` (`core/ui/form`) karena field teks berubah setiap ketikan.
- Mode aksi radial (Edit, Update, Delete) ada di `core/navigation/ShellViewModel.kt`, karena berlaku di tab Tasks dan Calendar.

## Mengerjakan satu fitur berdua

1. Pemilik `core/` mendaftarkan rute, interface repository, dan binding DI lebih dulu (walking skeleton), dengan layar stub.
2. Satu orang membuat implementasi `data/`. Orang lain membuat layar dan ViewModel memakai `Fake<X>Repository`.
3. Binding di `di/<X>Module.kt` lalu diganti dari fake ke implementasi asli.

## Branch dan PR

- Satu branch atau `git worktree` per tugas. PR kecil, merge ke `main` setiap hari.
- Perubahan hotspot (`core/`, Gradle, manifest) masuk lewat PR kecil dari pemilik `core/` dan di-merge lebih dulu.
- Migrasi skema data dikerjakan berurutan, tidak paralel.
- Conflict yang tetap muncul berarti dua tugas tidak independen atau agent keluar batas. Pindahkan bagian bersama ke `core/`.

## Cek sebelum selesai

```
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest
```

Di Windows pakai `.\gradlew.bat`. Laporkan file yang diubah. Kalau ada file di luar folder fitur yang berubah, sebutkan alasannya.
