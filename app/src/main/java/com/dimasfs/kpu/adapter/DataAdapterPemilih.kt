package com.dimasfs.kpu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dimasfs.kpu.database.PemilihHelper
import com.dimasfs.kpu.databinding.ItemPemilihBinding
import com.dimasfs.kpu.model.Pemilih

class DataAdapterPemilih(val dataPemilih: ArrayList<Pemilih> , private val pemilihHelper: PemilihHelper) : RecyclerView.Adapter<DataAdapterPemilih.ViewHolder>() {
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(nik : String)
        fun onItemDelete(nik: String)
    }

    fun setOnItemClickListener (listener: onItemClickListener){
        mListener = listener
    }

    class ViewHolder(val binding : ItemPemilihBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemPemilihBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }



    override fun getItemCount(): Int {
        return dataPemilih.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvNama.text = dataPemilih[position].nama
        holder.binding.tvNik.text = "NIK : "+dataPemilih[position].nik.toString()
        holder.binding.itemPemilih.setOnClickListener {
            mListener.onItemClick(dataPemilih[position].nik.toString())
        }
        holder.binding.btnDelete.setOnClickListener {
            mListener.onItemDelete(dataPemilih[position].nik.toString())
        }
    }

    fun setDataPemilih(listPemilih : ArrayList<Pemilih>) {
        dataPemilih.clear()
        dataPemilih.addAll(listPemilih)
        notifyDataSetChanged()
    }
}
