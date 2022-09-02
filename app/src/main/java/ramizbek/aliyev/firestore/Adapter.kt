package ramizbek.aliyev.firestore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ramizbek.aliyev.firestore.databinding.ItemRvBinding

class Adapter(var list: ArrayList<User>, val rvClickCourses: RVClickCourses) :
    RecyclerView.Adapter<Adapter.VH>() {
    inner class VH(var itemRV: ItemRvBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(user: User, position: Int) {
            itemRV.itemName.text = user.name
            itemRV.itemNumber.text = user.number

            itemRV.itemMore.setOnClickListener {
                rvClickCourses.onClick(user, view = itemRV.itemMore, position = position)
            }
            itemRV.cardView.setOnClickListener {
                rvClickCourses.onClickNumber()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position], position = position)
    }

    override fun getItemCount(): Int = list.size
    interface RVClickCourses {
        fun onClick(user: User, view: View, position: Int)
        fun onClickNumber()
    }
}