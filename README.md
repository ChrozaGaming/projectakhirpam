# 📦💰 Aplikasi Manajemen Barang dan Keuangan 

Sistem Android untuk pencatatan **barang dan keuangan** yang sederhana namun powerful. Dirancang khusus untuk membantu pengguna seperti pemilik usaha kecil dalam mencatat, mengelola, dan menganalisis transaksi harian. 

---

## 🧾 Informasi Proyek

- **📘 Mata Kuliah:** Pengembangan Aplikasi Mobile  
- **🏛️ Program Studi:** Teknologi Informasi  
- **👨‍🏫 Dosen Pengampu:** Muhammad Aminul Akbar, S.Kom., M.T.  
- **📈 Progress Saat Ini:** Progress II - Recycle View

---

## 👥 Anggota Kelompok

| Nama                     | NIM              |
|--------------------------|------------------|
| Azriel Maulani Rahman    | 235150707111034  |
| Ega Yurcel Satriaji      | 235150700111031  |
| Hilmy Raihan Alkindy     | 235150707111042  |
| Muhammad Naufal Majid    | 235150707111022  |

---

## 📱 Deskripsi Aplikasi

Aplikasi ini adalah solusi berbasis Android untuk:
- Mencatat barang masuk & keluar
- Mencatat uang masuk & keluar
- Melihat rekap dan laporan keuangan

Tujuannya adalah memberikan **kemudahan pencatatan** untuk pengguna yang membutuhkan aplikasi **praktis, ringan, dan fungsional** dalam kehidupan sehari-hari.

---

## 🚀 Fitur Utama

### 📦 Manajemen Barang
- ✅ Pencatatan Barang Masuk
- 📤 Pencatatan Barang Keluar
- 📋 Daftar Inventaris

### 💰 Manajemen Keuangan
- 💸 Pencatatan Uang Masuk
- 🧾 Pencatatan Uang Keluar
- 🗂️ Kategori Transaksi

### 📊 Rekapitulasi Data
- 📑 Laporan Pemasukan & Pengeluaran
- 📈 Grafik Visualisasi
- 🕓 Filter Berdasarkan Periode (harian, mingguan, bulanan)

### 👤 Fitur Pengguna
- 🔐 Login & Autentikasi
- 🧑 Profil Pengguna
- 🔔 Notifikasi Pengingat Stok / Pembayaran

---

## ⚙️ Implementasi Teknis (Progress I)

### 🎨 UI
- Desain intuitif dan modern (Material Design)
- Konsistensi layout di seluruh aplikasi

### 📂 Activity
- Pemisahan aktivitas sesuai fungsi
- Lifecycle management yang tepat

### 🔗 Intent
- Navigasi antar Activity
- Pengiriman data antar Activity
- Share data ke aplikasi lain

### 👆 Event-Handling
- Penanganan input & aksi user
- Validasi input dan feedback interaktif

---

## 🧰 Teknologi yang Digunakan

- 🛠️ **Bahasa:** Kotlin
- 📱 **Platform:** Android SDK
- 🗄️ **Database:** SQLite (lokal)
- 🧠 **Arsitektur:** MVVM
- 📦 **Libraries:**
  - Material Components
  - RecyclerView
  - Room
  - Glide (image loading)
  - MPAndroidChart (grafik visualisasi)

---

## 🖼️ Screenshot Aplikasi
📸 *(Akan ditambahkan pada tahap selanjutnya)*

---

## 💻 Cara Instalasi

```bash
# 1. Clone Repository
$ git clone https://github.com/nama-repo/project-mbk.git

# 2. Buka di Android Studio
$ cd project-mbk

# 3. Sync Gradle
# 4. Jalankan di emulator atau device Android
```

---

## 📂 Struktur Folder Project

```
project-mbk/
├── app/
│   ├── manifests/
│   │   └── AndroidManifest.xml
│   │
│   ├── java/com/example/projectmbk/
│   │   ├── AddExpensesActivity.kt
│   │   ├── AddIncomesActivity.kt
│   │   ├── AddItemActivity.kt
│   │   ├── FinancialTransactionAdapter.kt
│   │   ├── FinancialTransactionModel.kt
│   │   ├── ItemAdapter.kt
│   │   ├── ItemModel.kt
│   │   ├── MainActivity.kt
│   │   ├── ManageExpensesActivity.kt
│   │   ├── ManageFinancialsActivity.kt
│   │   ├── ManageItemsActivity.kt
│   │   └── utils/
│   │       └── FormatUtil.kt (opsional)
│   │
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml
│   │   │   ├── activity_add_items.xml
│   │   │   ├── activity_add_expenses.xml
│   │   │   ├── activity_add_incomes.xml
│   │   │   ├── activity_manage_items.xml
│   │   │   ├── activity_manage_expenses.xml
│   │   │   ├── activity_manage_financials.xml
│   │   │   ├── item_inventory.xml
│   │   │   ├── item_transaction.xml
│   │   │   └── dialog_add_transaction.xml
│   │   ├── menu/
│   │   │   └── bottom_nav_menu.xml
│   │   ├── values/
│   │   │   ├── colors.xml
│   │   │   ├── strings.xml
│   │   │   └── styles.xml
│   │   └── drawable/
│   │       └── ic_*.xml
│
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 📅 Rencana Pengembangan Selanjutnya

- 🗃️ Implementasi database untuk penyimpanan data permanen
- 📄 Laporan detail keuangan
- 📊 Grafik interaktif
- ✨ Penyempurnaan UI/UX
- 📤 Ekspor data ke CSV/PDF

---

## 🙌 Kontribusi Anggota Progress 1

| Anggota                  | Kontribusi                          |
|--------------------------|-------------------------------------|
| Azriel Maulani Rahman    | UI Design, Layout Implementation    |
| Ega Yurcel Satriaji      | Struktur Database, Activity         |
| Hilmy Raihan Alkindy     | Event Handling, Intent Navigation   |
| Muhammad Naufal Majid    | Business Logic, Testing             |

---

© 2025 Kelompok MBK - Pengembangan Aplikasi Mobile 💼📲
