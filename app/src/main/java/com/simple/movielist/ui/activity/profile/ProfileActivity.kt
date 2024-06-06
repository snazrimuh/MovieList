package com.simple.movielist.ui.activity.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bumptech.glide.Glide
import com.simple.movielist.R
import com.simple.data.local.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.simple.movielist.worker.BlurWorker
import org.koin.android.ext.android.inject


class ProfileActivity : AppCompatActivity() {
    private val dataStoreManager: DataStoreManager by inject()
    private lateinit var imageViewProfilePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imageViewProfilePicture = findViewById(R.id.profileImageView)
        val buttonUploadPicture = findViewById<Button>(R.id.buttonUploadPhoto)

        val textUsername = findViewById<TextView>(R.id.showUsernameProfile)
        val textEmail = findViewById<TextView>(R.id.showEmailProfile)
        val buttonEditProfile = findViewById<Button>(R.id.buttonEditProfile)

        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    val blurWorkRequest = OneTimeWorkRequestBuilder<BlurWorker>()
                        .setInputData(workDataOf(BlurWorker.KEY_IMAGE_URI to uri.toString()))
                        .build()
                    WorkManager.getInstance(this).enqueue(blurWorkRequest)

                    imageViewProfilePicture.setImageURI(it)
                    saveProfilePicture(it)
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

        lifecycleScope.launchWhenStarted {
            dataStoreManager.username.collect { username ->
                textUsername.text = username ?: ""
            }
        }

        lifecycleScope.launchWhenStarted {
            dataStoreManager.email.collect { email ->
                textEmail.text = email ?: ""
            }
        }

        lifecycleScope.launchWhenStarted {
            dataStoreManager.profilePicturePath.collect { path ->
                if (!path.isNullOrEmpty()) {
                    Glide.with(this@ProfileActivity)
                        .load(path)
                        .skipMemoryCache(true)
                        .into(imageViewProfilePicture)
                }
            }
        }

        buttonEditProfile.setOnClickListener {
            showEditProfileDialog()
        }
    }

    private fun saveProfilePicture(uri: Uri) {
        lifecycleScope.launch {
            dataStoreManager.saveProfilePhotoUri(uri.toString())
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
                editTextDialogUsername.setText(username ?: "")
            }
        }

        lifecycleScope.launchWhenStarted {
            dataStoreManager.email.collect { email ->
                editTextDialogEmail.setText(email ?: "")
            }
        }

        buttonDialogSave.setOnClickListener {
            val newUsername = editTextDialogUsername.text.toString().trim()
            val newEmail = editTextDialogEmail.text.toString().trim()

            lifecycleScope.launch {
                val currentProfilePicturePath = dataStoreManager.profilePicturePath.first() ?: ""
                dataStoreManager.saveUserCredentials(newUsername, newEmail, dataStoreManager.password.first() ?: "", currentProfilePicturePath)
                Toast.makeText(this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }
}
