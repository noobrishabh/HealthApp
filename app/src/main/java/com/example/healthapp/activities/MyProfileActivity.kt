package com.example.healthapp.activities

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.healthapp.R
import com.example.healthapp.firebase.FirestoreClass
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.example.healthapp.models.User
import de.hdodenhof.circleimageview.CircleImageView
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.healthapp.databinding.ActivityMyProfileBinding
import com.example.healthapp.utils.Constants
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var myProfileToolbar: Toolbar? = null
    private var userImageView: ImageView? = null
    private var userNameEditText: EditText? = null
    private var userEmailEditText: EditText? = null
    private var userMobileEditText: EditText? = null
    private var buttonUpdate: Button?=null

    private var binding: ActivityMyProfileBinding?=null

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE=1
        private const val PICK_IMAGE_REQUEST_CODE=2
    }
    // TODO (Step 10: Add a global variable for URI of a selected image from phone storage.)
    // START
    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null
    // END
    // TODO (Step 6: Add the global variables for UserDetails and Profile Image URL.)
    // START
    // A global variable for user details.
    private lateinit var mUserDetails: User
    // A global variable for a user profile image URL
    private var mProfileImageURL: String = ""
    // END

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Initialize the toolbar using findViewById
        myProfileToolbar = findViewById(R.id.toolbar_my_profile_activity)
//        userImageView = findViewById(R.id.iv_profile_user_image)
//        userNameEditText = findViewById(R.id.et_name)
//        userEmailEditText = findViewById(R.id.et_email)
//        userMobileEditText = findViewById(R.id.et_mobile)
//        buttonUpdate=findViewById(R.id.btn_update)

        // Call the setupActionBar function to set up the action bar
        setupActionBar()

        // TODO (Step 1: Call a function to get the current logged in user details.)
        // START
        FirestoreClass().loadUserData(this@MyProfileActivity)
        // END
        // TODO (Step 4: Add a click event for iv_profile_user_image.)
        // START
        binding?.ivProfileUserImage?.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED
            ) {
                // TODO (Step 8: Call the image chooser function.)
                // START
                showImageChooser()
                // END
            } else {
                /*Requests permissions to be granted to this application. These permissions
                 must be requested in your manifest, they should not be granted to your app,
                 and they should have protection level*/
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        // END
        binding?.btnUpdate?.setOnClickListener{
            // Here if the image is not selected then update the other details of user.
            if (mSelectedImageFileUri != null) {

                uploadUserImage()
            } else {

                showProgressDialog(resources.getString(R.string.please_wait))

                // Call a function to update user details in the database.
                updateUserProfileData()
            }
        }

    }

    // START
    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {
        setSupportActionBar(myProfileToolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        myProfileToolbar?.setNavigationOnClickListener { onBackPressed() }
    }
    // END
    // TODO (Step 2: Create a function to set the existing data in UI.)
    // START
    /**
     * A function to set the existing details in UI.
     */
    fun setUserDataInUI(user: User) {

        mUserDetails=user

        binding?.ivProfileUserImage?.let {
            Glide
                .with(this@MyProfileActivity)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(it)
        }


        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if (user.mobile != 0L) {
            binding?.etMobile?.setText(user.mobile.toString())
        }
    }
    // END
    // TODO (Step 5: Check the result of runtime permission after the user allows or deny based on the unique code.)
    // START
    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO (Step 9: Call the image chooser function.)
                // START
                showImageChooser()
                // END
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    // END
    // TODO (Step 7: Create a function for image selection from phone storage.)
    // START
    /**
     * A function for user profile image selection from phone storage.
     */
    private fun showImageChooser() {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    // END
    // TODO (Step 11: Get the result of the image selection based on the constant code.)
    // START
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data

            try {
                // Load the user image in the ImageView.

                binding?.ivProfileUserImage?.let {
                    Glide
                        .with(this@MyProfileActivity)
                        .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                        .centerCrop() // Scale type of the image.
                        .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                        .into(it)
                }
                // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    // END
    // Step 6: Now add the code to upload image.
    /**
     * A function to upload the selected user image to firebase cloud storage.
     */
    private fun uploadUserImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(
                    mSelectedImageFileUri
                )
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.i(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.i("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()

                            // Call a function to update user details in the database.
                            updateUserProfileData()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@MyProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
        }
    }
    // START
    /**
     * A function to update the user profile details into the database.
     */
    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()

        // Check if the profile image URL is not empty and has changed
        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        // Check if the user name has changed
        if (binding?.etName?.text?.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = binding?.etName?.text?.toString() ?: ""
        }

        // Check if the user email has changed
        if (binding?.etMobile?.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = binding?.etMobile?.text?.toString()?.toLong() ?: 0L
        }

        // Update the data in the database.
        FirestoreClass().updateUserProfileData(this@MyProfileActivity, userHashMap)
    }

    // END



    // TODO (Step 2: Create a function to get the extension of the selected image.)
    // START
    /**
     * A function to get the extension of selected image.
     */
    private fun getFileExtension(uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
    // END
    // TODO (Step 4: Create a function to notify the user profile is updated successfully.)
    // START
    /**
     * A function to notify the user profile is updated successfully.
     */
    fun profileUpdateSuccess() {

        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }
    // END
}
