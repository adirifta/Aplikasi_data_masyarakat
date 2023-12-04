package com.example.project

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class InformasiActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informasi)

        val titleTextView: TextView = findViewById(R.id.infoPemilihanTitle)
        val contentTextView: TextView = findViewById(R.id.infoPemilihanContent)

        // Mengatur konten informasi pemilihan umum
        val infoContent = "Aplikasi ini digunakan oleh petugas lapangan untuk mendata Keluarga yang memiliki hak pilih. Petugas dapat melakukan pendataan calon pemilih dengan mengisi data dan mengambil foto bukti rekam kunjungan pengambilan data."

        titleTextView.text = "Informasi Pemilihan Umum"
        contentTextView.text = infoContent
    }
}