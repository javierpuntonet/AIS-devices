package pl.sviete.dom.devices.ui.adddevicecreator.aplist

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.sviete.dom.devices.R
import kotlinx.android.synthetic.main.item_ap_list.view.*
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan

class APAdapter (private val items : ArrayList<AccessPointViewModel>, val context: Context, private val listener: OnItemClickListener)
    : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_ap_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    interface OnItemClickListener {
        fun onItemClick(item: AccessPointViewModel)
    }
}

class ViewHolder (val view: View) : RecyclerView.ViewHolder(view) {
    private val apName = view.txt_ap_name
    private val aisLogo = view.img_ap_aislogo

    fun bind(item: AccessPointViewModel , listener: APAdapter.OnItemClickListener) {
        if (item.isAis) {
            apName.text = SpannableStringBuilder(item.ssid).apply{
                setSpan(StyleSpan(Typeface.BOLD), 0, item.ssid.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            aisLogo.visibility = View.VISIBLE
        }
        else {
            apName.text = item.ssid
            aisLogo.visibility = View.GONE
        }
        itemView.setOnClickListener{
            listener.onItemClick(item)
        }
    }
}

