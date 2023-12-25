package com.example.healthapp.activities

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.healthapp.R
import com.example.healthapp.databinding.ItemExerciseStatusBinding


class WorkoutStatusAdapter(val items:ArrayList<ExerciseModel>) :
    RecyclerView.Adapter<WorkoutStatusAdapter.ViewHolder>(){

    class ViewHolder(binding: ItemExerciseStatusBinding):
        RecyclerView.ViewHolder(binding.root){
        val tvItem=binding.tvItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemExerciseStatusBinding.inflate(
            LayoutInflater.from(parent.context),parent
            ,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model:ExerciseModel=items[position]
        holder.tvItem.text=model.getId().toString()

        when{
            model.getIsSelected() -> {
                holder.tvItem.background=
                    ContextCompat.getDrawable(holder.itemView.context,R.drawable.item_circular_thin_color_accent_bordert)
                holder.tvItem.setTextColor(Color.parseColor("#212121"))

            }
            model.getIsCompleted() ->{
                holder.tvItem.background=
                    ContextCompat.getDrawable(holder.itemView.context,R.drawable.item_circular_color_accent_background)
                holder.tvItem.setTextColor(Color.parseColor("#ffffff"))
            }
            else ->{
                holder.tvItem.background=
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_circular_color_gray_background)
                holder.tvItem.setTextColor(Color.parseColor("#212121"))
            }
        }
    }

}