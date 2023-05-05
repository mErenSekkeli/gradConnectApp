package com.erensekkeli.gradconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.forEach
import com.erensekkeli.gradconnect.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeNavigationBar()

    }

    private fun initializeNavigationBar() {
        binding.bottomNavigationBar.menu.findItem(R.id.search).isChecked = true

        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    val intent = Intent(this@SearchActivity, FeedActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu -> {
                    val intent = Intent(this@SearchActivity, MenuActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.search -> {
                    false
                }
                R.id.profile -> {
                    val intent = Intent(this@SearchActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    fun searchUser(view: View) {
        val intent = Intent(this@SearchActivity, SearchResultActivity::class.java)
        val nameSurname: String? = binding.nameOrSurnameSearch.text?.toString()
        if (nameSurname != "") {
            intent.putExtra("nameSurname", nameSurname)
        }
        val country: String? = binding.countrySpinner?.selectedItem?.toString()
        intent.putExtra("country", country)
        val city: String? = binding.citySearch?.text?.toString()
        if(city != "") {
            intent.putExtra("city", city)
        }
        var graduateDate: String? = binding.graduationDateSearch?.text?.toString()
        if(graduateDate != "") {
            intent.putExtra("graduateDate", graduateDate)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationBar.menu.forEach {
            it.isChecked = false
        }
        binding.bottomNavigationBar.menu.findItem(R.id.search).isChecked = true
    }
}