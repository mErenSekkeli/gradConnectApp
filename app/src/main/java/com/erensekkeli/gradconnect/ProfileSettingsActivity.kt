package com.erensekkeli.gradconnect

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
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.erensekkeli.gradconnect.databinding.ActivityProfileSettingsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class ProfileSettingsActivity : AppCompatActivity(), PasswordChangeDialog.PasswordChangeDialogListener{

    private lateinit var binding: ActivityProfileSettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var overlayView: View
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var imageUri: Uri? = null
    private var selectedImage: Uri? = null
    private lateinit var requestedPermission: String
    private var profilePicture: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        registerLauncher()
        getData()
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImage = if (result.data?.data != null) {
                    result.data?.data
                } else {
                    imageUri
                }
                if (selectedImage != null && profilePicture != null ) {
                    binding.profileImage.setImageURI(selectedImage)
                    val reference = storage.reference
                    val uuid = profilePicture!!.substringAfterLast("/").split("?")[0].split("user_profile_images")[1].replaceFirst("%2F","/")
                    Toast.makeText(this, uuid, Toast.LENGTH_SHORT).show()
                    val imageRef = reference.child("user_profile_images$uuid")
                    val newUUID = UUID.randomUUID()
                    val newImageRef = reference.child("user_profile_images/${newUUID.toString()}.jpg")
                    imageRef.delete().addOnSuccessListener {
                        newImageRef.putFile(selectedImage!!).addOnSuccessListener {
                            newImageRef.downloadUrl.addOnSuccessListener { uri ->
                                firestore.collection("UserData").whereEqualTo("email", auth.currentUser!!.email!!).get()
                                    .addOnSuccessListener { documents ->
                                        val document = documents.documents[0]
                                        val docId = document.id

                                        val data: HashMap<String, Any> = hashMapOf(
                                            "profileImage" to uri.toString()
                                        )
                                        firestore.collection("UserData").document(docId).update(data)
                                            .addOnSuccessListener {
                                                profilePicture = uri.toString()
                                                Toast.makeText(this, R.string.profile_image_updated, Toast.LENGTH_SHORT).show()
                                            }.addOnFailureListener { exception ->
                                                Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
                                            }

                                    }.addOnFailureListener { exception ->
                                        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
                                    }

                            }.addOnFailureListener {
                                Toast.makeText(this, R.string.profile_image_update_failed, Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, R.string.profile_image_update_failed, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
                    }
                }else if(selectedImage != null && profilePicture == null) {
                    //add new profile picture
                    binding.profileImage.setImageURI(selectedImage)
                    val reference = storage.reference
                    val newUUID = UUID.randomUUID()
                    val newImageRef = reference.child("user_profile_images/${newUUID.toString()}.jpg")
                    newImageRef.putFile(selectedImage!!).addOnSuccessListener {
                        newImageRef.downloadUrl.addOnSuccessListener { uri ->
                            firestore.collection("UserData").whereEqualTo("email", auth.currentUser!!.email!!).get()
                                .addOnSuccessListener { documents ->
                                    val document = documents.documents[0]
                                    val docId = document.id

                                    val data: HashMap<String, Any> = hashMapOf(
                                        "profileImage" to uri.toString()
                                    )
                                    firestore.collection("UserData").document(docId).update(data)
                                        .addOnSuccessListener {
                                            profilePicture = uri.toString()
                                            Toast.makeText(this, R.string.profile_image_updated, Toast.LENGTH_SHORT).show()
                                        }.addOnFailureListener {
                                            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
                                        }

                                }.addOnFailureListener {
                                    Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
                                }

                        }.addOnFailureListener {
                            Toast.makeText(this, R.string.profile_image_update_failed, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, R.string.profile_image_update_failed, Toast.LENGTH_SHORT).show()
                    }
                }
                else {
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

    private fun getProcessAnimation() {
        binding.progressContainer.visibility = View.VISIBLE

    }

    private fun removeProcessAnimation() {
        binding.progressContainer.visibility = View.INVISIBLE
    }

    private fun timestampToString(timestamp: com.google.firebase.Timestamp): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(timestamp.toDate())
    }

    private fun getData() {
        getProcessAnimation()
        val usersCollection = firestore.collection("UserData")

        usersCollection.whereEqualTo("email", auth.currentUser!!.email!!).get().addOnSuccessListener { documents ->
            if(documents != null && !documents.isEmpty) {
                val document = documents.documents[0]
                val name = document.get("name")?.toString() ?: "-"
                val surname = document.get("surname")?.toString() ?: "-"
                val entryDate = timestampToString(document.get("entryDate") as Timestamp)
                val graduationDate = timestampToString(document.get("graduationDate") as Timestamp)
                val department = document.get("department")?.toString() ?: "-"
                val educationStatus = document.get("educationStatus")?.toString() ?: "-"
                val currentBusiness = document.get("currentBusiness")?.toString() ?: "-"
                val facebook = document.get("facebook")?.toString() ?: "-"
                val linkedin = document.get("linkedin")?.toString() ?: "-"
                profilePicture = document.get("profileImage")?.toString()
                val contactPhone = document.get("contactPhone")?.toString() ?: "-"
                val contactMail = document.get("contactMail")?.toString() ?: "-"

                binding.profileNameSurname.text = "$name $surname"
                binding.nameField.setText(name)
                binding.surnameField.setText(surname)
                binding.entryDateField.setText(entryDate)
                binding.graduationDateField.setText(graduationDate)
                val imageUri: Uri? = profilePicture?.toUri()
                if(imageUri == null || imageUri.toString() == "") {
                    binding.profileImage.setImageResource(R.drawable.app_icon)
                } else {
                    Glide.with(this@ProfileSettingsActivity).load(imageUri).into(binding.profileImage)
                }
                binding.contactPhoneField.setText(contactPhone)
                binding.contactMailField.setText(contactMail)
                binding.departmantField.setText(department)
                val spinIndex = resources.getStringArray(R.array.graduate_types).indexOf(educationStatus)
                binding.gradeSpinner.setSelection(spinIndex)
                binding.currentBusinessField.setText(currentBusiness)
                binding.facebookField.setText(facebook)
                binding.linkedinField.setText(linkedin)

            }
            removeProcessAnimation()
        }.addOnFailureListener { exception ->
            Toast.makeText(this@ProfileSettingsActivity, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
            removeProcessAnimation()
        }

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

    fun changeProfileImage(view: View) {
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

    private fun changeStringToTimestamp(date: String?): Timestamp {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val parsedDate = dateFormat.parse(date)
        return Timestamp(parsedDate!!)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun saveData(view: View) {
        hideKeyboard()
        val name = binding.nameField.text.toString()
        val surname = binding.surnameField.text.toString()
        val entryDate = changeStringToTimestamp(binding.entryDateField.text.toString())
        val graduationDate = changeStringToTimestamp(binding.graduationDateField.text.toString())
        val department = binding.departmantField.text.toString()
        val educationStatus = binding.gradeSpinner.selectedItem.toString()
        val currentBusiness = binding.currentBusinessField.text.toString()
        val facebook = binding.facebookField.text.toString()
        val linkedin = binding.linkedinField.text.toString()
        val contactPhone = binding.contactPhoneField.text.toString()
        val contactMail = binding.contactMailField.text.toString()

        if(name.isEmpty() || surname.isEmpty()) {
            Toast.makeText(this, R.string.fill_requried_fields, Toast.LENGTH_LONG).show()
        } else {
            getProcessAnimation()
            val usersCollection = firestore.collection("UserData")

            usersCollection.whereEqualTo("email", auth.currentUser!!.email!!).get().addOnSuccessListener { documents ->
                if(documents != null && !documents.isEmpty) {
                    val document = documents.documents[0]
                    val documentId = document.id
                    val user: HashMap<String, Any> = hashMapOf(
                        "name" to name,
                        "surname" to surname,
                        "entryDate" to entryDate,
                        "graduationDate" to graduationDate,
                        "department" to department,
                        "educationStatus" to educationStatus,
                        "currentBusiness" to currentBusiness,
                        "facebook" to facebook,
                        "linkedin" to linkedin,
                        "contactPhone" to contactPhone,
                        "contactMail" to contactMail
                    )
                    //TODO: not update profile image and set this update
                    //TODO: for Changing Password you must find a modal to change password

                    usersCollection.document(documentId).update(user).addOnSuccessListener {
                        Toast.makeText(this, R.string.user_data_saved, Toast.LENGTH_LONG).show()
                        removeProcessAnimation()
                        finish()
                    }.addOnFailureListener { exception ->
                        Toast.makeText(this, R.string.user_data_save_failed, Toast.LENGTH_LONG).show()
                        removeProcessAnimation()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, R.string.user_data_save_failed, Toast.LENGTH_LONG).show()
                removeProcessAnimation()
            }
        }
    }


    fun goBack(view: View) {
        finish()
    }

    fun changePassword(view: View) {
        val dialog = PasswordChangeDialog(this, this)
        dialog.show()
    }


    override fun onDialogOkButtonClicked(input: String, inputAgain: String) {
        if(input == inputAgain) {
            val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{8,}\$")
            if(!passwordRegex.matches(input)) {
                Toast.makeText(this, R.string.password_format_error, Toast.LENGTH_SHORT).show()
                return
            }

            getProcessAnimation()
            auth.currentUser!!.updatePassword(input).addOnSuccessListener {
                Toast.makeText(this, R.string.password_changed, Toast.LENGTH_LONG).show()
                removeProcessAnimation()
            }.addOnFailureListener {
                Toast.makeText(this, R.string.password_change_failed, Toast.LENGTH_LONG).show()
                removeProcessAnimation()
            }
        } else {
            Toast.makeText(this, R.string.passwords_not_match, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDialogCancelButtonClicked() {
        Toast.makeText(this, R.string.password_change_canceled, Toast.LENGTH_LONG).show()
    }

}