package com.example.globaltranslation

import android.app.Application
import com.example.globaltranslation.data.migration.AppMigrationManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class GloabTranslationApplication : Application() {
    
    @Inject
    lateinit var appMigrationManager: AppMigrationManager
    
    // Application-scoped coroutine scope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate() {
        super.onCreate()
        
        // Handle app version updates and migrations on startup
        applicationScope.launch {
            try {
                val currentVersionCode = packageManager
                    .getPackageInfo(packageName, 0)
                    .longVersionCode.toInt()
                
                appMigrationManager.handleAppUpdate(currentVersionCode)
            } catch (e: Exception) {
                // Log error but don't crash the app
                android.util.Log.e("GloabTranslationApp", "Migration error", e)
            }
        }
    }
}
