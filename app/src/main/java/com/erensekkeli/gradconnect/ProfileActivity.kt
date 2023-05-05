package com.erensekkeli.gradconnect

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.erensekkeli.gradconnect.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var overlayView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        getData()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getProcessAnimation() {
        binding.progressContainer.visibility = View.VISIBLE
    }

    private fun removeProcessAnimation() {
        binding.progressContainer.visibility = View.INVISIBLE
    }

    private fun getData() {
        getProcessAnimation()
        val usersCollection = firestore.collection("UserData")

        usersCollection.whereEqualTo("email", auth.currentUser!!.email!!).get().addOnSuccessListener { documents ->
            if(documents != null && !documents.isEmpty) {
                val document = documents.documents[0]
                val name = document.get("name") as String
                val surname = document.get("surname") as String
                val entryDate = document.get("entryDate")?.toString() ?: "-"
                val graduationDate = document.get("graduationDate")?.toString() ?: "-"
                val profilePicture = document.get("profileImage")?.toString() ?: ""
                val contactPhone = document.get("contactPhone")?.toString() ?: "-"
                val contactMail = document.get("contactMail")?.toString() ?: "-"

                binding.nameSurnameField.text = "$name $surname"
                binding.entryDateField.text = entryDate
                binding.graduationDateField.text = graduationDate
                val imageUri: Uri? = profilePicture.toUri()
                if(imageUri == null || imageUri.toString() == "") {
                    binding.profileImage.setImageResource(R.drawable.app_icon)
                }else{
                    Glide.with(this@ProfileActivity).load(imageUri).into(binding.profileImage)
                }
                binding.contactPhoneField.text = contactPhone
                binding.contactMailField.text = contactMail
            }
            removeProcessAnimation()
        }.addOnFailureListener { exception ->
            Toast.makeText(this@ProfileActivity, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
            removeProcessAnimation()
        }

    }

    fun goBack(view: View) {
        finish()
    }

    fun goProfileSettings(view: View) {
        val intent = Intent(this@ProfileActivity, ProfileSettingsActivity::class.java)
        startActivity(intent)
    }
}