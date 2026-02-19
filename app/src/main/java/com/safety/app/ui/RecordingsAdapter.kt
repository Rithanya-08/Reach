package com.safety.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safety.app.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordingsAdapter(
    private val onPlayClick: (File) -> Unit
) : RecyclerView.Adapter<RecordingsAdapter.ViewHolder>() {

    private var files: List<File> = emptyList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(android.R.id.text1)
        val tvDate: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        holder.tvName.text = file.name
        holder.tvDate.text = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()).format(Date(file.lastModified()))
        
        holder.itemView.setOnClickListener { onPlayClick(file) }
    }

    override fun getItemCount() = files.size

    fun submitList(newFiles: List<File>) {
        files = newFiles
        notifyDataSetChanged()
    }
}
