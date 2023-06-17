package com.shubhasai.wellnation

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shubhasai.wellnation.databinding.FragmentChanneldetailsBinding

class ChanneldetailsFragment : Fragment() {
    private lateinit var binding:FragmentChanneldetailsBinding
    private val arg:ChanneldetailsFragmentArgs  by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChanneldetailsBinding.inflate(layoutInflater)
        binding.sendreply.setOnClickListener {
            val builder = activity?.let { BottomSheetDialog(it) }
            val  inflate = layoutInflater
            val dialogLayout = inflate.inflate(R.layout.messagesend_drawer,null)
            val text = dialogLayout.findViewById<EditText>(R.id.etcommunitymessage)
            val btn = dialogLayout.findViewById<ImageView>(R.id.btn_Send)
            val db = FirebaseFirestore.getInstance().collection("channels").document(arg.id).collection("Replies")
            val key = db.document().id
            btn.setOnClickListener {
                val messageText = text.text.toString()
                val messageHtml = Html.toHtml(Html.fromHtml(messageText))
                val message = replies(
                    message = messageText,
                    createdBy = Userinfo.userid,
                    createdAt = Timestamp.now(),
                    name = Userinfo.uname,
                )
                db.document(key).set(message)
                Toast.makeText(activity,"Successfully Posted", Toast.LENGTH_SHORT).show()
                builder?.dismiss()
            }
            builder?.setContentView(dialogLayout)
            builder?.show()
        }
        getreplyies()
        return binding.root
    }
    fun getreplyies(){
        val db = FirebaseFirestore.getInstance().collection("channels").document(arg.id).get().addOnSuccessListener {
            if(it.exists()){
                val channelsCollection = FirebaseFirestore.getInstance().collection("channels").document(arg.id).collection("Replies")

                channelsCollection
                    .orderBy("createdAt", Query.Direction.DESCENDING) // Sort channels by createdAt field in descending order
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val channels = ArrayList<replies>()

                        for (document in querySnapshot.documents) {
                            val channel = document.toObject(replies::class.java)
                            channel?.let { channels.add(it) }
                        }

                        // Channels are now sorted by most recent first in the 'channels' list
                        // Perform further operations with the channels as needed
                        binding.repliesRv.layoutManager = LinearLayoutManager(activity)
                        binding.repliesRv.adapter = activity?.let { RepliesAdapter(channels) }
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors that occurred while fetching channels
                    }
            }
            else{
                Toast.makeText(activity,"No Replies",Toast.LENGTH_SHORT).show()
            }
        }

    }
}