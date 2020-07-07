package com.example.instegramclone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_post.*
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.HashMap

class AddPost : AppCompatActivity() {
    private var selectedImage: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    fun onClickButton(view: View) {
        val storage = FirebaseStorage.getInstance()
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        //referans olusturduk (Storage içindeki klasörlere ulaşmak için kullanılır)
        val referance = storage.reference
        val imageReferance = referance.child("images").child(imageName)
        if (selectedImage != null) {
            imageReferance.putFile(selectedImage!!).addOnSuccessListener {
                val uploadedImage =
                    FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedImage.downloadUrl.addOnSuccessListener {
                    val downladedUri = it.toString()
                    var postMap = HashMap<String, Any>()
                    postMap.put("downloadUri", downladedUri)
                    postMap.put("user", auth.currentUser?.email.toString())
                    postMap.put("comment", etComment.text.toString())
                    postMap.put("date", Timestamp.now())

                    database.collection("Posts").add(postMap).addOnCompleteListener { task ->
                        if (task.isComplete && task.isSuccessful) {
                                finish()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            applicationContext, it.localizedMessage.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    fun onSelectedImage(view: View) {
        //eğer izin verilmemişse
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //izin iste ---> String[].java == arrayOf.kotlin
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }//izin verilmessi galeriyi ac
        else {
            //kullanıcın galerisine gir
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 2)
        }
    }

    //izin alındığında
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {

            try {
                //seçilen resmin Uri alıyoruz
                selectedImage = data.data
                if (selectedImage != null) {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(contentResolver, selectedImage!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        imgPost.setImageBitmap(bitmap)
                    } else {
                        //Uri to bitmap
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        imgPost.setImageBitmap(bitmap)
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}