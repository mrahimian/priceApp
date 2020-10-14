package com.example.price

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.price.DBInfo.Companion.DB
import com.example.price.DBInfo.Companion.PASS
import com.example.price.DBInfo.Companion.SERVER
import com.example.price.DBInfo.Companion.USER
import kotlinx.android.synthetic.main.activity_phones.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class Phones : AppCompatActivity() {
    companion object {
        var phones : MutableList<String> = mutableListOf()
    }

    lateinit var mAdapter: PhoneAdapter
    lateinit var recyclerView: RecyclerView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phones)
        try {
            phones.removeAll(phones)
        }catch (e : java.lang.Exception){}
        ShowPhone(this,true).execute()

        add_phone.setOnClickListener {
            var count = phones.size
            phones.removeAll(phones)
            for (i in 0 until count) {
                var view = recyclerView.getChildAt(i)
                var phone: EditText = view.findViewById(R.id.phone_number)
                var name = phone.text.toString()
                phones.add(name)
            }
            phones.add("")
            mAdapter.notifyDataSetChanged()
        }

        submit.setOnClickListener {
            var count = phones.size
            phones.removeAll(phones)
            for (i in 0 until count) {
                var view = recyclerView.getChildAt(i)
                var phone: EditText = view.findViewById(R.id.phone_number)
                var name = phone.text.toString()
                phones.add(name)
            }
            ShowPhone(this,false).execute()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    override fun onPause() {
        super.onPause()
        this.finish()
    }

    override fun onStop() {
        super.onStop()
        this.finish()
    }

    private inner class ShowPhone(var context: Context, var getPhones: Boolean) :
        AsyncTask<Void, Void, Void>() {
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

            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var conn: Connection? = null
            var ConnURL: String? = null
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver")
                ConnURL = ("jdbc:jtds:sqlserver://" + SERVER + ";"
                        + "databaseName=" + DB + ";user=" + USER + ";password="
                        + PASS + ";")
                conn = DriverManager.getConnection(ConnURL)

                val stmt = conn.createStatement()
                if (getPhones) {

                    val tableInfoQuery = "select * from dbo.Phones"
                    val result = stmt.executeQuery(tableInfoQuery)

                    while (result.next()) {
                        var phoneNumber = result.getString(1)
                        phones.add(
                            phoneNumber
                        )
                    }
                    runOnUiThread {
                        recyclerView = findViewById(R.id.phone_rec) as RecyclerView
                        mAdapter = PhoneAdapter(phones, recyclerView)
                        val mLayoutManager = LinearLayoutManager(applicationContext)
                        recyclerView!!.layoutManager = mLayoutManager
                        recyclerView!!.itemAnimator = DefaultItemAnimator()
                        recyclerView!!.adapter = mAdapter

                    }
                }
                else{
                    val stmt2 = conn.createStatement()
                    var delete = "delete from dbo.Phones"
                    val del = stmt2.executeUpdate(delete)
                    for(phone in phones) {
                        if(phone.trim().isNotEmpty()) {
                            Log.e(phone, "INSERTION")
                            val addPhones = "INSERT INTO dbo.Phones VALUES ('$phone')"
                            val rslt = stmt.executeUpdate(addPhones)
                        }
                    }
                    stmt2.close()
                }

                stmt.close()
                conn.close()


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