package com.losmugiwaras.proyectomoviles01.reseach

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.losmugiwaras.proyectomoviles01.R

// Define the Comment data class
data class Comment(
    val author: String,
    val text: String,
    val grade: Int
)

class PresentationFragment : Fragment() {
    private lateinit var researchId: String
    private lateinit var textViewTitle: TextView
    private lateinit var textViewTopic: TextView
    private lateinit var textViewGrade: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var recyclerViewComments: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var submitButton: View
    private lateinit var commentsList: MutableList<Comment>
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_presentation, container, false)

        // Initializing views
        textViewTitle = view.findViewById(R.id.textViewTitle)
        textViewTopic = view.findViewById(R.id.textViewTopic)
        textViewGrade = view.findViewById(R.id.textViewGrade)
        textViewDescription = view.findViewById(R.id.textViewDescription)
        recyclerViewComments = view.findViewById(R.id.recyclerViewComments)
        commentInput = view.findViewById(R.id.commentInput)
        ratingBar = view.findViewById(R.id.ratingBar)
        submitButton = view.findViewById(R.id.submitButton)

        // Setup RecyclerView
        recyclerViewComments.layoutManager = LinearLayoutManager(context)
        commentsList = mutableListOf()
        commentsAdapter = CommentsAdapter(commentsList) // Pass commentsList to the adapter
        recyclerViewComments.adapter = commentsAdapter

        // Get research ID from the arguments passed to this fragment
        researchId = arguments?.getString("researchId") ?: ""
        fetchResearchDetails()

        // Set up the submit button to add a new comment
        submitButton.setOnClickListener {
            submitComment()
        }

        return view
    }

    // Fetch the research details
    private fun fetchResearchDetails() {
        val db = FirebaseFirestore.getInstance()
        val researchDoc = db.collection("research").document(researchId)

        // Fetch the research data
        researchDoc.get().addOnSuccessListener { document ->
            val title = document.getString("title") ?: "No Title"
            val topic = document.getString("topic") ?: "No Topic"
            val academicGrade = document.getString("academicGrade") ?: "No Grade"
            val description = document.getString("description") ?: "No Description"

            // Display research details
            textViewTitle.text = title
            textViewTopic.text = topic
            textViewGrade.text = academicGrade
            textViewDescription.text = description

            // Fetch comments associated with this research
            fetchComments()
        }
    }

    // Fetch the comments for the selected research work
    private fun fetchComments() {
        val db = FirebaseFirestore.getInstance()
        db.collection("research")
            .document(researchId)
            .collection("comments")
            .get()
            .addOnSuccessListener { result ->
                commentsList.clear() // Clear the current list before adding new data
                for (document in result) {
                    val comment = Comment(
                        author = document.getString("author") ?: "Anonymous",
                        text = document.getString("text") ?: "No Comment",
                        grade = document.getLong("grade")?.toInt() ?: 0
                    )
                    commentsList.add(comment)
                }
                commentsAdapter.notifyDataSetChanged() // Update the RecyclerView with new data
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching comments: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Handle new comment submission
    private fun submitComment() {
        val commentText = commentInput.text.toString()
        val rating = ratingBar.rating.toInt()

        if (commentText.isNotBlank() && rating > 0) {
            val db = FirebaseFirestore.getInstance()
            val newComment = hashMapOf(
                "author" to "User", // Replace with actual user info if available
                "text" to commentText,
                "grade" to rating
            )

            // Add the new comment to Firestore
            db.collection("research")
                .document(researchId)
                .collection("comments")
                .add(newComment)
                .addOnSuccessListener {
                    Toast.makeText(context, "Comment added successfully", Toast.LENGTH_SHORT).show()
                    commentInput.text.clear()  // Clear the input field
                    ratingBar.rating = 0f  // Reset the rating bar
                    fetchComments()  // Refresh the comments list
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error adding comment: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Please write a comment and rate the work", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(researchId: String): PresentationFragment {
            val fragment = PresentationFragment()
            val args = Bundle()
            args.putString("researchId", researchId)
            fragment.arguments = args
            return fragment
        }
    }
}
