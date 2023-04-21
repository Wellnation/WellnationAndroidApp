package com.shubhasai.wellnation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.shubhasai.wellnation.databinding.FragmentExercisedetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ExercisedetailsFragment : Fragment() {
    private lateinit var binding: FragmentExercisedetailsBinding
    private val safeArgs by navArgs<ExercisedetailsFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExercisedetailsBinding.inflate(inflater, container, false)
        getexercisedetails()
        return binding.root
    }
    fun getexercisedetails(){
        lifecycleScope.launch {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://musclewiki.p.rapidapi.com/exercises/${safeArgs.id}")
                .get()
                .addHeader("X-RapidAPI-Key", "8ef956b0bemshf87a92439462ec1p1eceb9jsnbf2330ecf6d9")
                .addHeader("X-RapidAPI-Host", "musclewiki.p.rapidapi.com")
                .build()

            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val json = responseBody
                val exerciseResult = Gson().fromJson(json, exerciseresultItem::class.java)
                binding.tvDetails.text = exerciseResult.details
                binding.tvExerciseName.text = "Exercise Name: "+exerciseResult.exercise_name
                binding.tvForce.text = "Force: "+exerciseResult.Force
                binding.tvGrips.text = "Grip: "+exerciseResult.Grips
                binding.tvTarget.text ="Target: "+ exerciseResult.target.toString()
                binding.tvDifficulty.text = "Difficulty: "+exerciseResult.Difficulty
                binding.tvCategory.text = "Category: "+exerciseResult.Category
                binding.tvAka.text = "AKA: "+exerciseResult.Aka
                binding.tvSteps.text = "Steps: "+exerciseResult.steps.toString()
                binding.tvVideoURL.text = "Video URL: "+exerciseResult.videoURL.toString()
                binding.tvYoutubeURL.text = "YOUTUBE URL: "+exerciseResult.youtubeURL
            } else {
                // Handle the error here
            }
        }
    }
}