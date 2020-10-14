package com.example.price

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PhoneAdapter(
    private val phones: MutableList<String>,
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<PhoneAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var del = view.findViewById<ImageView>(R.id.delete)
        var phoneNumber = view.findViewById<EditText>(R.id.phone_number)

        init {
            del.setOnClickListener {
                var count = itemCount
                phones.removeAll(phones)
                for (i in 0..count-1) {
                    var view = recyclerView.getChildAt(i)
                    var phone : EditText = view.findViewById(R.id.phone_number)
                    var name = phone.text.toString()
                    phones.add(name)
                    Log.e(name,"NUMBER")
//View view = recyclerView.getChildAt(i);
//    EditText nameEditText = (EditText) view.findViewById(R.id.et_name);
//    String name = nameEditText.getText().toString();
                }
                phones.removeAt(adapterPosition)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.phone_view, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhoneAdapter.MyViewHolder, position: Int) {
        val phone = phones[position]
        holder.phoneNumber.setText(phone)
    }

    override fun getItemCount(): Int {
        return phones.size
    }
}