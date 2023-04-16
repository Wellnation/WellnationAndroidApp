package com.shubhasai.wellnation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.shubhasai.wellnation.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val REQUEST_FINE_LOCATION_PERMISSION = 1
    private val REQUEST_BACKGROUND_LOCATION_PERMISSION = 2
    private val REQUEST_COURSE_LOCATION_PERMISSION = 3
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permission()
        binding.floatingActionButton.setOnClickListener {
            setupbotsuport()
        }
        binding.navMenu.setOnItemSelectedListener {
            Log.d("Item",it.itemId.toString())
            NavigationUI.onNavDestinationSelected(it,findNavController(binding.navHostFragment.id))
            findNavController(R.id.nav_host_fragment).popBackStack(it.itemId, inclusive = false)
            true
        }
    }
    fun setupbotsuport(){
        val builder = BottomSheetDialog(this)
        val  inflate = layoutInflater
        val dialogLayout = inflate.inflate(R.layout.bot_drawerlayout,null)
        val webview = dialogLayout.findViewById<WebView>(R.id.webview_bot)
        webview.webViewClient = WebViewClient()
        webview.settings.javaScriptEnabled = true
//        webview.loadUrl("https://chatthing.ai/bots/6ecb864c-68c7-451b-b032-8d674e1888de/")
        webview.loadDataWithBaseURL(null, "<html><body><iframe src=\"https://chatthing.ai/bots/6ecb864c-68c7-451b-b032-8d674e1888de/embed\" width=\"100%\" height=\"600\" frameborder=\"0\"></iframe></body></html>", "text/html", "UTF-8", null)
        builder.setContentView(dialogLayout)
        builder.show()
    }

    fun permission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission for fine location is already granted
            // Do something with the location here
        } else {
            // Request permission for fine location
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_PERMISSION
            )
        }

        // Check if permission for background location is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission for background location
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQUEST_BACKGROUND_LOCATION_PERMISSION
            )
        }

        // Check if permission for course location is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission for course location is already granted
            // Do something with the location here
        } else {
            // Request permission for course location
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_COURSE_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_FINE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission for fine location is granted
                // Do something with the location here
            }
        } else if (requestCode == REQUEST_BACKGROUND_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission for background location is granted
                // Do something with the location here
            }
        } else if (requestCode == REQUEST_COURSE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission for course location is granted
                // Do something with the location here
            }
        }
    }
}