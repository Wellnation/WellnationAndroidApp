package com.shubhasai.wellnation

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChallengeAdapter (private val context: Context?, val challenges: ArrayList<challengedata>,val listener:challengeClicked) : RecyclerView.Adapter<ChallengeAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cname: TextView = itemView.findViewById(R.id.tvCname)
        val cardView:CardView = itemView.findViewById(R.id.cardViewChallenge)
        val cdays: TextView = itemView.findViewById(R.id.tvCdays)
        val cdesc: TextView = itemView.findViewById(R.id.tvCdesc)
        val cpoints: TextView = itemView.findViewById(R.id.tvCpoint)
        val cyourpoints: TextView = itemView.findViewById(R.id.tvYourScore)
        val cImage: ImageView = itemView.findViewById(R.id.challengeImage)
        val bookbtn: Button = itemView.findViewById(R.id.btnParticipate)
    }

    interface challengeClicked {
        fun onChallengeclicked(challenge:challengedata){

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = ChallengeAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.challenge_item, parent, false)
        )
        viewholder.bookbtn.setOnClickListener {
            listener.onChallengeclicked(challenges[viewholder.adapterPosition])
            val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid)
                .collection("challengeStreak").document(challenges[viewholder.adapterPosition].id)
            db.get().addOnSuccessListener {
                if (it.exists()){
                    viewholder.bookbtn.isEnabled = false
                    viewholder.bookbtn.text ="PARTICIPATED"
                    Toast.makeText(context,"Click on the card to proceed with the challenge",Toast.LENGTH_LONG).show()
                }
            }
        }
        viewholder.cardView.setOnClickListener {
            showYesNoDialog(viewholder)

        }
        return viewholder
    }

    override fun getItemCount(): Int {
        return challenges.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val challenge = challenges[position]
        holder.cname.text = challenge.name
        holder.cdesc.text = challenge.description
        holder.cpoints.text = challenge.target.toString()+" Coins"
        holder.cdays.text = challenge.days.toString() + "Days"
        Glide.with(context!!).load(challenge.image).error(R.drawable.ic_campaign).into(holder.cImage)
        val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid)
            .collection("challengeStreak").document(challenge.id)
        db.get().addOnSuccessListener {
            if(it.exists()){
                val data = it.toObject(challengeparamenter::class.java)
                holder.bookbtn.text = "PARTICIPATED"
                holder.bookbtn.isEnabled = false
                if (data != null) {
                    holder.cyourpoints.text = "Your Score: ${data.points}"
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showYesNoDialog(viewholder:ChallengeAdapter.ViewHolder) {
        val builder = AlertDialog.Builder(context)

        // Set dialog title
        builder.setTitle("Confirmation")

        // Set dialog message
        builder.setMessage("Have to Completed today's Challenge?")

        // Set positive button and its click listener
        builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
            // Handle "Yes" button click
            // Do something when the user selects "Yes"
            Toast.makeText(context,"Todays Streak Has Been Updated",Toast.LENGTH_SHORT).show()
            var updatedscore = 0
            val db = FirebaseFirestore.getInstance().collection("publicusers").document(Userinfo.userid)
                .collection("challengeStreak").document(challenges[viewholder.adapterPosition].id)
            db.get().addOnSuccessListener {
                if(it.exists()){
                    val data = it.toObject(challengeparamenter::class.java)
                    if (data != null) {
                        updatedscore =data.points+1
                        val currentDate = LocalDate.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val formattedLastDate = currentDate.format(formatter)
                        val date1 = LocalDate.parse(formattedLastDate, formatter)
                        val date2 = LocalDate.parse(data.endDate, formatter)
                        val comparison = date1.compareTo(date2)
                        println(comparison)
                        if (comparison<=0){
                            if(formattedLastDate !=data.lastDay && updatedscore<=data.max){
                                db.update("points",updatedscore)
                                db.update("lastDay",formattedLastDate)
                            }
                        }
                        else{
                            Toast.makeText(context,"Challenge Completed",Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
            dialogInterface.dismiss()
        }

        // Set negative button and its click listener
        builder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
            // Handle "No" button click
            // Do something when the user selects "No"
            dialogInterface.dismiss()
        }

        // Create and display the dialog
        val dialog = builder.create()
        dialog.show()
    }

}