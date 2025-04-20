package com.example.projectakhirpam

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityAddExpensesBinding
import java.text.SimpleDateFormat
import java.util.*

class AddExpensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpensesBinding
    private val calendar = Calendar.getInstance()
    private var selectedImageUri: Uri? = null

    // ActivityResultLauncher untuk menangani hasil pemilihan gambar
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.ivReceiptPreview.setImageURI(uri)
                binding.ivReceiptPreview.visibility = View.VISIBLE
                binding.layoutUploadReceipt.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol back
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Format tanggal awal
        updateDateEditText()

        // Klik tanggal dan ikon kalender
        binding.etDate.setOnClickListener { showDatePickerDialog() }
        binding.ivCalendar.setOnClickListener { showDatePickerDialog() }

        // Spinner Kategori
        val kategoriList = listOf("Gajian", "Pembelian", "Angsuran", "Makanan & Minuman", "Transportasi", "Belanja", "Hiburan", "Tagihan", "Lainnya")
        val kategoriAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kategoriList)
        binding.spinnerCategory.adapter = kategoriAdapter

        // Spinner Metode Pembayaran
        val metodeList = listOf("Tunai", "Transfer", "Kartu Kredit", "E-wallet")
        val metodeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, metodeList)
        binding.spinnerPayment.adapter = metodeAdapter

        // Setup untuk upload bukti pengeluaran
        binding.layoutUploadReceipt.setOnClickListener {
            openImageChooser()
        }

        // Tombol Simpan
        binding.btnSave.setOnClickListener {
            val tanggal = binding.etDate.text.toString().trim()
            val kategori = binding.spinnerCategory.selectedItem.toString()
            val jumlah = binding.etAmount.text.toString().trim()
            val metode = binding.spinnerPayment.selectedItem.toString()
            val deskripsi = binding.etDescription.text.toString().trim()
            val penerima = binding.etRecipient.text.toString().trim()

            if (tanggal.isEmpty() || jumlah.isEmpty()) {
                Toast.makeText(this, "Mohon isi tanggal dan jumlah", Toast.LENGTH_SHORT).show()
            } else {
                // Simpan data pengeluaran beserta bukti jika ada
                saveExpenseWithReceipt(tanggal, kategori, jumlah, metode, deskripsi, penerima)
            }
        }
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, y, m, d ->
            calendar.set(y, m, d)
            updateDateEditText()
        }, year, month, day)

        datePicker.show()
    }

    private fun updateDateEditText() {
        val format = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
        binding.etDate.setText(format.format(calendar.time))
    }

    // Method untuk membuka pemilih gambar
    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    // Method untuk mendapatkan jalur file sebenarnya dari URI
    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        }
    }

    // Method untuk menyimpan data transaksi beserta bukti pembayaran
    private fun saveExpenseWithReceipt(tanggal: String, kategori: String, jumlah: String, metode: String, deskripsi: String, penerima: String) {
        // TODO: Implementasi penyimpanan data ke Firebase atau lokal storage

        // Contoh sederhana jika ada gambar yang dipilih
        if (selectedImageUri != null) {
            val filePath = getRealPathFromURI(selectedImageUri!!)
            // TODO: Upload bukti ke storage dan dapatkan URL-nya

            Toast.makeText(this, "Pengeluaran berhasil disimpan dengan bukti!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Pengeluaran berhasil disimpan!", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
