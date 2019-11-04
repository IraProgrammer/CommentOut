package ru.islab.evilcomments.presentation.creators

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.widget.TextView
import kotlinx.android.synthetic.main.item_creator.view.*

class CreatorsAdapter : RecyclerView.Adapter<CreatorsAdapter.CreatorsViewHolder>() {

    private var list: List<Creator> = ArrayList()

    fun setList(list: List<Creator>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CreatorsViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(ru.islab.evilcomments.R.layout.item_creator, viewGroup, false)
        return CreatorsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: CreatorsViewHolder, i: Int) {
        viewHolder.bind(list[i])
    }

    inner class CreatorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(creator: Creator) {

            if (creator.link.equals("")) {itemView.tvName.text = creator.username}

            else {
                val textView = itemView.tvName
                textView.isClickable = true
                textView.movementMethod = LinkMovementMethod.getInstance()
                val text = "<a href='${creator.link}'>${creator.username}</a>"
                textView.text = Html.fromHtml(text)
            }
        }
    }
}