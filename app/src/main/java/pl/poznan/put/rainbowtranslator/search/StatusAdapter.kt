package pl.poznan.put.rainbowtranslator.search

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.item_status.view.*
import pl.poznan.put.rainbowtranslator.R


class StatusAdapter(private var statuses: ArrayList<String>, private val context: Context) : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {

    private var lastPosition: Int = -1
    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(statuses[position])
        setAnimation(holder.itemView, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        return StatusViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_status, parent, false))
    }

    override fun getItemCount(): Int = statuses.count()

    fun addStatus(status: String) {
        statuses.add(status)
        notifyItemInserted(status.length - 1)
    }

    class StatusViewHolder(statusView: View) : RecyclerView.ViewHolder(statusView) {
        fun bind(status: String) = with(itemView) {
            tvStatus.text = status
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}