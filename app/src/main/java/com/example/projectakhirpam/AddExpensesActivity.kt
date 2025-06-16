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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Form “Tambah Pengeluaran”
 *  • Data →  /users/{uid}/expenses/{expenseId}
 *  • balance ↓, totalExpense ↑  (transaction-safe)
 *  • receiptUrl = URI lokal (tidak di-upload)
 */
class AddExpensesActivity : AppCompatActivity() {

    private lateinit var b: ActivityAddExpensesBinding
    private val cal    = Calendar.getInstance()
    private var imgUri : Uri? = null               // URI lokal (galeri)

    /* ---------- galeri picker ---------- */
    private val picker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ r ->
        if (r.resultCode == Activity.RESULT_OK){
            imgUri = r.data?.data
            imgUri?.let {
                b.ivReceiptPreview.setImageURI(it)
                b.ivReceiptPreview.visibility           = View.VISIBLE
                b.layoutUploadPlaceholder.visibility    = View.GONE   // ✅ id yang benar
            }
        }
    }

    /* ---------- onCreate ---------- */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddExpensesBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* tombol back */
        b.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        /* tanggal default */
        updateEtDate()
        b.etDate.setOnClickListener { showDatePicker() }
        b.ivCalendar.setOnClickListener { showDatePicker() }

        /* spinner kategori & pembayaran */
        b.spinnerCategory.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf(
                "Makanan & Minuman", "Transportasi", "Belanja",
                "Tagihan / Utilitas", "Hiburan", "Lainnya"
            )
        )
        b.spinnerPayment.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Tunai", "Transfer", "Kartu Kredit", "E-wallet")
        )

        /* pilih bukti */
        val pick: View.OnClickListener = View.OnClickListener { openGallery() }
        b.layoutUploadPlaceholder.setOnClickListener(pick)   // ✅ id yang benar
        b.ivReceiptPreview.setOnClickListener(pick)

        /* simpan */
        b.btnSave.setOnClickListener { saveExpense() }
    }

    /* ---------- Date helpers ---------- */
    private fun showDatePicker() =
        DatePickerDialog(
            this,
            { _, y, m, d -> cal.set(y, m, d); updateEtDate() },
            cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
        ).show()

    private fun updateEtDate(){
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale("in","ID"))
        b.etDate.setText(fmt.format(cal.time))
    }

    /* ---------- Galeri ---------- */
    private fun openGallery() = picker.launch(
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
    )

    /* ---------- Simpan ---------- */
    private fun saveExpense(){
        val rawAmt = b.etAmount.text.toString().trim()
        val amt    = rawAmt.replace("[^0-9]".toRegex(),"").toLongOrNull()
        if (amt==null || amt<=0){
            Toast.makeText(this,"Jumlah tidak valid",Toast.LENGTH_SHORT).show(); return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        val expRef  = userRef.child("expenses").push()
        val id      = expRef.key!!

        val expense = Expense(
            id            = id,
            date          = cal.timeInMillis,
            category      = b.spinnerCategory.selectedItem.toString(),
            amount        = amt,
            paymentMethod = b.spinnerPayment.selectedItem.toString(),
            description   = b.etDescription.text.toString(),
            recipient     = b.etRecipient.text.toString(),
            receiptUrl    = imgUri?.toString() ?: ""      // URI lokal
        )

        b.btnSave.isEnabled = false
        writeExpense(expRef, expense, userRef, -amt)
    }

    /* ---------- Firebase writes ---------- */
    private fun writeExpense(
        expRef : DatabaseReference,
        exp    : Expense,
        userRef: DatabaseReference,
        delta  : Long          // negatif
    ){
        expRef.setValue(exp)
            .addOnSuccessListener {
                adjustBalance(userRef, delta)
                Toast.makeText(
                    this,
                    "Pengeluaran disimpan: IDR ${
                        NumberFormat.getInstance(Locale("in","ID")).format(exp.amount)
                    }",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
            .addOnFailureListener {
                b.btnSave.isEnabled = true
                Toast.makeText(this,"Gagal: ${it.localizedMessage}",Toast.LENGTH_LONG).show()
            }
    }

    /* balance ↓  totalExpense ↑ */
    private fun adjustBalance(userRef: DatabaseReference, balDelta: Long){
        fun mutate(child: String, diff: Long){
            userRef.child(child).runTransaction(object: Transaction.Handler{
                override fun doTransaction(cur: MutableData): Transaction.Result {
                    cur.value = (cur.getValue(Long::class.java) ?: 0L) + diff
                    return Transaction.success(cur)
                }
                override fun onComplete(e: DatabaseError?, c:Boolean, s:DataSnapshot?){}
            })
        }
        mutate("balance",       balDelta)   // balance turun
        mutate("totalExpense", -balDelta)   // totalExpense naik (+ |delta|)
    }
}
