package com.palavrizar.tec.palavrizapp.utils.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.LocationBlacklist
import com.palavrizar.tec.palavrizapp.utils.extensions.inflate
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnRemoveLocationClicked
import kotlinx.android.synthetic.main.item_location_limit.view.*

class LocationAdapter(val listener: OnRemoveLocationClicked) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    var context: Context? = null
    var locationBlacklist: ArrayList<LocationBlacklist> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addLocation(location: LocationBlacklist){
        locationBlacklist.add(0, location)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = inflate(R.layout.item_location_limit, p0)
        context = p0.context
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return locationBlacklist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, index: Int) {
        val location = locationBlacklist[index]

        holder.cityName = location.city
        holder.stateName = location.state

        holder.ivRemove.setOnClickListener {
            listener.onRemoveClicked(location, index)
            locationBlacklist.remove(location)
            notifyDataSetChanged()
        }

    }

    class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        var ivRemove: ImageView = view.findViewById(R.id.iv_remove_city)

        var cityName: String? = null
            set(value) {
                field = value
                view.tv_city_name?.text = value
            }

        var stateName: String? = null
            set(value) {
                field = value
                view.tv_state_name?.text = value
            }

    }
}