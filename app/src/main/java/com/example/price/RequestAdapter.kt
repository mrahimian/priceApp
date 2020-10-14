package com.example.price

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RequestAdapter (private val info: List<Requests> , val context: Context) : RecyclerView.Adapter<RequestAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var username: TextView = view.findViewById(R.id.user2)
        var carName: TextView = view.findViewById(R.id.car2)
        var creationTime: TextView = view.findViewById(R.id.creation_time2)
        var carModel: TextView = view.findViewById(R.id.car2Mo)
        var chassis: TextView = view.findViewById(R.id.chassis2)
        var date: TextView = view.findViewById(R.id.date2)
        var client: TextView = view.findViewById(R.id.client2)
        var shopId: TextView = view.findViewById(R.id.id2)
        var phone: TextView = view.findViewById(R.id.phone2)
        var part: TextView = view.findViewById(R.id.part2)
        var state: TextView = view.findViewById(R.id.req_state)
        var des: TextView = view.findViewById(R.id.des2)


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.req_design, parent, false)

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
        holder.chassis.text = inf.carChassis
        holder.date.text = inf.date
        holder.client.text = inf.clientName
        holder.shopId.text = inf.shopId
        holder.phone.text = inf.clientPhone
        holder.part.text = inf.partName
        holder.state.text = inf.state
        holder.des.text = inf.description

        holder.phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+holder.phone.text.toString()));

            context.startActivity(intent)
        }


    }

}