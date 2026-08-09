package com.ustad

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class UstadApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Ticket 6: Firebase Emulator Suite configuration for local dev
        if (BuildConfig.USE_EMULATOR) {
            try {
                val host = if (BuildConfig.EMULATOR_HOST.isNotEmpty()) BuildConfig.EMULATOR_HOST else "10.0.2.2"
                val authPort = 9099
                val firestorePort = 8080
                val storagePort = 9199

                FirebaseAuth.getInstance().useEmulator(host, authPort)
                FirebaseFirestore.getInstance().useEmulator(host, firestorePort)
                FirebaseStorage.getInstance().useEmulator(host, storagePort)
            } catch (e: Exception) {
                // Ignore if emulators were already configured
                e.printStackTrace()
            }
        }
    }
}
