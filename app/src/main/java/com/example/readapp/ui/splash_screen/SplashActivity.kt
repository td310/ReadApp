package com.example.readapp.ui.splash_screen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import com.example.readapp.R
import com.example.readapp.ui.dashboard_admin.DashboardAdminActivity
import com.example.readapp.ui.dashboard_user.DashboardUserActivity
import com.example.readapp.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            checkUser()
        },1000)
    }

    private fun checkUser() {
        //get current user, if logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        else{
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            firebaseAuth.uid?.let {
                ref.child(it)
                    .addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
                            //get user type (user/admin)
                            val userType = snapshot.child("userType").value
                            if(userType == "User"){
                                startActivity(Intent(this@SplashActivity, DashboardUserActivity::class.java))
                                finish()
                            } else {
                                startActivity(Intent(this@SplashActivity, DashboardAdminActivity::class.java))
                                finish()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
            }
        }
    }
}