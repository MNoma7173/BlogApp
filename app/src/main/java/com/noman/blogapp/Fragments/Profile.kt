package com.noman.blogapp.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.noman.blogapp.R
import com.noman.blogapp.SplashScreen
import com.noman.blogapp.databinding.FragmentProfileBinding


class Profile : Fragment() {

    private var binding: FragmentProfileBinding? = null
    private var account: GoogleSignInAccount? = null
    private lateinit var signInOptions: GoogleSignInOptions
    private lateinit var signInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initvar()
    }

    private fun initvar() {
        account = GoogleSignIn.getLastSignedInAccount(requireContext())
        binding?.uName?.text = account?.displayName
        binding?.uEmail?.text = account?.email
        binding?.profileDp?.let { Glide.with(requireContext()).load(account?.photoUrl).into(it) }

        logoutuser()
    }

    private fun logoutuser() {
        binding?.btnLogout?.setOnClickListener {
            AlertDialog.Builder(requireActivity())
                .setTitle("Log Out?")
                .setMessage("Are you sure to logout from app??")
                .setCancelable(false)
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Yes!") { dialog, _ ->
                    FirebaseAuth.getInstance().signOut() //logout from firebase

                    signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                    signInClient = GoogleSignIn.getClient(requireContext(), signInOptions)

                    signInClient.signOut().addOnCompleteListener { task ->
                        dialog.dismiss()
                        startActivity(Intent(requireActivity().applicationContext, SplashScreen::class.java))
                        requireActivity().finish()
                    }
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}