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
import com.example.projectakhirpam.databinding.ActivityAddIncomesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AddIncomesActivity : AppCompatActivity() {

    private lateinit var b       : ActivityAddIncomesBinding
    private val cal              = Calendar.getInstance()
    private var selectedImageUri : Uri? = null   // (belum di-upload, demo saja)

    private val imgLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ r ->
        if (r.resultCode == Activity.RESULT_OK) {
            r.data?.data?.let {
                selectedImageUri = it
                b.ivReceiptPreview.setImageURI(it)
                b.ivReceiptPreview.visibility       = View.VISIBLE
                b.layoutUploadPlaceholder.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddIncomesBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* back */
        b.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        /* tanggal default & picker */
        updateDateEt()
        b.etDate.setOnClickListener { showDatePicker() }
        b.ivCalendar.setOnClickListener { showDatePicker() }

        /* spinner */
        b.spinnerCategory.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Gaji","Donasi","Investasi","Penjualan","Lainnya")
        )
        b.spinnerPayment.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Tunai","Transfer","E-wallet")
        )

        /* upload bukti (opsional) */
        b.layoutUploadPlaceholder.setOnClickListener { chooseImg() }
        b.ivReceiptPreview.setOnClickListener      { chooseImg() }

        /* simpan */
        b.btnSave.setOnClickListener { saveIncome() }
    }

    /* ───────── util tanggal ───────── */
    private fun showDatePicker() {
        DatePickerDialog(this, { _, y,m,d -> cal.set(y,m,d); updateDateEt() },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    private fun updateDateEt() {
        b.etDate.setText(SimpleDateFormat("dd MMM yyyy", Locale("in","ID")).format(cal.time))
    }

    /* ───────── pilih gambar ───────── */
    private fun chooseImg() =
        imgLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        })

    /* ───────── simpan ke DB ───────── */
    private fun saveIncome() {

        /* validasi */
        val amountStr = b.etAmount.text.toString().trim()
        if (amountStr.isEmpty()){
            Toast.makeText(this,"Jumlah harus diisi",Toast.LENGTH_SHORT).show(); return
        }
        val amount = amountStr.replace("[^\\d]".toRegex(),"").toLong()

        /* siapkan data */
        val uid     = FirebaseAuth.getInstance().currentUser!!.uid
        val refIn   = FirebaseDatabase.getInstance()
            .reference.child("users").child(uid).child("incomes")
        val key     = refIn.push().key ?: return
        val income  = Income(
            id            = key,
            date          = cal.timeInMillis,
            category      = b.spinnerCategory.selectedItem.toString(),
            amount        = amount,
            paymentMethod = b.spinnerPayment.selectedItem.toString(),
            description   = b.etDescription.text.toString()
        )

        /* simpan */
        refIn.child(key).setValue(income)
            .addOnSuccessListener {
                updateBalance(amount)
                Toast.makeText(this,"Tersimpan",Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this,"Gagal: ${it.localizedMessage}",Toast.LENGTH_LONG).show()
            }
    }

    /* tambah saldo & totalIncome */
    private fun updateBalance(delta: Long) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val uref = FirebaseDatabase.getInstance().reference.child("users").child(uid)

        val handler = object: Transaction.Handler{
            override fun doTransaction(cur: MutableData): Transaction.Result {
                val now = (cur.getValue(Long::class.java) ?: 0L) + delta
                cur.value = now
                return Transaction.success(cur)
            }
            override fun onComplete(e: DatabaseError?, c:Boolean, s:DataSnapshot?){}
        }
        uref.child("balance").runTransaction(handler)
        uref.child("totalIncome").runTransaction(handler)
    }
}
