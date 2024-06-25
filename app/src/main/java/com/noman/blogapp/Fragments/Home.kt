package com.noman.blogapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.noman.blogapp.Adapter
import com.noman.blogapp.Model
import com.noman.blogapp.databinding.FragmentHomeBinding


class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var list: ArrayList<Model>
    private lateinit var adapter: Adapter
    private lateinit var model: Model

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRv()
        setSearchView()
    }

    private fun setSearchView() {
        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return true
            }
        })
    }

    private fun filter(newText: String) {
        val filteredList = ArrayList<Model>()
        for (item in list) {
            if (item.title.toLowerCase().contains(newText)) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            adapter.filter_list(list)
        } else {
            adapter.filter_list(filteredList)
        }
    }

    private fun setupRv() {
        list = ArrayList()
        FirebaseFirestore.getInstance().collection("Blogs").orderBy("timestamp")
            .addSnapshotListener { value, error ->
                list.clear()
                if (value != null) {
                    for (snapshot in value.documents) {
                        model = snapshot.toObject(Model::class.java)!!
                        model.id = snapshot.id
                        list.add(model)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

        adapter = Adapter(list)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        binding.rvBlogs.layoutManager = linearLayoutManager
        binding.rvBlogs.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}