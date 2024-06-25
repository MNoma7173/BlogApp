package com.noman.blogapp.Fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.noman.blogapp.databinding.FragmentPublishBinding
import java.util.Date

class Publish : Fragment() {

    private var binding: FragmentPublishBinding? = null
    private var filepath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPublishBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectImage()
    }

    private fun selectImage() {
        binding?.view2?.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(intent, "Select Your Image"), 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filepath = data.data
            binding?.imgThumbnail?.visibility = View.VISIBLE
            binding?.imgThumbnail?.setImageURI(filepath)
            binding?.view2?.visibility = View.INVISIBLE
            binding?.bSelectImage?.visibility = View.INVISIBLE
            uploadData(filepath)
        }
    }

    private fun uploadData(filepath: Uri?) {
        binding?.btnPublish?.setOnClickListener {
            Dexter.withActivity(requireActivity())
                .withPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            binding?.bTittle?.text.toString().let { title ->
                                if (title.isBlank()) {
                                    binding?.bTittle?.error = "Field is Required!!"
                                    return@let
                                }
                                binding?.bDesc?.text.toString().let { desc ->
                                    if (desc.isBlank()) {
                                        binding?.bDesc?.error = "Field is Required!!"
                                        return@let
                                    }
                                    binding?.bAuthor?.text.toString().let { author ->
                                        if (author.isBlank()) {
                                            binding?.bAuthor?.error = "Field is Required!!"
                                            return@let
                                        }
                                        val pd = ProgressDialog(requireContext()).apply {
                                            setTitle("Uploading...")
                                            setMessage("Please wait for a while until we upload this data to our Firebase Storage and Firestore")
                                            setCancelable(false)
                                            show()
                                        }

                                        filepath?.let { filePath ->
                                            FirebaseStorage.getInstance().reference.child("images/${filePath.toString()}.jpg")
                                                .putFile(filePath)
                                                .addOnSuccessListener { taskSnapshot ->
                                                    taskSnapshot.storage.downloadUrl.addOnCompleteListener { task ->
                                                        val fileUrl = task.result.toString()
                                                        val date = DateFormat.format("dd MMM", Date()).toString()
                                                        val timestamp = System.currentTimeMillis().toString()

                                                        val map = hashMapOf(
                                                            "title" to title,
                                                            "desc" to desc,
                                                            "author" to author,
                                                            "date" to date,
                                                            "img" to fileUrl,
                                                            "timestamp" to timestamp,
                                                            "share_count" to "0"
                                                        )

                                                        FirebaseFirestore.getInstance().collection("Blogs").document()
                                                            .set(map)
                                                            .addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    pd.dismiss()
                                                                    Toast.makeText(requireContext(), "Post Uploaded!!!", Toast.LENGTH_SHORT).show()
                                                                    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

                                                                    binding?.imgThumbnail?.visibility = View.INVISIBLE
                                                                    binding?.view2?.visibility = View.VISIBLE
                                                                    binding?.bSelectImage?.visibility = View.VISIBLE
                                                                    binding?.bTittle?.text = "".toEditable()
                                                                    binding?.bDesc?.text = "".toEditable()
                                                                    binding?.bAuthor?.text = "".toEditable()
                                                                }
                                                            }
                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            showSettingDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                })
                .withErrorListener { requireActivity().finish() }
                .onSameThread()
                .check()
        }
    }

    private fun showSettingDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Need Permission")
            .setMessage("This app needs permission to use this feature. You can grant us these permission manually by clicking on below button")
            .setPositiveButton("Next") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireActivity().packageName, null)
                }
                startActivityForResult(intent, 101)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
                requireActivity().finish()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}