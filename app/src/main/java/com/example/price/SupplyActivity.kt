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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.zarinpal.ewallets.purchase.OnCallbackVerificationPaymentListener
import com.zarinpal.ewallets.purchase.ZarinPal
import kotlinx.android.synthetic.main.supply.*
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class SupplyActivity : AppCompatActivity() {
    lateinit var alertDialog: AlertDialog
    var price: Double = 0.0
    var address: String = ""
    lateinit var info: Array<String>
    lateinit var pd2 : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.supply)
        pd2 = progressBar2


        textView12.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    priceText.text = "" + (textView12.text.toString().toDouble() * price) + " تومان "
                } catch (e: Exception) {
                    priceText.text = ""
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        val intent = intent
        var carName = intent.getStringExtra("carName")
        var creationTime = intent.getStringExtra("creationTime")
        var carModel = intent.getStringExtra("carModel")
        var brandName = intent.getStringExtra("brandName")
        var carChassis = intent.getStringExtra("carChassis")
        var partName = intent.getStringExtra("partName")
        price = intent.getDoubleExtra("price", 0.0)
        priceText.text = "" + (price) + " تومان "

        textView2.setText(carName)
        textView3.setText(creationTime)
        textView4.setText(carModel)
        textView5.setText(brandName)
        textView6.setText(carChassis)
        textView13.setText(partName)

    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null) {
            ZarinPal.getPurchase(applicationContext).verificationPayment(intent.data,
                OnCallbackVerificationPaymentListener { isPaymentSuccess, refID, paymentRequest ->
                    Log.i("TAG", "onCallbackResultVerificationPayment: $refID")
                    if (isPaymentSuccess) {
                        Toast.makeText(
                            this@SupplyActivity,
                            "پرداخت با موفقیت انجام شد",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Request(this, true, true, address, info).execute()
                        return@OnCallbackVerificationPaymentListener
                    }
                    Toast.makeText(
                        this@SupplyActivity,
                        "پرداخت موفقیت آمیز نبود",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Request(this, true, false, address, info).execute()
                })
        }
    }



    fun payment(amount: Long) {
        var purchase = ZarinPal.getPurchase(this)
//        var payment = ZarinPal.getSandboxPaymentRequest()
        var payment = ZarinPal.getPaymentRequest()
        payment.merchantID = "b4dade44-9e77-457c-8834-98cf99ac34ca"
        payment.amount = amount
        payment.description = "تست برنامه"
        payment.setCallbackURL("return://zarinpalpayment")

        purchase.startPayment(payment) { status, authority, paymentGatewayUri, intent ->
            if (status == 100) {
                intent.putExtra("pay", "status")
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent)
            } else {
                Toast.makeText(this, "خطا در ایجاد درخواست", Toast.LENGTH_SHORT).show()
//                Toast.makeText(this, authority, Toast.LENGTH_SHORT).show()
            }
            pd2.visibility = ProgressBar.INVISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public fun purchase(v: View) {
        if (textView12.text.toString().trim().equals("") || textView12.text.toString()
                .toInt() <= 0
        ) {
            var toast = Toast.makeText(this, "لطفا تعداد را به درستی مشخص کنید", Toast.LENGTH_SHORT)
            toast.view.background = getDrawable(R.drawable.warning_toast)
            toast.show()
            return
        }
        if (textView10.text.toString().trim().equals("") || textView8.text.toString().trim()
                .equals("") || textView11.text.toString().trim().equals("")
        ) {
            var toast =
                Toast.makeText(this, "لطفا فیلد های ستاره دار را پر نمایید", Toast.LENGTH_SHORT)
            toast.view.background = getDrawable(R.drawable.warning_toast)
            toast.show()
            return
        }

        if (textView13.text.toString().trim().equals("")) {
            var toast =
                Toast.makeText(this, "فیلد کالای درخواستی نمی تواند خالی باشد", Toast.LENGTH_SHORT)
            toast.view.background = getDrawable(R.drawable.warning_toast)
            toast.show()
        }

        popup()

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun popup() {
        val li = LayoutInflater.from(this)
        val promptsView: View = li.inflate(R.layout.pay, null)

        val alertDialogBuilder = AlertDialog.Builder(
            this
        )

        // set prompts.xml to alertdialog builder

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView)

        val rg = promptsView
            .findViewById<View>(R.id.radioGroup) as RadioGroup
        val address = promptsView
            .findViewById<View>(R.id.address) as EditText
        val isOnline = promptsView
            .findViewById<View>(R.id.radioButton) as RadioButton
        val payButton = promptsView
            .findViewById<View>(R.id.pay_btn) as Button
        payButton.setOnClickListener {
            if (address.text.toString().isEmpty()) {
                var toast = Toast.makeText(this, "لطفا آدرس را وارد نمایید", Toast.LENGTH_SHORT)
                toast.view.background = getDrawable(R.drawable.warning_toast)
                toast.show()
                return@setOnClickListener
            }

            var carName = textView2.text.toString()
            var creationTime = textView3.text.toString()
            var carModel = textView4.text.toString()
            var brandName = textView5.text.toString()
            var carChassis = textView6.text.toString()
            var description = textView7.text.toString()
            var clientName = textView10.text.toString()
            var shopId = textView8.text.toString()
            var clientPhone = textView11.text.toString()
            var partName = textView13.text.toString()
            var count = textView12.text.toString()
            info = arrayOf(
                carName,
                creationTime,
                carModel,
                brandName,
                carChassis,
                description,
                clientName,
                shopId,
                clientPhone,
                partName,
                count
            )
            this.address = address.text.toString()
            if (!(isOnline.isChecked)) {
                Request(this, false, false, this.address, info).execute()
            } else {

                pd2.visibility = ProgressBar.VISIBLE
                payment((textView12.text.toString().toLong() * price.toLong()))
            }
            alertDialog.dismiss()
        }


        alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
    }


    inner class Request(
        var context: Context,
        var onlinePay: Boolean,
        var successPay: Boolean,
        var address: String,
        var info: Array<String>
    ) : AsyncTask<String, String, String>() {
        var pd = ProgressDialog(context)
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

                if (!onlinePay) {
                    conn = DriverManager.getConnection(ConnURL)
                    val pdate = PersianDate()
                    val pdformater1 = PersianDateFormat("Y/m/d H:i:s")
                    val date = pdformater1.format(pdate)
                    val queryStmt =
                        "INSERT INTO dbo.Supply VALUES ('${LoginActivity.user}',N'${info[0]}',N'${info[1]}',N'${info[2]}',N'${info[3]}',N'${info[4]}',N'${info[5]}',N'${info[6]}',N'${info[7]}',N'${info[8]}',N'${info[9]}',N'${info[10]}',N'پرداخت در محل',0,N'$address',N'$date','${
                            (textView12.text.toString().toLong() * price.toLong())
                        }')"


                    val stmt = conn.createStatement()
                    val rslt = stmt.executeUpdate(queryStmt)



                    stmt.close()
                    conn.close()
                } else {
                    Log.e("Come Here","pay")
                    conn = DriverManager.getConnection(ConnURL)
                    val pdate = PersianDate()
                    val pdformater1 = PersianDateFormat("Y/m/d H:i:s")
                    val date = pdformater1.format(pdate)


                    if (successPay) {

                        val queryStmt =
                            "INSERT INTO dbo.Supply VALUES ('${LoginActivity.user}',N'${info[0]}',N'${info[1]}',N'${info[2]}',N'${info[3]}',N'${info[4]}',N'${info[5]}',N'${info[6]}',N'${info[7]}',N'${info[8]}',N'${info[9]}',N'${info[10]}',N'پرداخت آنلاین',1,N'$address',N'$date','${
                                (textView12.text.toString().toLong() * price.toLong())
                            }')"


                        val stmt = conn.createStatement()
                        val rslt = stmt.executeUpdate(queryStmt)
                        stmt.close()
                        conn.close()

                    } else {
                        Log.e("yeeesss","tag")

                        val queryStmt =
                            "INSERT INTO dbo.Supply VALUES ('${LoginActivity.user}',N'${info[0]}',N'${info[1]}',N'${info[2]}',N'${info[3]}',N'${info[4]}',N'${info[5]}',N'${info[6]}',N'${info[7]}',N'${info[8]}',N'${info[9]}',N'${info[10]}',N'پرداخت آنلاین',0,N'$address',N'$date','${
                                (textView12.text.toString().toLong() * price.toLong())
                            }')"

                        val stmt = conn.createStatement()
                        val rslt = stmt.executeUpdate(queryStmt)
                        stmt.close()
                        conn.close()
                    }


//"INSERT INTO dbo.Supply VALUES ('${LoginActivity.user}',N'${info[0]}',N'${info[1]}',N'${info[2]}',N'${info[3]}',N'${info[4]}',N'${info[5]}',N'${info[6]}',N'${info[7]}',N'${info[8]}',N'${info[9]}',N'${info[10]}',N'پرداخت آنلاین',1,N'$address',N'$date','${(textView12.text.toString().toLong() * price.toLong())}')"
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
            return ""
        }


    }
}
