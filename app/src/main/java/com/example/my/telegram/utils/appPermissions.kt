package com.example.my.telegram.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val READ_CONTACTS = Manifest.permission.READ_CONTACTS
const val PERMISSIONS_REQUEST = 200

@SuppressLint("ObsoleteSdkInt")
fun checkPermission(permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= 23 &&
        ContextCompat.checkSelfPermission(
            APP_ACTIVITY,
            permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(APP_ACTIVITY, arrayOf(permission), PERMISSIONS_REQUEST)
        false
    } else true

}