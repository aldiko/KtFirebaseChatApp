package com.example.ktfirebasechatapp

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.ktfirebasechatapp.R

class MessageAdapter(context: Context?, resource: Int, messages: List<MessageModel?>?) : ArrayAdapter<MessageModel?>(context!!, resource, messages!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = (context as Activity).layoutInflater.inflate(R.layout.message_item, parent, false)
        }
        val photoImageView = convertView!!.findViewById<ImageView>(R.id.photoImageView)
        val textTextView = convertView.findViewById<TextView>(R.id.textTextView)
        val nameTextView = convertView.findViewById<TextView>(R.id.nameTextView)
        val message = getItem(position)
        val isText = message!!.imageurl == null
        if (isText) {
            textTextView.visibility = View.VISIBLE
            photoImageView.visibility = View.GONE
            textTextView.text = message.text
        } else {
            textTextView.visibility = View.GONE
            photoImageView.visibility = View.VISIBLE
            Glide.with(photoImageView.context).load(message.imageurl).into(photoImageView) // библиотека glide
        }
        nameTextView.text = message.name
        return convertView
    }
}