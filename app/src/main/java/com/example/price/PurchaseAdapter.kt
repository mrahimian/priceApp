package com.example.price

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.price.PurchasesList.Companion.wholePrice
import com.example.price.PurchasesList.Companion.wholePriceTexView

class PurchaseAdapter(private val shops: MutableList<Shopping>, private val recyclerView: RecyclerView , val context: Context) : RecyclerView.Adapter<PurchaseAdapter.MyViewHolder>()  {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var part: TextView = view.findViewById(R.id.cart_part)
        var count: Spinner = view.findViewById(R.id.cart_count)
        var price: TextView = view.findViewById(R.id.cart_price)
        var delete: ImageView = view.findViewById(R.id.remove)



    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.purcases, parent, false)

        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return shops.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val shop = shops[position]
        holder.part.text = shop.part
        holder.delete.setOnClickListener {
            shops.removeAt(position)
            notifyDataSetChanged()
        }
        val arraySpinner = arrayOf(
            "1", "2", "3", "4", "5", "6", "7" , "8" , "9" , "10"
        )
        val adapter = ArrayAdapter<String>(context,
            android.R.layout.simple_spinner_item, arraySpinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.count.adapter = adapter
        holder.count.setSelection(shop.count - 1)
        holder.count.onItemSelectedListener = ComboSelection(this,shop,holder)
        holder.price.text = ((shop.price) * (shop.count.toDouble())).toString()


        /*holder.count.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            var prev = holder.count.getSelectedItemPosition();
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                shop.count = holder.count.selectedItem.toString().toInt()
                var prevPrice = holder.price.text.toString().toDouble()
                Log.e(prevPrice.toString(),"PREVIOUS")
                holder.price.text = ((shop.price) * (holder.count.selectedItem.toString().toDouble())).toString()
                var newPrice = holder.price.text.toString().toDouble()
                Log.e(newPrice.toString(),"NEW")

                if (newPrice - prevPrice != 0.0) {
                    wholePrice += (newPrice - prevPrice)
                    wholePriceTexView.text = wholePrice.toString()
                }

            }

        }*/

    }

    inner class ComboSelection(val adapter : PurchaseAdapter , val shop : Shopping , val holder: MyViewHolder ) : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            shop.count = holder.count.selectedItem.toString().toInt()
            shop.wholePrice = shop.count * shop.price
            holder.price.text = shop.wholePrice.toString() + "\n تومان "

            wholePrice = 0.0
            for (i in 0 until itemCount) {
                wholePrice += shops[i].wholePrice
            }
            wholePriceTexView.text = wholePrice.toString() + " تومان "
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }


}