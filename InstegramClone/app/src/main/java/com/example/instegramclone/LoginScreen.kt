package com.example.instegramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_screan.*
import java.lang.Exception

class LoginScreen : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screan)

            //authontication objemi olusturdum
            auth= FirebaseAuth.getInstance()

        //kullanıcı giriş yaptımı kontrolu
        isLogin()
    }

    //kayıt etme fonksyionu with Create
    fun singUp(view: View) {

        var email=etMail.text.toString()
        var password=etPassword.text.toString()
        //burada  it-> bahsedilen task yani görev
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            //başarılı olduysa
            if (it.isSuccessful){
                Toast.makeText(applicationContext,"Hoşgeldin ${auth.currentUser!!.email.toString()}",Toast.LENGTH_SHORT).show()
                changeScreen()
            }
            //başarısız olduysa neden başarısız oldu(Kullanılır Bu)
        }.addOnFailureListener {
            Toast.makeText(this,it.localizedMessage.toString(),Toast.LENGTH_LONG).show()
        }
    }
    //İçeri alma Fonksiyonu
    fun singIn(view: View) {
        auth.signInWithEmailAndPassword(etMail.text.toString(),etPassword.text.toString()).addOnCompleteListener {
            if (it.isSuccessful){
               changeScreen()
            }
        }.addOnFailureListener {
            Toast.makeText(applicationContext,it.localizedMessage.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeScreen(){
        val intent=Intent(applicationContext,MainScreen::class.java)
        startActivity(intent)
        finish()
    }

    private fun isLogin(){
        if (auth.currentUser != null){
           changeScreen()
        }
    }
}