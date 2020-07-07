package com.example.instegramclone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MyAdapter(private val emailList:ArrayList<String>,private val uriList:ArrayList<String>,private val commentList:ArrayList<String>) : RecyclerView.Adapter<MyAdapter.PostHolder>() {

    //view tanımları
    class PostHolder(view:View):RecyclerView.ViewHolder(view){
        var emailText:TextView?= null
        var commentText:TextView?=null
        var imageView:ImageView?=null

        init {
            emailText=view.findViewById(R.id.tvUser)
            commentText=view.findViewById(R.id.tvComment)
            imageView=view.findViewById(R.id.imgPost)
        }
    }
    //bağlama işlemleri (PostHolder classına bahsi geçen viewları buldurmak ıcın bu methodu dolduruz)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        var inflat=LayoutInflater.from(parent.context)
        val view=inflat.inflate(R.layout.tek_satir,parent,false)
        return PostHolder(view)
    }

    override fun getItemCount(): Int {
        return emailList.size
    }
    //viewlaırn içinde ne olucak
    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.emailText!!.text=emailList[position]
        holder.commentText!!.text=commentList[position]
        Picasso.get().load(uriList[position]).into(holder.imageView)
    }
}