package pt.ua.openairgame.creategame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import pt.ua.openairgame.R
import pt.ua.openairgame.model.Riddle

class RiddleListAdapter: RecyclerView.Adapter<RiddleListAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val question: TextView = itemView.findViewById(R.id.question)
        val answer: TextView = itemView.findViewById(R.id.answer)
        val index: TextView = itemView.findViewById(R.id.index)
        val constraintLayoutItem: ConstraintLayout = itemView.findViewById(R.id.constraintLayoutItem)
    }

    var data =  mutableListOf<Riddle>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.riddle_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.index.text = "Riddle #${position+1}"
        holder.question.text = "Q: ${item.question}"
        holder.answer.text = "A: ${item.answer}"
        if(position %2 == 0){
            holder.constraintLayoutItem.setBackgroundResource(R.color.green_200)
        }else{
            holder.constraintLayoutItem.setBackgroundResource(R.color.green_yellow)
        }
    }


    fun removeAt(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

}