package com.example.kotlinweatherforecast.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinweatherforecast.databinding.RowLayoutBinding

import com.example.kotlinweatherforecast.common.onCliclLongRecyclerView


class TempeturesRecyclerViewAdapter(private var placeName: ArrayList<String>) : RecyclerView.Adapter<TempeturesRecyclerViewAdapter.RowHolder>(){
    private var listener: onCliclLongRecyclerView? = null

    fun setListener(listener: onCliclLongRecyclerView){
        this.listener = listener
    }
    class RowHolder(val binding: RowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val binding = RowLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RowHolder(binding)
    }

    override fun getItemCount(): Int {
        return placeName.count()
    }

    @SuppressLint("ResourceType", "SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RowHolder, @SuppressLint("RecyclerView") position: Int) {

        holder.binding.name.setOnLongClickListener {
            if (listener != null) {
                listener!!.onLongClick(position)
            }
            true
        }

        holder.binding.name.text = placeName[position]
    }
    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        placeName.removeAt(position)
        notifyDataSetChanged()
    }
}