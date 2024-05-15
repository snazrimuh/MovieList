package com.simple.movielist.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simple.movielist.data.DataStoreManager
import com.simple.movielist.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dataStoreManager = DataStoreManager(this)

        val textUsername = findViewById<TextView>(R.id.showUsernameProfile)
        val textEmail = findViewById<TextView>(R.id.showEmailProfile)
        val buttonEditProfile = findViewById<Button>(R.id.buttonEditProfile)

        lifecycleScope.launchWhenStarted {
            dataStoreManager.username.collect { username ->
                textUsername.setText(username)
            }
        }

        lifecycleScope.launchWhenStarted {
            dataStoreManager.email.collect { email ->
                textEmail.setText(email)
            }
        }

        buttonEditProfile.setOnClickListener {
            showEditProfileDialog()
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val editTextDialogUsername = dialogView.findViewById<EditText>(R.id.editTextDialogUsername)
        val editTextDialogEmail = dialogView.findViewById<EditText>(R.id.editTextDialogEmail)
        val buttonDialogSave = dialogView.findViewById<Button>(R.id.buttonDialogSave)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        lifecycleScope.launchWhenStarted {
            dataStoreManager.username.collect { username ->
                editTextDialogUsername.setText(username)
            }
        }

        lifecycleScope.launchWhenStarted {
            dataStoreManager.email.collect { email ->
                editTextDialogEmail.setText(email)
            }
        }

        buttonDialogSave.setOnClickListener {
            val newUsername = editTextDialogUsername.text.toString().trim()
            val newEmail = editTextDialogEmail.text.toString().trim()

            lifecycleScope.launch {
                dataStoreManager.saveUserCredentials(newUsername, newEmail, dataStoreManager.password.first() ?: "")
                Toast.makeText(this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }
}
