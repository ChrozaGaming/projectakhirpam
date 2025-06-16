# 📦💰 Aplikasi Manajemen Barang dan Keuangan 

Sistem Android untuk pencatatan **barang dan keuangan** yang sederhana namun powerful. Dirancang khusus untuk membantu pengguna seperti pemilik usaha kecil dalam mencatat, mengelola, dan menganalisis transaksi harian. 

---

## 🧾 Informasi Proyek

- **📘 Mata Kuliah:** Pengembangan Aplikasi Mobile  
- **🏛️ Program Studi:** Teknologi Informasi  
- **👨‍🏫 Dosen Pengampu:** Muhammad Aminul Akbar, S.Kom., M.T.  
- **📈 Progress Saat Ini:** Progress IV - Firebase | Realtime Database

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

## 📱 Tampilan Aplikasi


<img width="392" alt="image" src="https://github.com/user-attachments/assets/0de23f8a-0ac8-487c-8e60-d02be9dbf960" />
<img width="400" alt="image" src="https://github.com/user-attachments/assets/956a304d-0a3c-40e5-a16d-830cbf76c6d2" />
<img width="393" alt="image" src="https://github.com/user-attachments/assets/f98a2201-84cf-42d6-bbe5-174bc4568873" />
<img width="399" alt="image" src="https://github.com/user-attachments/assets/95399662-f181-434b-9f70-ecf34a175ea8" />
<img width="406" alt="image" src="https://github.com/user-attachments/assets/5c026728-6567-4ca7-b65f-94d921747c2e" />

<img width="407" alt="image" src="https://github.com/user-attachments/assets/7a052b24-8650-4f88-a11c-edd7b6637f20" />
<img width="402" alt="image" src="https://github.com/user-attachments/assets/fce95eba-6317-4b6d-bc5e-3f422128f01a" />
<img width="396" alt="image" src="https://github.com/user-attachments/assets/1a94a8f6-8461-4aba-9bdd-9892ae932ed9" />
<img width="410" alt="image" src="https://github.com/user-attachments/assets/941169d5-4efe-465d-b0c3-2a137fbf4c5f" />
<img width="401" alt="image" src="https://github.com/user-attachments/assets/e32ecbc0-8a01-4b8a-ac50-a1acbf070e5d" />
<img width="402" alt="image" src="https://github.com/user-attachments/assets/45311f31-fb13-4e50-b8b1-e9a50e022ad5" />
<img width="395" alt="image" src="https://github.com/user-attachments/assets/73d3fc5e-4e66-4894-a0ee-4cd94f876282" />
<img width="403" alt="image" src="https://github.com/user-attachments/assets/cd0238f3-b2b8-4d2f-beb8-9421b6de92d9" />
<img width="398" alt="image" src="https://github.com/user-attachments/assets/b012fb80-9863-456e-961e-498f903bf885" />










© 2025 Kelompok MBK - Pengembangan Aplikasi Mobile 💼📲
