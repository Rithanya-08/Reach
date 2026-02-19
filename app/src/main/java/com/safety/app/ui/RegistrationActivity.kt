package com.safety.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.safety.app.R
import com.safety.app.data.db.AppDatabase
import com.safety.app.data.db.entities.User
import com.safety.app.data.repository.SafetyRepository
import com.safety.app.utils.EncryptionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationActivity : AppCompatActivity() {

    private lateinit var repository: SafetyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val db = AppDatabase.getDatabase(this)
        repository = SafetyRepository(db)

        val etFullName = findViewById<TextInputEditText>(R.id.etFullName)
        val etAge = findViewById<TextInputEditText>(R.id.etAge)
        val etGender = findViewById<TextInputEditText>(R.id.etGender)
        val etPhoneNumber = findViewById<TextInputEditText>(R.id.etPhoneNumber)
        val etAddress = findViewById<TextInputEditText>(R.id.etAddress)
        val etBloodGroup = findViewById<TextInputEditText>(R.id.etBloodGroup)
        val etMedicalNotes = findViewById<TextInputEditText>(R.id.etMedicalNotes)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val ageStr = etAge.text.toString().trim()
            val gender = etGender.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val bloodGroup = etBloodGroup.text.toString().trim()
            val medicalNotes = etMedicalNotes.text.toString().trim()

            if (fullName.isEmpty() || ageStr.isEmpty() || gender.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = ageStr.toIntOrNull()
            if (age == null) {
                 Toast.makeText(this, "Invalid Age", Toast.LENGTH_SHORT).show()
                 return@setOnClickListener
            }

            // Encrypt sensitive data
            val encPhone = EncryptionUtils.encrypt(phoneNumber)
            val encAddress = EncryptionUtils.encrypt(address)
            val encNotes = if (medicalNotes.isNotEmpty()) EncryptionUtils.encrypt(medicalNotes) else null

            val user = User(
                fullName = fullName,
                age = age,
                gender = gender,
                phoneNumber = encPhone,
                address = encAddress,
                bloodGroup = if (bloodGroup.isNotEmpty()) bloodGroup else null,
                medicalNotes = encNotes
            )

            CoroutineScope(Dispatchers.IO).launch {
                repository.insertUser(user)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistrationActivity, "Profile Saved!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegistrationActivity, DashboardActivity::class.java))
                    finish()
                }
            }
        }
    }
}
