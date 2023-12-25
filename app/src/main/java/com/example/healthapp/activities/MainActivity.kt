package com.example.healthapp.activities

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.healthapp.R
import com.example.healthapp.databinding.ActivityMainBinding
import com.example.healthapp.firebase.FirestoreClass
import com.example.healthapp.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var toolbar: Toolbar? = null
    private var drawerLayout: DrawerLayout? = null
    private var navView: NavigationView? = null
    private var waterReminder: LinearLayout?=null
    private var Exercise: LinearLayout?=null
    private var DoctorSection: LinearLayout?=null
    private var ArticleActivit: LinearLayout?=null

    private var binding:ActivityMainBinding?=null

    companion object{
        const val MY_PROFILE_REQUEST_CODE:Int=11
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Initialize the toolbar and drawerLayout using findViewById
        toolbar = findViewById(R.id.toolbar_main_activity)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        waterReminder=findViewById(R.id.waterReminderSection)
        Exercise=findViewById(R.id.doctorsSection)
        DoctorSection=findViewById(R.id.articlesSection)
        ArticleActivit=findViewById(R.id.exerciseSection)


        // Call the setupActionBar function to set up the action bar
        setupActionBar()
        navView?.setNavigationItemSelectedListener(this)
        FirestoreClass().loadUserData(this@MainActivity)

        waterReminder?.setOnClickListener{

            val intent= Intent(this@MainActivity,ReminderActivity::class.java)
            startActivity(intent)


        }
        Exercise?.setOnClickListener{

            val intent= Intent(this@MainActivity,ExerciseActivity::class.java)
            startActivity(intent)


        }
        DoctorSection?.setOnClickListener{

            val intent= Intent(this@MainActivity,DoctorsActivity::class.java)
            startActivity(intent)


        }
        ArticleActivit?.setOnClickListener{

            val intent= Intent(this@MainActivity,ArticleActivity::class.java)
            startActivity(intent)


        }



    }



    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        toolbar?.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        // Add click event for navigation in the action bar and call the toggleDrawer function
        toolbar?.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        // Check if the drawer is open and close it, otherwise open it
        if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    // TODO (Step 5: Add a onBackPressed function and check if the navigation drawer is open or closed.)
    // START
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout?.isDrawerOpen(GravityCompat.START)==true) {
            drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }
    // END
    // TODO (Step 4: Add the onActivityResult function and check the result of the activity for which we expect the result.)
    // START
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
            FirestoreClass().loadUserData(this@MainActivity)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }
    // END

    // TODO (Step 7: Implement members of NavigationView.OnNavigationItemSelectedListener.)
    // START
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        // TODO (Step 9: Add the click events of navigation menu items.)
        // START
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {

                startActivityForResult(Intent(this@MainActivity, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
        // END
        return true
    }
    // END
    // TODO (Step 5: Create a function to update the user details in the navigation view.)
    // START
    /**
     * A function to get the current user details from firebase.
     */
    fun updateNavigationUserDetails(user: User) {
        // The instance of the header view of the navigation view.
        val headerView = navView?.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val navUserImage = headerView?.findViewById<ImageView>(R.id.iv_user_image)

        // Load the user image in the ImageView.
        if (navUserImage != null) {
            Glide
                .with(this@MainActivity)
                .load(user.image) // URL of the image
                .centerCrop() // Scale type of the image.
                .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                .into(navUserImage)
        } // the view in which the image will be loaded.

        // The instance of the user name TextView of the navigation view.
        val navUsername = headerView?.findViewById<TextView>(R.id.tv_username)
        // Set the user name
        navUsername?.text = user.name
    }
    // END


}
