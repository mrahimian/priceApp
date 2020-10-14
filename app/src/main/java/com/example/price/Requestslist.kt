package com.example.price

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class Requestslist : AppCompatActivity() {
    var results = arrayListOf<Requests>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requestslist)

        Request(this).execute()
    }

    private inner class Request(var context: Context) : AsyncTask<Void, Void, Void>() {
        var pd = ProgressDialog(context)

        override fun onPreExecute() {
            pd.setMessage("در حال دریافت اطلاعات")
            pd.show()
            super.onPreExecute()
        }

        override fun onPostExecute(result: Void?) {
            pd.dismiss()
            super.onPostExecute(result)
        }

        override fun doInBackground(vararg params: Void?): Void? {
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


                val stmt = conn.createStatement()

                val tableInfoQuery = "select * from dbo.Request order by date desc"
                val result = stmt.executeQuery(tableInfoQuery)

                while (result.next()) {
                    var userName = result.getString(1)
                    var carName = result.getString(2)
                    var creationTime = result.getString(3)
                    var carModel = result.getString(4)
                    var carChassis = result.getString(5)
                    var clientName = result.getString(6)
                    var shopId = result.getString(7)
                    var clientPhone = result.getString(8)
                    var partName = result.getString(9)
                    var state = result.getString(10)
                    var date = result.getString(11)
                    var description = result.getString(12)
                    results.add(
                        Requests(
                            userName, carName, creationTime, carModel, carChassis, clientName, clientPhone, shopId, description, partName, state, date
                        )
                    )
                }

                stmt.close()
                conn.close()
                runOnUiThread {
                    var recyclerView = findViewById(R.id.rec2) as RecyclerView
                    var mAdapter = RequestAdapter(results,context)
                    Log.e(results.toString(), "msg")
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    recyclerView!!.layoutManager = mLayoutManager
                    recyclerView!!.itemAnimator = DefaultItemAnimator()
                    recyclerView!!.adapter = mAdapter
                }


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
            return null
        }

    }
}