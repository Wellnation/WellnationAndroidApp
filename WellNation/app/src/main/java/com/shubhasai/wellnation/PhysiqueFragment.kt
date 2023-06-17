package com.shubhasai.wellnation

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.shubhasai.wellnation.databinding.FragmentPhysiqueBinding
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import com.google.android.gms.common.api.Scope
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.toObject
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class PhysiqueFragment : Fragment(),ChallengeAdapter.challengeClicked {

    private lateinit var binding:FragmentPhysiqueBinding
    var steps = 0
    var heartscore = 0
    var heartbeat = 0.0
    var countheartbeat =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPhysiqueBinding.inflate(layoutInflater)
        binding.btnCalculate.setOnClickListener {
            steps = 0
            heartscore = 0
            heartbeat = 0.0
            countheartbeat =0
            //initiateGoogleSignIn()
            binding.circularProgressBar.setProgressWithAnimation(0f,0)
            requestGoogleFitPermissions()
        }
        fetchChallenges()
        UpdateScore()
        val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid).get()
        db.addOnSuccessListener {
            if(it.exists()){
                val data = it.toObject(userdetails::class.java)
                if (data != null) {
                    binding.rewards.text = "WellCoins: ${data.wellcoin}"
                    binding.circularProgressBar.setProgressWithAnimation(data.HealthScore.toFloat(),2000)
                    binding.tvScore.text = data.HealthScore.toInt().toString()+"%"
                    binding.firstBar.setProgressPercentage(data.NormalizedHeartRate,true)
                    binding.secondBar.setProgressPercentage(data.NormalizedHeartScore,true)
                    binding.thirdBar.setProgressPercentage(data.NormalizedSteps,true)
                    binding.fourthBar.setProgressPercentage(data.NormalizedLifestyle,true)
                }
            }
        }
        return binding.root
    }

    private val GOOGLE_SIGN_IN_REQUEST_CODE = 123
    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 456
    val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEART_POINTS, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_OXYGEN_SATURATION,FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE,FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE,FitnessOptions.ACCESS_READ)
        .build()
    private lateinit var googleSignInClient: GoogleSignInClient

    private fun initiateGoogleSignIn() {
        googleSignInClient = GoogleSignIn.getClient(requireContext(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build())

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestGoogleFitPermissions() {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                account,
                fitnessOptions
            )
        } else {
            // Permissions already granted, proceed with fetching data
            fetchGoogleFitData()
        }
    }

// Rest of the code remains the same


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        } else if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val account = GoogleSignIn.getLastSignedInAccount(requireContext())
                if (account != null) {
                    // User is signed in, you can proceed with fetching Google Fit data
                    fetchGoogleFitData()
                } else {
                    // User is not signed in, handle the error or initiate the Google Sign-In process again
                    Toast.makeText(activity, "Failed to sign in with Google.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Permissions denied
                Toast.makeText(activity, "Google Fit permissions denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Google Sign-In successful, you can now fetch Google Fit data
            fetchGoogleFitData()
        } catch (e: ApiException) {
            // Google Sign-In failed, handle the error
            Toast.makeText(context, "Google Sign-In failed: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchGoogleFitData() {
        val account = GoogleSignIn.getAccountForExtension(requireContext(), fitnessOptions)
        println("Called and Account is:" + account.id)
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusWeeks(1)

        val readRequest =
            DataReadRequest.Builder()
                // The data request can specify multiple data types to return,
                // effectively combining multiple data queries into one call.
                // This example demonstrates aggregating only one data type.
                .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_HEART_POINTS)
                .aggregate(DataType.TYPE_HEART_RATE_BPM)
                .aggregate(HealthDataTypes.TYPE_OXYGEN_SATURATION)
                .aggregate(HealthDataTypes.TYPE_BLOOD_PRESSURE)
                // Analogous to a "Group By" in SQL, defines how data should be
                // aggregated.
                // bucketByTime allows for a time span, whereas bucketBySession allows
                // bucketing by <a href="/fit/android/using-sessions">sessions</a>.
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()

        Fitness.getHistoryClient(requireContext(), account)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                // Process the data
                // Heart Rate
                println(response.buckets.size)
                for (dataSet in response.buckets.flatMap { it.dataSets }) {
                    dumpDataSet(dataSet)
                }
                Log.i("TAG","Steps:$steps")
                Log.i("TAG","HeartScore:$heartscore")
                Log.i("TAG","sum HeartBeat:$heartbeat")
                Log.i("TAG","Count HeartBeat:$countheartbeat")
                heartbeat /= countheartbeat
                Log.i("TAG","Avg HeartBeat:$heartbeat")
                val NormalizedHeartRate = 1-(Math.abs(heartbeat - 72.0) / 72)
                val NormalizedHeartScore =(heartscore.toFloat()/150)
                val NormalizedSteps = (steps.toFloat()/49000.0)
                val fasting = 1-(Math.abs(90.0 - 85) / 85)
                val postmealsugar = 1-(Math.abs(130.0-120)/120)
                val sys = 1-(Math.abs(120.0 - 125) / 125)
                val dys = 1-(Math.abs(80.0-85)/85)
                val healthScore = (NormalizedHeartScore*0.05+NormalizedHeartRate*0.3+NormalizedSteps*0.05+postmealsugar*0.1+fasting*0.1+sys*0.1+dys*0.1)*100
                Log.i("TAG","Normalised HeartBeat:$NormalizedHeartRate")
                Log.i("TAG","Normalised HeartScore:$NormalizedHeartScore")
                Log.i("TAG","Normalised Steps:$NormalizedSteps")
                Log.i("TAG","HEALTH SCORE:$healthScore")
                Log.i("TAG","Normalised Fasting:$fasting")
                Log.i("TAG","Normalised Postmeal:$postmealsugar")
                val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid)
                db.update("HealthScore",healthScore)
                db.update("NormalizedHeartRate",NormalizedHeartRate*100)
                db.update("NormalizedHeartScore",NormalizedHeartScore.toDouble()*100)
                db.update("NormalizedSteps",NormalizedSteps*100)
                db.update("NormalizedLifestyle",(postmealsugar*0.25+fasting*0.25+sys*0.25+dys*0.25)*100)
                binding.circularProgressBar.setProgressWithAnimation(healthScore.toFloat(),2000)
                binding.tvScore.text = healthScore.toInt().toString()+"%"
                binding.firstBar.setProgressPercentage(NormalizedHeartRate*100,true)
                binding.secondBar.setProgressPercentage(NormalizedHeartScore.toDouble()*100,true)
                binding.thirdBar.setProgressPercentage(NormalizedSteps*100,true)
                binding.fourthBar.setProgressPercentage((postmealsugar*0.25+fasting*0.25+sys*0.25+dys*0.25)*100,true)
                // Heart Points
//                val heartPointsDataSet = response.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)
//                if (heartPointsDataSet != null && !heartPointsDataSet.isEmpty) {
//                    for (dp in heartPointsDataSet.dataPoints) {
//                        val heartPoints = dp.getValue(Field.FIELD_INTENSITY).asFloat()
//                        val startTime = dp.getStartTime(TimeUnit.MILLISECONDS)
//                        val endTime = dp.getEndTime(TimeUnit.MILLISECONDS)
//                        // Do something with heart points data
//                        println("Heart Points: $heartPoints")
//                    }
//                }
            }
            .addOnFailureListener { e ->
                // Handle the error
                Toast.makeText(
                    activity,
                    "Failed to fetch Google Fit data: " + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun dumpDataSet(dataSet: DataSet) {
        Log.i("TAG", "Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints) {
            Log.i("TAG","Data point:")
            Log.i("TAG","\tType: ${dp.dataType.name}")
            Log.i("TAG","\tStart: ${dp.getStartTimeString()}")
            Log.i("TAG","\tEnd: ${dp.getEndTimeString()}")
            for (field in dp.dataType.fields) {
                when(field.name){
                    "steps"->{
                        steps+=dp.getValue(field).toString().toInt()
                    }
                    "duration"->{
                        heartscore+=dp.getValue(field).toString().toInt()
                    }
                    "average"->{
                        heartbeat+=dp.getValue(field).toString().toFloat()
                        countheartbeat++
                    }

                }
                Log.i("TAG","\tField: ${field.name.toString()} Value: ${dp.getValue(field)}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun DataPoint.getStartTimeString() = Instant.ofEpochSecond(this.getStartTime(TimeUnit.SECONDS))
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime().toString()

    @RequiresApi(Build.VERSION_CODES.O)
    fun DataPoint.getEndTimeString() = Instant.ofEpochSecond(this.getEndTime(TimeUnit.SECONDS))
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime().toString()


    fun fetchChallenges(){
        val challenges:ArrayList<challengedata> = ArrayList()
        val db = FirebaseFirestore.getInstance().collection("challenges")
        db.get().addOnSuccessListener {
            if(!it.isEmpty){
                for (doc in it.documents){
                    val challenge = doc.toObject(challengedata::class.java)
                    if (challenge != null) {
                        challenges.add(challenge)
                    }
                }
                binding.rvChallenges.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
                binding.rvChallenges.adapter = ChallengeAdapter(activity,challenges,this)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onChallengeclicked(challenge: challengedata) {
        val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid)
            .collection("challengeStreak").document(challenge.id)
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedStartDate = currentDate.format(formatter)
        val postDate = currentDate.plusDays(challenge.days.toLong()-1)
        val formattedEndDate = postDate.format(formatter)
        val data:HashMap<String,Any> = HashMap()
        data.put("id",challenge.id)
        data.put("startDate",formattedStartDate)
        data.put("endDate",formattedEndDate)
        data.put("lastDay"," ")
        data.put("points",0)
        data.put("max",challenge.target)
        db.set(data)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun UpdateScore(){
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date1String = currentDate.format(formatter)
        val date1 = LocalDate.parse(date1String, formatter)

        // Compare the two dates

        val formattedStartDate = currentDate.format(formatter)
        var score = 0
        val db2 = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid).collection("challengeStreak")
        db2.get().addOnSuccessListener {
            for(doc in it.documents){
                val docdata = doc.toObject(challengeparamenter::class.java)
                if (docdata!=null){
                    val date2 = LocalDate.parse(docdata.endDate, formatter)
                    val comparison = date1.compareTo(date2)

                    if (score != null) {
                        if(comparison>=0){
                            Toast.makeText(activity,docdata.points.toString(),Toast.LENGTH_SHORT).show()
                            score+=docdata.points
                        }
                    }
                }
            }
            val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid)
            db.update("wellcoin",score)
        }

    }
}