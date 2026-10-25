# Setup dan troubleshooting

Panduan ini untuk anggota tim yang tidak bisa build, sync, atau run Emobilize di laptopnya, atau yang pull request-nya gagal di CI.

Setiap laptop memakai setup yang sama dari repo:

- Gradle 9.7.1, dipasang lewat wrapper (`gradlew` / `gradlew.bat`). Jangan pakai Gradle yang terpasang di laptop.
- JDK 25 untuk menjalankan build, diatur di `gradle/gradle-daemon-jvm.properties`. Gradle mengunduhnya sendiri kalau belum ada.
- Versi library dan plugin hanya ada di `gradle/libs.versions.toml`.

## Sebelum push

Jalankan pemeriksaan yang sama dengan CI dari root repo. Di Windows, pakai Git Bash.

```bash
bash scripts/check-config.sh
```

```bash
./gradlew assembleDebug testDebugUnitTest
```

Di PowerShell atau CMD, ganti `./gradlew` dengan `.\gradlew.bat`.

## Error di CI: `scripts/check-config.sh`

Setiap baris `FAIL` diikuti cara memperbaikinya. Penjelasan singkat:

| Baris FAIL | Penyebab | Perbaikan |
|---|---|---|
| `Per-machine files are committed` | File milik satu laptop ikut ter-commit, misalnya `local.properties`, `build/`, `*.iml`, atau `.idea/misc.xml`. | `git rm --cached <file>`, lalu commit. File tetap ada di laptopmu dan sudah di-ignore. |
| `Build files contain machine-specific settings` | Ada path laptop (`C:\Users\…`, `/Users/…`) atau `sdk.dir` / `org.gradle.java.home` di file Gradle yang di-commit. | Hapus baris itu. Path SDK masuk `local.properties`. Path JDK masuk `~/.gradle/gradle.properties` di laptopmu sendiri. |
| `Gradle wrapper ... distributionSha256Sum` | `gradle/wrapper/gradle-wrapper.properties` diubah tanpa checksum. | Kembalikan file itu: `git checkout origin/main -- gradle/wrapper`. Perubahan versi Gradle dikerjakan pemilik file Gradle. |
| `gradle-daemon-jvm.properties must set toolchainVersion` | File JDK build hilang atau diubah. | `git checkout origin/main -- gradle/gradle-daemon-jvm.properties` |
| `Hard-coded versions found in build files` | Dependency ditulis seperti `"androidx.core:core-ktx:1.19.1"`. | Tambahkan versinya di `gradle/libs.versions.toml`, lalu pakai `libs.<alias>`. |
| `Wrapper scripts have the wrong mode or line endings` | `gradlew` kehilangan izin eksekusi atau ter-commit dengan CRLF. | `git update-index --chmod=+x gradlew`, lalu `git add --renormalize .` dan commit. |

## Error di CI: build gagal di satu OS

Buka job yang merah di tab **Actions** dan cari error pertama di step **Build debug APK and run unit tests**.

- Gagal hanya di Windows, atau hanya di macOS/Linux: biasanya path dengan huruf besar/kecil yang salah. Windows tidak membedakan `Task.kt` dan `task.kt`, macOS/Linux membedakannya. Samakan nama file dengan import-nya.
- Gagal di semua OS: kodenya memang tidak compile atau tes gagal. Jalankan `./gradlew assembleDebug testDebugUnitTest` di laptopmu dan perbaiki error yang sama.

## Error di laptop

### Android Studio: `incompatible version (AGP 9.4.1) of the Android Gradle plugin`

Android Studio-mu lebih lama dari versi AGP proyek. Update lewat **Help > Check for Updates** (macOS: **Android Studio > Check for Updates**) ke versi stable terbaru, lalu sync ulang.

Jangan turunkan versi AGP di `gradle/libs.versions.toml` supaya cocok dengan Android Studio-mu.

### Android Studio menawarkan upgrade AGP atau Gradle

Tolak atau tutup notifikasinya. Upgrade Assistant mengubah `gradle/libs.versions.toml` dan wrapper di laptopmu saja, lalu laptop lain bisa gagal sync. Kalau upgrade memang perlu, minta ke pemilik file Gradle dan kerjakan di pull request terpisah.

### `SDK location not found`

Gradle tidak tahu letak Android SDK. Pilih salah satu:

- Buka proyek di Android Studio sekali. Android Studio membuat `local.properties` sendiri.
- Buat `local.properties` di root repo dengan isi seperti ini. Sesuaikan dengan path SDK di laptopmu (Android Studio: **Settings > Languages & Frameworks > Android SDK**).

  ```
  sdk.dir=C\:\\Users\\<nama>\\AppData\\Local\\Android\\Sdk
  ```

  macOS: `sdk.dir=/Users/<nama>/Library/Android/sdk`

Jangan commit `local.properties`.

### Android SDK Platform 37 tidak ditemukan

Proyek memakai `compileSdk 37`. Buka **Tools > SDK Manager**, centang **Android API 37** di tab **SDK Platforms**, lalu **Apply**.

### Gradle tidak bisa menemukan atau mengunduh JDK 25

Gradle mengunduh JDK 25 saat build pertama, jadi build pertama butuh internet. Kalau unduhan gagal (jaringan kampus, proxy):

1. Pasang JDK 25 manual, misalnya Temurin 25 dari adoptium.net.
2. Di Android Studio, buka **Settings > Build, Execution, Deployment > Build Tools > Gradle**.
3. Di **Gradle JDK**, pilih JDK 25 itu, lalu sync ulang.

Jangan menulis path JDK ke `gradle.properties` di repo.

### `./gradlew: Permission denied` (macOS/Linux)

```bash
chmod +x gradlew
```

### `/bin/sh^M: bad interpreter` atau `env: sh\r: No such file or directory`

`gradlew` di laptopmu tersimpan dengan CRLF. Hapus lalu ambil ulang dari Git; `.gitattributes` membuatnya kembali dengan LF:

```bash
rm gradlew && git checkout -- gradlew
```

### `.\gradlew` tidak dikenali di PowerShell

Pakai `.\gradlew.bat`. `gradlew` tanpa `.bat` hanya untuk Git Bash, macOS, dan Linux.

### Setelah pull, emulator yang dipilih hilang

`.idea/misc.xml`, `.idea/deploymentTargetSelector.xml`, dan `.idea/deviceManager.xml` tidak lagi di-commit karena isinya berbeda di setiap laptop. Git menghapusnya sekali saat pull. Android Studio membuatnya lagi; pilih emulator-mu lagi di toolbar.

### Masih gagal

Coba langkah ini berurutan, lalu sync ulang setelah setiap langkah:

1. **File > Sync Project with Gradle Files**.
2. `./gradlew --stop`, lalu build lagi.
3. **File > Invalidate Caches > Invalidate and Restart**.

Kalau masih gagal, kirim ke grup output lengkap dari `./gradlew assembleDebug --stacktrace` dan hasil `bash scripts/check-config.sh`.
