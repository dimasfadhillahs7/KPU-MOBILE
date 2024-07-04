package com.dimasfs.kpu.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.dimasfs.kpu.R
import com.dimasfs.kpu.database.PemilihHelper
import com.dimasfs.kpu.databinding.FragmentInputDataBinding
import com.dimasfs.kpu.model.Pemilih
import com.dimasfs.kpu.utils.createCustomTempFile
import com.dimasfs.kpu.utils.reduceFileImage
import com.dimasfs.kpu.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION")
class InputDataFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var database: PemilihHelper
    private lateinit var binding: FragmentInputDataBinding
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
     var lat : Double = 0.0
     var lon : Double = 0.0


    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val REQUEST_CODE_PERMISSIONS = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        database = PemilihHelper(requireContext())
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())


        getLocation()
        getTanggal()
        getPhoto()
        save()
    }



    private fun save() {
        binding.btnSimpan.setOnClickListener {

            val nik = binding.etNIK.text.toString()
            val nama = binding.etNama.text.toString()
            val noHp = binding.etTelp.text.toString()
            val jenisKelamin = when (binding.radioGroupKelamin.checkedRadioButtonId) {
                R.id.radioButtonLakiLaki -> "Laki-Laki"
                R.id.radioButtonPerempuan -> "Perempuan"
                else -> "Kelamin Tidak Diketahui"
            }
            val tanggalPendataan = binding.etTanggal.text.toString()
            val alamat = binding.etAlamat.text.toString()

            if (nik.isNotEmpty() && nama.isNotEmpty() && noHp.isNotEmpty() && jenisKelamin.isNotEmpty() &&
                tanggalPendataan.isNotEmpty() && alamat.isNotEmpty() && getFile != null
            ) {

                val file = reduceFileImage(getFile as File)
                val gambar = file.absolutePath  // Ambil path gambar


                    val pemilih =
                        Pemilih(nik, nama, noHp, jenisKelamin, tanggalPendataan, alamat, gambar, lat, lon)

                    if (database.checkDataOntable(nik)) {
                        Toast.makeText(requireContext(), "Data sudah ada", Toast.LENGTH_SHORT)
                            .show()
                        DetailFragment.newInstance(nik)
                            .show(parentFragmentManager, "DetailFragment")
                    } else {
                        database.insertData(pemilih)
                        Toast.makeText(
                            requireContext(),
                            "Data berhasil disimpan",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.apply {
                            etNIK.setText("")
                            etNama.setText("")
                            etTelp.setText("")
                            etTanggal.setText("")
                            etAlamat.setText("")
                            imageUpload.setImageResource(R.drawable.camera)
                        }
                    }
            } else {
                    Toast.makeText(
                        requireContext(),
                        "Harap Isi Semua Data dan Pilih Gambar!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
    }

    private fun getPhoto() {
        binding.btnPilihFoto.setOnClickListener {
            val pilih = arrayOf("Ambil Foto", "Pilih dari Galeri")
            val builder = AlertDialog.Builder(requireContext())
            builder.setItems(pilih) { _, menu ->
                when (menu) {
                    0 -> {
                        if (allPermissionsGranted()) {
                            startTakePhoto()
                        } else {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                REQUIRED_PERMISSIONS,
                                REQUEST_CODE_PERMISSIONS
                            )
                        }
                    }
                    1 -> {
                        startGallery()
                    }
                }
            }
            builder.show()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Gambar!")
        launcherIntentGallery.launch(chooser)
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, requireContext())
                getFile = myFile
                binding.imageUpload.setImageURI(uri)
            }
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        createCustomTempFile(requireActivity().application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.dimasfs.kpu",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                binding.imageUpload.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun getTanggal() {
        binding.etTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
            datePickerDialog.show()

        }
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)

        binding.etTanggal.setText(formattedDate)
    }



    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        binding.btnCekAlamat.setOnClickListener{
            val locationTask = fusedLocation.lastLocation
            locationTask.addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addressList: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    lat = location.latitude
                    lon = location.longitude
                    if (addressList!!.isNotEmpty()) {
                        val streetAddress = addressList[0].getAddressLine(0)
                        binding.etAlamat.setText(streetAddress)
                    } else {
                        Toast.makeText(requireContext(), "Lokasi Tidak Ditemukan!, Aktifkan Lokasi Anda", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Lokasi Tidak Ditemukan!, Aktifkan Lokasi Anda", Toast.LENGTH_SHORT).show()
                }
            }

            locationTask.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Gagal mengambil lokasi. Pastikan layanan lokasi diaktifkan.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

//        private fun getCoordinatesFromAddress(address: String): Pair<Double, Double>? {
//            val geocoder = Geocoder(requireContext(), Locale.getDefault())
//            val addresses: List<Address>?
//
//            return try {
//                addresses = geocoder.getFromLocationName(address, 1)
//                if (addresses != null && addresses.isNotEmpty()) {
//                    val location = addresses[0]
//                    Pair(location.latitude, location.longitude)
//                } else {
//                    null
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        }

}