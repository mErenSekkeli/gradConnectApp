package com.erensekkeli.gradconnect.fragments

import android.content.BroadcastReceiver
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erensekkeli.gradconnect.R
import com.erensekkeli.gradconnect.adapters.MediaListAdapter
import com.erensekkeli.gradconnect.databinding.FragmentProfileBinding
import com.erensekkeli.gradconnect.models.Media
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var recyclerView: RecyclerView
    private var mediaList: ArrayList<Media> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MediaListAdapter(mediaList, 1)

        getData()

        binding.profileSettingsBtn.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exit_left_to_right
            )
            transaction.replace(R.id.feedContainerFragment, ProfileSettingsFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.createAnnouncementBtn.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exit_left_to_right
            )
            transaction.replace(R.id.feedContainerFragment, CreateAnnouncementFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
                val actionType = intent?.getStringExtra("actionType")
                if(actionType == "hide") {
                    getProcessAnimation()
                }else if(actionType == "open") {
                    removeProcessAnimation()
                }

            }
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, android.content.IntentFilter("com.erensekkeli.gradconnect.PROFILE_FRAGMENT_HIDE_PROGRESS_BAR"))

        getProcessAnimation()
        firestore.collection("UserMedia").whereEqualTo("email", auth.currentUser!!.email!!).get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val media = Media(document.id, document.getString("email"), document.getString("title"), document.getString("description"), document.getString("mediaUrl"), document.getString("mediaType"), document.getTimestamp("date"))
                    mediaList.add(media)
                }
                binding.recyclerView.adapter?.notifyDataSetChanged()
                removeProcessAnimation()
            }.addOnFailureListener {
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
                removeProcessAnimation()
            }
    }

    override fun onResume() {
        super.onResume()
        mediaList.clear()
        getData()
    }

    private fun getData() {
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
                    try {
                        Glide.with(this@ProfileFragment).load(imageUri).into(binding.profileImage)
                    }catch (e: Exception) {
                        binding.profileImage.setImageResource(R.drawable.app_icon)
                    }
                }
                binding.contactPhoneField.text = contactPhone
                binding.contactMailField.text = contactMail
            }
            removeProcessAnimation()
        }.addOnFailureListener {
            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
            removeProcessAnimation()
        }

    }

    private fun getProcessAnimation() {
        binding.progressContainer.visibility = View.VISIBLE
    }

    private fun removeProcessAnimation() {
        binding.progressContainer.visibility = View.INVISIBLE
    }

}