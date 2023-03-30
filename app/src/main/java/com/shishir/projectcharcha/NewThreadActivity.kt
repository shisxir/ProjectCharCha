package com.shishir.projectcharcha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class NewThreadActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var username: String
    private lateinit var categorySpinner: Spinner
    private lateinit var requiresAttentionRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_thread)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val threadText = findViewById<EditText>(R.id.thread_text)
        val postButton = findViewById<Button>(R.id.post_button)
        categorySpinner = findViewById(R.id.category_spinner)
        requiresAttentionRadioGroup = findViewById(R.id.immediate_attention_group)

        // Set up the category dropdown menu
        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

        postButton.setOnClickListener {
            val threadContent = threadText.text.toString()

            if (threadContent.isNotEmpty()) {
                val selectedCategory = categorySpinner.selectedItem.toString()
                val requiresAttention = requiresAttentionRadioGroup.checkedRadioButtonId == R.id.yes_radio
                val thread = hashMapOf(
                    "username" to FirebaseAuth.getInstance().currentUser?.displayName,
                    "content" to threadContent,
                    "category" to selectedCategory,
                    "requiresAttention" to requiresAttention
                )

                db.collection("post")
                    .add(thread)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        Toast.makeText(this, "Thread posted successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                        Toast.makeText(this, "Failed to post thread", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please enter thread content", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "NewThreadActivity"
    }
}

