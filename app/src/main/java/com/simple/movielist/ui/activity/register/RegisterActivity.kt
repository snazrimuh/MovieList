package com.simple.movielist.ui.activity.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simple.movielist.R
import com.simple.data.local.DataStoreManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RegisterActivity : AppCompatActivity() {
    private val dataStoreManager: DataStoreManager by inject()
    private var profilePictureUri: Uri? = null
    private lateinit var imageViewProfilePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        imageViewProfilePicture = findViewById(R.id.ViewProfilePicture)
        val buttonUploadPicture = findViewById<Button>(R.id.buttonUploadPicture)

        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                profilePictureUri = result.data?.data
                profilePictureUri?.let {
                    imageViewProfilePicture.setImageURI(it)
                }
            }
        }

        buttonUploadPicture.setOnClickListener {
            val pickImageIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            pickImageLauncher.launch(pickImageIntent)
        }

        buttonRegister.setOnClickListener {
            val username = editTextUsername.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            if (password == confirmPassword) {
                lifecycleScope.launch {
                    dataStoreManager.saveUserCredentials(username, email, password, profilePictureUri?.toString() ?: "")
                    finish()
                }
            } else {
                Toast.makeText(this, "Password and Confirm Password must match", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
