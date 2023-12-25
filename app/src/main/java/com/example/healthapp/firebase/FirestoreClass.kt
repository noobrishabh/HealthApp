package com.example.healthapp.firebase

import android.util.Log
import com.example.healthapp.activities.MainActivity
import com.example.healthapp.activities.SignInActivity
import com.example.healthapp.activities.SignUpActivity
import com.example.healthapp.models.User
import com.example.healthapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import android.app.Activity
import android.widget.Toast
import com.example.healthapp.activities.MyProfileActivity


class FirestoreClass {

    // Create a instance of Firebase Firestore
    private val mFireStore = FirebaseFirestore.getInstance()

    // TODO (Step 5: Create a function to make an entry of the registered user in the firestore database.)
    /**
     * A function to make an entry of the registered user in the firestore database.
     */
    fun registerUser(activity: SignUpActivity, userInfo: com.example.healthapp.models.User) {

        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(getCurrentUserID())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }
    // TODO (Step 5: Create a function to update the user profile data into the database.)
    // START
    /**
     * A function to update the user profile data into the database.
     */
    fun updateUserProfileData(activity: MyProfileActivity?, userHashMap: HashMap<String, Any>) {
        if (activity == null || activity.isFinishing) {
            // Activity reference is null or the activity is finishing, do not proceed.
            return
        }

        val userId = getCurrentUserID()

        if (userId == null) {
            // Handle the case where the user ID is null.
            Log.e(activity.javaClass.simpleName, "User ID is null.")
            return
        }

        mFireStore.collection(Constants.USERS)
            .document(userId)
            .update(userHashMap)
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.i(activity.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                // Notify the success result.
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating profile data.",
                    e
                )
                Toast.makeText(activity,"Error while updating the profile",Toast.LENGTH_LONG).show()
            }
    }

    // END

    // TODO (Step 6: Create a function for getting the user id of current logged user.)
    // START
    fun loadUserData(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!

                // TODO(Step 6: Modify the parameter and check the instance of activity and send the success result to it.)
                // START
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                    // END
                }
                // END
            }
            .addOnFailureListener { e ->
                // TODO(Step 2: Hide the progress dialog in failure function based on instance of activity.)
                // START
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    // END
                }
                // END
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }
    /**
     * A function for getting the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // TODO (Step 1: Return the user id if he is already logged in before or else it will be blank.)
        // START
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
        // END
    }
}