package com.example.foodieree.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodieree.R
import com.example.foodieree.databinding.ItemFoodspotBinding
import com.example.foodieree.model.FoodSpot

class NearbyAdapter(private var foodSpots: List<FoodSpot>) : RecyclerView.Adapter<NearbyAdapter.FoodSpotViewHolder>()  {

    class FoodSpotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSpotName: TextView = itemView.findViewById(R.id.tvSpotName)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodSpotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_foodspot, parent, false)
        return FoodSpotViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodSpots.size
    }

    override fun onBindViewHolder(holder: FoodSpotViewHolder, position: Int) {
        val spot = foodSpots[position]
        holder.tvSpotName.text = "${spot.name} - ${"%.2f".format(spot.distance)} km"
    }

    fun updateData(newFoodSpots: List<FoodSpot>) {
        foodSpots = newFoodSpots.sortedBy { it.distance } // Sort by distance
        notifyDataSetChanged()

    }


}