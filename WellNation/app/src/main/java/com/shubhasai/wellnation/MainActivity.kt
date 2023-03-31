package com.shubhasai.wellnation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.shubhasai.wellnation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
}