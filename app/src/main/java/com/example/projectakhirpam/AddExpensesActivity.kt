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
import com.example.projectakhirpam.databinding.ActivityAddExpensesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Form “Tambah Pengeluaran” (Expense)
 *  – upload bukti ke **Cloudinary**
 *  – simpan node  /users/{uid}/expenses/{expenseId}
 *  – update balance ↓  &  totalExpense ↑  secara atomic
 */
class AddExpensesActivity : AppCompatActivity() {

    /* ---------- ViewBinding ---------- */
    private lateinit var b: ActivityAddExpensesBinding

    /* ---------- Cloudinary ---------- */
    private val cloudinary by lazy {
        Cloudinary(
            hashMapOf(
                "cloud_name" to "dfd671sfr",
                "api_key"    to "731364415221193",
                "api_secret" to "QIl2LALYKbOPL45-ndeLxjBWUBM"
            )
        )
    }

    /* ---------- state ---------- */
    private val cal     = Calendar.getInstance()
    private var imgUri  : Uri?  = null          // lokal
    private var imgUrl  : String = ""           // Cloudinary URL setelah upload

    /* ---------- gallery picker ---------- */
    private val picker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            imgUri = res.data?.data
            imgUri?.let {
                b.ivReceiptPreview.setImageURI(it)
                b.ivReceiptPreview.visibility    = View.VISIBLE
                b.layoutUploadPlaceholder.visibility = View.GONE
            }
        }
    }

    /* ---------- lifecycle ---------- */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddExpensesBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* back */
        b.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        /* tanggal */
        updateEtDate()
        b.etDate.setOnClickListener { showDatePicker() }
        b.ivCalendar.setOnClickListener { showDatePicker() }

        /* spinners */
        b.spinnerCategory.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Makanan & Minuman","Transportasi","Belanja",
                "Tagihan / Utilitas","Hiburan","Lainnya")
        )
        b.spinnerPayment.adapter  = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Tunai","Transfer","Kartu Kredit","E-wallet")
        )

        /* pilih bukti */
        b.layoutUploadPlaceholder.setOnClickListener { openGallery() }
        b.ivReceiptPreview.setOnClickListener        { openGallery() }

        /* simpan */
        b.btnSave.setOnClickListener { saveExpense() }
    }

    /* ---------- UI helpers ---------- */
    private fun showDatePicker() =
        DatePickerDialog(
            this,
            { _, y, m, d -> cal.set(y, m, d); updateEtDate() },
            cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
        ).show()

    private fun updateEtDate() {
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale("in","ID"))
        b.etDate.setText(fmt.format(cal.time))
    }

    private fun openGallery() = picker.launch(
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
    )

    /* ---------- SIMPAN ---------- */
    private fun saveExpense() {
        val amtRaw  = b.etAmount.text.toString().trim()
        val amount  = amtRaw.replace("[^0-9]".toRegex(),"").toLongOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this,"Jumlah tidak valid",Toast.LENGTH_SHORT).show(); return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        val expRef  = userRef.child("expenses").push()
        val expId   = expRef.key!!

        /* disable tombol */
        b.btnSave.isEnabled = false
        b.btnSave.text = "Menyimpan…"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Upload ke Cloudinary jika ada gambar
                imgUri?.let { imgUrl = uploadToCloudinary(it) }

                // 2. Buat objek Expense & tulis ke DB
                val expense = Expense(
                    id            = expId,
                    date          = cal.timeInMillis,
                    category      = b.spinnerCategory.selectedItem.toString(),
                    amount        = amount,
                    paymentMethod = b.spinnerPayment.selectedItem.toString(),
                    description   = b.etDescription.text.toString(),
                    recipient     = b.etRecipient.text.toString(),
                    receiptUrl    = imgUrl
                )

                expRef.setValue(expense).await()

                // 3. Update saldo & totalExpense atomik
                adjustBalance(userRef, -amount)

                launch(Dispatchers.Main) {
                    Toast.makeText(this@AddExpensesActivity,
                        "Pengeluaran tersimpan",Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    b.btnSave.isEnabled = true
                    b.btnSave.text = "Simpan"
                    Toast.makeText(this@AddExpensesActivity,
                        "Gagal: ${e.localizedMessage}",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /* ---------- upload helper ---------- */
    private fun uploadToCloudinary(uri: Uri): String {
        val tmp = File.createTempFile("exp_upload",".tmp",cacheDir)
        contentResolver.openInputStream(uri)?.use { i ->
            tmp.outputStream().use { i.copyTo(it) }
        }
        val res = cloudinary.uploader().upload(tmp, ObjectUtils.emptyMap())
        return res["secure_url"] as String
    }

    /* ---------- balance & totalExpense ---------- */
    private fun adjustBalance(userRef: DatabaseReference, delta: Long) {
        fun mutate(child: String) = userRef.child(child)
            .runTransaction(object: Transaction.Handler {
                override fun doTransaction(cur: MutableData): Transaction.Result {
                    cur.value = (cur.getValue(Long::class.java) ?: 0L) + delta
                    return Transaction.success(cur)
                }
                override fun onComplete(e: DatabaseError?, b:Boolean, s:DataSnapshot?) {}
            })
        mutate("balance")        // turun
        mutate("totalExpense")   // naik (karena delta negatif)
    }
}