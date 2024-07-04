package com.dimasfs.kpu.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dimasfs.kpu.R
import com.dimasfs.kpu.database.PemilihHelper
import com.dimasfs.kpu.databinding.FragmentDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetailFragment : BottomSheetDialogFragment() {
    lateinit var database : PemilihHelper
    lateinit var binding : FragmentDetailBinding

    companion object {
        fun newInstance(key: String): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle().apply {
                putString("nik", key)
            }
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = PemilihHelper(requireContext())
        val nik = arguments?.getString("nik")

        if (nik != null) {
            val warga = database.getPemilihByNik(nik)
            warga?.let {
                binding.tvNIK.text = "NIK : ${it.nik}"
                binding.tvNama.text = "Nama : ${it.nama}"
                binding.tvNomorHP.text = "Nomor HP : ${it.nomorHandphone}"
                binding.tvKelamin.text = "Jenis Kelamin : ${it.jenisKelamin}"
                binding.tvTanggal.text = "Tanggal Pendataan : ${it.tanggalPendataan}"
                binding.tvAlamat.text = "Lokasi : ${it.alamat}"
                binding.ivGambar.setImageBitmap(BitmapFactory.decodeFile(it.gambar))
                Log.d("DetailFragment", "onViewCreated: ${it.gambar}")
                Log.d("DetailFragment", "onViewCreated: ${it.alamat}")
            }
        }
    }
}