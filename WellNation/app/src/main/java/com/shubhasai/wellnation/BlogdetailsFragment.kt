package com.shubhasai.wellnation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhasai.wellnation.databinding.FragmentBlogdetailsBinding
import com.shubhasai.wellnation.utils.dateandtimeformat

class BlogdetailsFragment : Fragment() {
    private lateinit var binding: FragmentBlogdetailsBinding
    private val args:BlogdetailsFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBlogdetailsBinding.inflate(inflater, container, false)
        loadblogdetails(args.blogid)
        return binding.root
    }
    fun loadblogdetails(blogid: String) {
        val db = FirebaseFirestore.getInstance().collection("blogs").document(blogid).get().addOnSuccessListener {
            binding.dblogTitle.text = it.getString("title")
            binding.dblogContent.text = it.getString("body")
            binding.dblogAuthor.text = it.getString("drname")
            binding.dblogDate.text = it.getTimestamp("time")?.toDate()
                ?.let { it1 -> dateandtimeformat.formatSocialTime(it1) }
            val url = it.getString("imageurl")
            Glide.with(context!!).load(url).into(binding.dblogImage)
        }
    }
}