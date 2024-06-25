package com.noman.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.noman.blogapp.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var signInOptions: GoogleSignInOptions
    private lateinit var signInclient: GoogleSignInClient
    private var auth: FirebaseAuth? = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupsignin()
    }

    private fun setupsignin() {
        signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        signInclient = GoogleSignIn.getClient(this, signInOptions)
    }

    override fun onStart() {
        val currentUser = auth?.currentUser
        if(currentUser!=null)
        {
            val intent = Intent(this, DrawerActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            signin()
        }
        super.onStart()
    }
    
    private fun signin()
    {
        val intent = signInclient.signInIntent
        startActivityForResult(intent, 100)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth?.signInWithCredential(authCredential)?.addOnCompleteListener { authResultTask ->
                    if(authResultTask.isSuccessful) {
                        Toast.makeText(applicationContext, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(applicationContext, DrawerActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }
}