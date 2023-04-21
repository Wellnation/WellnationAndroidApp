package com.shubhasai.wellnation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ExerciseAdapter(private val context: Context?,val exercise: ArrayList<exerciseresultItem>, val listener:ExerciseClicked) :
    RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardview:CardView = itemView.findViewById(R.id.card_view_exercise)
        val category: TextView = itemView.findViewById(R.id.tvCategory)
        val difficulty: TextView = itemView.findViewById(R.id.tvDifficulty)
        val force: TextView = itemView.findViewById(R.id.tvForce)
        val grip: TextView = itemView.findViewById(R.id.tvGrip)
        val name: TextView = itemView.findViewById(R.id.tvExerciseName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewholder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.exercise_item, parent, false))
        viewholder.cardview.setOnClickListener {
            listener.onExerciseClicked(exercise[viewholder.adapterPosition])
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exercise[position]
        holder.category.text = "Category: "+exercise.Category
        holder.difficulty.text ="Difficulty: "+ exercise.Difficulty
        holder.force.text = "Force: "+exercise.Force
        holder.grip.text = "Grips: "+exercise.Grips
        holder.name.text = "Name: "+exercise.exercise_name

    }

    override fun getItemCount(): Int {
        return exercise.size
    }
    interface ExerciseClicked {
        fun onExerciseClicked(exercise: exerciseresultItem){

        }
    }
}


