package com.noman.blogapp

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.noman.blogapp.databinding.ActivityBlogDetailBinding
import java.util.HashMap

class BlogDetail : AppCompatActivity() {
    private lateinit var binding: ActivityBlogDetailBinding
    private var id: String = ""
    private var title: String = ""
    private var desc: String = ""
    private var count: String = ""
    private var nCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showData()
    }

    private fun showData() {
        id = intent.getStringExtra("id") ?: ""

        FirebaseFirestore.getInstance().collection("Blogs").document(id)
            .addSnapshotListener(EventListener<DocumentSnapshot> { value, error ->
                if (error != null) {
                    return@EventListener
                }
                if (value != null && value.exists()) {
                    Glide.with(applicationContext).load(value.getString("img")).into(binding.imageView3)
                    binding.textView4.text = Html.fromHtml("<font color='B7B7B7'>By </font> <font color='#000000'>${value.getString("author")}")
                    binding.textView5.text = value.getString("tittle")
                    binding.textView6.text = value.getString("desc")
                    title = value.getString("tittle") ?: ""
                    desc = value.getString("desc") ?: ""
                    count = value.getString("share_count") ?: ""

                    val iCount = count.toIntOrNull() ?: 0
                    nCount = iCount + 1
                }
            })

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            val shareBody = desc
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, title)
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(intent, "Share Using"))

            val map = HashMap<String, Any>()
            map["share_count"] = nCount.toString()
            FirebaseFirestore.getInstance().collection("Blogs").document(id).update(map)
        }

        binding.imageView4.setOnClickListener { onBackPressed() }
    }
}
