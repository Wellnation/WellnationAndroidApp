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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shubhasai.wellnation.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment(),ChannelAdapter.ChannelClicked {
    private lateinit var binding:FragmentCommunityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(layoutInflater)
        getcoversation()
        binding.btnAddpost.setOnClickListener {
            val builder = activity?.let { BottomSheetDialog(it) }
            val  inflate = layoutInflater
            val dialogLayout = inflate.inflate(R.layout.messagesend_drawer,null)
            val ettext = dialogLayout.findViewById<EditText>(R.id.etcommunitymessage)
            val btn = dialogLayout.findViewById<ImageView>(R.id.btn_Send)
            val db = FirebaseFirestore.getInstance().collection("channels")
            val key = db.document().id
            btn.setOnClickListener {
                val messageText = ettext.text.toString()
                val messageHtml = Html.toHtml(Html.fromHtml(messageText))
                val message = Channel(
                    message = messageText,
                    createdBy = Userinfo.userid,
                    createdAt = Timestamp.now(),
                    name = Userinfo.uname,
                    docId = key
                )
                db.document(key).set(message)
                Toast.makeText(activity,"Successfully Posted",Toast.LENGTH_SHORT).show()
                builder?.dismiss()
            }
            builder?.setContentView(dialogLayout)
            builder?.show()
        }
        return binding.root
    }

    fun getcoversation(){
        val channelsCollection = FirebaseFirestore.getInstance().collection("channels")

        channelsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING) // Sort channels by createdAt field in descending order
            .get()
            .addOnSuccessListener { querySnapshot ->
                val channels = ArrayList<Channel>()

                for (document in querySnapshot.documents) {
                    val channel = document.toObject(Channel::class.java)
                    channel?.let { channels.add(it) }
                }

                // Channels are now sorted by most recent first in the 'channels' list
                // Perform further operations with the channels as needed
                binding.rvChannels.layoutManager = LinearLayoutManager(activity)
                binding.rvChannels.adapter = activity?.let { ChannelAdapter(it, channels,this) }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred while fetching channels
            }
    }

    override fun onchannelClicked(channel: Channel) {
        val direction = CommunityFragmentDirections.actionCommunityFragmentToChanneldetailsFragment(channel.docId)
        findNavController().navigate(direction)
    }
}