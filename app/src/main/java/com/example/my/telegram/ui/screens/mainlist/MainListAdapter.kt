package com.example.my.telegram.ui.screens.mainlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my.telegram.R
import com.example.my.telegram.models.CommonModel
import com.example.my.telegram.ui.screens.singlechat.SingleChatFragment
import com.example.my.telegram.ui.screens.group.GroupChatFragment
import com.example.my.telegram.utils.TYPE_CHAT
import com.example.my.telegram.utils.TYPE_GROUP
import com.example.my.telegram.utils.downloadAndSetImage
import com.example.my.telegram.utils.replaceFragment
import de.hdodenhof.circleimageview.CircleImageView

class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListHolder>() {

    private var listItems = mutableListOf<CommonModel>()

    class MainListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.main_list_item_name
        val itemLastMessage: TextView = view.main_list_last_message
        val itemPhoto: CircleImageView = view.main_list_item_photo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)

        val holder = MainListHolder(view)
        holder.itemView.setOnClickListener {
            when(listItems[holder.adapterPosition].type){
                TYPE_CHAT ->replaceFragment(SingleChatFragment(listItems[holder.adapterPosition]))
                TYPE_GROUP -> replaceFragment(GroupChatFragment(listItems[holder.adapterPosition]))
            }
        }
        return holder
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemName.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
    }

    fun updateListItems(item: CommonModel){
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }

}