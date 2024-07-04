package com.dimasfs.kpu.model

data class Pemilih(
    var nik: String,
    var nama: String,
    var nomorHandphone: String,
    var jenisKelamin: String,
    var tanggalPendataan: String,
    var alamat: String,
    var gambar: String,
    val latitude: Double,
    val longitude: Double
)
