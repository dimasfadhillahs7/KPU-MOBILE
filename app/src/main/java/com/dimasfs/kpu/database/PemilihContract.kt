package com.dimasfs.kpu.database

import android.provider.BaseColumns

object PemilihContract {
    class PemilihEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "pemilih"
            const val COLUMN_NIK = "nik"
            const val COLUMN_NAMA = "nama"
            const val COLUMN_NO_HP = "nomor_handphone"
            const val COLUMN_JENIS_KELAMIN = "jenis_kelamin"
            const val COLUMN_TANGGAL = "tanggal_pendataan"
            const val COLUMN_ALAMAT = "alamat"
            const val COLUMN_GAMBAR = "gambar"
            const val COLUMN_LATITUDE = "latitude"
            const val COLUMN_LONGITUDE = "longitude"

        }
    }
}