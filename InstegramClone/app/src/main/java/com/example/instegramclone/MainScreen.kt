package com.example.instegramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.main_screen.*


class MainScreen : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseFirestore
    private var emailList:ArrayList<String> = ArrayList()
    private var commentList:ArrayList<String> = ArrayList()
    private var uriList:ArrayList<String> = ArrayList()
    private var adapter:MyAdapter?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)

        auth= FirebaseAuth.getInstance()
        database= FirebaseFirestore.getInstance()

        getDataFireBase()

        //recyclerview
        var layoutManager=LinearLayoutManager(this)
        rootLayout.layoutManager=layoutManager

         adapter=MyAdapter(emailList,uriList,commentList)
        rootLayout.adapter=adapter
    }

    private fun getDataFireBase() {
        //saate göre azalarak
        database.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null){
                Toast.makeText(applicationContext,exception.localizedMessage.toString(),Toast.LENGTH_SHORT).show()
            }else{
                if (snapshot != null){
                    if (!snapshot.isEmpty){
                        emailList.clear()
                        commentList.clear()
                        uriList.clear()
                        val documents=snapshot.documents
                        //her bir dökümanı teker teker inceliyeceğiz
                        for (document in documents){
                               val comment= document.get("comment") as String
                               val user= document.get("user") as String
                               val uri= document.get("downloadUri") as String
                               var date= document.get("date") as com.google.firebase.Timestamp
                               val javaDate=date.toDate()
                            println("user: " +user)
                            println("comment: "+ comment)
                            println("uri: "+uri)

                            emailList.add(user)
                            commentList.add(comment)
                            uriList.add(uri)

                            adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //javadan bin kat daha hızlı
        menuInflater.inflate(R.menu.add_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.add_post){
            //upload Activty
            val intent=Intent(applicationContext,AddPost::class.java)
            startActivity(intent)
        }
        else if(item.itemId==R.id.logout){
            auth.signOut()
            val intent=Intent(applicationContext,LoginScreen::class.java)
            startActivity(intent)
            finish()

        }
        return super.onOptionsItemSelected(item)
    }

}