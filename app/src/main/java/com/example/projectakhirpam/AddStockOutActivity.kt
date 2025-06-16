package com.example.projectakhirpam

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.projectakhirpam.databinding.ActivityAddStockoutBinding
import com.example.projectakhirpam.databinding.DialogReceiptPreviewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddStockOutActivity : AppCompatActivity() {

    private lateinit var b       : ActivityAddStockoutBinding
    private lateinit var db      : DatabaseReference
    private val items            = mutableListOf<Item>()
    private var selected : Item? = null

    private val cal = Calendar.getInstance()
    private var localUri : Uri?  = null
    private var cloudUrl : String = ""

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

    /* ---------- gallery picker ---------- */
    private val picker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ res ->
        if (res.resultCode == Activity.RESULT_OK){
            localUri = res.data?.data
            localUri?.let {
                b.ivReceiptPreview.setImageURI(it)
                b.ivReceiptPreview.visibility        = View.VISIBLE
                b.layoutUploadPlaceholder.visibility = View.GONE
            }
        }
    }

    /* ------------------------------------------------------------------ */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddStockoutBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* back */
        b.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        /* tanggal */
        updateEtDate()
        val dateClick = View.OnClickListener { showDatePicker() }
        b.etDate.setOnClickListener(dateClick)
        b.ivCalendar.setOnClickListener(dateClick)

        /* gallery */
        val imgClick = View.OnClickListener { openGallery() }
        b.layoutUploadPlaceholder.setOnClickListener(imgClick)
        b.ivReceiptPreview.setOnClickListener(imgClick)

        /* Firebase & spinner data */
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db = FirebaseDatabase.getInstance().reference
        loadItems(uid)

        /* simpan */
        b.btnSave.setOnClickListener { saveStockOut(uid) }
    }

    /* ---------- UI helpers ---------- */
    private fun updateEtDate(){
        val fmt = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        b.etDate.setText(fmt.format(cal.time))
    }
    private fun showDatePicker() =
        DatePickerDialog(this,{_,y,m,d-> cal.set(y,m,d); updateEtDate()},
            cal[Calendar.YEAR],cal[Calendar.MONTH],cal[Calendar.DAY_OF_MONTH]).show()

    private fun openGallery() = picker.launch(
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply{
            type = "image/*"
        })

    /* ---------- load spinner ---------- */
    private fun loadItems(uid:String){
        db.child("users").child(uid).child("items")
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(s: DataSnapshot) {
                    items.clear()
                    val names = mutableListOf<String>()
                    for (c in s.children){
                        c.getValue(Item::class.java)?.let { items.add(it); names.add(it.name) }
                    }
                    val adp = ArrayAdapter(
                        this@AddStockOutActivity,
                        android.R.layout.simple_spinner_item, names
                    ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)}
                    b.spItem.adapter = adp
                    b.spItem.setOnPosChanged { pos -> selected = items.getOrNull(pos) }
                }
                override fun onCancelled(e: DatabaseError) { }
            })
    }
    /* spinner extension */
    private fun android.widget.Spinner.setOnPosChanged(cb:(Int)->Unit){
        onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, v: View?, p: Int, id: Long) = cb(p)
            override fun onNothingSelected(p0: AdapterView<*>?) = cb(-1)
        }
    }

    /* ---------- SAVE ---------- */
    private fun saveStockOut(uid:String){
        val qty = b.etQty.text.toString().trim().toIntOrNull()
        if (selected==null || qty==null || qty<=0){
            Toast.makeText(this,"Lengkapi data",Toast.LENGTH_SHORT).show(); return
        }
        if (selected!!.stock < qty){
            Toast.makeText(this,"Stok tidak mencukupi",Toast.LENGTH_SHORT).show(); return
        }

        /* upload (optional) & DB write di coroutine */
        b.btnSave.isEnabled = false
        CoroutineScope(Dispatchers.IO).launch {
            try {
                localUri?.let { cloudUrl = uploadToCloudinary(it) }

                writeToFirebase(uid, qty)

                launch(Dispatchers.Main){
                    Toast.makeText(this@AddStockOutActivity,
                        "Stock-out tersimpan",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }catch (e: Exception){
                launch(Dispatchers.Main){
                    b.btnSave.isEnabled = true
                    Toast.makeText(this@AddStockOutActivity,
                        "Gagal: ${e.localizedMessage}",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /* ---------- Cloudinary upload ---------- */
    private fun uploadToCloudinary(uri: Uri): String {
        val tmp = File.createTempFile("upload", ".tmp", cacheDir)
        contentResolver.openInputStream(uri)?.use { inp ->
            tmp.outputStream().use { out -> inp.copyTo(out) }
        }
        val res = cloudinary.uploader().upload(tmp, ObjectUtils.emptyMap())
        return res["secure_url"] as String
    }

    /* ---------- Firebase write ---------- */
    private suspend fun writeToFirebase(uid:String, qty:Int){
        val stockOutRef = db.child("users").child(uid).child("stockouts")
        val id          = stockOutRef.push().key ?: return
        val totalPrice  = selected!!.price * qty

        val data = StockOut(
            id           = id,
            itemId       = selected!!.id,
            itemName     = selected!!.name,
            qty          = qty,
            unitPrice    = selected!!.price,
            totalPrice   = totalPrice,
            date         = b.etDate.text.toString(),
            destination  = b.etDest.text.toString(),
            note         = b.etNote.text.toString(),
            timestamp    = System.currentTimeMillis(),
            receiptUrl   = cloudUrl                     // <—
        )

        val upd = hashMapOf<String,Any>(
            "/users/$uid/stockouts/$id"                to data,
            "/users/$uid/items/${selected!!.id}/stock" to (selected!!.stock - qty)
        )
        db.updateChildren(upd).await()
    }

    /* ---------- utility: preview image (dipakai di adapter) ---------- */
    fun previewImage(url:String){
        val vb = DialogReceiptPreviewBinding.inflate(layoutInflater)
        val dlg = AlertDialog.Builder(this,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            .setView(vb.root).create()
        vb.ivReceiptBig.load(url)
        vb.btnClose.setOnClickListener { dlg.dismiss() }
        dlg.show()
    }
}
