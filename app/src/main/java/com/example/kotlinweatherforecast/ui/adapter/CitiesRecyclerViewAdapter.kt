package com.example.kotlinweatherforecast.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinweatherforecast.common.OnHymnClickListener
import com.example.kotlinweatherforecast.databinding.CitiesanddistrictsRowLayoutBinding

class CitiesRecyclerViewAdapter(private var places:ArrayList<String>) : RecyclerView.Adapter<CitiesRecyclerViewAdapter.RowHolder>() {
    private var listener: OnHymnClickListener? = null
    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filtered:ArrayList<String>) {
        places= filtered
        notifyDataSetChanged()
    }

    fun setListener(listener: OnHymnClickListener){
        this.listener = listener
    }

    class RowHolder(val binding: CitiesanddistrictsRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val binding = CitiesanddistrictsRowLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RowHolder(binding)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.binding.districtOrCityName.text =places[position].uppercase()

        holder.itemView.setOnClickListener {
            if (listener != null) {
                listener!!.onHymnClick(places[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return places.count()
    }
}