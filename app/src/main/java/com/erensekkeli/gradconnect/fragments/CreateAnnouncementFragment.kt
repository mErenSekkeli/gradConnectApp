package com.erensekkeli.gradconnect.fragments


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.erensekkeli.gradconnect.R
import com.erensekkeli.gradconnect.databinding.FragmentCreateAnnouncementBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CreateAnnouncementFragment : Fragment() {

    private lateinit var binding: FragmentCreateAnnouncementBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var overlayView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreateAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        firestore = Firebase.firestore

        binding.goBackBtn.setOnClickListener { goBack(view) }

        binding.createAnnouncementBtn.setOnClickListener { createAnnouncement(view) }
    }

    private fun getProcessAnimation() {
        binding.progressBar.visibility = View.VISIBLE
        binding.createAnnouncementBtn.visibility = View.INVISIBLE
        overlayView = View(context)
        overlayView!!.setBackgroundColor(Color.parseColor("#99000000"))
        overlayView!!.alpha = 0.5f
        overlayView!!.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.root.addView(overlayView)
    }

    private fun removeProcessAnimation() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.createAnnouncementBtn.visibility = View.VISIBLE
        binding.root.removeViewAt(binding.root.childCount - 1)
    }

    private fun createAnnouncement(view: View) {
        hideKeyboard()
        val title = binding.title.text.toString()
        val content = binding.content.text.toString()
        val deadline = binding.deadline.text.toString()

        if(title.isEmpty() || content.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(context, R.string.fill_requried_fields, Toast.LENGTH_LONG).show()
        } else {
            getProcessAnimation()
            val usersCollection = firestore.collection("UserAnnouncements")
            val currentUser = auth.currentUser
            if(currentUser != null) {
                val announcement = hashMapOf(
                    "id" to usersCollection.document().id,
                    "title" to title,
                    "content" to content,
                    "deadline" to deadline,
                    "email" to currentUser.email
                )
                usersCollection.add(announcement)
                    .addOnSuccessListener {
                        Toast.makeText(context, R.string.announcement_created, Toast.LENGTH_LONG).show()
                        removeProcessAnimation()
                        goBack(view)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, R.string.announcement_create_failed, Toast.LENGTH_LONG).show()
                        removeProcessAnimation()
                    }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun goBack(view: View) {
        hideKeyboard()
        val fragmentManager = activity?.supportFragmentManager
        fragmentManager?.popBackStack()
    }

}