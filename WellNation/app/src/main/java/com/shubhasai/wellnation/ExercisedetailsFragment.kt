package com.shubhasai.wellnation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
                binding.tvExerciseName.text = exerciseResult.exercise_name
                binding.tvForce.text = "Force: "+exerciseResult.Force
                binding.tvGrips.text = "Grip: "+exerciseResult.Grips
                binding.tvTarget.text ="Target: "+ exerciseResult.target.toString()
                binding.tvDifficulty.text = "Difficulty: "+exerciseResult.Difficulty
                binding.tvCategory.text = "Category: "+exerciseResult.Category
                binding.tvAka.text = "AKA: "+exerciseResult.Aka
//                binding.tvSteps.text = "Steps: "+exerciseResult.steps.toString()
//                binding.tvVideoURL.text = "Video URL: "+exerciseResult.videoURL.toString()
               // binding.tvYoutubeURL.text = "YOUTUBE URL: "+exerciseResult.youtubeURL
                binding.tvYoutubebtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exerciseResult.youtubeURL))
                    startActivity(intent)
                }
                var moredetailsvisibility = false
                binding.btnmore.setOnClickListener {
                    moredetailsvisibility = !moredetailsvisibility
                    if (moredetailsvisibility){
                        binding.btnmore.text = "Hide Details"
                        binding.tvDetails.visibility = View.VISIBLE
                    }
                    else{
                        binding.btnmore.text = "More Details"
                        binding.tvDetails.visibility = View.GONE
                    }
                }

                showsteps(exerciseResult.steps)
                showviedeo(exerciseResult.videoURL)
            } else {
                // Handle the error here
            }
        }
    }
    fun showsteps(steps:List<String>){
        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout
        val adapter = ExerciseStepsAdapter(steps) // Replace `getData()` with your data source method

        // Set the adapter to the ViewPager
        viewPager.adapter = adapter

        // Connect the TabLayout with the ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Customize the tab titles here if needed
            tab.text = "Step ${position+1}"
        }.attach()
    }
    fun showviedeo(videos:List<String>){
        val viewPager: ViewPager2 = binding.viewPager2
        val tabLayout: TabLayout = binding.tabLayout2
        val adapter =
            activity?.let { ExerciseVideoAdapter(videos, it) } // Replace `getData()` with your data source method

        // Set the adapter to the ViewPager
        viewPager.adapter = adapter

        // Connect the TabLayout with the ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Customize the tab titles here if needed
            tab.text = "${position+1}"
        }.attach()
    }
}