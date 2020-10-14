package com.example.price

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.AccessController.getContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class StartActivity : AppCompatActivity() {

    var frontFlag = false

    var backFlag = false

    companion object {
        val users: HashMap<String, String> = hashMapOf()
        val admins: HashMap<String, String> = hashMapOf()
        val stockInfo: ArrayList<SearchInfo> = arrayListOf()
        val parts: HashSet<String> = hashSetOf()

        val cars: HashSet<String> = hashSetOf()
        val brands: HashSet<String> = hashSetOf()

        var tokens: ArrayList<String> = arrayListOf()


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        logo.animate().alpha(1.0f).setDuration(2000)
        var ra = RotateAnimation(270f, 365f)
        ra.duration = 2000
        logo.startAnimation(ra)
        GetData(this).execute()

        ra.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                ra = RotateAnimation(365f, 360f)
                ra.duration = 500
                logo.startAnimation(ra)
                ra.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        frontFlag = true
                        if (frontFlag && backFlag) {
                            progressBar.visibility = ProgressBar.GONE
                            val intent = Intent(this@StartActivity, LoginActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                        if (frontFlag && !backFlag) {
                            progressBar.visibility = ProgressBar.VISIBLE
                            textView.visibility = TextView.VISIBLE
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })


    }


    inner class GetData(val context: Context) : AsyncTask<String, Void, String>() {


        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun doInBackground(vararg params: String?): String {
            admins.put("mahdi", "admin")

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
                    "select * from dbo.users2"
                val queryStmt2 =
                    "select * from dbo.adminUsers"
                val queryStmt3 =
                    "select token from dbo.adminUsers"

                val stmt = conn.createStatement()
                val stmt2 = conn.createStatement()
                val stmt3 = conn.createStatement()
                val stmt4 = conn.createStatement()
                val rslt = stmt.executeQuery(queryStmt)
                val rslt2 = stmt2.executeQuery(queryStmt2)
                val rslt3 = stmt3.executeQuery(queryStmt3)

                while (rslt.next()) {
                    var user = rslt.getString(1)
                    var pass = rslt.getString(2)
                    users.put(user, pass)
                }

                while (rslt2.next()) {
                    var user = rslt2.getString(1)
                    var pass = rslt2.getString(2)

                    admins.put(user, pass)
                }

                while (rslt3.next()) {
                    var token = rslt3.getString(1)
                    if(token != null){
                        tokens.add(token)
                    }
                }

                val tableInfoQuery = "select * from dbo.Stock"
                val result = stmt.executeQuery(tableInfoQuery)

                while (result.next()) {
                    var id = result.getString(1)
                    var partName = result.getString(2)
                    var carName = result.getString(3)
                    var creationTime = result.getString(4)
                    var carModel = result.getString(5)
                    var carChassis = result.getString(6)
                    var brandName = result.getString(7)
                    var country = result.getString(8)
                    var price = 0
                    try {
                        price = result.getString(9).toInt()
                    }catch (e : java.lang.Exception){
                        runOnUiThread {
                        }
                    }
                    var supplierId = result.getString(10)
                    var stockCount = 1
                    try {
                         stockCount = result.getString(11).toInt()
                    }catch (e:java.lang.Exception){
                    }
                    var description = result.getString(13)
                    var lastModificationTime = result.getString(16)
                    try {
                        stockInfo.add(
                            SearchInfo(
                                id.toInt(),
                                partName,
                                carName,
                                creationTime,
                                carModel,
                                carChassis,
                                brandName,
                                country,
                                price,
                                supplierId,
                                stockCount,
                                description,
                                lastModificationTime
                            )
                        )
                    }catch (e : Exception){

                    }
                }
                fill()
                val phonesQuery = "select * from dbo.Phones"
                val phones = stmt4.executeQuery(phonesQuery)

                while (phones.next()) {
                    var phoneNumber = phones.getString(1)
                    Phones.phones.add(
                        phoneNumber
                    )
                }


                backFlag = true

                if (frontFlag && backFlag) {
                    runOnUiThread { progressBar.visibility = ProgressBar.GONE }
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

                stmt.close()
                conn.close()
                rslt.close()
                rslt2.close()
                rslt3.close()

            } catch (se: SQLException) {
                runOnUiThread {
                    // some code #3 (Write your code here to run in UI thread)
                    Toast.makeText(context, "خطا در ایجاد اتصال", Toast.LENGTH_LONG).show()
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("خطا در ایجاد اتصال (ممکن است نیاز باشد فیلترشکن خود را روشن کنید)")
                    builder.apply {
                        setPositiveButton("اتصال مجدد",
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

        private fun fill() {
            for (inf in stockInfo) {
                parts.add(handleParantheses(inf.partName))
                cars.add(handleParantheses(inf.carName))
                brands.add(handleParantheses(inf.brandName))
            }
        }

        private fun handleParantheses(string: String) : String{
            var newString : String
            if (string.contains('(')){
                newString = string.replace("("," ( ")
                return newString
            }
            return string
        }

    }
}