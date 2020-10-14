package com.example.price

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class SupplyAdapter (private val info: List<Supplies> , val context: Context) : RecyclerView.Adapter<SupplyAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var username: TextView = view.findViewById(R.id.user)
        var carName: TextView = view.findViewById(R.id.car)
        var creationTime: TextView = view.findViewById(R.id.creation_time)
        var carModel: TextView = view.findViewById(R.id.carMo)
        var brand: TextView = view.findViewById(R.id.brand)
        var chassis: TextView = view.findViewById(R.id.chassis)
        var date: TextView = view.findViewById(R.id.date)
        var client: TextView = view.findViewById(R.id.client)
        var shopId: TextView = view.findViewById(R.id.id)
        var phone: TextView = view.findViewById(R.id.phone)
        var part: TextView = view.findViewById(R.id.part)
        var count: TextView = view.findViewById(R.id.count)
        var payMethod: TextView = view.findViewById(R.id.payment_method)
        var state: TextView = view.findViewById(R.id.pay_state)
        var cost: TextView = view.findViewById(R.id.cost)
        var address: TextView = view.findViewById(R.id.add)
        var des: TextView = view.findViewById(R.id.des)


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sup_design, parent, false)

        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return info.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val inf = info[position]
        holder.username.text = inf.userName
        holder.carName.text = inf.carName
        holder.creationTime.text = inf.creationTime
        holder.carModel.text = inf.carModel
        holder.brand.text = inf.brandName
        holder.chassis.text = inf.carChassis
        holder.date.text = inf.date
        holder.client.text = inf.clientName
        holder.shopId.text = inf.shopId
        holder.phone.text = inf.clientPhone
        holder.part.text = inf.partName
        holder.count.text = inf.count
        holder.payMethod.text = inf.payMethod
        if (inf.isPaid.toInt() == 0){
            holder.state.text = "پرداخت نا موفق"
        }
        else {
            holder.state.text = "پرداخت شده"
        }
        holder.cost.text = inf.wholePrice
        holder.address.text = inf.address
        holder.des.text = inf.description

        holder.phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+holder.phone.text.toString()));

            context.startActivity(intent)
        }


    }

}