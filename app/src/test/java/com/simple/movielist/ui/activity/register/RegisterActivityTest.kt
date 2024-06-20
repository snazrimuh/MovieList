package com.simple.movielist.ui.activity.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.simple.data.local.DataStoreManager
import com.simple.movielist.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowActivity
import org.robolectric.shadows.ShadowToast
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class RegisterActivityTest : KoinTest {

    private lateinit var activity: RegisterActivity
    private lateinit var dataStoreManager: DataStoreManager

    @Before
    fun setUp() {
        startKoin {
            modules(
                module {
                    single { Mockito.mock(DataStoreManager::class.java) }
                }
            )
        }

        dataStoreManager = declareMock()

        activity = Robolectric.buildActivity(RegisterActivity::class.java)
            .create()
            .start()
            .resume()
            .get()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testRegisterSuccess() = runBlockingTest {
        // Arrange
        val username = "testuser"
        val email = "test@example.com"
        val password = "password"
        val confirmPassword = "password"

        activity.findViewById<EditText>(R.id.editTextUsername).setText(username)
        activity.findViewById<EditText>(R.id.editTextEmail).setText(email)
        activity.findViewById<EditText>(R.id.editTextPassword).setText(password)
        activity.findViewById<EditText>(R.id.editTextConfirmPassword).setText(confirmPassword)

        // Act
        activity.findViewById<Button>(R.id.buttonRegister).performClick()

        // Assert
        Mockito.verify(dataStoreManager).saveUserCredentials(username, email, password, "")
    }

    @Test
    fun testRegisterPasswordMismatch() {
        // Arrange
        val password = "password"
        val confirmPassword = "different"

        activity.findViewById<EditText>(R.id.editTextPassword).setText(password)
        activity.findViewById<EditText>(R.id.editTextConfirmPassword).setText(confirmPassword)

        // Act
        activity.findViewById<Button>(R.id.buttonRegister).performClick()

        // Assert
        val toast = ShadowToast.getLatestToast()
        assertNotNull(toast)
        assertEquals(Toast.LENGTH_SHORT, toast.duration)
        assertEquals("Password and Confirm Password must match", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun testUploadProfilePicture() {
        // Arrange
        val buttonUploadPicture = activity.findViewById<Button>(R.id.buttonUploadPicture)
        buttonUploadPicture.performClick()

        val shadowActivity = ShadowActivity()
        val startedIntent = shadowActivity.nextStartedActivity
        val expectedIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        assertEquals(expectedIntent.action, startedIntent.action)
        assertEquals(expectedIntent.categories, startedIntent.categories)
        assertEquals(expectedIntent.type, startedIntent.type)

        val expectedUri = Uri.parse("content://test/path")
        val resultData = Intent().apply { data = expectedUri }
        shadowActivity.receiveResult(startedIntent, Activity.RESULT_OK, resultData)

        // Assert
        assertEquals(expectedUri, activity.profilePictureUri)
        assertNotNull(activity.findViewById<ImageView>(R.id.ViewProfilePicture).drawable)
    }
}
