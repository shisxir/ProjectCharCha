package com.shishir.projectcharcha

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.util.Log

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val db = FirebaseFirestore.getInstance()
        // Get references to the UI elements
        val nameEditText = findViewById<EditText>(R.id.name_edittext)
        val emailEditText = findViewById<EditText>(R.id.email_edittext)
        val passwordEditText = findViewById<EditText>(R.id.password_edittext)
        val registerButton = findViewById<Button>(R.id.register_button)

        // Find the back button by its ID
        val backButton = findViewById<Button>(R.id.back_button)

        // Set an OnClickListener to the back button
        backButton.setOnClickListener {
            // Launch the Welcome activity
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            // Finish the Register activity
            finish()
        }

        // Set up the register button click listener
        registerButton.setOnClickListener {
            // Get the user input from the UI elements
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validate the user input
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // TODO: Add code to save the user's registration information to a database or server
            val newUser = hashMapOf(
                "UserName" to name,
                "Email" to email,
                "Password" to password
            )

            db.collection("Users")
                .add(newUser)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show()
                }

            // Navigate back to the login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()



        }
    }
}
