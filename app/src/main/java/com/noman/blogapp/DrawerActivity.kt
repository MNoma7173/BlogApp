package com.noman.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.navigation.NavigationView
import com.noman.blogapp.Fragments.Home
import com.noman.blogapp.Fragments.Profile
import com.noman.blogapp.Fragments.Publish
import com.noman.blogapp.databinding.ActivityDrawerBinding

class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityDrawerBinding
    private lateinit var account: GoogleSignInAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showedp()
        setupdrawer()
    }

    private fun setupdrawer() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, Home())
            .commit()

        binding.menuIcon.setOnClickListener {
            binding.drawer.openDrawer(Gravity.LEFT)
        }
        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    private fun showedp() {
        account = GoogleSignIn.getLastSignedInAccount(this)!!
        Glide.with(this).load(account.photoUrl).into(binding.profileIcon)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, Home())
                    .commit()
                binding.drawer.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_publish -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, Publish())
                    .commit()
                binding.drawer.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, Profile())
                    .commit()
                binding.drawer.closeDrawer(GravityCompat.START)
                return true
            }
        }
        binding.drawer.closeDrawer(GravityCompat.START)
        return true
    }
}