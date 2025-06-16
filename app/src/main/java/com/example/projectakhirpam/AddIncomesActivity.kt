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
import androidx.lifecycle.lifecycleScope
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.projectakhirpam.databinding.ActivityAddIncomesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddIncomesActivity : AppCompatActivity() {

    /* ---------- UI & state ---------- */
    private lateinit var b: ActivityAddIncomesBinding
    private val cal                        = Calendar.getInstance()
    private var selectedImageUri: Uri?     = null
    private var receiptUrl: String         = ""

    /* ---------- Cloudinary ---------- */
    private val cloudinary by lazy {
        Cloudinary(
            mapOf(
                "cloud_name" to "dfd671sfr",
                "api_key"    to "731364415221193",
                "api_secret" to "QIl2LALYKbOPL45-ndeLxjBWUBM"
            )
        )
    }

    /* ---------- image picker launcher ---------- */
    private val imgLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { r ->
        if (r.resultCode == Activity.RESULT_OK) {
            r.data?.data?.let {
                selectedImageUri = it
                b.ivReceiptPreview.apply {
                    visibility = View.VISIBLE
                    setImageURI(it)
                }
                b.layoutUploadPlaceholder.visibility = View.GONE
            }
        }
    }

    /* ---------- lifecycle ---------- */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddIncomesBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* back */
        b.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        /* tanggal default */
        updateDateEt()
        val dateClick = View.OnClickListener { showDatePicker() }
        b.etDate.setOnClickListener(dateClick)
        b.ivCalendar.setOnClickListener(dateClick)

        /* spinners */
        b.spinnerCategory.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Gaji", "Donasi", "Investasi", "Penjualan", "Lainnya")
        )
        b.spinnerPayment.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Tunai", "Transfer", "E-Wallet")
        )

        /* pilih gambar */
        val imgClick = View.OnClickListener { chooseImg() }
        b.layoutUploadPlaceholder.setOnClickListener(imgClick)
        b.ivReceiptPreview.setOnClickListener(imgClick)

        /* simpan */
        b.btnSave.setOnClickListener { saveIncome() }
    }

    /* ---------- tanggal util ---------- */
    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, y, m, d -> cal.set(y, m, d); updateDateEt() },
            cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
        ).show()
    }

    private fun updateDateEt() {
        b.etDate.setText(
            SimpleDateFormat("dd MMM yyyy", Locale("in", "ID")).format(cal.time)
        )
    }

    /* ---------- pilih gambar ---------- */
    private fun chooseImg() = imgLauncher.launch(
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
    )

    /* ---------- SIMPAN ---------- */
    private fun saveIncome() {
        val amountStr = b.etAmount.text.toString().trim()
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Jumlah harus diisi", Toast.LENGTH_SHORT).show(); return
        }
        val amount = amountStr.replace("[^\\d]".toRegex(), "").toLong()

        b.btnSave.isEnabled = false
        b.btnSave.text = "Menyimpan…"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                /* 1. upload jika ada gambar */
                selectedImageUri?.let { receiptUrl = uploadToCloudinary(it) }

                /* 2. push ke firebase */
                pushIncomeToFirebase(amount)

                launch(Dispatchers.Main) {
                    Toast.makeText(this@AddIncomesActivity, "Tersimpan", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(this@AddIncomesActivity,
                        "Gagal: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    b.btnSave.isEnabled = true
                    b.btnSave.text = "Simpan"
                }
            }
        }
    }

    /* ---------- Cloudinary upload ---------- */
    private fun uploadToCloudinary(uri: Uri): String {
        val temp = File.createTempFile("upload", ".tmp", cacheDir)
        contentResolver.openInputStream(uri)!!.use { input ->
            temp.outputStream().use { output -> input.copyTo(output) }
        }
        val result = cloudinary.uploader().upload(temp, ObjectUtils.emptyMap())
        return result["secure_url"] as String
    }

    /* ---------- Firebase ---------- */
    private suspend fun pushIncomeToFirebase(amount: Long) {
        val uid   = FirebaseAuth.getInstance().currentUser!!.uid
        val refIn = FirebaseDatabase.getInstance().reference
            .child("users").child(uid).child("incomes")
        val key   = refIn.push().key ?: return

        val income = Income(
            id            = key,
            date          = cal.timeInMillis,
            category      = b.spinnerCategory.selectedItem.toString(),
            amount        = amount,
            paymentMethod = b.spinnerPayment.selectedItem.toString(),
            description   = b.etDescription.text.toString(),
            receiptUrl    = receiptUrl
        )

        // simpan income
        refIn.child(key).setValue(income).await()

        // atomik update saldo & totalIncome
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        val handler = object : Transaction.Handler {
            override fun doTransaction(cur: MutableData): Transaction.Result {
                val now = (cur.getValue(Long::class.java) ?: 0L) + amount
                cur.value = now
                return Transaction.success(cur)
            }
            override fun onComplete(e: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {}
        }
        userRef.child("balance").runTransaction(handler)
        userRef.child("totalIncome").runTransaction(handler)
    }
}
