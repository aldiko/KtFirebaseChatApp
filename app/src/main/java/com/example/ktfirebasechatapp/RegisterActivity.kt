package com.example.ktfirebasechatapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ktfirebasechatapp.LoginActivity
import com.example.ktfirebasechatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var editTextFullName: EditText? = null
    private var editTextAge: EditText? = null
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null
    private var progressBar: ProgressBar? = null
    private var chatAppTextView: TextView? = null
    private var registrationButton: Button? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        editTextFullName = findViewById<View>(R.id.register_name_edit_text) as EditText
        editTextAge = findViewById<View>(R.id.register_age_edit_text) as EditText
        editTextEmail = findViewById<View>(R.id.register_email_edit_text) as EditText
        editTextPassword = findViewById<View>(R.id.register_password_edit_text) as EditText
        progressBar = findViewById<View>(R.id.register_progress_bar) as ProgressBar
        registrationButton = findViewById<View>(R.id.registration_button) as Button
        registrationButton!!.setOnClickListener(this)
        chatAppTextView = findViewById<View>(R.id.register_chat_app_text_view) as TextView
        chatAppTextView!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.register_chat_app_text_view -> startActivity(Intent(this, LoginActivity::class.java))
            R.id.registration_button -> registerUser()
        }
    }

    private fun registerUser() {
        val email: String = editTextEmail!!.text.toString().trim()
        val password: String = editTextPassword!!.text.toString().trim()
        val fullName: String = editTextFullName!!.text.toString().trim()
        val age: String = editTextAge!!.text.toString().trim()
        if (fullName == null) {
            editTextFullName!!.error = "Full name is required!"
            editTextFullName!!.requestFocus()
            return
        }
        if (age == null) {
            editTextAge!!.error = "Age is required!"
            editTextAge!!.requestFocus()
            return
        }
        if (email == null) {
            editTextEmail!!.error = "E-mail is required!"
            editTextEmail!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail!!.error = "Please provide valid email!"
            editTextEmail!!.requestFocus()
            return
        }
        if (password == null) {
            editTextPassword!!.error = "Password is required!"
            editTextPassword!!.requestFocus()
            return
        }
        if (password.length < 6) {
            editTextPassword!!.error = "Password length should more than 6"
            editTextPassword!!.requestFocus()
            return
        }
        progressBar!!.visibility = View.VISIBLE
        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = User(fullName, age, email)
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(user).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@RegisterActivity,
                            "User has been registered successfully", Toast.LENGTH_LONG).show()
                        progressBar!!.visibility = View.GONE
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    } else {
                        Toast.makeText(this@RegisterActivity,
                            "Failed to Register! Try Again!", Toast.LENGTH_LONG).show()
                        progressBar!!.visibility = View.GONE
                    }
                }
            } else {
                Toast.makeText(this@RegisterActivity,
                    "Failed to Register! Try Again!", Toast.LENGTH_LONG).show()
                progressBar!!.visibility = View.GONE
            }
        }
    }
}