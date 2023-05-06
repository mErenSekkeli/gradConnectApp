package com.erensekkeli.gradconnect.adapters


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erensekkeli.gradconnect.R
import com.erensekkeli.gradconnect.activities.FeedActivity
import com.erensekkeli.gradconnect.databinding.MediaItemBinding
import com.erensekkeli.gradconnect.fragments.MediaDetailFragment
import com.erensekkeli.gradconnect.fragments.ProfileDetailFragment
import com.erensekkeli.gradconnect.fragments.ProfileFragment
import com.erensekkeli.gradconnect.models.Media
import com.erensekkeli.gradconnect.models.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MediaListAdapter(val mediaList: ArrayList<Media>, var fragmentType: Int = 0): RecyclerView.Adapter<MediaListAdapter.MediaViewHolder>(){

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    class MediaViewHolder(val binding: MediaItemBinding): RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage
        val binding = MediaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    private fun changeTimestampToStringDate(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val simpleDateFormat = android.icu.text.SimpleDateFormat("dd/MM/yyyy")
        return simpleDateFormat.format(date)
    }


    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaList[position]
        holder.binding.mediaTitle.text = media.title ?: ""
        holder.binding.mediaDescription.text = media.description ?: ""
        holder.binding.mediaCreatadDate.text = changeTimestampToStringDate(media.date!!)
        if(media.mediaType == "video"){
            holder.binding.mediaPlayButton.visibility = ViewGroup.VISIBLE
        }

        Glide.with(holder.itemView.context).load(media.mediaUrl).override(1024, 768)
            .into(holder.binding.mediaImage)

        holder.binding.mediaImage.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("media", media)

            val fragment = MediaDetailFragment()
            fragment.arguments = bundle
            val transaction = (holder.itemView.context as FeedActivity).supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exit_left_to_right
            )
            transaction.replace(R.id.feedContainerFragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        firestore.collection("UserData").whereEqualTo("email", media.email).get()
            .addOnSuccessListener { documents ->
                if(documents.size() > 0) {
                    val document = documents.documents[0]
                    holder.binding.mediaUsername.text = document.getString("name") + " " + document.getString("surname")
                    Glide.with(holder.itemView.context).load(document.getString("profileImage")).into(holder.binding.mediaProfileImage)

                    holder.binding.mediaUsername.setOnClickListener {
                        val bundle = Bundle()
                        val user = User(document.getString("name")!!, document.getString("surname")!!, document.getString("country"), document.getString("city"), document.getString("entryDate"),
                            document.getString("graduationDate"), document.getString("contactMail"), document.getString("contactPhone"), document.getString("currentBusiness"),
                            document.getString("department"), document.getString("educationStatus"), document.getString("facebook"), document.getString("linkedin"),
                            document.getString("profileImage"))

                        bundle.putSerializable("user", user)
                        val fragment = ProfileDetailFragment()
                        fragment.arguments = bundle
                        val transaction = (holder.itemView.context as FeedActivity).supportFragmentManager.beginTransaction()
                        transaction.setCustomAnimations(
                            R.anim.enter_right_to_left,
                            R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right,
                            R.anim.exit_left_to_right
                        )
                        transaction.replace(R.id.feedContainerFragment, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }

                    holder.binding.mediaProfileImage.setOnClickListener {
                        val bundle = Bundle()
                        val user = User(document.getString("name")!!, document.getString("surname")!!, document.getString("country"), document.getString("city"), document.getString("entryDate"),
                            document.getString("graduationDate"), document.getString("contactMail"), document.getString("contactPhone"), document.getString("currentBusiness"),
                            document.getString("department"), document.getString("educationStatus"), document.getString("facebook"), document.getString("linkedin"),
                            document.getString("profileImage"))

                        bundle.putSerializable("user", user)
                        val fragment = ProfileDetailFragment()
                        fragment.arguments = bundle
                        val transaction = (holder.itemView.context as FeedActivity).supportFragmentManager.beginTransaction()
                        transaction.setCustomAnimations(
                            R.anim.enter_right_to_left,
                            R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right,
                            R.anim.exit_left_to_right
                        )
                        transaction.replace(R.id.feedContainerFragment, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                }

            }

        if(fragmentType == 1) {
            holder.binding.deleteMedia.visibility = ViewGroup.VISIBLE
            val reference = storage.reference
            holder.binding.deleteMedia.setOnClickListener {
                val alertDialog = AlertDialog.Builder(holder.itemView.context)
                alertDialog.setTitle(R.string.delete_media)
                alertDialog.setMessage(R.string.delete_media_message)
                alertDialog.setPositiveButton(R.string.yes) { dialog, which ->
                    val uuid = media.mediaUrl!!.substringAfterLast("/").split("?")[0].split("user_media")[1].replaceFirst("%2F", "/")
                    val mediaRef = reference.child("user_media$uuid")
                    hideProgress(holder.itemView.context)
                    mediaRef.delete().addOnSuccessListener {
                        firestore.collection("UserMedia").document(media.id!!).delete()
                            .addOnSuccessListener {
                                mediaList.removeAt(position)
                                notifyDataSetChanged()
                                Toast.makeText(
                                    holder.itemView.context,
                                    R.string.media_deleted,
                                    Toast.LENGTH_SHORT
                                ).show()
                                openFragment(holder.itemView.context)
                            }.addOnFailureListener {
                                Toast.makeText(
                                    holder.itemView.context,
                                    R.string.media_delete_failed,
                                    Toast.LENGTH_SHORT
                                ).show()
                                openFragment(holder.itemView.context)
                            }
                    }.addOnFailureListener {
                        Toast.makeText(
                            holder.itemView.context,
                            R.string.media_delete_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                        openFragment(holder.itemView.context)
                    }
                }
                alertDialog.setNegativeButton(R.string.no) { dialog, which ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
        }

    }

    private fun hideProgress(context: Context) {
        val hideIntent = Intent("com.erensekkeli.gradconnect.PROFILE_FRAGMENT_HIDE_PROGRESS_BAR")
        hideIntent.putExtra("actionType", "hide")
        LocalBroadcastManager.getInstance(context).sendBroadcast(hideIntent)
    }

    private fun openFragment(context: Context) {
        val openIntent = Intent("com.erensekkeli.gradconnect.PROFILE_FRAGMENT_HIDE_PROGRESS_BAR")
        openIntent.putExtra("actionType", "open")
        LocalBroadcastManager.getInstance(context).sendBroadcast(openIntent)
    }
}