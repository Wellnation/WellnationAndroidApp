package com.shubhasai.wellnation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhasai.wellnation.databinding.FragmentBlogBinding

class BlogFragment : Fragment(),BlogAdapter.BlogClicked {
    private lateinit var binding: FragmentBlogBinding
    private val blogs = ArrayList<blogdata>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBlogBinding.inflate(inflater, container, false)

        getblogs()
        return binding.root
    }
    fun getblogs(){
        val db = FirebaseFirestore.getInstance().collection("blogs")
        db.get().addOnSuccessListener {
            for (document in it.documents){
                val blog = document.toObject(blogdata::class.java)
                if (blog != null) {
                    blogs.add(blog)
                }
            }
            binding.blogRecyclerView.layoutManager = LinearLayoutManager(activity)
            binding.blogRecyclerView.adapter = BlogAdapter(activity, blogs,this)
        }
    }

    override fun onBlogClicked(blog: blogdata) {
        val directions = BlogFragmentDirections.actionBlogFragmentToBlogdetailsFragment(blog.blogid)
        view?.findNavController()?.navigate(directions)
    }
}