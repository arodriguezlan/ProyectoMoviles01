package com.losmugiwaras.proyectomoviles01.reseach

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.losmugiwaras.proyectomoviles01.R

class CommentsAdapter(private val commentsList: MutableList<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentsList[position]
        holder.authorTextView.text = comment.author
        holder.commentTextView.text = comment.text
        holder.ratingBar.rating = comment.grade.toFloat()
    }

    override fun getItemCount(): Int = commentsList.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorTextView: TextView = itemView.findViewById(R.id.commentAuthor)
        val commentTextView: TextView = itemView.findViewById(R.id.commentText)
        val ratingBar: RatingBar = itemView.findViewById(R.id.commentRatingBar)
    }
}
