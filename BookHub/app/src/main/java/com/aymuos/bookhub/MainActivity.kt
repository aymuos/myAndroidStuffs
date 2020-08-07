 package com.aymuos.bookhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
//import android.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat
import com.google.android.material.circularreveal.coordinatorlayout.CircularRevealCoordinatorLayout
import com.google.android.material.navigation.NavigationView

 class MainActivity : AppCompatActivity() {


    private lateinit var drawerLayout: DrawerLayout
     private lateinit var coordinatorLayout: CoordinatorLayout
     private lateinit var toolbar: Toolbar
     private lateinit var frameLayout: FrameLayout
     private lateinit var navigationView: NavigationView
     //declaring the navigation view

     private var previousMenuItem:MenuItem?= null
             //present menu item is 'it', earlier menu is previousMenuItems

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //declaration stats
        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frame)
        navigationView=findViewById(R.id.navigationView)
        //declaration complete
        setUpToolbar()

        openDashboard()

        //setting up function of the back hamburger button
        val actionBarDrawerToggle=ActionBarDrawerToggle(this@MainActivity,drawerLayout,R.string.open_drawer,R.string.close_drawer)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        //this will set a click listener on the hamburger toggle

        actionBarDrawerToggle.syncState()
        //synchronize state of toggle with nav bar i.e when outside will show back



        navigationView.setNavigationItemSelectedListener {
            //it means the present items in the itemId
            //M4T2V4 09:33
            if(previousMenuItem !=null){
                previousMenuItem?.isChecked = false
            }
            it.isCheckable =true
            it.isChecked=true
            previousMenuItem=it //next time someone checks the present item will be needed to become the previous menu item


            //all fragments are defined here
            when(it.itemId){
                R.id.dashboard->{
                    //creating the fragment code
                  openDashboard()
                    //changing title bar name accordingly for each fragment
                    //implementation below
                    drawerLayout.closeDrawers()
                    //mind it closeDrawers not close dRAWER
                }
                R.id.favourites->{
                    //creating the fragment code
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,DashboardFragment())
                        .addToBackStack("Favourites")
                        .commit()

                    //Now we need to close drawer once the transaction is committed
                    supportActionBar?.title="Favourites"
                    drawerLayout.closeDrawers()
                    //mind it closeDrawers not close dRAWER
                }
                R.id.profile->{
                    //creating the fragment code
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,ProfileFragment())
                        .addToBackStack("Profiles")

                        .commit()

                    //Now we need to close drawer once the transaction is committed
                    supportActionBar?.title="Profile"
                    drawerLayout.closeDrawers()
                    //mind it closeDrawers not close dRAWER
                }
                R.id.aboutapp->{
                    //creating the fragment code
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,AboutAppFragment())
                        .addToBackStack("Favourites")
                        .commit()

                    //Now we need to close drawer once the transaction is committed
                    supportActionBar?.title="About App"
                    drawerLayout.closeDrawers()
                    //mind it closeDrawers not close dRAWER

                }
            }
            return@setNavigationItemSelectedListener true
        }
        // click listener
    }


     //setting up the top toolbar
     private fun setUpToolbar(){
         setSupportActionBar(toolbar)
         supportActionBar?.title="Toolbar Title"

         //enables the hamburger icon of back
         supportActionBar?.setHomeButtonEnabled(true)
         supportActionBar?.setDisplayHomeAsUpEnabled(true )

     }
     override fun onOptionsItemSelected(item:MenuItem):Boolean{

         val id= item.itemId
         if( id == android.R.id.home){
             drawerLayout.openDrawer(GravityCompat.START)
             //open drawer from the side the screen starts ie gravity starts
         }
         return super.onOptionsItemSelected(item)
     }


// this following function ensures that the app always opens in dashboard fragment
private fun openDashboard(){
         val fragment=DashboardFragment()
         val transaction=supportFragmentManager.beginTransaction()
         transaction.replace(R.id.frame,fragment)
         transaction.commit()
         supportActionBar?.title = "Dashboard"
        navigationView.setCheckedItem(R.id.dashboard) //setting dashboard for the 1st time 
     }
// implements the back press functionality M4T2V4 6:33
     override fun onBackPressed() {
         val frag = supportFragmentManager.findFragmentById(R.id.frame)

         when(frag) {
             !is DashboardFragment -> openDashboard()
             //when frame is not the dashboard then open the dashboard
             else -> super.onBackPressed()
         }
         }
     }



