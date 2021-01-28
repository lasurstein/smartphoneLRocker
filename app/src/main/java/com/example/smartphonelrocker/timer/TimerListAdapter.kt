package com.example.smartphonelrocker.timer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smartphonelrocker.R

class TimerListAdapter : ListAdapter<Timer, TimerListAdapter.TimerViewHolder>(TimersComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        return TimerViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.name, current.time)
    }

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

    class TimersComparator : DiffUtil.ItemCallback<Timer>() {
        override fun areItemsTheSame(oldItem: Timer, newItem: Timer): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Timer, newItem: Timer): Boolean {
            return oldItem.id == newItem.id
        }
    }
}