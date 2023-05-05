package com.erensekkeli.gradconnect.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.erensekkeli.gradconnect.R
import com.erensekkeli.gradconnect.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        //text underline
        binding.haventAccount.paint.isUnderlineText = true

        sharedPreferences = this.getSharedPreferences("com.erensekkeli.gradconnect", MODE_PRIVATE)
        val remindMe = sharedPreferences.getBoolean("remindMe", false)

        if(remindMe) {
            val currentUser = auth.currentUser
            if(currentUser != null) {
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    fun goSignUp(view: View) {
        val intent = Intent(this@MainActivity, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun getProcessAnimation() {
        binding.progressBarLogin.visibility = View.VISIBLE
        binding.signInButton.visibility = View.INVISIBLE
        val overlayView = View(this)
        overlayView.setBackgroundColor(Color.parseColor("#99000000"))
        overlayView.alpha = 0.5f
        overlayView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.root.addView(overlayView)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun removeProcessAnimation() {
        binding.progressBarLogin.visibility = View.INVISIBLE
        binding.signInButton.visibility = View.VISIBLE
        binding.root.removeViewAt(binding.root.childCount - 1)
    }

    fun signIn(view: View) {
        hideKeyboard()
        val email = binding.emailInputForLogin.text.toString()
        val password = binding.passInputForLogin.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.email_or_password_empty, Toast.LENGTH_SHORT).show()
            return
        }
        getProcessAnimation()
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show()
            removeProcessAnimation()
            val intent = Intent(this@MainActivity, FeedActivity::class.java)
            sharedPreferences?.edit()?.putBoolean("remindMe", binding.remindMeCheckBox.isChecked)?.apply()
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show()
            removeProcessAnimation()
        }

    }
}