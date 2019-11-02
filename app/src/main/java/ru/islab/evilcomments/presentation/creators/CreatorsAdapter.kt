package ru.islab.evilcomments.presentation.creators

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_creator.view.*
import ru.islab.evilcomments.R

class CreatorsAdapter : RecyclerView.Adapter<CreatorsAdapter.CreatorsViewHolder>() {

    private var list: List<String> = ArrayList()

    fun setList(words: List<String>) {
        this.list = words
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CreatorsViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_creator, viewGroup, false)
        return CreatorsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: CreatorsViewHolder, i: Int) {
        viewHolder.bind(list[i])
    }

    inner class CreatorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(name: String) {
            itemView.tvName.text = name
        }
    }
}