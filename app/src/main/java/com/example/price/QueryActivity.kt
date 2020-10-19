package com.example.price

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.price.Phones.Companion.phones
import com.example.price.StartActivity.Companion.tokens
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import ir.mp.java.mpjava.SoapClient
import kotlinx.android.synthetic.main.query.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

const val TOPIC = "/topics/myTopic"
class QueryActivity : AppCompatActivity() {
    val TAG = "VISITOR"

    private lateinit var carName : String
    private lateinit var year : String
    private lateinit var carModel : String
    private lateinit var carChassis : String
    private lateinit var clientName : String
    private lateinit var clientPhone : String
    private lateinit var shopId : String
    private lateinit var description : String
    private lateinit var partName : String
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.query)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        var intent = intent
        req_car_name.setText(intent.getStringExtra("car"))
        req_part_name.setText(intent.getStringExtra("part"))

        request_btn.setOnClickListener {
            carName = req_car_name.text.toString()
            year = req_year.text.toString()
            carModel = req_car_model.text.toString()
            carChassis = req_car_chassis.text.toString()
            clientName = req_client_name.text.toString()
            clientPhone = req_client_phone.text.toString()
            shopId = req_shop_id.text.toString()
            description = req_description.text.toString()
            partName = req_part_name.text.toString()
            if (partName.isEmpty()){
                var toast =
                    Toast.makeText(this, "پر کردن فیلد های ستاره دار الزامی ست", Toast.LENGTH_SHORT)
                toast.view.background = getDrawable(R.drawable.warning_toast)
                toast.show()
                return@setOnClickListener
            }
            Request(this).execute()
        }

    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                } else {
                }
            } catch (e: java.lang.Exception) {
//                Log.e(TAG, e.toString())
                e.printStackTrace()
            }
        }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        super.onBackPressed()
    }




    inner class Request(val context: Context) : AsyncTask<String, Void, String>(){
        val pd = ProgressDialog(context)
        override fun onPreExecute() {
            pd.setMessage("در حال ارسال درخواست ...")
            pd.show()
            super.onPreExecute()
        }

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

                val pdate = PersianDate()
                val pdformater1 = PersianDateFormat("Y/m/d H:i:s")
                val date = pdformater1.format(pdate)
                val queryStmt =
                    "INSERT INTO dbo.Request VALUES ('${LoginActivity.user}',N'$carName',N'$year',N'$carModel',N'$carChassis',N'$clientName',N'$shopId',N'$clientPhone',N'$partName',0,N'$date',N'$description')"


                val stmt = conn.createStatement()
                val rslt = stmt.executeUpdate(queryStmt)



                stmt.close()
                conn.close()

                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                for (item in tokens) {
                    Log.e(tokens.toString(), "error")
                    PushNotification(
                        NotificationData("استعلام جدید !!", "لطفا پروفایل خود را بررسی نمایید "),
                        item
                    ).also {
                        sendNotification(it)
                    }
                }


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
            pd.dismiss()
            runOnUiThread {
                var toast = Toast.makeText(
                    context,
                    "درخواست شما با موفقیت ثبت شد",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
            context.startActivity(Intent(context,MainActivity::class.java))
            sendSMS()
            return ""
        }

        override fun onPostExecute(result: String?) {

            super.onPostExecute(result)
        }

        private fun sendSMS(){
            val from = "100091076434"
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            try {
//                var p = arrayOf("09197664124","09124441765")
                val soapClient = SoapClient("mahdi.jafari98", "@25506339Aa")
//                val obj: Any? = soapClient.SendSimpleSMS(phones.toTypedArray(), from, "استعلام جدید : $partName", false)
                for(num in phones) {
                    val obj: Any? =
                        soapClient.SendSimpleSMS2(num, from, "استعلام جدید : $partName", false)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
}
