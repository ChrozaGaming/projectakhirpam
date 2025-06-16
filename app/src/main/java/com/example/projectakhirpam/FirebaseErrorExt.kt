package com.example.projectakhirpam

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*

/**
 * Konversi Throwable Firebase → pesan ramah pengguna (Bahasa Indonesia).
 * Gunakan di seluruh aplikasi agar error konsisten.
 */
fun Throwable.toUserMessage(): String = when (this) {
    is FirebaseAuthInvalidCredentialsException ->
        "Kredensial salah atau tidak valid."
    is FirebaseAuthInvalidUserException ->
        "Pengguna tidak ditemukan atau akun dinonaktifkan."
    is FirebaseAuthUserCollisionException ->
        "E-mail sudah terdaftar pada akun lain."
    is FirebaseAuthRecentLoginRequiredException ->
        "Operasi sensitif — silakan login ulang, lalu coba lagi."
    is FirebaseAuthException -> when (errorCode) {
        /* provider Email/Password dimatikan di Firebase Console */
        "ERROR_OPERATION_NOT_ALLOWED" ->
            "Provider Email/Password dinonaktifkan. Aktifkan di Firebase Console."
        /* password belum pernah dibuat (akun Google login) */
        "ERROR_USER_NOT_FOUND" ->
            "Akun tidak memiliki provider Email/Password."
        else -> localizedMessage ?: "Kesalahan Firebase tidak dikenal."
    }
    is FirebaseNetworkException ->
        "Tidak ada koneksi internet."
    else ->
        localizedMessage ?: "Terjadi kesalahan, coba lagi."
}
