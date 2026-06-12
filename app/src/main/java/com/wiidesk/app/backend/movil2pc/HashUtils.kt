package com.wiidesk.app.backend.movil2pc

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HashUtils {
    private const val ITERATIONS = 100_000
    private const val KEY_LENGTH_BYTES = 32 // 256 bits

    /**
     * Verifica si el PIN provisto coincide con el salt y el hash esperado.
     */
    fun verifyPin(pin: String, saltHex: String, expectedHashHex: String): Boolean {
        return try {
            val salt = hexToByteArray(saltHex)
            val expectedHash = hexToByteArray(expectedHashHex)
            val passwordBytes = pin.toByteArray(Charsets.UTF_8)

            val derivedKey = pbkdf2HmacSha256(passwordBytes, salt, ITERATIONS, KEY_LENGTH_BYTES)

            derivedKey.contentEquals(expectedHash)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Implementación pura de PBKDF2-HMAC-SHA256 compatible con Rust ring.
     */
    private fun pbkdf2HmacSha256(
        password: ByteArray,
        salt: ByteArray,
        iterations: Int,
        keyLengthBytes: Int
    ): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        val result = ByteArray(keyLengthBytes)
        val hLen = 32 // Largo de salida de SHA-256 (32 bytes)
        val l = (keyLengthBytes + hLen - 1) / hLen
        val r = keyLengthBytes - (l - 1) * hLen

        val u = ByteArray(hLen)
        val t = ByteArray(hLen)
        val blockIndexBytes = ByteArray(4)

        for (i in 1..l) {
            // blockIndexBytes en Big-Endian
            blockIndexBytes[0] = (i ushr 24).toByte()
            blockIndexBytes[1] = (i ushr 16).toByte()
            blockIndexBytes[2] = (i ushr 8).toByte()
            blockIndexBytes[3] = i.toByte()

            // U_1 = PRF(P, S || INT(i))
            mac.init(SecretKeySpec(password, "HmacSHA256"))
            mac.update(salt)
            mac.update(blockIndexBytes)
            val u1 = mac.doFinal()
            System.arraycopy(u1, 0, t, 0, hLen)
            System.arraycopy(u1, 0, u, 0, hLen)

            // Iteraciones adicionales
            for (j in 2..iterations) {
                mac.init(SecretKeySpec(password, "HmacSHA256"))
                mac.update(u)
                val uj = mac.doFinal()
                System.arraycopy(uj, 0, u, 0, hLen)
                for (k in 0 until hLen) {
                    t[k] = (t[k].toInt() xor u[k].toInt()).toByte()
                }
            }

            val destPos = (i - 1) * hLen
            val len = if (i == l) r else hLen
            System.arraycopy(t, 0, result, destPos, len)
        }
        return result
    }

    /**
     * Convierte un String hexadecimal a un ByteArray.
     */
    fun hexToByteArray(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            val h = Character.digit(hex[i], 16)
            val l = Character.digit(hex[i + 1], 16)
            if (h == -1 || l == -1) {
                throw IllegalArgumentException("Carácter hexadecimal inválido")
            }
            data[i / 2] = ((h shl 4) or l).toByte()
        }
        return data
    }

    /**
     * Convierte un ByteArray a un String hexadecimal.
     */
    fun byteArrayToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

