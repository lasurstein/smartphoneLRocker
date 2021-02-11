package com.example.smartphonelrocker.timer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smartphonelrocker.R

class TimerListAdapter : ListAdapter<MyTimer, TimerListAdapter.TimerViewHolder>(TimersComparator()) {

    lateinit var listener: OnItemClickListener

    class TimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timerNameView: TextView = itemView.findViewById(R.id.timer_name)
        private val timerTimeView: TextView = itemView.findViewById(R.id.timer_time)

        fun bind(name: String?, time: String?) {
            timerNameView.text = name
            timerTimeView.text = time
        }

        companion object {
            fun create(parent: ViewGroup): TimerViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return TimerViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        return TimerViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.name, current.time)

        // onClick
        holder.itemView.findViewById<CardView>(R.id.timer_card).setOnClickListener {
            listener.onItemClickListener(it, position, current)
        }
    }

    class TimersComparator : DiffUtil.ItemCallback<MyTimer>() {
        override fun areItemsTheSame(oldItem: MyTimer, newItem: MyTimer): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MyTimer, newItem: MyTimer): Boolean {
            return oldItem.id == newItem.id
        }
    }

    interface OnItemClickListener {
        fun onItemClickListener(view: View, position: Int, clickedTimer: MyTimer)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}