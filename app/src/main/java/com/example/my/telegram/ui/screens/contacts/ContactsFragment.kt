package com.example.my.telegram.ui.screens.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my.telegram.R
import com.example.my.telegram.database.CURRENT_UID
import com.example.my.telegram.database.NODE_PHONES_CONTACTS
import com.example.my.telegram.database.NODE_USERS
import com.example.my.telegram.database.REF_DATABASE_ROOT
import com.example.my.telegram.database.getCommonModel
import com.example.my.telegram.databinding.FragmentContactsBinding
import com.example.my.telegram.models.CommonModel
import com.example.my.telegram.ui.screens.singlechat.SingleChatFragment
import com.example.my.telegram.ui.screens.base.BaseFragment
import com.example.my.telegram.utils.APP_ACTIVITY
import com.example.my.telegram.utils.AppValueEventListener
import com.example.my.telegram.utils.downloadAndSetImage
import com.example.my.telegram.utils.replaceFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentContactsBinding.inflate(layoutInflater)
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefUsersListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Контакты"
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecyclerView = binding.contactsRecycleView
        mRefContacts = REF_DATABASE_ROOT.child(
            NODE_PHONES_CONTACTS
        ).child(CURRENT_UID)

        //Настройка для адаптера, где указываем какие данные и откуда получать
        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(mRefContacts, CommonModel::class.java)
            .build()

        //Адаптер принимает данные, отображает в холдере
        mAdapter = object : FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                //Запускается тогда когда адаптер получает доступ к ViewGroup
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_item, parent, false)
                return ContactsHolder(
                    view
                )
            }

            // Заполняет holder
            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                mRefUsers = REF_DATABASE_ROOT.child(
                    NODE_USERS
                ).child(model.id)

                mRefUsersListener = AppValueEventListener {
                    val contact = it.getCommonModel()

                    if (contact.fullname.isEmpty()) {
                        holder.name.text = model.fullname
                    } else holder.name.text = contact.fullname

                    holder.status.text = contact.state
                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener {
                        replaceFragment(
                            SingleChatFragment(
                                model
                            )
                        )
                    }
                }

                mRefUsers.addValueEventListener(mRefUsersListener)
                mapListeners[mRefUsers] = mRefUsersListener
            }
        }

        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()

    }

    // Холдер для захвата ViewGroup
    class ContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.contact_fullname1)
        val status: TextView = view.findViewById(R.id.contact_status)
        val photo: CircleImageView = view.findViewById(R.id.contact_photo)
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        println()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
        println()
    }

}