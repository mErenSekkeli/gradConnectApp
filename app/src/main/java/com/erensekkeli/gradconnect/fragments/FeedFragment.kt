package com.erensekkeli.gradconnect.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erensekkeli.gradconnect.R
import com.erensekkeli.gradconnect.activities.FeedActivity
import com.erensekkeli.gradconnect.adapters.MediaListAdapter
import com.erensekkeli.gradconnect.databinding.FragmentFeedBinding
import com.erensekkeli.gradconnect.models.Media
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private var mediaList: ArrayList<Media> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        auth = Firebase.auth
        firestore = Firebase.firestore
        recyclerView = binding.mediaRecyclerView
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false);
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MediaListAdapter(mediaList, 0)

        binding.addNewMediaButton.setOnClickListener {
            goToCreateMedia(view)
        }
        getProcessAnimation()

        firestore.collection("UserMedia").get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val media = Media(document.id, document.getString("email"), document.getString("title"), document.getString("description"), document.getString("mediaUrl"), document.getString("mediaType"), document.getTimestamp("date"))
                    mediaList.add(media)
                }
                binding.mediaRecyclerView.adapter?.notifyDataSetChanged()
                removeProcessAnimation()
            }.addOnFailureListener {
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
                removeProcessAnimation()
            }
    }

    private fun getProcessAnimation() {
        binding.progressContainer.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        getProcessAnimation()
        mediaList.clear()
    }

    private fun removeProcessAnimation() {
        binding.progressContainer.visibility = View.INVISIBLE
    }

    private fun goToCreateMedia(view: View) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.enter_right_to_left,
            R.anim.exit_right_to_left,
            R.anim.enter_left_to_right,
            R.anim.exit_left_to_right
        )
        transaction.replace(R.id.feedContainerFragment, CreateMediaFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }


}