package com.erensekkeli.gradconnect.activities

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.erensekkeli.gradconnect.guitools.BottomSheetFragment
import com.erensekkeli.gradconnect.R
import com.erensekkeli.gradconnect.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var overlayView: View
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestedPermission: String
    private var imageUri: Uri? = null
    private var selectedImage: Uri? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        registerLauncher()
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                    selectedImage = if (result.data?.data != null) {
                    result.data?.data
                } else {
                    imageUri
                }
                if (selectedImage != null) {
                    binding.profilePic.setImageURI(selectedImage)
                } else {
                    Toast.makeText(this, R.string.failed_to_get_image, Toast.LENGTH_SHORT).show()
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if(result) {
                when(requestedPermission) {
                    android.Manifest.permission.CAMERA -> {
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, "New Picture")
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                        if (imageUri != null) {
                            val intentToCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intentToCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                            intentToCamera.putExtra("isCamera", true)
                            activityResultLauncher.launch(intentToCamera)
                        } else {
                            Toast.makeText(this, R.string.failed_to_get_image, Toast.LENGTH_SHORT).show()
                        }
                    }
                    android.Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        activityResultLauncher.launch(intentToGallery)
                    }
                }
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
            }
        }
    }


    fun getSheet(view: View) {
        val bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, "BottomSheetFragment")
    }

    private fun userInfoChecker(): Boolean{

        if(binding.nameInput.text.toString().isEmpty() || binding.surnameInput.text.toString().isEmpty()) {
            Toast.makeText(this, R.string.name_or_surname_empty, Toast.LENGTH_SHORT).show()
            return true
        }

        if(binding.mailInput.text.toString().isEmpty()) {
            Toast.makeText(this, R.string.email_empty, Toast.LENGTH_SHORT).show()
            return true
        }

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)\$")
        if(!emailRegex.matches(binding.mailInput.text.toString())) {
            Toast.makeText(this, R.string.email_format_error, Toast.LENGTH_SHORT).show()
            return true
        }

        if(binding.passInput.text.toString().isEmpty() || binding.passInput2.text.toString().isEmpty()) {
            Toast.makeText(this, R.string.password_empty, Toast.LENGTH_SHORT).show()
            return true
        }

        if(binding.passInput.text.toString() != binding.passInput2.text.toString()) {
            Toast.makeText(this, R.string.passwords_not_match, Toast.LENGTH_SHORT).show()
            return true
        }

        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{8,}\$")
        if(!passwordRegex.matches(binding.passInput.text.toString())) {
            Toast.makeText(this, R.string.password_format_error, Toast.LENGTH_SHORT).show()
            return true
        }

        return false
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun getProcessAnimation() {
        binding.progressBar.visibility = View.VISIBLE
        binding.signUpButton.visibility = View.INVISIBLE
        overlayView = View(this)
        overlayView.setBackgroundColor(Color.parseColor("#99000000"))
        overlayView.alpha = 0.5f
        overlayView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.root.addView(overlayView)
    }

    private fun removeProcessAnimation() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.signUpButton.visibility = View.VISIBLE
        binding.root.removeViewAt(binding.root.childCount - 1)
    }

    fun signUp(view: View): Boolean{
        hideKeyboard()
        if(userInfoChecker())
            return false;

        getProcessAnimation()
        val email = binding.mailInput.text.toString()
        val password = binding.passInput.text.toString()
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {

            val reference = storage.reference
            var imageReference: StorageReference? = null
            try{
                val uuid = UUID.randomUUID()
                val imageName = "$uuid.jpg"
                imageReference = reference.child("user_profile_images").child(imageName)
            } catch (e: Exception) {
                imageReference = null
                Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
            }

            if(selectedImage != null && imageReference != null) {
                imageReference.putFile(selectedImage!!).addOnSuccessListener {

                    val downloadUrlTask = imageReference.downloadUrl
                    downloadUrlTask.addOnSuccessListener {
                        val downloadUrl = it.toString()
                        val postMap = hashMapOf<String, Any>()
                        postMap["name"] = binding.nameInput.text.toString()
                        postMap["surname"] = binding.surnameInput.text.toString()
                        postMap["email"] = auth.currentUser!!.email!!
                        postMap["profileImage"] = downloadUrl
                        postMap["entryDate"] = binding.entryDate.text.toString()
                        postMap["graduationDate"] = binding.graduationDate.text.toString()

                        firestore.collection("UserData").add(postMap).addOnSuccessListener {
                            Toast.makeText(this, R.string.registration_successful, Toast.LENGTH_SHORT).show()
                            removeProcessAnimation()
                            val intent = Intent(this@SignUpActivity, FeedActivity::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener { exception ->
                            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                            removeProcessAnimation()
                        }
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    removeProcessAnimation()
                }
            }else if(selectedImage == null) {
                val postMap = hashMapOf<String, Any>()
                postMap["name"] = binding.nameInput.text.toString()
                postMap["surname"] = binding.surnameInput.text.toString()
                postMap["email"] = auth.currentUser!!.email!!
                postMap["entryDate"] = binding.entryDate.text.toString()
                postMap["graduationDate"] = binding.graduationDate.text.toString()

                firestore.collection("UserData").add(postMap).addOnSuccessListener {
                    Toast.makeText(this, R.string.registration_successful, Toast.LENGTH_SHORT).show()
                    removeProcessAnimation()
                    val intent = Intent(this@SignUpActivity, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    removeProcessAnimation()
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            removeProcessAnimation()
        }

        return true
    }

    private fun permissionType(permissionType: String, view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                permissionType
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionType)) {
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
            if(permissionType == android.Manifest.permission.CAMERA) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "New Picture")
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                if (imageUri != null) {
                    val intentToCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intentToCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    intentToCamera.putExtra("isCamera", true)
                    activityResultLauncher.launch(intentToCamera)
                } else {
                    Toast.makeText(this, R.string.failed_to_get_image, Toast.LENGTH_SHORT).show()
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    fun selectImageFromGallery(view: View) {

        //show to user if choose camera or gallery
        val alert = AlertDialog.Builder(this)
        alert.setTitle(R.string.choose_image_from_gallery)
        alert.setMessage(R.string.choose_image_from)
        alert.setPositiveButton(R.string.gallery) { _, _ ->
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionType(android.Manifest.permission.READ_MEDIA_IMAGES, view)
            } else {
                permissionType(android.Manifest.permission.READ_EXTERNAL_STORAGE, view)
            }
        }
        alert.setNegativeButton(R.string.camera) { _, _ ->
            permissionType(android.Manifest.permission.CAMERA, view)
        }
        alert.show()

    }
}