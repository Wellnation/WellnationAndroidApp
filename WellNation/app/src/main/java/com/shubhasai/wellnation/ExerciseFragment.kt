package com.shubhasai.wellnation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.shubhasai.wellnation.databinding.FragmentExerciseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ExerciseFragment : Fragment(),ExerciseAdapter.ExerciseClicked {
    private lateinit var binding: FragmentExerciseBinding
    private var exerciseList: ArrayList<exerciseresultItem> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExerciseBinding.inflate(inflater, container, false)
        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handle the user's selection from the Spinner here
                when (position) {
                    0 -> {
                        binding.filterbycategories.visibility = View.VISIBLE
                        binding.filterbydifficulties.visibility = View.GONE
                        binding.filterbyforce.visibility = View.GONE
                        binding.filterbymuscle.visibility = View.GONE
                    }
                    1 -> {
                        binding.filterbycategories.visibility = View.GONE
                        binding.filterbydifficulties.visibility = View.VISIBLE
                        binding.filterbyforce.visibility = View.GONE
                        binding.filterbymuscle.visibility = View.GONE
                    }
                    2 -> {
                        binding.filterbycategories.visibility = View.GONE
                        binding.filterbydifficulties.visibility = View.GONE
                        binding.filterbyforce.visibility = View.VISIBLE
                        binding.filterbymuscle.visibility = View.GONE
                    }
                    3 -> {
                        binding.filterbycategories.visibility = View.GONE
                        binding.filterbydifficulties.visibility = View.GONE
                        binding.filterbyforce.visibility = View.GONE
                        binding.filterbymuscle.visibility = View.VISIBLE
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        binding.filterbycategoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle the user's selection from the Spinner here
                val selectedRadius = parent?.getItemAtPosition(position).toString()
                getexercisedatabycategory(selectedRadius)
                Log.d("ExerciseFragment", "onCreateView: ${exerciseList.size}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        binding.filterbyforceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle the user's selection from the Spinner here
                val selectedRadius = parent?.getItemAtPosition(position).toString()
                getexercisedatabyforce(selectedRadius)
                Log.d("ExerciseFragment", "onCreateView: ${exerciseList.size}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        binding.filterDifficultiesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle the user's selection from the Spinner here
                val selectedRadius = parent?.getItemAtPosition(position).toString()
                getexercisedatabydifficulties(selectedRadius)
                Log.d("ExerciseFragment", "onCreateView: ${exerciseList.size}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        getexercisedatabycategory("Barbell")
        return binding.root
    }
    fun getexercisedatabycategory(category:String){
        lifecycleScope.launch {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://musclewiki.p.rapidapi.com/exercises")
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
                val exerciseResult = Gson().fromJson(json, exerciseresults::class.java)
                exerciseList.clear()
                for (exercise in exerciseResult){
                    if (exercise.Category == category){
                        exerciseList.add(exercise)
                    }
                }
                loaddatatorv(exerciseResult)
            } else {
                // Handle the error here
            }
        }

    }
    fun getexercisedatabydifficulties(category:String){
        lifecycleScope.launch {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://musclewiki.p.rapidapi.com/exercises")
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
                val exerciseResult = Gson().fromJson(json, exerciseresults::class.java)
                exerciseList.clear()
                for (exercise in exerciseResult){
                    if (exercise.Difficulty == category){
                        exerciseList.add(exercise)
                    }
                }
                loaddatatorv(exerciseResult)
            } else {
                // Handle the error here
            }
        }

    }
    fun getexercisedatabyforce(category:String){
        lifecycleScope.launch {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://musclewiki.p.rapidapi.com/exercises")
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
                val exerciseResult = Gson().fromJson(json, exerciseresults::class.java)
                exerciseList.clear()
                for (exercise in exerciseResult){
                    if (exercise.Force == category){
                        exerciseList.add(exercise)
                    }
                }
                loaddatatorv(exerciseResult)
            } else {
                // Handle the error here
            }
        }
    }
    fun loaddatatorv(exerciselists: exerciseresults){
        val adapter = ExerciseAdapter(activity, exerciselists, this)
        binding.exerciseRecyclerview.adapter = adapter
        binding.exerciseRecyclerview.layoutManager = LinearLayoutManager(context)
    }

    override fun onExerciseClicked(exercise: exerciseresultItem) {
        val direction = ExerciseFragmentDirections.actionExerciseFragmentToExercisedetailsFragment(exercise.id)
        findNavController().navigate(direction)
    }
}