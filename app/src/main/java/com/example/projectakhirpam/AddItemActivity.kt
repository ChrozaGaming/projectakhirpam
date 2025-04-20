package com.example.projectakhirpam

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityAddItemsBinding
import java.text.NumberFormat
import java.util.*

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol kembali (ikon back)
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Tombol batal (kembali juga)
        binding.btnCancel.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Format harga saat mengetik
        binding.etPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    try {
                        val cleanString = s.toString().replace("[Rp,.\\s]".toRegex(), "")
                        val parsed = cleanString.toLong()
                        val formatted = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(parsed)
                        binding.tvPriceDisplay.text = formatted
                    } catch (_: Exception) {
                        binding.tvPriceDisplay.text = "Rp0"
                    }
                } else {
                    binding.tvPriceDisplay.text = "Rp0"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Tombol simpan
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val stock = binding.etStock.text.toString().trim()
            val code = binding.etCode.text.toString().trim()
            val price = binding.etPrice.text.toString().trim()

            if (name.isEmpty() || stock.isEmpty() || code.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua field", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Simpan data ke database atau kirim ke intent
                Toast.makeText(this, "Item berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
