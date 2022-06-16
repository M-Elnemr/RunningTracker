package com.elnemr.runningtracker.presentation.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData

object RequestPermissionsHelper {
    private lateinit var permissionRequest: ActivityResultLauncher<Array<String>>
    val permissionResult: MutableLiveData<HashMap<String, Boolean>> = MutableLiveData()
    fun registerPermission(fragmentActivity: FragmentActivity) {

        permissionRequest = fragmentActivity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            result.entries.forEach {
                val hashMap: HashMap<String, Boolean> = hashMapOf()
                hashMap[it.key] = it.value
                permissionResult.postValue(hashMap)
            }
        }
    }

    fun requestPermissions(permission: Array<String>) {
        permissionRequest.launch(permission)
    }

    fun checkIfPermissionGranted(permission: String, context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkIfPermissionGranted(permissions: List<String>, context: Context): Boolean {
        permissions.forEach {
            if (
                ContextCompat.checkSelfPermission(
                    context,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            )
                return false
        }
        return true

    }
}