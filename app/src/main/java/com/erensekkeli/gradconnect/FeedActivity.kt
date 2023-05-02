package com.erensekkeli.gradconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.erensekkeli.gradconnect.databinding.ActivityFeedBinding

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //TODO: create bottom menu
    }
}