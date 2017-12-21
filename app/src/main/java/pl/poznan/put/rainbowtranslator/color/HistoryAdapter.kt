package pl.poznan.put.rainbowtranslator.color

import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_history.view.*
import pl.poznan.put.rainbowtranslator.R
import pl.poznan.put.rainbowtranslator.model.ColorData

class HistoryAdapter(private var history: ArrayList<ColorData>, private val onClickListener: OnClickListener) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(history[position])
        holder.itemView.ibDelete.setOnClickListener {
            history.removeAt(holder.adapterPosition)
            onClickListener.onClick(history[holder.adapterPosition])
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false))
    }

    override fun getItemCount(): Int = history.count()

    fun addData(colorData: ColorData) {
        history.add(0, colorData)
        notifyItemInserted(0)
    }

    fun setData(list: List<ColorData>) {
        history.clear()
        history.addAll(list)
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onClick(color: ColorData)
    }

    class HistoryViewHolder(statusView: View) : RecyclerView.ViewHolder(statusView) {
        fun bind(colorData: ColorData) = with(itemView) {
            ivColor.drawable.setColorFilter(Color.parseColor(colorData.rgb), PorterDuff.Mode.ADD)
            tvColorName.text = colorData.color
        }
    }

}