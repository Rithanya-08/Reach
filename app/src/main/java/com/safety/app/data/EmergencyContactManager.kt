package com.safety.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class EmergencyContact(
    val name: String,
    val phoneNumber: String,
    val isPrimary: Boolean = false
)

class EmergencyContactManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("safety_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveContact(contact: EmergencyContact) {
        val contacts = getContacts().toMutableList()
        contacts.add(contact)
        saveList(contacts)
    }

    fun removeContact(phoneNumber: String) {
        val contacts = getContacts().toMutableList()
        contacts.removeAll { it.phoneNumber == phoneNumber }
        saveList(contacts)
    }

    fun getContacts(): List<EmergencyContact> {
        val json = prefs.getString("contacts", null) ?: return emptyList()
        val type = object : TypeToken<List<EmergencyContact>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getPrimaryContact(): EmergencyContact? {
        return getContacts().find { it.isPrimary } ?: getContacts().firstOrNull()
    }

    fun setPrimaryContact(phoneNumber: String) {
        val contacts = getContacts().map { 
            it.copy(isPrimary = it.phoneNumber == phoneNumber) 
        }
        saveList(contacts)
    }

    private fun saveList(contacts: List<EmergencyContact>) {
        prefs.edit().putString("contacts", gson.toJson(contacts)).apply()
    }
}
