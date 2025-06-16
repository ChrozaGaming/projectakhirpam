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
import com.example.projectakhirpam.databinding.ActivityAddItemsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.NumberFormat
import java.util.*

class AddItemActivity : AppCompatActivity() {

    private lateinit var b: ActivityAddItemsBinding
    private var selectedUri: Uri? = null
    private var imageUrl  = ""

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

    /* ---------- Image picker ---------- */
    private val imgPicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            res.data?.data?.let {
                selectedUri = it
                b.ivReceiptPreview.setImageURI(it)
                b.ivReceiptPreview.visibility = View.VISIBLE
                b.layoutUploadPlaceholder.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddItemsBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* ← back */
        b.btnBack.setOnClickListener { finish() }

        /* price formatter (IDR) */
        val nf = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        b.etPrice.setOnFocusChangeListener { _, has ->
            if (!has) {
                val txt = b.etPrice.text.toString().replace("[^\\d]".toRegex(), "")
                if (txt.isNotEmpty()) b.tvPriceDisplay.text = nf.format(txt.toLong())
            }
        }

        /* picker click */
        val pick: View.OnClickListener = View.OnClickListener { pickImage() }
        b.layoutUploadPlaceholder.setOnClickListener(pick)
        b.ivReceiptPreview.setOnClickListener(pick)

        /* SAVE */
        b.btnSave.setOnClickListener { saveItem() }
        b.btnCancel.setOnClickListener { finish() }
    }

    /* ---------- choose image ---------- */
    private fun pickImage() = imgPicker.launch(
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
    )

    /* ---------- SAVE ---------- */
    private fun saveItem() {
        /* basic validation */
        if (b.etName.text!!.isBlank()) {
            toast("Nama harus diisi"); return
        }
        if (b.etPrice.text!!.isBlank()) {
            toast("Harga harus diisi"); return
        }

        toggleLoading(true)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                /* 1. upload ke Cloudinary jika ada foto */
                selectedUri?.let { imageUrl = uploadToCloudinary(it) }

                /* 2. push node /items */
                pushToFirebase()

                launch(Dispatchers.Main) {
                    toast("Item tersimpan"); finish()
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    toggleLoading(false)
                    toast("Gagal: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun uploadToCloudinary(uri: Uri): String {
        val temp = File(cacheDir, "temp_item")
        contentResolver.openInputStream(uri)!!.use { input ->
            temp.outputStream().use { output -> input.copyTo(output) }
        }
        val result = cloudinary
            .uploader()
            .upload(temp, ObjectUtils.emptyMap())
        return result["secure_url"] as String
    }

    private suspend fun pushToFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance()
            .reference.child("users").child(uid).child("items")

        val key = ref.push().key ?: return
        val item = Item(
            id    = key,
            name  = b.etName.text.toString(),
            stock = b.etStock.text.toString().toInt(),
            code  = b.etCode.text.toString(),
            price = b.etPrice.text.toString().replace("[^\\d]".toRegex(), "").toLong(),
            imageUrl = imageUrl
        )
        ref.child(key).setValue(item).await()
    }

    private fun toggleLoading(load: Boolean) = lifecycleScope.launch(Dispatchers.Main) {
        b.btnSave.isEnabled = !load
        b.btnSave.text = if (load) "Menyimpan…" else "Simpan"
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
