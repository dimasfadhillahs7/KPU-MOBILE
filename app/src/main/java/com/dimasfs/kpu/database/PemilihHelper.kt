package com.dimasfs.kpu.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dimasfs.kpu.model.Pemilih

class PemilihHelper (context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "pemilu.db"
        private const val DATABASE_VERSION = 1

        private val SQL_CREATE_ENTRIES =
            " CREATE TABLE ${PemilihContract.PemilihEntry.TABLE_NAME} (" +
                    "${PemilihContract.PemilihEntry.COLUMN_NIK} VARCHAR PRIMARY KEY," +
                    "${PemilihContract.PemilihEntry.COLUMN_NAMA} VARCHAR(255)," +
                    "${PemilihContract.PemilihEntry.COLUMN_NO_HP} VARCHAR(255)," +
                    "${PemilihContract.PemilihEntry.COLUMN_JENIS_KELAMIN} VARCHAR(255)," +
                    "${PemilihContract.PemilihEntry.COLUMN_TANGGAL} VARCHAR(255)," +
                    "${PemilihContract.PemilihEntry.COLUMN_ALAMAT} VARCHAR(255)," +
                    "${PemilihContract.PemilihEntry.COLUMN_GAMBAR} BLOB, " +
                    "${PemilihContract.PemilihEntry.COLUMN_LATITUDE} REAL," +
                    "${PemilihContract.PemilihEntry.COLUMN_LONGITUDE} REAL)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${PemilihContract.PemilihEntry.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
    }

    fun insertData(pemilih : Pemilih){
        val db = writableDatabase
        val sql = " INSERT INTO ${PemilihContract.PemilihEntry.TABLE_NAME} " +
                " (${PemilihContract.PemilihEntry.COLUMN_NIK}, " +
                " ${PemilihContract.PemilihEntry.COLUMN_NAMA}, " +
                " ${PemilihContract.PemilihEntry.COLUMN_NO_HP}, " +
                " ${PemilihContract.PemilihEntry.COLUMN_JENIS_KELAMIN}, " +
                " ${PemilihContract.PemilihEntry.COLUMN_TANGGAL}, " +
                " ${PemilihContract.PemilihEntry.COLUMN_ALAMAT}, " +
                " ${PemilihContract.PemilihEntry.COLUMN_GAMBAR}, " +
                "${PemilihContract.PemilihEntry.COLUMN_LATITUDE}, " +
                "${PemilihContract.PemilihEntry.COLUMN_LONGITUDE}) " +
                "VALUES ('${pemilih.nik}', '${pemilih.nama}', '${pemilih.nomorHandphone}', " +
                "'${pemilih.jenisKelamin}', '${pemilih.tanggalPendataan}', '${pemilih.alamat}', " +
                "'${pemilih.gambar}', ${pemilih.latitude}, ${pemilih.longitude})"
        db.execSQL(sql)
        db.close()
    }

    fun getPemilih() : ArrayList<Pemilih>{
        val listPemilih = ArrayList<Pemilih>()
        val db = readableDatabase
        val sql = " SELECT * FROM ${PemilihContract.PemilihEntry.TABLE_NAME}"
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToFirst()){
            do {
                val nik = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_NIK))
                val nama = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_NAMA))
                val nomorHandphone = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_NO_HP))
                val jenisKelamin = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_JENIS_KELAMIN))
                val tanggalPendataan = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_TANGGAL))
                val alamat = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_ALAMAT))
                val gambar = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_GAMBAR))
                val lat = cursor.getDouble(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_LATITUDE))
                val lon = cursor.getDouble(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_LONGITUDE))
                listPemilih.add(Pemilih(nik, nama, nomorHandphone, jenisKelamin, tanggalPendataan, alamat, gambar, lat, lon))
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listPemilih
    }

    fun getPemilihByNik(nik: String): Pemilih? {
        val db = readableDatabase
        val sql = "SELECT * FROM ${PemilihContract.PemilihEntry.TABLE_NAME} WHERE ${PemilihContract.PemilihEntry.COLUMN_NIK} = '$nik'"
        val cursor = db.rawQuery(sql, null)
        var pemilih: Pemilih? = null

        if (cursor.moveToFirst()) {
            val nama = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_NAMA))
            val nomorHandphone = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_NO_HP))
            val jenisKelamin = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_JENIS_KELAMIN))
            val tanggalPendataan = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_TANGGAL))
            val alamat = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_ALAMAT))
            val gambar = cursor.getString(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_GAMBAR))
            val lat = cursor.getDouble(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_LATITUDE))
            val lon = cursor.getDouble(cursor.getColumnIndexOrThrow(PemilihContract.PemilihEntry.COLUMN_LONGITUDE))
            pemilih = Pemilih(nik, nama, nomorHandphone, jenisKelamin, tanggalPendataan, alamat, gambar, lat, lon)
        }

        cursor.close()
        db.close()
        return pemilih
    }


    fun checkDataOntable(nik : String) : Boolean{
        val db = readableDatabase
        val sql = "SELECT * FROM ${PemilihContract.PemilihEntry.TABLE_NAME} WHERE ${PemilihContract.PemilihEntry.COLUMN_NIK} = '$nik'"
        val cursor = db.rawQuery(sql, null)
        var check = false
        if (cursor.moveToFirst()){
            check = true
        }
        cursor.close()
        db.close()
        return check
    }

    fun deleteData(nik: String): Int {
        val db = writableDatabase
        val result = db.delete(PemilihContract.PemilihEntry.TABLE_NAME, "${PemilihContract.PemilihEntry.COLUMN_NIK}=?", arrayOf(nik))
        db.close()
        return result
    }
}