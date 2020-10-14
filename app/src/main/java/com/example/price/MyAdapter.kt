package com.example.price

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val info: List<SearchInfo>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var partName: TextView = view.findViewById(R.id.part_name)
        var partBrand: TextView = view.findViewById(R.id.part_brand)
        var carName: TextView = view.findViewById(R.id.car_name)
        var brandName: TextView = view.findViewById(R.id.brand_name)
        var price: TextView = view.findViewById(R.id.price)
        var button: Button = view.findViewById(R.id.supply)
        init {
            button.setOnClickListener{
                val parentRow = it.getParent() as View
                val listView = parentRow.parent as RecyclerView
//                val position = listView.getPositionForView(parentRow)
//                val position = listView.position
                var intent = Intent(it.context, SupplyActivity::class.java)
                intent.putExtra("carName",info.get(position).carName)
                intent.putExtra("creationTime",info.get(position).creationTime)
                intent.putExtra("carModel",info.get(position).carModel)
                intent.putExtra("brandName",info.get(position).brandName)
                intent.putExtra("carChassis",info.get(position).carChassis)
                intent.putExtra("partName",info.get(position).partName)
                intent.putExtra("price",(info.get(position).price.toDouble()/10))
                it.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.response, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val inf = info[position]
        holder.partName.text = inf.partName
        holder.partBrand.text = inf.brandName
        holder.carName.text = inf.carName
        holder.brandName.text = inf.carModel
        holder.price.text = (inf.price.toDouble()/10).toString() + " تومان "

    }

    override fun getItemCount(): Int {
        return info.size
    }
}