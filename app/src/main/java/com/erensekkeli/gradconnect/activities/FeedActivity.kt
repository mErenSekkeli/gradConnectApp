package com.erensekkeli.gradconnect.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.erensekkeli.gradconnect.fragments.AnnouncementFragment
import com.erensekkeli.gradconnect.fragments.FeedFragment
import com.erensekkeli.gradconnect.fragments.ProfileFragment
import com.erensekkeli.gradconnect.R
import com.erensekkeli.gradconnect.fragments.SearchFragment
import com.erensekkeli.gradconnect.databinding.ActivityFeedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        firestore = Firebase.firestore

        replaceFragment(FeedFragment())

        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    replaceFragment(FeedFragment())
                }
                R.id.announcement -> {
                    replaceFragment(AnnouncementFragment())
                }
                R.id.search -> {
                    replaceFragment(SearchFragment())
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                }
                else -> {
                    replaceFragment(FeedFragment())
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_right_to_left,
            R.anim.exit_right_to_left,
            R.anim.enter_left_to_right,
            R.anim.exit_left_to_right
        )
        fragmentTransaction.replace(R.id.feedContainerFragment, fragment)
        fragmentTransaction.commit()
    }

}