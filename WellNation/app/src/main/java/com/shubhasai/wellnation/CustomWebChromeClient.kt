package com.shubhasai.wellnation

import android.Manifest
import android.content.pm.PackageManager
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class CustomWebChromeClient(private val fragment: Fragment) : WebChromeClient() {
    override fun onPermissionRequest(request: PermissionRequest) {
        val permissions = request.resources ?: return
        val permissionsList = mutableListOf<String>()

        if (request.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
            permissionsList.add(Manifest.permission.CAMERA)
        }

        if (request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
            permissionsList.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissionsList.isNotEmpty()) {
            val permissionArray = permissionsList.toTypedArray()

            if (ContextCompat.checkSelfPermission(
                    fragment.requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    fragment.requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permissions dialog
                ActivityCompat.requestPermissions(
                    fragment.requireActivity(),
                    permissionArray,
                    PERMISSION_REQUEST_CODE
                )
            } else {
                // Permissions already granted
                request.grant(permissions)
            }
        } else {
            // No permissions requested
            request.deny()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}
