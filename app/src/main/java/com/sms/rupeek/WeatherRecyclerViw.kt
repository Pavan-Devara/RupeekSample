package com.sms.rupeek

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sms.rupeek.data.Data
import java.text.SimpleDateFormat
import java.util.*

class  WeatherRecyclerViw(val data: List<Data>) : RecyclerView.Adapter<WeatherRecyclerViw.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //initialize the views
        val tempData = itemView.findViewById<TextView>(R.id.temparature)
        val dateData = itemView.findViewById<TextView>(R.id.date)
        val rainData = itemView.findViewById<TextView>(R.id.rainText)
        val windData = itemView.findViewById<TextView>(R.id.windText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.weather_recycler_layout, parent, false )
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val details = data[position]
        val sdf = SimpleDateFormat("MMMM dd yyyy")
        val date = Date((details.time * 1000).toLong())
        val dateSDF = sdf.format(date)

        holder.tempData.text = details.temp.toString() + "\u2103"
        holder.dateData.text = dateSDF
        holder.rainData.text = details.rain.toString() + "%"
        holder.windData.text = details.wind.toString() + " km/hr"
    }
}