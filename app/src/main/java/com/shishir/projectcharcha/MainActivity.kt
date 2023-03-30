package com.shishir.projectcharcha

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestore.*
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var fab: FloatingActionButton
    private lateinit var postAdapter: PostAdapter
    private var postList: MutableList<Post> = mutableListOf()

    private fun fetchPosts() {
        val db = getInstance()
        db.collection("post")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val username = document.getString("username")
                    val content = document.getString("content")
                    val timestamp = document.getTimestamp("timestamp")
                    val date = timestamp?.toDate().toString()

                    val post = Post(username, content, date)
                    postList.add(post)
                }

                // update UI with fetched posts
                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        // Initialize the logout button
        val logoutButton: Button = findViewById(R.id.logout_button)

// Set an onClickListener for the logout button
        logoutButton.setOnClickListener {
            // Sign out the current user
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Log Out successful!", Toast.LENGTH_SHORT).show()

            // Take the user back to the WelcomeActivity
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        // Initialize fab instance
        fab = findViewById(R.id.fab)

        // Set onClick listener for fab
        fab.setOnClickListener {
            // Start NewThreadActivity on fab click
            val intent = Intent(this, NewThreadActivity::class.java)
            startActivity(intent)
        }

        floatingActionButton = findViewById(R.id.fab)
        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, NewThreadActivity::class.java))
        }

        // Initialize the RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.postRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)



        // Initialize the PostAdapter
        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        fun updatePosts() {
            val db = getInstance()
            db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val posts = snapshot.documents.mapNotNull {
                        it.toObject(Post::class.java)?.withId<Post>(it.id)
                    }
                    postList.clear()
                    postList.addAll(posts)
                    postAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting posts", exception)
                }
        }

        // Get a reference to the "post" collection in Firestore
        val db = Firebase.firestore
        val postRef = db.collection("post")

        // Listen for changes in the "post" collection
        postRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e(TAG, "Error getting posts", exception)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Convert the QuerySnapshot to a list of Posts
                val posts = snapshot.documents.mapNotNull {
                    it.toObject(Post::class.java)?.withId<Post>(it.id)
                }
                // Update the PostAdapter with the new list of Posts
                postAdapter.updatePosts(posts)
            }
        }



    }


    data class Post(
        val username: String? = null,
        val content: String? = null,
        val date: String? = null,
        val postId: String = ""
    )
    // Extension function to add document id to Post object
    fun <T : Any> T.withId(id: String): T {
        if (this is Post) {
            return copy(postId = id) as T
        }
        throw IllegalArgumentException("Object does not have a field named 'postId'")
    }


    class PostAdapter(private val postList: List<Post>) :
        RecyclerView.Adapter<PostAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
            val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
            val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        }

        fun updatePosts(newPosts: List<Post>) {
//            postList.clear()
//            postList.addAll(newPosts)
            notifyDataSetChanged()
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val post = postList[position]
            holder.usernameTextView.text = post.username
            holder.contentTextView.text = post.content
            holder.dateTextView.text = post.date
        }

        override fun getItemCount(): Int {
            return postList.size
        }
    }



}




