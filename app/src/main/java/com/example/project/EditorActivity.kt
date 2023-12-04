package com.example.project

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.project.data.AppDatabase
import com.example.project.data.entity.User
import com.example.project.databinding.ActivityEditorBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.imageview.ShapeableImageView
import java.io.ByteArrayOutputStream
import android.widget.ImageView
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditorActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditorBinding
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var nik: EditText
    private lateinit var fullName: EditText
    private lateinit var phone: EditText
    private lateinit var jeniskelamin: RadioGroup
    private lateinit var tanggal: EditText
    private lateinit var alamat: EditText
    private lateinit var gambar: ImageView
    private lateinit var btnSave: Button
    private lateinit var btnAmbil: Button
    private var capturedImage: Bitmap? = null
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nik = findViewById(R.id.nik)
        fullName = findViewById(R.id.full_name)
        phone = findViewById(R.id.phone)
        jeniskelamin = findViewById(R.id.jeniskelamin)
        tanggal = findViewById(R.id.tanggal)
        alamat = findViewById(R.id.alamat)
        gambar = findViewById(R.id.gambar)
        btnAmbil = findViewById(R.id.btn_ambil)
        btnSave = findViewById(R.id.btn_save)

        val database = AppDatabase.getInstance(applicationContext)

        val intent = intent.extras
        if (intent != null) {
            val id = intent.getInt("id", 0)
            val user = database.userDao().get(id)

            nik.text = Editable.Factory.getInstance().newEditable(user.nik)
            fullName.text = Editable.Factory.getInstance().newEditable(user.fullName)
            phone.text = Editable.Factory.getInstance().newEditable(user.phone)
            val selectedRadioButtonId = when (user.jeniskelamin) {
                "Laki-laki" -> R.id.radio_laki
                "Perempuan" -> R.id.radio_perempuan
                else -> -1
            }
            if (selectedRadioButtonId != -1) {
                jeniskelamin.check(selectedRadioButtonId)
            }
            tanggal.text = Editable.Factory.getInstance().newEditable(user.tanggal)
            alamat.text = Editable.Factory.getInstance().newEditable(user.alamat)
            // Load gambar if user has image data
//            if (user.gambar != null) {
//                val bitmap = BitmapFactory.decodeByteArray(user.gambar, 0, user.gambar.size)
//                gambar.setImageBitmap(bitmap)
//            }
        }

        btnSave.setOnClickListener {
            val selectedGender = findViewById<RadioButton>(jeniskelamin.checkedRadioButtonId)
            if (nik.text.isNotEmpty() && fullName.text.isNotEmpty() && phone.text.isNotEmpty() && selectedGender.text.isNotEmpty()
                && tanggal.text.isNotEmpty() && alamat.text.isNotEmpty() && capturedImage != null
            ) {
                val imageByteArray = saveImageToDatabase(capturedImage!!)

                val existingUserWithSameNIK = database.userDao().getUserByNIK(nik.text.toString())

                if (existingUserWithSameNIK != null && (intent == null || existingUserWithSameNIK.id?.toInt() != intent.getInt("id", 0))) {
                    // User with same NIK already exists
                    Toast.makeText(
                        applicationContext,
                        "Data dengan NIK yang sama sudah ada",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (intent != null) {
                        // Edit Data
                        database.userDao().update(
                            User(
                                intent.getInt("id", 0).toLong(),
                                nik.text.toString(),
                                fullName.text.toString(),
                                phone.text.toString(),
                                selectedGender.text.toString(),
                                tanggal.text.toString(),
                                alamat.text.toString(),
                                imageByteArray
                            )
                        )
                    } else {
                        // Tambah data
                        val newUser = User(
                            null,
                            nik.text.toString(),
                            fullName.text.toString(),
                            phone.text.toString(),
                            selectedGender.text.toString(),
                            tanggal.text.toString(),
                            alamat.text.toString(),
                            imageByteArray
                        )
                        database.userDao().insertAll(newUser)
                    }

                    finish()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Silahkan isi data dengan valid",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Gambar
        btnAmbil.isEnabled = true
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            btnAmbil.isEnabled = true
        }

        btnAmbil.setOnClickListener {
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, 101)
        }

        // Tanggal
        val calendar = Calendar.getInstance()
        tanggal.setOnClickListener {
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat.getDateInstance()
                    tanggal.setText(dateFormat.format(selectedCalendar.time))
                }, year, month, day
            )
            datePickerDialog.show()
        }

        // Lokasi
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        binding.getLocation.setOnClickListener {
            // Langkah 1 adalah memeriksa izin sendiri
            checkLocationPermission()
        }
    }

    // Lokasi
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Izin lokasi sudah diberikan
            checkGPS()
        } else {
            // Izin lokasi belum diberikan, meminta izin
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    private fun checkGPS() {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        var builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(
            this.applicationContext
        )
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                // Saat GPS aktif
                val response = task.getResult(
                    ApiException::class.java
                )
                getUserLocation()
            } catch (e: ApiException) {
                // Saat GPS mati
                e.printStackTrace()
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // di sini kita mengirimkan permintaan untuk mengaktifkan GPS
                        val resolveApiException = e as ResolvableApiException
                        resolveApiException.startResolutionForResult(this, 200)
                    } catch (sendIntentException: IntentSender.SendIntentException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // saat pengaturan tidak tersedia
                    }
                }
            }
        }
    }

    private fun openLocation(location: String) {
        // Menjalankan tindakan ketika elemen alamat diklik
        binding.alamat.setOnClickListener {
            // Memeriksa apakah teks pada elemen alamat tidak kosong
            if (!binding.getLocation.text.isEmpty()) {
                // Membentuk URI dengan informasi lokasi
                val uri = Uri.parse("geo:0,0?q=$location")

                // Membuat intent untuk membuka Google Maps dengan URI yang ditentukan
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")

                // Memulai aktivitas untuk membuka Google Maps
                startActivity(intent)
            }
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Meminta izin lokasi jika belum diberikan
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val location = task.result
                if (location != null) {
                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val addressLine = address?.get(0)?.getAddressLine(0)
                        binding.alamat.setText(addressLine)
                        val addressLocation = address?.get(0)?.getAddressLine(0)
                        openLocation(addressLocation.toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    // Jika lokasi tidak tersedia
                    Toast.makeText(this, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Jika terjadi kesalahan dalam mengambil lokasi
                Toast.makeText(this, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            capturedImage = data?.extras?.get("data") as Bitmap
            gambar.setImageBitmap(capturedImage)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            btnAmbil.isEnabled = true
        }
    }

    private fun saveImageToDatabase(imageBitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}