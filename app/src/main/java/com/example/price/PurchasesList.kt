package com.example.price

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.supply.*

class PurchasesList : AppCompatActivity() {

    companion object{
        var wholePrice : Double = 0.0
        lateinit var wholePriceTexView : TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchases_list)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        wholePriceTexView = findViewById(R.id.textView56)
        show()

    }

    fun show(){
        var recyclerView = findViewById(R.id.items) as RecyclerView
        var mAdapter = PurchaseAdapter(SupplyActivity.shops,recyclerView,this)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = mAdapter
    }
}