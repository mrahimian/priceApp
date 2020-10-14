package com.example.price

import android.app.Activity;
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

public class ResultList : AppCompatActivity() {
    var results = arrayListOf<SearchInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_result_list)
        var intent = intent
        var part = intent.getStringExtra("part")
        var car = intent.getStringExtra("car")
        var model = intent.getStringExtra("model")
        var carChecked = intent.getStringExtra("carChecked")
        var modelChecked = intent.getStringExtra("modelChecked")
        if (carChecked.trim().equals("") && modelChecked.trim().equals("")) {
            for (item in StartActivity.stockInfo) {
                if (item.partName.trim().equals(part.trim())) {
                    results.add(item)
                }
            }
        }
        if (carChecked.trim().equals("") && !modelChecked.trim().equals("")) {
            for (item in StartActivity.stockInfo) {
                if (item.partName.trim().equals(part.trim()) && item.brandName.trim().equals(model.trim())) {
                    results.add(item)
                }
            }
        }
        if (!carChecked.trim().equals("") && modelChecked.trim().equals("")) {
            for (item in StartActivity.stockInfo) {
                if (item.partName.trim().equals(part.trim()) && item.carName.trim().equals(car.trim())) {
                    results.add(item)
                }
            }
        } else {
            for (item in StartActivity.stockInfo) {
                if (item.partName.trim().equals(part.trim()) && item.carName.trim().equals(car.trim()) && item.brandName.trim().equals(model.trim())) {
                    results.add(item)
                }
            }
        }

        var recyclerView = findViewById(R.id.recyclerView) as RecyclerView
        if (results.size != 0 && isStock()) {
            var mAdapter = MyAdapter(results)
            val mLayoutManager = LinearLayoutManager(applicationContext)
            recyclerView!!.layoutManager = mLayoutManager
            recyclerView!!.itemAnimator = DefaultItemAnimator()
            recyclerView!!.adapter = mAdapter
        }
        else{
            val intent = Intent(this,QueryActivity::class.java)
            intent.putExtra("car",car)
            intent.putExtra("part",part)
            startActivity(
                intent
            )
        }

    }
    fun isStock():Boolean{
        for (item in results){
            if (item.stockCount!=0){
                return true
            }
        }
        return false
    }
}
