package com.example.project.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "nik") var nik: String?,
    @ColumnInfo(name = "full_name") var fullName: String?,
    @ColumnInfo(name = "phone") var phone: String?,
    @ColumnInfo(name = "jenis_kelamin") var jeniskelamin: String?,
    @ColumnInfo(name = "tanggal") var tanggal: String?,
    @ColumnInfo(name = "alamat") var alamat: String?,
    @ColumnInfo(name = "gambar_uri") var gambarUri: ByteArray? // Menggunakan String untuk menyimpan URI
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (nik != other.nik) return false
        if (fullName != other.fullName) return false
        if (phone != other.phone) return false
        if (jeniskelamin != other.jeniskelamin) return false
        if (tanggal != other.tanggal) return false
        if (alamat != other.alamat) return false
        if (!gambarUri.contentEquals(other.gambarUri)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (nik?.hashCode() ?: 0)
        result = 31 * result + (fullName?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + (jeniskelamin?.hashCode() ?: 0)
        result = 31 * result + (tanggal?.hashCode() ?: 0)
        result = 31 * result + (alamat?.hashCode() ?: 0)
        result = 31 * result + gambarUri.contentHashCode()
        return result
    }
}