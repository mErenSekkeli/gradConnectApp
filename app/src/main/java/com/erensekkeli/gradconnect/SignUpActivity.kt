package com.erensekkeli.gradconnect

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.erensekkeli.gradconnect.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var overlayView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

    }
    fun getSheet(view: View) {
        val bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, "BottomSheetFragment")
    }

    private fun userInfoChecker(): Boolean{

        if(binding.nameInput.text.toString().isEmpty() || binding.surnameInput.text.toString().isEmpty()) {
            Toast.makeText(this, R.string.name_or_surname_empty, Toast.LENGTH_SHORT).show()
            return true
        }

        if(binding.mailInput.text.toString().isEmpty()) {
            Toast.makeText(this, R.string.email_empty, Toast.LENGTH_SHORT).show()
            return true
        }

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)\$")
        if(!emailRegex.matches(binding.mailInput.text.toString())) {
            Toast.makeText(this, R.string.email_format_error, Toast.LENGTH_SHORT).show()
            return true
        }

        if(binding.passInput.text.toString().isEmpty() || binding.passInput2.text.toString().isEmpty()) {
            Toast.makeText(this, R.string.password_empty, Toast.LENGTH_SHORT).show()
            return true
        }

        if(binding.passInput.text.toString() != binding.passInput2.text.toString()) {
            Toast.makeText(this, R.string.passwords_not_match, Toast.LENGTH_SHORT).show()
            return true
        }

        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{8,}\$")
        if(!passwordRegex.matches(binding.passInput.text.toString())) {
            Toast.makeText(this, R.string.password_format_error, Toast.LENGTH_SHORT).show()
            return true
        }

        return false
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun getProcessAnimation() {
        binding.progressBar.visibility = View.VISIBLE
        binding.signUpButton.visibility = View.INVISIBLE
        overlayView = View(this)
        overlayView.setBackgroundColor(Color.parseColor("#99000000"))
        overlayView.alpha = 0.5f
        overlayView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.root.addView(overlayView)
    }

    private fun removeProcessAnimation() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.signUpButton.visibility = View.VISIBLE
        binding.root.removeViewAt(binding.root.childCount - 1)
    }

    fun signUp(view: View): Boolean{
        hideKeyboard()
        if(userInfoChecker())
            return false;

        getProcessAnimation()
        val email = binding.mailInput.text.toString()
        val password = binding.passInput.text.toString()
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            Toast.makeText(this, R.string.registration_successful, Toast.LENGTH_SHORT).show()
            removeProcessAnimation()
            val intent = Intent(this@SignUpActivity, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            removeProcessAnimation()
        }
        return true
    }
}