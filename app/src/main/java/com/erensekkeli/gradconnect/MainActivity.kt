package com.erensekkeli.gradconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.erensekkeli.gradconnect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    //deneme
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}