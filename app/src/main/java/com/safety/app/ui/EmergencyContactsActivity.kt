package com.safety.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.safety.app.R
import com.safety.app.data.db.AppDatabase
import com.safety.app.data.db.entities.EmergencyContact
import com.safety.app.data.repository.SafetyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmergencyContactsActivity : AppCompatActivity() {

    private lateinit var repository: SafetyRepository
    private lateinit var adapter: ContactsAdapter
    private var currentUserId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contacts)

        val db = AppDatabase.getDatabase(this)
        repository = SafetyRepository(db)
        
        // Fetch User and Contacts
        repository.user.observe(this) { user ->
            if (user != null) {
                currentUserId = user.userId
                setupContactsObserver()
            } else {
                 Toast.makeText(this, "User not found. Please register.", Toast.LENGTH_SHORT).show()
                 finish()
            }
        }

        val rvContacts = findViewById<RecyclerView>(R.id.rvContacts)
        rvContacts.layoutManager = LinearLayoutManager(this)
        
        adapter = ContactsAdapter(emptyList()) { contact ->
            CoroutineScope(Dispatchers.IO).launch {
                repository.deleteContact(contact)
            }
        }
        rvContacts.adapter = adapter

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString()
            val phone = findViewById<EditText>(R.id.etPhone).text.toString()
            
            if (name.isNotEmpty() && phone.isNotEmpty()) {
                addContact(name, phone)
                findViewById<EditText>(R.id.etName).text.clear()
                findViewById<EditText>(R.id.etPhone).text.clear()
            }
        }
    }

    private fun setupContactsObserver() {
        repository.getContacts(currentUserId).observe(this) { contacts ->
            adapter.updateList(contacts)
        }
    }

    private fun addContact(name: String, phone: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentContacts = repository.getContactsSync(currentUserId)
            val nextPriority = currentContacts.size + 1
            
            val newContact = EmergencyContact(
                userId = currentUserId,
                name = name,
                phoneNumber = phone,
                priority = nextPriority
            )
            repository.insertContact(newContact)
        }
    }

    // Adapter moved to separate file

}
