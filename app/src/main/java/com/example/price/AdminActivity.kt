package com.example.price

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import com.example.price.Requestslist
import com.example.price.SupplyList
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.query.*
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class AdminActivity : AppCompatActivity() {

    val TAG = "VISITOR"
    private val TOPIC = "/topics/myTopic"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        var topic: String = ""
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            topic = it.token
            TokenWriter(this, topic).execute()

        }



        supplies_btn.setOnClickListener {
            startActivity(Intent(this, SupplyList::class.java))
        }
        query_btn.setOnClickListener {
            startActivity(Intent(this, Requestslist::class.java))
        }
        edit_phone_btn.setOnClickListener {
            if (LoginActivity.user.equals("admin") || LoginActivity.user.equals("mohammad")) {
                startActivity(Intent(this, Phones::class.java))
            }else{
                var toast = Toast.makeText(this, "دسترسی به این قسمت برای شما مجاز نمی باشد", Toast.LENGTH_SHORT)
                toast.view.background = getDrawable(R.drawable.warning_toast)
                toast.show()
            }
        }
    }

    inner class TokenWriter(var context: Context, var token: String) :
        AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            val _user = "sa"
            val _pass = DBInfo.PASS
            val _DB = DBInfo.DB
            val _server = DBInfo.SERVER
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var conn: Connection? = null
            var ConnURL: String? = null
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                ConnURL = ("jdbc:jtds:sqlserver://" + _server + ";"
                        + "databaseName=" + _DB + ";user=" + _user + ";password="
                        + _pass + ";")
                conn = DriverManager.getConnection(ConnURL)


                val queryStmt =
                    "UPDATE dbo.adminUsers SET token = '$token' where username = '${LoginActivity.user}'"


                val stmt = conn.createStatement()
                val rslt = stmt.executeUpdate(queryStmt)



                stmt.close()
                conn.close()


            } catch (se: SQLException) {
                runOnUiThread {
                    // some code #3 (Write your code here to run in UI thread)
                    Toast.makeText(context, "خطا در ایجاد اتصال", Toast.LENGTH_LONG).show()
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("خطا در ایجاد اتصال (ممکن است نیاز باشد فیلترشکن خود را روشن کنید)")
                    builder.apply {
                        setPositiveButton("تلاش مجدد",
                            DialogInterface.OnClickListener { dialog, id ->
                                // User clicked OK button
                                doInBackground()
                            })
                        setNegativeButton("خروج از برنامه",
                            DialogInterface.OnClickListener { dialog, id ->
                                // User cancelled the dialog
                                System.exit(0);
                            })
                    }
                    // Set other dialog properties

                    // Create the AlertDialog
                    builder.create()
                    builder.show()
                }
                se.printStackTrace()
            } catch (e: Exception) {
                runOnUiThread {
                    // some code #3 (Write your code here to run in UI thread)
                    Toast.makeText(context, "PLEASE CHECK YOUR CONNECTION!", Toast.LENGTH_LONG)
                        .show()
                }
                e.printStackTrace()
            }
            return ""
        }

    }
}