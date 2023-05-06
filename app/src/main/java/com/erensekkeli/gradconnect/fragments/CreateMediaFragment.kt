package com.erensekkeli.gradconnect.fragments


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.erensekkeli.gradconnect.R
import com.erensekkeli.gradconnect.databinding.FragmentCreateMediaBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID


class CreateMediaFragment : Fragment() {

    private lateinit var binding: FragmentCreateMediaBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private var selectedMedia: Uri? = null
    private var selectedVideo: Uri? = null
    private lateinit var requestedPermission: String
    private var overlayView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreateMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        registerLauncher()
        binding.goBackBtn.setOnClickListener {
            goBack(view)
        }

        binding.contentMedia.setOnClickListener {
            addMediaFile(view)
        }

        binding.createMediaBtn.setOnClickListener {
            createMedia(view)
        }

    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val selectedMediaUri = result.data?.data
                selectedMedia = selectedMediaUri
                if(selectedMediaUri.toString().contains("image")) {

                    binding.contentMedia.setImageURI(selectedMedia)
                } else if(selectedMediaUri.toString().contains("video")) {
                    try{
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(context, selectedMedia)

                        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        val time = duration!!.toInt() / 2

                        val bitmap = retriever.getFrameAtTime(time.toLong() * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        binding.contentMedia.setImageBitmap(bitmap)

                    } catch (e: Exception) {
                        Toast.makeText(context, "Video format not supported", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if(result) {
                when(requestedPermission) {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        intentToGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/* video/*")
                        activityResultLauncher.launch(intentToGallery)
                    }
                }
            } else {
                Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun permissionType(permissionType: String, view: View) {
        if (ContextCompat.checkSelfPermission(
                view.context,
                permissionType
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permissionType)) {
                Snackbar.make(view, R.string.give_external_permission, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.give_permission) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            requestedPermission = permissionType
                            permissionLauncher.launch(permissionType)
                        }
                    }.show()
            } else {
                requestedPermission = permissionType
                permissionLauncher.launch(permissionType)
            }
        }else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/* video/*"
            val mimeTypes = arrayOf("image/*", "video/*")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            activityResultLauncher.launch(intent)
        }
    }

    private fun addMediaFile(view: View) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionType(android.Manifest.permission.READ_MEDIA_IMAGES, view)
        } else {
            permissionType(android.Manifest.permission.READ_EXTERNAL_STORAGE, view)
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

    private fun getProcessAnimation() {
        binding.progressBar.visibility = View.VISIBLE
        binding.createMediaBtn.visibility = View.INVISIBLE
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
        binding.createMediaBtn.visibility = View.VISIBLE
        binding.root.removeViewAt(binding.root.childCount - 1)
    }

    private fun createMedia(view: View) {
        getProcessAnimation()
        val title = binding.titleMedia.text.toString()
        val description = binding.descriptionMedia.text?.toString() ?: "-"
        if(title.isEmpty() || selectedMedia == null){
            Toast.makeText(context, R.string.fill_requried_fields, Toast.LENGTH_SHORT).show()
            return
        }
        val reference = storage.reference
        val newUUID = UUID.randomUUID()
        val newMediaRef: Any
        if(selectedMedia.toString().contains("video"))
            newMediaRef = reference.child("user_media/${newUUID.toString()}.mp4")
        else
            newMediaRef = reference.child("user_media/${newUUID.toString()}.jpg")

        newMediaRef.putFile(selectedMedia!!).addOnSuccessListener {
            newMediaRef.downloadUrl.addOnSuccessListener { uri ->

                val data: HashMap<String, Any> = hashMapOf(
                    "title" to title,
                    "description" to description,
                    "mediaUrl" to uri.toString(),
                    "mediaType" to if(selectedMedia.toString().contains("video")) "video" else "image",
                    "date" to Timestamp.now(),
                    "email" to auth.currentUser?.email.toString()
                )

                firestore.collection("UserMedia").add(data)
                    .addOnSuccessListener {
                        Toast.makeText(context,
                            R.string.media_created, Toast.LENGTH_SHORT).show()
                        removeProcessAnimation()
                        goBack(view)
                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            R.string.media_create_failed, Toast.LENGTH_LONG
                        ).show()
                        removeProcessAnimation()
                    }

            }.addOnFailureListener {
                Toast.makeText(context, R.string.profile_image_update_failed, Toast.LENGTH_SHORT).show()
                removeProcessAnimation()
            }
        }.addOnFailureListener {
            Toast.makeText(context, R.string.profile_image_update_failed, Toast.LENGTH_SHORT).show()
            removeProcessAnimation()
        }
    }
}