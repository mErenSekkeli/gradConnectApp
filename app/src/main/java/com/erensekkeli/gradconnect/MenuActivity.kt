package com.erensekkeli.gradconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEach
import com.erensekkeli.gradconnect.databinding.ActivityMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.bottomNavigationBar.menu.findItem(R.id.menu).isChecked = true

        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    val intent = Intent(this@MenuActivity, FeedActivity::class.java)
                    startActivity(intent)
                    false
                }
                R.id.menu -> {
                    false
                }
                R.id.profile -> {
                    val intent = Intent(this@MenuActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    fun logoutClickListener(view: View) {
        //open confirmation dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.exit_app)
        builder.setMessage(R.string.exit_app_message)
        builder.setPositiveButton(R.string.yes) { _, _ ->
            auth.signOut()
            val intent = Intent(this@MenuActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton(R.string.no) { _, _ ->

        }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationBar.menu.forEach {
            it.isChecked = false
        }
        binding.bottomNavigationBar.menu.findItem(R.id.menu).isChecked = true
    }

}