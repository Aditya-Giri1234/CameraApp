package com.example.cameraapp.adapter

import android.content.Context
import android.content.Intent
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraapp.ImageActivity
import com.example.cameraapp.R
import com.example.cameraapp.VideoActivity
import java.io.File

class MediaAdapter(var context: Context, var list:ArrayList<String>,var isImage:Boolean):RecyclerView.Adapter<MediaAdapter.ViewHolder> (){

    class ViewHolder(var itemView: View):RecyclerView.ViewHolder(itemView) {

        lateinit var ivThumbPic:ImageView
        lateinit var tvFileName: TextView
        lateinit var content: ConstraintLayout
        init {

            ivThumbPic=itemView.findViewById(R.id.ivThumbPic)
            tvFileName=itemView.findViewById(R.id.tvFileName)
            content=itemView.findViewById(R.id.content)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.sample_media,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var file= File(list[position])
        if(isImage){
            holder.ivThumbPic.setImageURI(Uri.parse(file.absolutePath))
        }
        else{

            holder.ivThumbPic.setImageBitmap(ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Images.Thumbnails.MINI_KIND))
        }
        holder.tvFileName.setText(file.name)
        holder.tvFileName.setOnClickListener{
            navigate(file.absolutePath)
        }
        holder.ivThumbPic.setOnClickListener{
            navigate(file.absolutePath)
        }
        holder.content.setOnClickListener{
            navigate(file.absolutePath)
        }

    }

    fun navigate(path:String){
        if(isImage){
            context.startActivity(Intent(context,ImageActivity::class.java).putExtra("image",path))
        }
        else{
            context.startActivity(Intent(context,VideoActivity::class.java).putExtra("video",path))
        }
    }

}