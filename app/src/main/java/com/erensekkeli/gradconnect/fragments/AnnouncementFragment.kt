package com.erensekkeli.gradconnect.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erensekkeli.gradconnect.R
import com.erensekkeli.gradconnect.adapters.AnnouncementListAdapter
import com.erensekkeli.gradconnect.databinding.FragmentAnnouncementBinding
import com.erensekkeli.gradconnect.models.Announcement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AnnouncementFragment : Fragment() {

    private lateinit var binding: FragmentAnnouncementBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private var announcementList: ArrayList<Announcement> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        firestore = Firebase.firestore
        recyclerView = binding.profileDetailRecyclerView
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false);
        binding.profileDetailRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.profileDetailRecyclerView.adapter = AnnouncementListAdapter(announcementList)

        getProcessAnimation()

        firestore.collection("UserAnnouncements").get().addOnSuccessListener { result ->
            for (document in result) {
                val id = document.getString("id")
                val title = document.getString("title")
                val content = document.getString("content")
                val deadline = document.getString("deadline")
                val email = document.getString("email")
                val announcement = Announcement(id, title, content, deadline, email)
                announcementList.add(announcement)
            }
            binding.profileDetailRecyclerView.adapter?.notifyDataSetChanged()
            removeProcessAnimation()
        }.addOnFailureListener {
            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
            removeProcessAnimation()
        }
    }

    private fun getProcessAnimation() {
        binding.progressContainer.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        getProcessAnimation()
        announcementList.clear()
    }

    private fun removeProcessAnimation() {
        binding.progressContainer.visibility = View.INVISIBLE
    }
}