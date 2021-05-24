package com.example.ktfirebasechatapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ktfirebasechatapp.MainActivity
import com.example.ktfirebasechatapp.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var register: TextView? = null
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null
    private var signIn: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        register = findViewById<View>(R.id.login_register_text_view) as TextView
        register!!.setOnClickListener(this)
        signIn = findViewById<View>(R.id.login_button) as Button
        signIn!!.setOnClickListener(this)
        editTextEmail = findViewById<View>(R.id.email_input) as EditText
        editTextPassword = findViewById<View>(R.id.password_input) as EditText
        mAuth = FirebaseAuth.getInstance()
        progressBar = findViewById<View>(R.id.progress_bar) as ProgressBar
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_register_text_view -> {
                startActivity(Intent(this, RegisterActivity::class.java))
                userLogin()
            }
            R.id.login_button -> userLogin()
        }
    }

    private fun userLogin() {
        val email: String = editTextEmail!!.text.toString()
        val password: String = editTextPassword!!.text.toString()
        if (email == null) {
            editTextEmail!!.error = "Email is required!"
            editTextEmail!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail!!.error = "Please enter a valid email!"
            editTextEmail!!.requestFocus()
            return
        }
        if (password == null) {
            editTextPassword!!.error = "Password is required!"
            editTextPassword!!.requestFocus()
            return
        }
        if (password.length < 6) {
            editTextPassword!!.error = "Min password length is 6 characters!"
            editTextPassword!!.requestFocus()
            return
        }
        progressBar!!.visibility = View.VISIBLE
        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // redirect to chat
                val user = FirebaseAuth.getInstance().currentUser
                if (user!!.isEmailVerified) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("userId", user.uid)
                    startActivity(intent)
                } else {
                    user.sendEmailVerification()
                    Toast.makeText(this@LoginActivity, "Check your email for verification!",
                        Toast.LENGTH_LONG).show()
                }
                progressBar!!.visibility = View.GONE
            } else {
                Toast.makeText(this@LoginActivity, "Failed to Login! Please check your credentials!",
                    Toast.LENGTH_LONG).show()
                progressBar!!.visibility = View.GONE
            }
        }
    }
}