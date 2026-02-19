package com.safety.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safety.app.R
import com.safety.app.data.db.entities.EmergencyContact

class ContactsAdapter(
    private var contacts: List<EmergencyContact>, 
    private val isReadOnly: Boolean = false,
    private val onDelete: ((EmergencyContact) -> Unit)? = null
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPriority: TextView = view.findViewById(R.id.tvPriority)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPhone: TextView = view.findViewById(R.id.tvPhone)
        val btnRemove: Button = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.tvName.text = contact.name
        holder.tvPhone.text = contact.phoneNumber
        
        val priorityLabel = if (contact.priority == 1) "Primary Contact" else "Secondary Contact"
        holder.tvPriority.text = priorityLabel
        holder.tvPriority.setTextColor(if (contact.priority == 1) 0xFFFF0000.toInt() else 0xFF666666.toInt())

        if (isReadOnly) {
            holder.btnRemove.visibility = View.GONE
        } else {
            holder.btnRemove.visibility = View.VISIBLE
            holder.btnRemove.setOnClickListener { onDelete?.invoke(contact) }
        }
    }

    override fun getItemCount() = contacts.size

    fun updateList(newContacts: List<EmergencyContact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}
