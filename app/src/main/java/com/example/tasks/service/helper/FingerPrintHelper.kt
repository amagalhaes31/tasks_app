package com.example.tasks.service.helper

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager


// Verifica se o dispositivo possui identificação biométrica
class FingerPrintHelper {

    companion object {

        // Verifica a permissão para uso do fingerprintf
        fun isAuthenticationAvailable(context: Context): Boolean {

            // Verifica a SDK do SO Android
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return false
            }

            val biometricManager: BiometricManager = BiometricManager.from(context)

            // verifica o status do fingerprintf
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> return true                                   // Sucesso ao acesso
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> return false                        // SDK acima da 23, porém por algum motivo o android não possui o leitor digital
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> return false                     // Hardware indisponível
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> return false                      // Hardware presente e disponivel, mas não configurado corretamete
            }

            return false;
        }
    }
}