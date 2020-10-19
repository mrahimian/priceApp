package com.example.price

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.price.PushNotification
import com.example.price.StartActivity.Companion.admins
import com.example.price.StartActivity.Companion.users
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    val TAG = "StartActivity"

    companion object {
        var user = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public fun login(v: View) {
        if (!users.containsKey(username.text.toString()) && !admins.containsKey(username.text.toString())) {
            var toast = Toast.makeText(this, "نام کاربری صحیح نمی باشد", Toast.LENGTH_SHORT)
            toast.view.background = getDrawable(R.drawable.warning_toast)
            toast.show()
            return
        }
        if (users.containsKey(username.text.toString())) {
            if (!users.get(username.text.toString())?.equals(password.text.toString())!!) {
                var toast =
                    Toast.makeText(this, "نام کاربری و رمز عبور مطابقت ندارند", Toast.LENGTH_SHORT)
                toast.view.background = getDrawable(R.drawable.warning_toast)
                toast.show()
                return
            }
            user = username.text.toString()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            if (!admins.get(username.text.toString())?.equals(password.text.toString())!!) {
                var toast =
                    Toast.makeText(this, "نام کاربری و رمز عبور مطابقت ندارند", Toast.LENGTH_SHORT)
                toast.view.background = getDrawable(R.drawable.warning_toast)
                toast.show()
                return
            }
            user = username.text.toString()
            startActivity(Intent(this, Choose::class.java))
        }

    }

    public fun seen(v: View) {
        if (v.id == R.id.not_see) {
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            not_see.visibility = ImageView.GONE
            see.visibility = ImageView.VISIBLE
        } else {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance())
            see.visibility = ImageView.GONE
            not_see.visibility = ImageView.VISIBLE
        }
    }


}