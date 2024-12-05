package com.losmugiwaras.proyectomoviles01.reseach

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.losmugiwaras.proyectomoviles01.R

class ResearchAdapter(
private val researchList: List<Research>,
private val onItemClicked: (Research) -> Unit
) : RecyclerView.Adapter<ResearchAdapter.ResearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_research, parent, false)
        return ResearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResearchViewHolder, position: Int) {
        val research = researchList[position]
        holder.bind(research)
        holder.itemView.setOnClickListener { onItemClicked(research) }
    }

    override fun getItemCount() = researchList.size

    class ResearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(research: Research) {
            // Vincula datos a la vista aquí
        }
    }
}

class Research {

}
