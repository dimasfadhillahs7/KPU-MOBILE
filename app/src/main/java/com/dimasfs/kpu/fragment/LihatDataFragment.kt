package com.dimasfs.kpu.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dimasfs.kpu.R
import com.dimasfs.kpu.adapter.DataAdapterPemilih
import com.dimasfs.kpu.database.PemilihHelper
import com.dimasfs.kpu.databinding.FragmentLihatDataBinding
import com.dimasfs.kpu.model.Pemilih

class LihatDataFragment : Fragment() {

    lateinit var binding: FragmentLihatDataBinding
    lateinit var pemilihHelper: PemilihHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLihatDataBinding.inflate(inflater, container, false)
        pemilihHelper = PemilihHelper(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DataAdapterPemilih(ArrayList(), pemilihHelper)

        val listPemilih: ArrayList<Pemilih> = pemilihHelper.getPemilih()
        adapter.setDataPemilih(listPemilih)
        adapter.setOnItemClickListener(object :DataAdapterPemilih.onItemClickListener{
            override fun onItemClick(nik: String) {
                DetailFragment.newInstance(nik).show(parentFragmentManager, "DetailFragment")
            }

            override fun onItemDelete(nik: String) {
                showDeleteConfirmationDialog(nik, adapter)
            }
        })
        binding.rvPemilih.adapter = adapter
        binding.rvPemilih.layoutManager = LinearLayoutManager(context)


        binding.tambah.setOnClickListener {
            findNavController().navigate(R.id.action_nav_pemilih_to_inputDataFragment)
        }
    }

    private fun showDeleteConfirmationDialog(nik: String, adapter: DataAdapterPemilih) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus data ini?")
            .setPositiveButton("Ya") { dialog, which ->
                pemilihHelper.deleteData(nik)
                adapter.setDataPemilih(pemilihHelper.getPemilih())
                Toast.makeText(requireContext(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}